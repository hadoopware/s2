package com.syspulse.s2.grid.akka

import com.typesafe.scalalogging.Logger
import akka.actor.{ActorRef, Props}

class Cluster(name:String,selfAddress:String,config:String) {
	val logger = Logger("Cluster-"+name)

    var nodes:IndexedSeq[Node] = {
        logger.info(s"cluster=${name}: self=${selfAddress}: config=${config}")
        config.split(",").map( address=>
            //AkkaGrid.system.actorOf(Props[StoreNode],address)
            if(selfAddress == address)
                LocalNode(address)
            else
                RemoteNode(address,this)
        )
    }

    val self = resolve(selfAddress)

    def resolve(address:Any):Node = nodes(Math.abs(address.hashCode) % nodes.size)
}
