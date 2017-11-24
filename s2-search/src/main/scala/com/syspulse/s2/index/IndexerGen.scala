package com.syspulse.s2.index

import com.syspulse.s2.Searchable
import com.syspulse.s2.grid.Grid
import com.typesafe.scalalogging.Logger

import scala.collection.JavaConverters._
import scala.collection.mutable
import java.util
import com.syspulse.s2.SearchParams
import scala._
import scala.AnyRef
import scala.Predef._
import com.googlecode.javaewah.EWAHCompressedBitmap
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

trait IndexerGen[T] extends Indexer {

    this: Grid =>
    // generate Index from requested index names and objects
    //def create(sortedAttrName:String,filters:Seq[String],filterValues:Seq[Any],objs:Seq[T]):Seq[(String,IndexPage)]

	val logger = Logger("IndexerGen")

    val indexName:String
    val infoName:String = "IndexInfo"

    protected def create(ss:Searchable[String,T],
                    sortedAttrName:String,filters:Seq[String],
                    filterValues:Seq[Any],
                    numPages:Int,
                    objs:Seq[T]):Seq[(String,IndexPage)] = {

        require(ss!=null)
        require(sortedAttrName!=null)

        val sortedAttrOffset = ss.getAttrOffset(sortedAttrName)

        val ipages:IndexedSeq[IndexPageBitmatrix] = for( i <- 0 to numPages-1) yield new IndexPageBitmatrix(i)

        logger.info(s"sorted-attr='${sortedAttrName}': sorted-offset=${sortedAttrOffset}: pages=(${ipages.size})")

        // global sorting pager
        val superIndex = new util.TreeSet[IndexRef]();
        for( o <- objs) {
            val iref = ss.getIndexRef(sortedAttrOffset,o)

            // check that it passes the filter
            val valid = filters.view.zipWithIndex.forall {
                case (filterName,i) => ss.equalsAttrValueByName(filterName,o,filterValues(i))
            }
            if(valid)
                superIndex.add(iref)
        }

        logger.info(s"objects=(${objs.size}): filtered=(${superIndex.size})")


        // created filtered pages
        var i = 0
        for( iref <- superIndex.asScala) {
            logger.debug(s"iref=${iref}: ")
            ipages(i).refs.add(iref)
            i = if(ipages(i).size() >= (superIndex.size/numPages) && i<ipages.size-1 ) i+1 else i
        }

        // populate pages with bitmaps
        for( ipage <- ipages) {
            logger.info(s"ipage: ${ipage.getPageId}: (${ipage.size})")


            // traverse all filters
            for((fName,fValue) <- ss.getIndexFilters) {
                val bitmap = new EWAHCompressedBitmap();
                //bitmap.setSizeInBits(IndexPageBitmatrix.PAGE_SIZE,false)

                var bitOffset:Int = 0
                val attrOffset = ss.getAttrOffset(fName)
                for(iref <- ipage.refs.asScala) {
                    for( v <- ss.split(fName,iref.attrs.get(attrOffset))) {
                        logger.info(s"ipage: ${ipage.getPageId}: iref=[${iref}]: (${fName},${fValue}) == ${v}")
                        if(fValue == v)
                            bitmap.set(bitOffset)
                    }
                    bitOffset += 1
                }
                val ifb = new IndexFilterBitmap[Any](attrOffset,fValue,bitmap)
                ipage.filterMatrix.add(ifb)
                logger.info(s"ipage=${ipage}")

            }
        }


        val rr =
            for(ipage <- ipages;
                ikey = new IndexKey(
                    ipage.getPageId,
                    sortedAttrName,
                    filters.toArray,
                    filterValues.toArray.asInstanceOf[Array[AnyRef]]     // ??? conversion
                )

            ) yield (ikey.getKey,ipage)

        logger.info("'%s': %d:\n%s".format(
            sortedAttrName,
            rr.size,
            rr.foldLeft("")( (r,r1) => r+"'%s' -> %s\n\n".format(r1._1,r1._2))
        ))
        rr
    }

