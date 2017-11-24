package com.syspulse.s2.index

import com.syspulse.s2.grid.Grid
import com.syspulse.s2.data.{VideoSearchable, Video}

trait IndexerVideo extends IndexerGen[Video] {
    this: Grid =>

    val indexName = "VideoIndex"

    def fromObjects(sortedAttrName:String,
                    filters:Seq[String],filterValues:Seq[Any],
                    numPages:Int,
                    objs:Seq[Video]):Seq[(String,IndexPage)] = {
        val ipages =
            create(
                new VideoSearchable,
                sortedAttrName,filters,filterValues,numPages,objs
            )
        ipages
    }


}
