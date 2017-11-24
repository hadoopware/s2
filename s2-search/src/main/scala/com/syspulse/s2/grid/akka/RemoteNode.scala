package com.syspulse.s2.grid.akka

import akka.actor.{ActorRef, Props, Actor}
import akka.pattern.{ ask, pipe }
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import akka.util.Timeout
import java.util.concurrent.TimeUnit

import com.typesafe.scalalogging.Logger
import scala.reflect.ClassTag

case class DataPut(k:Any,v:Any)
case class DataGet(k:Any)
case class DataRemove(k:Any)

object RemoteNode {
    def apply(address:String,cluster:Cluster):Node = new RemoteNode(address,cluster)
}

object RemoteNodeActor {
    def props(address: String): Props = Props(classOf[RemoteNodeActor], address)
}


object PeerNodeActor {
    def props(address: String): Props = Props(classOf[PeerNodeActor], address)
}


class RemoteNodeActor(node: RemoteNode) extends Actor {
	val logger = Logger("RemoteNodeActor-"+node.toString)

    logger.info(s"remoteActor=${this}")

    def receive = {
        case DataPut(k,v) => {
            val v0 = node.store.map.put(k,v)
            sender ! v0
        }
        case DataGet(k) => {
            val v = node.store.map.get(k)
            sender ! v
        }
        case DataRemove(k) => {
            val v = node.store.map.remove(k)
            sender ! v
        }
    }
}


class PeerNodeActor(node: RemoteNode,remoteActor:ActorRef) extends Actor {
	val logger = Logger("RemoteNodeActor-"+node.toString)

    logger.info(s"peerActor=${this} -> remoteActor=${remoteActor}")

    def receive = {
        case _ =>
    }

    def put[K, V](k: K, v: V): Option[V] = {
        implicit val t = Timeout(5000L,TimeUnit.MILLISECONDS)
        val f:Future[V] = (remoteActor ? new DataPut(k,v)).mapTo(ClassTag[V](v.getClass))
        val r = Await.result[V](f,Duration(5,SECONDS))
        new Some[V](r)
    }
}

class RemoteNode(val address:String,val cluster:Cluster) extends StoreNode {
	val logger = Logger("RemoteNode-"+address)

    val peerActor = AkkaGrid.system.actorOf(RemoteNodeActor.props(address))
    val remoteActor = AkkaGrid.system.actorOf(RemoteNodeActor.props(address))

    logger.info(s"node='${address}': peerActor='${peerActor}, remoteActor=${remoteActor}")

    def put[K, V](k: K, v: V): Option[V] = {
        None
    }
}
