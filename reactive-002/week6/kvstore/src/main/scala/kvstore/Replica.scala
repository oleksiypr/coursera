package kvstore

import akka.actor.{ OneForOneStrategy, Props, ActorRef, Actor }
import kvstore.Arbiter._
import scala.collection.immutable.Queue
import akka.actor.SupervisorStrategy.Restart
import scala.annotation.tailrec
import akka.pattern.{ ask, pipe }
import akka.actor.Terminated
import scala.concurrent.duration._
import akka.actor.PoisonPill
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import akka.util.Timeout

object Replica {
  sealed trait Operation {
    def key: String
    def id: Long
  }
  case class Insert(key: String, value: String, id: Long) extends Operation
  case class Remove(key: String, id: Long) extends Operation
  case class Get(key: String, id: Long) extends Operation

  sealed trait OperationReply
  case class OperationAck(id: Long) extends OperationReply
  case class OperationFailed(id: Long) extends OperationReply
  case class GetResult(key: String, valueOption: Option[String], id: Long) extends OperationReply

  def props(arbiter: ActorRef, persistenceProps: Props): Props = Props(new Replica(arbiter, persistenceProps))
}

class Replica(val arbiter: ActorRef, persistenceProps: Props) extends Actor {
  import Replica._
  import Replicator._
  import Persistence._
  import context.dispatcher
  
  arbiter ! Join
  
  var kv = Map.empty[String, String]
  
  /**
   * A map from secondary replicas to replicators.
   */
  var secondaries = Map.empty[ActorRef, ActorRef]
  
  /**
   * The current set of replicators.
   */
  var replicators = Set.empty[ActorRef]
  
  var expectd = 0L;

  def receive = {
    case JoinedPrimary   => context.become(leader)
    case JoinedSecondary => context.become(replica)
  }

  //TODO: solve step 5
  val leader: Receive = { case operation: Operation => handleOperation(operation) }

  //TODO: solve step 4
  val replica: Receive = {
    case Get(k, id)               => sender ! GetResult(k, kv.get(k), id)
    case Snapshot(k, valOpt, seq) => handleSnapshot(k, valOpt, seq)
  }
  
  private def handleOperation(operation: Operation) {
    val perform =  operationAck(operation.id)
    operation match {
      case Insert(k, v, id) => perform { kv += (k -> v) } 
      case Remove(k, id)    => perform { kv -= k }
      case Get(k, id)       => sender ! GetResult(k, kv.get(k), id)
    }
  }  
  
  private def handleSnapshot(key: String, valueOption: Option[String], seq: Long) {
    val perform = snapshotAck(key, seq)
    if (seq > expectd) ()
    else if (seq < expectd) sender ! SnapshotAck(key, seq)
    else {
      valueOption match {
        case Some(v) => perform { kv += (key -> v) }
        case None    => perform { kv -= key }
      }
      expectd += 1
    }
  }

  private def operationAck(id: Long) = acknowledge(OperationAck(id))_
  private def snapshotAck(key: String, seq: Long) = acknowledge(SnapshotAck(key, seq))_
  private def acknowledge(msg: AnyRef)(perform: => Unit) {
    perform
    sender ! msg
  }
}

