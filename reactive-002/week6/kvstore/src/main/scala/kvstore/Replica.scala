package kvstore

import akka.actor.{ OneForOneStrategy, Props, ActorRef, Actor, ActorKilledException }
import kvstore.Arbiter._
import scala.collection.immutable.Queue
import scala.language.postfixOps
import akka.actor.SupervisorStrategy._
import scala.annotation.tailrec
import akka.pattern.{ ask, pipe }
import akka.actor.Terminated
import scala.concurrent.duration._
import akka.actor.Cancellable
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
  
  sealed trait ClusterAction
  case object JoinToCluster extends ClusterAction
  case object LeaveTheCluster extends ClusterAction
  case object Unchanged extends ClusterAction

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
   * Map from sequence number to cancellable tokens for Persist retry scheduled
   */
  var cancellables = Map.empty[Long, Cancellable]

  /**
   * Map from operation id to operation requesters
   */
  var requesters = Map.empty[Long, ActorRef]
  
  /**
   * Map from operation id to replicators pending for acknowledgement
   */
  var pendings = Map.empty[Long, Set[ActorRef]]
  
  /**
   * A persistence actor to be supervised by this replica.
   */
  val persistence = context.actorOf(persistenceProps, "persistence")
  
  var expectd = 0L;

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10) {
    case _: PersistenceException => Restart
    case _: ActorKilledException => Stop
    case _                       => Restart
  }
  
  def receive = {
    case JoinedPrimary   => context become leader
    case JoinedSecondary => context become replica 
  }

  val leader: Receive = {
    case operation: Operation => {
      requesters += operation.id -> sender
      handleOperation(operation)
    }
    case Persisted(key, id) => {
      cancelRetry(id)
      if (noPendindsFor(id)) acknowledgeOperation(id)
    }
    case Replicated(_, id) => {
      updatePending(id, sender)
      if (!cancellables.contains(id) && noPendindsFor(id)) {
        acknowledgeOperation(id)
      }
    }
    case Replicas(rs) => {
      val action = if (secondaries.size < rs.size - 1) JoinToCluster else 
                   if (secondaries.size > rs.size - 1) LeaveTheCluster else Unchanged
      handleReplicas(rs, action)
    }
    case OperationAck(id) => requesters -= id
  }

  val replica: Receive = {
    case Get(k, id)               => sender ! GetResult(k, kv.get(k), id)
    case Snapshot(k, valOpt, seq) => {
      secondaries += self -> sender
      handleSnapshot(k, valOpt, seq)
    }
    case Persisted(key, id) => {
      cancelRetry(id)
      secondaries(self) ! SnapshotAck(key, id)
    }
  }
  
  val insert = (key: String, value: String) => kv += (key -> value)
  val remove = (key: String) =>  kv -= key
  
  def handleOperation(operation: Operation) {
    operation match {
      case Insert(k, v, id) => perform(Some(v)) { insert(k, v) }
      case Remove(k, id)    => perform(None)    { remove(k) }
      case Get(k, id)       => sender ! GetResult(k, kv.get(k), id)
    }
    def perform(valOpt: Option[String])(action: => Unit) {
      action
      schedule(operation.id) { persistence ! Persist(operation.key, valOpt, operation.id) }
      pendings += operation.id -> secondaries.values.toSet
      secondaries.values foreach relicate 
      timeOut(1 second)
      
      def relicate(replicator: ActorRef) = replicator ! Replicate(operation.key, valOpt, operation.id)
    }
    def timeOut(max: FiniteDuration) = {
      context.system.scheduler.scheduleOnce(1 second) {
        if (requesters.contains(operation.id)) requesters(operation.id) ! OperationFailed(operation.id)
      }
    }
  }

  def handleReplicas(rs: Set[ActorRef], action: ClusterAction) {
    action match {
      case JoinToCluster => {
        val joined = (rs - self) -- secondaries.keySet
        replicas(join, joined)
      }
      case LeaveTheCluster => {
        val left = secondaries.keySet -- (rs - self)
        replicas(leave, left)
      }
      case Unchanged =>
    }
  }
  
  def handleSnapshot(key: String, valueOption: Option[String], seq: Long) {
    if (seq > expectd) ()
    else if (seq < expectd) sender ! SnapshotAck(key, seq)
    else {
      valueOption match {
        case Some(v) => insert(key, v)  
        case None    => remove(key) 
      }
      schedule(seq) { persistence ! Persist(key, valueOption, seq) }      
      expectNext()
    }
  }

  def acknowledgeOperation(id: Long) {
    if (requesters.contains(id)) requesters(id) ! OperationAck(id)
    requesters -= id
  }

  def join(replica: ActorRef) {
    val replicator = replicatorFor(replica)
    kv foreach forward
    secondaries += replica -> replicator

    def forward(kv: (String, String)) = {
      val (k, v) = kv
      val id = expectd
      requesters += id -> self
      replicator ! Replicate(k, Some(v), id)
      expectNext()
    }
  }
  def leave(replica: ActorRef) {
    val replicator = secondaries(replica)
    acknowledgePendings(replicator)
    secondaries -= replica
    context.stop(replicator)
  }
  def acknowledgePendings(replicator: ActorRef) = 
    for (id <- pendings.keys) {
      acknowledgeOperation(id)
      updatePending(id, replicator)
    }
  def replicas(perform: ActorRef => Unit, update: Set[ActorRef]) = update foreach perform
  def replicatorFor(secondary: ActorRef) = context.actorOf(Replicator.props(secondary))  
  def updatePending(id: Long, replicator: ActorRef) {
    if (pendings.contains(id)) pendings += id -> (pendings(id) - replicator)
    if (pendings.contains(id) && pendings(id).isEmpty) pendings -= id
  }
  def noPendindsFor(id: Long) = !pendings.contains(id) || pendings(id).isEmpty
  def schedule(id: Long)(retry: => Unit) {
    cancellables += id -> context.system.scheduler.schedule(0 nanos, 100 milliseconds)(retry)  
  }
  def cancelRetry(id: Long) {
    if (cancellables.contains(id)) cancellables(id).cancel()
    cancellables -= id
  } 
  def expectNext() = expectd += 1 
  
  override def postStop() = cancellables.values foreach { _.cancel() }
}