package com.syspulse.s2.data

import com.syspulse.s2.Searchable

import scala.collection.JavaConversions._
import scala.collection._

class VideoSearchable extends Searchable[String,Video]{
    def getId(o:Video):String = o.getId

    val attributes = Array("title","description","category","subcategories","titleExtended","credits","rating","ratingMPAA","adult")

    def getIndexAttributes:Seq[String] = attributes
    def getAttrValueByName(an: String,o:Video):Option[AnyRef] = {
        an match {
            case "title" =>  Some(o.getTitle)
            case "description" =>  Some(o.getDescription)
            case "category" => Some(o.getCategory)
            case "subcategories" => Some(o.getSubcategories)
            case "titleExtended" => Some(o.getTitleExtended)
            case "credits" => Some(o.getCredits)
            case "rating" => Some(o.getRating)
            case "ratingMPAA" => Some(o.getRatingMPAA)
            case "adult" => Some(o.getAdult)
            case _ => None
        }
    }

    override def split[A](an:String,v:A):Seq[A] = {
        an match {
            case "subcategories" => v.asInstanceOf[java.util.List[String]].toArray.toSeq.asInstanceOf[Seq[A]]
            case "credits" => Seq(v)
            case "rating" => Seq(v)
            case _ => Seq(v)
        }
    }

    override def equalsAttrValueByName(an: String, o:Video, value:Any):Boolean = {
        an match {
            case "subcategories" => {
                o.getSubcategories.toList.foldLeft(false)(_ || _.equalsIgnoreCase(value.asInstanceOf[String]))
            }
            case _ => super.equalsAttrValueByName(an,o,value)
        }
    }

    def getAttrOffset(attr:String):Int = {
        var i=0
        while(attributes(i) != attr) {
            i += 1
            if (i >= attributes.size)
                throw new IndexOutOfBoundsException(s"Attribute: '${attr}': not found in ${attributes.toList}")
        }
        i
    }

    override def getIndexFilters:Seq[(String,Any)] =
        Seq(
            ("category","TV"),("category","Movies"),
            ("subcategories","Kids"),("subcategories","Animation"),
            ("rating","****"),
            ("ratingMPAA","NR (Not Rated)")
        )
}


