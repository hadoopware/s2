package com.syspulse.s2.feed

import com.syspulse.s2.data.Video

class FeedTestObject extends Feed {
    val oo = List[Any](
        {val c = new Video; c.setId("MV000001"); c.setTitle("Avatar");  c},
        {val c = new Video; c.setId("MV000002"); c.setTitle("Star Wars"); c}
    )
    var it = oo.iterator

    def start(path:String) = {

    }

    def hasNext = it.hasNext
    def next() = it.next()

}
