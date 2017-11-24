package com.syspulse.s2.grid.akka

import akka.actor.ActorSystem
import com.syspulse.s2.grid.{Grid, GridMap}
import scala.concurrent.Future
import com.typesafe.scalalogging.Logger

class AkkaGrid(name:String,node:String,config:String) extends Grid {
	val logger = Logger("AkkaGrid-"+name)

    val cluster = new Cluster(name,node,config)

    def create[K, V](name: String): GridMap[K, V] = {
        logger.info(s"name='${name}'")
        val map = new AkkaMap[K,V](name,cluster)
        AkkaGrid.maps = AkkaGrid.maps + (name->map)

        logger.info(s"name='${name}' : ${map.toString}")
        map
    }
    def getMap[K, V](name: String): GridMap[K, V] = {
        AkkaGrid.maps.get(name).getOrElse(create(name)).asInstanceOf[GridMap[K,V]]
    }
}

class AkkaMap[K,V](name:String,cluster:Cluster) extends GridMap[K,V](name) {

    def put(k: K, v: V): Option[V] = {
        cluster.resolve(k).put(k,v)
    }

    def get(k: K): Option[V] = ???

    def remove(k: K): Option[V] = ???

    def size: Int = ???

    def entrySet(): Iterator[(K, V)] = ???

    def execute[R](fun: => R): Future[R] = ???
}

object AkkaGrid {
    var maps = Map[String,GridMap[_,_]]()
    val system = ActorSystem("AkkaGrid")



//    def main(args:Array[String]) = {
//        val self = if(args.size>0) args(0) else "node1"
//
//        val grid = new AkkaGrid("GRID-1",self,"akka://node1,akka://node2,akka://node3,akka://node4")
//        for(n<-grid.cluster.nodes) {
//            val resolvedNode = grid.cluster.resolve(n)
//            Shell.println(s"node: '${n}': resolved='${resolvedNode}'")
//        }
//
//        val m1 = grid.getMap[String,String]("map1")
//
//        Shell.println(m1.get("k1"))
//        Shell.println(m1.put("k1","v1"))
//        Shell.println(m1.get("k1"))
//        Shell.println(m1.remove("k1"))
//        Shell.println(m1.get("k1"))
//    }
}
