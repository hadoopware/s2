package com.syspulse.s2.feed

import com.typesafe.scalalogging.Logger

abstract class FeedFileScannable extends Feed {
	val logger = Logger(this.getClass)

    def start(path:String) = {
        feedsDirectory = path
    }

    private var it:Iterator[Any] = null

    var feedsDirectory = "."

    // queue of files to be parsed
    var queue:List[String] = Nil

    // format id (file extension)
    val format:String

    protected final def scan(dir:String):Seq[String] = {
        //List("1."+format,"2."+format)
        List(feedsDirectory)
    }

    // parse file to seq of Objects
    protected def parse(file:String):Seq[AnyRef]

    def checkNext:Boolean = {
        if(null==it) {
            // update files queue
            queue = queue ++ scan(feedsDirectory)
            // parse one file from the queue
            it = queue.headOption.map( f=> parse(f)).head.iterator
        }
        null!=it && it.hasNext
    }

    override def hasNext = checkNext
    override def next() = it.next()
}
