package kvstore

import akka.actor.Props
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import scala.concurrent.duration.*
import akka.actor.Cancellable

object Replicator:
  case class Replicate(key: String, valueOption: Option[String], id: Long)
  case class Replicated(key: String, id: Long)
  
  case class Snapshot(key: String, valueOption: Option[String], seq: Long)
  case class SnapshotAck(key: String, seq: Long)

  def props(replica: ActorRef): Props = Props(Replicator(replica))

class Replicator(val replica: ActorRef) extends Actor:
  import Replicator.*
  import context.dispatcher
  
  /*
   * The contents of this actor is just a suggestion, you can implement it in any way you like.
   */

  // map from sequence number to pair of sender and request
  var acks = Map.empty[Long, (ActorRef, Replicate)]
  // a sequence of not-yet-sent snapshots (you can disregard this if not implementing batching)
  var pending = Vector.empty[Snapshot]


  /**
    * Map from sequence number to cancellable tokens for Snapshot retry scheduled
    */
  var cancellables =  Map.empty[Long, Cancellable]
  
  var _seqCounter = 0L
  def nextSeq(): Long =
    val ret = _seqCounter
    _seqCounter += 1
    ret


  val receive: Receive = {
    case repl @ Replicate(k, valOpt, _) =>
      val requester = sender()
      val seq = nextSeq()
      acks += seq -> (requester, repl)

      def retry(): Unit  = replica ! Snapshot(k, valOpt, seq)

      cancellables += seq ->
        context.system.scheduler.schedule(0.nanos, 100.milliseconds)(retry())

    case SnapshotAck(key, seq) if acks contains seq =>
      val (requester, request) = acks(seq)
      if (cancellables contains seq) cancellables(seq).cancel()
      acks -= seq
      cancellables -= seq
      requester ! Replicated(key, request.id)
  }

  override def postStop(): Unit = cancellables.values foreach { _.cancel() }