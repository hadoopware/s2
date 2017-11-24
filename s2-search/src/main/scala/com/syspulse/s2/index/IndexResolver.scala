package com.syspulse.s2.index


sealed class IndexAttrResolver(attrs:Seq[String]) {
    val attributes:Map[String,Int] = attrs.zipWithIndex.foldLeft(Map[String,Int]())( (v1,v2) => v1 + (v2._1 -> v2._2))
    def apply(attrName:String):Int = attributes(attrName)
    override def toString():String = "%s".format(attributes)
}

class IndexResolver private (rtab:Map[String,IndexAttrResolver]){

    def this() = {
        this(Map())
    }

    def add(id:String,attributes:Seq[String]):IndexResolver = {
        new IndexResolver(Map(id-> new IndexAttrResolver(attributes)))
    }

    def resolveIndex(id:String,attrName:String):Int = rtab(id)(attrName)

    def resolve(id:String,attrName:String,iref:IndexRef):AnyRef =
        iref.attrs.get(resolveIndex(id,attrName))


    override def toString():String = "%s".format(rtab)
}
