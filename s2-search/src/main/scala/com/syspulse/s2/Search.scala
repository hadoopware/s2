package com.syspulse.s2

import com.syspulse.s2.data.VideoSearchable
import com.syspulse.s2.grid.mem.MemGrid
import com.syspulse.s2.index.IndexerVideo
import com.typesafe.scalalogging.Logger
import com.syspulse.s2.index.{IndexKey, IndexPage}
import scala.collection.JavaConverters._

class Search extends IndexerVideo with MemGrid {
	override val logger = Logger("Search")

    type TmsId = String
    def query(orderby:String="title",filters:Seq[(String,Any)],pageStart:Int=0,pageEnd:Int=10):Seq[TmsId] = {
        logger.info(s"order=${orderby}: filters=${filters}: page=[${pageStart},${pageEnd}]")

        val res = search[String](new VideoSearchable,
                         new SearchParams(orderby,filters,pageStart,pageEnd))
        res
    }
}
