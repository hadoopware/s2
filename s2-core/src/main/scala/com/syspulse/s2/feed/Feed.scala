package com.syspulse.s2.feed

trait Feed extends Iterator[Any] {
    def start(path:String)
}

object Feed {

    def getExtension(fileName:String):String = {
        val re = """.*\.(\w+)""".r
        fileName match {
            case re(ext) => ext
            case _ => ""
        }
    }

    def apply(feedFile:String,feedEngine:String=""):Feed = {
        val engine = {
            if(feedFile.isEmpty)
                feedEngine
            else {
                if(feedEngine.isEmpty) {
                    // try to guess by extension
                    getExtension(feedFile)
                } else
                    feedEngine
            }
        }

        engine.toLowerCase match {
            case "feed" => new FeedFilePlain
            case "test" => new FeedTestObject
            case _ => {
                // discover engine based on file type
                this.getClass.getClassLoader.loadClass(engine).newInstance().asInstanceOf[Feed]
            }
        }
    }
}
