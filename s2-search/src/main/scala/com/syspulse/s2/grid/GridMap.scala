package com.syspulse.s2.grid

import scala.concurrent.Future

abstract class GridMap[K,V](val name:String) {
    def put(k:K,v:V):Option[V]
    def get(k:K):Option[V]
    def remove(k:K):Option[V]
    def size:Int
    def entrySet():Iterator[(K,V)]

    def execute[R](fun: => R):Future[R]
 }
