package com.syspulse.s2.feed

import com.syspulse.s2.data.Video

import scala.io.Source
import scala.collection.convert.Wrappers._
import scala.collection.JavaConverters._
import scala.collection.mutable

class FeedFilePlain extends FeedFileScannable {
    val format = "feed"

    def parse(file:String):Seq[AnyRef] = {
        logger.info(s"parsing: '${file}'")
        val cr =  """(?i)content:\s*(.*)\s*\;\s*(.*)\s*\;\s*(.*)\s*\;\s*(.*)\s*\;\s*(.*)\s*\;\s*(.*)\s*\;\s*(.*).*""".r
        val lines = Source.fromFile(file).getLines
        val ccd = lines.flatMap(
            s => s match {
                case cr(tmsProgramId,title,mainCategory,subCategoryList,episodeTitle,starRating,rating) =>
                    val cd = new Video()
                    cd.setId(tmsProgramId)
                    cd.setTitle(title)
                    cd.setCategory(mainCategory)
                    cd.getSubcategories.addAll(subCategoryList.split(",").toList.asJavaCollection)
                    cd.setTitleExtended(episodeTitle)

                    cd.setRating(starRating)
                    cd.setRatingMPAA(rating)
                    //recId.toInt,title,"", NOW.toDate, NOW.toDate, 30, false, false, false, false, dvrId.toInt, "MV0000001", channelName, channelNumber.toInt
                    Some(cd)
                case _ => None  // this is comment
            }
        )
        // DON'T, or iterator will be empty!
        //logger.info(s"file: '${file}': parsed: (${ccd.size})")
        ccd.toSeq
    }
}