    def insert(ipages:Seq[(String,IndexPage)]) {
        val m = getMap[String,IndexPage](indexName)
        for( ipage <- ipages) {
            logger.info(s"'${ipage._1}' -> [${ipage._2}] ")
            m.put(ipage._1,ipage._2)
        }
        // put the size of the page to later map-reducer
        val key = IndexInfo.getKey("")

        val mInfo =  getMap[String,IndexInfo](infoName)
        mInfo.put(key,new IndexInfo(10,ipages(0)._2.refs.size()))
    }


    def dump = {
        logger.info(s"index: '${indexName}': ${getMap(indexName).size}")
        var i = 0
        for( e <- getMap(indexName).entrySet) {
            logger.info(s"${i}: ${e._1} -> ${e._2}")
            i+=1
        }

        logger.info(s"info: '${infoName}': ${getMap(infoName).size}")
        i=0
        for( e <- getMap(infoName).entrySet) {
            logger.info(s"${i}: ${e._1} -> ${e._2}")
            i+=1
        }
    }


    def search[ID](ss:Searchable[ID,T],params:SearchParams[ID]):Seq[ID] = {
        logger.info(s"params: (${params})")

        val ikey = new IndexKey(
            0,
            params.order,
            params.filters.map(f => f._1).toArray,
            params.filters.map(f => f._2).toArray.asInstanceOf[Array[AnyRef]]
        )
        logger.info(s"ikey=${ikey}")

        val map = getMap[String,IndexPageBitmatrix](indexName)

        var max = 0
        var i = 0
        val quantum = 3
        val numPages = 10

        var results = Vector[ID]()

        while(results.size < params.pageEnd && i<=numPages ) {

            // spawn 3 || workers
            val ikeys = for( k <- i to i+quantum) yield new IndexKey(k, params.order)

            logger.info(s"ikeys=${ikeys}")

            val ff = for(ikey <- ikeys) yield Future[Seq[ID]] {

                // prepare bitmap
                var filterBitmap = new EWAHCompressedBitmap()
                filterBitmap.setSizeInBits(IndexPageBitmatrix.PAGE_SIZE,true)

                map.get(ikey.getKey) match {
                    case None =>
                        logger.warn(s"searching: ikey=${ikey.getKey}: not found")
                        Nil
                    case Some(ipage) =>
                        logger.info(s"searching: ${ikey}: ipage=${ipage}...")
                        for( (fName,fValue) <- params.filters) {
                            val ibf = ipage.findBitmap(ss.getAttrOffset(fName),fValue)
                            logger.info(s"searching: ${ikey}: (${fName},${fValue}) : bitmap=${ibf} : ${filterBitmap}")
                            if(null!=ibf) {
                                filterBitmap = filterBitmap.and( ibf.filterBitmap )
                            } else
                                filterBitmap.clear()
                        }

                        logger.info(s"searching: ${ikey}: bitmap=${filterBitmap}")

                        // map matched Id
                        // TODO: ATTENTION !!! THIS IS VERY SLOW and HORRIBLE!
                        val ids=
                        filterBitmap.asScala.flatMap( bit =>
                            ipage.getRef(bit) match {
                                case null => None
                                case iref:IndexRef => Some(iref.id.asInstanceOf[ID])
                            }
                        ).toSeq

                        logger.info(s"searching: ${ikey}: ids=${ids}")
                        ids

                }
            }

            results = ff.foldLeft(results)( (r,f)=> r ++ Await.result(f,10000L millis) )

            i+=quantum
            logger.info(s"searching: pages=(${i}),collected=(${results.size}): range=[${params.pageStart},${params.pageEnd}]: results=${results}")
        }

        val res = results.slice(params.pageStart,params.pageEnd)
        logger.info(s"params: (${params}) -> (${res})")
        res
    }

}


