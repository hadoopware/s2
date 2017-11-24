package com.syspulse.s2.grid.akka

object LocalNode {
    def apply(address:String):Node = new LocalNode(address)
}

class LocalNode(val address:String) extends StoreNode {

    def put[K, V](k: K, v: V): Option[V] = store.map.put(k,v).asInstanceOf[Option[V]]
}
