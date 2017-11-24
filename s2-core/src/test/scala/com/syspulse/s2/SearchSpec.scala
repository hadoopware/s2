package com.syspulse.s2

import com.syspulse.s2.data.{VideoSearchable, Video}
import com.syspulse.s2.index.{IndexerVideo, IndexResolver}
import org.scalatest.FlatSpec

class SearchSpec extends FlatSpec {

  "Search" should "work" in {

    val cd1 = new Video
    cd1.setTitle("Avatar")
    cd1.setId("MV0000001")
    val scd = new VideoSearchable

    val indexId = "Video-Title"
    val r = new IndexResolver()
      .add(indexId, scd.getIndexAttributes)

    info(s"IndexResolver: ${r}")

    info(s"IndexRef: ${scd.getIndexRef(0, cd1)}")

    for (an <- scd.getIndexAttributes) {
      val v = r.resolve(indexId, an, scd.getIndexRef(0, cd1))
      info(s"Resolve: '${an}'=${v}")
    }

    val indexer = new Search
    val pages = indexer.fromObjects("title", Seq(), Seq(), 10, Seq(cd1))

    assert(pages.size != 0)
  }

}
