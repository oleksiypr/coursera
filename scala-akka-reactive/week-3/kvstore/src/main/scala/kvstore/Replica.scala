package kvstore

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorRef, Cancellable, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy, Terminated, actorRef2Scala}
import kvstore.Arbiter.*
import akka.pattern.{ask, pipe}
import akka.actor.{Actor, ActorKilledException, ActorRef, OneForOneStrategy, Props}

import scala.concurrent.duration.*
import akka.util.Timeout

object Replica:
  sealed trait Operation:
    def key: String
    def id: Long
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

  def props(arbiter: ActorRef, persistenceProps: Props): Props = Props(Replica(arbiter, persistenceProps))

class Replica(val arbiter: ActorRef, persistenceProps: Props) extends Actor:
  import Replica.*
  import Replicator.*
  import Persistence.*
  import context.dispatcher

  arbiter ! Join

  /*
   * The contents of this actor is just a suggestion, you can implement it in any way you like.
   */
  var kv = Map.empty[String, String]

  // a map from secondary replicas to replicators
  var secondaries = Map.empty[ActorRef, ActorRef]

  // the current set of replicators
  var replicators = Set.empty[ActorRef]

  /**
    * A persistence actor to be supervised by this replica.
    */
  val persistence = context.actorOf(persistenceProps, "persistence")

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
  var pending = Map.empty[Long, Set[ActorRef]]

  var expected = 0L;

  override val supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10) {
      case _: PersistenceException => Restart
      case _: ActorKilledException => Stop
      case _                       => Restart
    }

  val receive: Receive =
    case JoinedPrimary   => context.become(leader)
    case JoinedSecondary => context.become(replica)

  val leader: Receive = {
    case operation: Operation =>
      requesters += operation.id -> sender()
      handleOperation(operation)
    case Persisted(key, id) =>
      cancelRetry(id)
      if (noPendingFor(id)) acknowledgeOperation(id)
    case Replicated(_, id) =>
      updatePending(id, sender())
      if (!(cancellables contains id) && noPendingFor(id)) {
        acknowledgeOperation(id)
      }
    case Replicas(rs) =>
      val action = if (secondaries.size < rs.size - 1) JoinToCluster else
        if (secondaries.size > rs.size - 1) LeaveTheCluster else Unchanged
      handleReplicas(rs, action)
    case OperationAck(id) => requesters -= id
  }

  val replica: Receive = {
    case Get(k, id)               => sender() ! GetResult(k, kv.get(k), id)
    case Snapshot(k, valOpt, seq) =>
      secondaries += self -> sender()
      handleSnapshot(k, valOpt, seq)
    case Persisted(key, id) =>
      cancelRetry(id)
      secondaries(self) ! SnapshotAck(key, id)
  }

  val insert = (key: String, value: String) => kv += (key -> value)
  val remove = (key: String) =>  kv -= key

  def handleOperation(operation: Operation): Unit = {
    operation match {
      case Insert(k, v, id) => perform(Some(v)) { insert(k, v) }
      case Remove(k, id)    => perform(None)    { remove(k) }
      case Get(k, id)       => sender() ! GetResult(k, kv.get(k), id)
    }
    def perform(valOpt: Option[String])(action: => Unit): Unit = {
      action
      schedule(operation.id) { persistence ! Persist(operation.key, valOpt, operation.id) }
      pending += operation.id -> secondaries.values.toSet
      secondaries.values foreach replicate
      timeOut(1.second)

      def replicate(replicator: ActorRef): Unit = replicator ! Replicate(operation.key, valOpt, operation.id)
    }
    def timeOut(max: FiniteDuration): Cancellable = {
      context.system.scheduler.scheduleOnce(1.second) {
        if (requesters contains operation.id) requesters(operation.id) ! OperationFailed(operation.id)
      }
    }
  }

  def handleReplicas(rs: Set[ActorRef], action: ClusterAction): Unit =
    action match {
      case JoinToCluster =>
        val joined = (rs - self) -- secondaries.keySet
        replicas(join, joined)
      case LeaveTheCluster =>
        val left = secondaries.keySet -- (rs - self)
        replicas(leave, left)
      case Unchanged =>
    }

  def handleSnapshot(
      key: String,
      valueOption: Option[String], seq: Long
    ): Unit = {
    if (seq > expected) ()
    else if (seq < expected) sender() ! SnapshotAck(key, seq)
    else {
      valueOption match {
        case Some(v) => insert(key, v)
        case None    => remove(key)
      }
      schedule(seq) { persistence ! Persist(key, valueOption, seq) }
      expectNext()
    }
  }

  def acknowledgeOperation(id: Long): Unit =
    if (requesters.contains(id)) requesters(id) ! OperationAck(id)
    requesters -= id


  def join(replica: ActorRef): Unit = {
    val replicator = replicatorFor(replica)
    kv foreach forward
    secondaries += replica -> replicator

    def forward(kv: (String, String)): Unit = {
      val (k, v) = kv
      val id = expected
      requesters += id -> self
      replicator ! Replicate(k, Some(v), id)
      expectNext()
    }
  }

  def leave(replica: ActorRef): Unit =
    val replicator = secondaries(replica)
    acknowledgePending(replicator)
    secondaries -= replica
    context.stop(replicator)


  def acknowledgePending(replicator: ActorRef): Unit =
    for (id <- pending.keys) {
      acknowledgeOperation(id)
      updatePending(id, replicator)
    }

  def replicas(perform: ActorRef => Unit, update: Set[ActorRef]): Unit =
    update foreach perform

  def replicatorFor(secondary: ActorRef): ActorRef =
    context.actorOf(Replicator.props(secondary))

  def updatePending(id: Long, replicator: ActorRef): Unit =
    if (pending contains id) pending += id -> (pending(id) - replicator)
    if ((pending contains id) && pending(id).isEmpty) pending -= id

  def noPendingFor(id: Long): Boolean =
    !(pending contains id) || pending(id).isEmpty

  def schedule(id: Long)(retry: => Unit): Unit =
    cancellables += id ->
      context.system.scheduler.schedule(0.nanos, 100.milliseconds)(retry)

  def cancelRetry(id: Long): Unit =
    if (cancellables contains id) cancellables(id).cancel()
    cancellables -= id

  def expectNext(): Unit = expected += 1

  override def postStop(): Unit =
    cancellables.values foreach { _.cancel() }