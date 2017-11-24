package com.syspulse.s2.grid.mem

import com.syspulse.s2.grid.{Grid, GridMap}

trait MemGrid extends Grid {
    def create[K, V](name:String):GridMap[K,V] = {
        val map = new MemMap[K,V](name)
        MemGrid.maps = MemGrid.maps + (name->map)

        map
    }

    def getMap[K,V](name: String): GridMap[K, V] = {
        MemGrid.maps.get(name).getOrElse(create(name)).asInstanceOf[GridMap[K,V]]    // <- horrible cast!
    }

}

object MemGrid {
    var maps = Map[String,GridMap[_,_]]()
}
