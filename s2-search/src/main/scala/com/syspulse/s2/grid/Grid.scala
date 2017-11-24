package com.syspulse.s2.grid

trait Grid {
    def create[K,V](name:String):GridMap[K,V]
    def getMap[K,V](name:String):GridMap[K,V]
}

//object Grid {
//    def apply(engineName:String):Grid = {
//        engineName match {
//            case "mem" => new MemGrid
//            case _ => new MemGrid
//        }
//    }
//}
