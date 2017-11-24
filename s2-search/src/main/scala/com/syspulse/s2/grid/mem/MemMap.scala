package com.syspulse.s2.grid.mem

import com.syspulse.s2.grid.GridMap

import scala.collection.concurrent.TrieMap

import com.typesafe.scalalogging.Logger

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class MemMap[K,V](name:String) extends GridMap[K,V](name) {
	val logger = Logger("MemMap-"+name)
    val map:scala.collection.concurrent.Map[K,V] = new TrieMap[K,V]
    def put(k: K, v: V): Option[V] = map.put(k,v)
    def get(k: K): Option[V] = map.get(k)
    def remove(k: K): Option[V] = map.remove(k)
    def size:Int = map.size
    def entrySet(): Iterator[(K, V)] = map.iterator

    def execute[R](fun: => R): Future[R] = {
        logger.info(s"${fun} -> ${this}")
        Future { fun }
    }
}