package com.syspulse.s2

import com.syspulse.s2.index.IndexRef
import scala.collection.JavaConversions._
import com.typesafe.scalalogging.Logger

trait Searchable[I,T] {
	val logger = Logger(this.getClass)

    def getId(o:T):I

    def getIndexAttributes:Seq[String]
    def getAttrValueByName(an: String,o:T):Option[AnyRef]
    def equalsAttrValueByName(an: String,o:T, value:Any):Boolean = {
        getAttrValueByName(an,o).get == value
    }
    //def getAttrValueByOffset(i: Int):Option[AnyRef]

    def getAttrOffset(an:String):Int

    def getIndexRef(sortedAttrIndex:Int,o:T) :IndexRef = {
        logger.debug(s"attrindex=${sortedAttrIndex}: obj=${o}")
        val vv = for ( an <- getIndexAttributes ) yield getAttrValueByName(an,o)

        // special mapping since IndexRef is grid's object and must have all its attributes specified
        // for indexed access
        new IndexRef(getId(o),sortedAttrIndex,vv.map( v=> v.getOrElse(null)))
    }

    def getIndexFilters:Seq[(String,Any)]

    // prepare attribute for indexing
    def split[A](an:String,v:A):Seq[A]
}

object Searchable {
    def apply[T](o:T):Searchable[_,T] = {
        null
    }
}
