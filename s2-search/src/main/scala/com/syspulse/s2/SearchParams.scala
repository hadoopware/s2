package com.syspulse.s2

class SearchParams[ID](val order:String,val filters:Seq[(String,Any)],val pageStart:Int,val pageEnd:Int) {
    val result:Seq[ID] = Nil

    override def toString = "'%s',%s,[%d-%d]".format(order,filters,pageStart,pageEnd)
}

object SearchParams {
    def apply[ID](order:String,filters:Seq[(String,Any)],pageStart:Int,pageEnd:Int): SearchParams[ID] =
        new SearchParams[ID](order,filters,pageStart,pageEnd)
}