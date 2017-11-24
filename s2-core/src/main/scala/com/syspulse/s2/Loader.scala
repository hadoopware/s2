package com.syspulse.s2

import com.syspulse.s2.data.Video
import com.syspulse.s2.feed.Feed
import com.typesafe.scalalogging.Logger


object Loader {

	val logger = Logger("Loader")

    def main(args:Array[String]):Unit = {
        val feedFile = if(args.size>0) args(0) else "feeds/1.feed"
        val feedEngine = if(args.size>1) args(1) else ""

        val feed = Feed(feedFile,feedEngine)
        Console.out.println(s"feed='${feed.getClass}'")

        feed.start(feedFile)

        val data:Seq[Video] = feed.flatMap(
            _ match {
                case o:Video => Some(o)
                case a:AnyRef => System.err.format("o=%s\n",a); None
            }
        ).toList

        logger.debug("data=%s\n",data)

        for( o <- data) {
            logger.debug("o=[%s]\n",o.toString)
        }
        Console.out.println(s"objects: ${data.size}")

    }

}
