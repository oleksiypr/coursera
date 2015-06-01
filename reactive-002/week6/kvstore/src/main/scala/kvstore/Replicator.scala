package kvstore

import akka.actor.Props
import akka.actor.Actor
import akka.actor.ActorRef
import akka.persistence._
import scala.concurrent.duration._
import scala.language.postfixOps

object Replicator {
  case class Replicate(key: String, valueOption: Option[String], id: Long)
  case class Replicated(key: String, id: Long)
  
  case class Snapshot(key: String, valueOption: Option[String], seq: Long)
  case class SnapshotAck(key: String, seq: Long)
  case class Retry(key: String, valueOption: Option[String], seq: Long)

  def props(replica: ActorRef): Props = Props(new Replicator(replica))
}

class Replicator(val replica: ActorRef) extends Actor {
  import Replicator._
  import Replica._
  import context.dispatcher

  /**
   * Map from sequence number to pair of sender and request
   */
  var acks = Map.empty[Long, (ActorRef, Replicate)]
   
  /**
   * A sequence of not-yet-sent snapshots (you can disregard this if not implementing batching)
   */
  var pending = Vector.empty[Snapshot]
  
  var _seqCounter = 0L
  def nextSeq = {
    val ret = _seqCounter
    _seqCounter += 1
    ret
  }
  
  //TODO: Step 3
  def receive: Receive = {
    case repl @ Replicate(k, valOpt, _) => { 
      val requester = sender()
      val seq = nextSeq
      acks += seq -> (requester, repl)
      context.system.scheduler.scheduleOnce(100 milliseconds, self, Retry(k, valOpt, seq))
      //replica ! Snapshot(k, valOpt, seq)
    }
    case SnapshotAck(key, seq) => {
      val (requester, request) = acks(seq)
      acks -= seq
      requester ! Replicated(key, request.id)
    }
    case Retry(k, valOpt, seq) => {
      val msg = Snapshot(k, valOpt, seq)
      println(msg)
      replica ! msg
    }
  }
}
