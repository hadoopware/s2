package com.syspulse.s2.grid.akka

import com.typesafe.scalalogging.Logger

trait Node {
    val address:String

    def put[K,V](k: K, v: V): Option[V]

//    def get(k: K): Option[V] = ???
//    def remove(k: K): Option[V] = ???
//    def size: Int = ???
}
