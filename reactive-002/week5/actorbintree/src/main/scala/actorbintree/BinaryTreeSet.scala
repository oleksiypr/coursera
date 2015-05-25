/**
 * Copyright (C) 2009-2013 Typesafe Inc. <http://www.typesafe.com>
 */
package actorbintree

import akka.actor._
import scala.collection.immutable.Queue

object BinaryTreeSet {

  trait Operation {
    def requester: ActorRef
    def id: Int
    def elem: Int
  }

  trait OperationReply {
    def id: Int
  }

  /**
   * Request with identifier `id` to insert an element `elem` into the tree.
   * The actor at reference `requester` should be notified when this operation
   * is completed.
   */
  case class Insert(requester: ActorRef, id: Int, elem: Int) extends Operation

  /**
   * Request with identifier `id` to check whether an element `elem` is present
   * in the tree. The actor at reference `requester` should be notified when
   * this operation is completed.
   */
  case class Contains(requester: ActorRef, id: Int, elem: Int) extends Operation

  /**
   * Request with identifier `id` to remove the element `elem` from the tree.
   * The actor at reference `requester` should be notified when this operation
   * is completed.
   */
  case class Remove(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request to perform garbage collection*/
  case object GC

  /**
   * Holds the answer to the Contains request with identifier `id`.
   * `result` is true if and only if the element is present in the tree.
   */
  case class ContainsResult(id: Int, result: Boolean) extends OperationReply

  /** Message to signal successful completion of an insert or remove operation. */
  case class OperationFinished(id: Int) extends OperationReply

}

class BinaryTreeSet extends Actor {
  import BinaryTreeSet._
  import BinaryTreeNode._

  def createRoot: ActorRef = context.actorOf(BinaryTreeNode.props(0, initiallyRemoved = true))

  var root = createRoot
  var pendingQueue = Queue.empty[Operation]

  def receive = normal

  /** Accepts `Operation` and `GC` messages. */
  val normal: Receive = {
    case operation: Operation => root ! operation
    case GC                   => {
      val newRoot = createRoot
      root ! CopyTo(newRoot) 
      context.become(garbageCollecting(newRoot))
    }
  }

  /**
   * Handles messages while garbage collection is performed.
   * `newRoot` is the root of the new binary tree where we want to copy
   * all non-removed elements into.
   */
  def garbageCollecting(newRoot: ActorRef): Receive = {
    case CopyFinished  => {
      root ! PoisonPill
      root = newRoot
      while (!pendingQueue.isEmpty) {
        val (operation, pq) = pendingQueue.dequeue
        pendingQueue = pq
        root ! operation
      }
      context become normal
    }
      
    case operation: Operation => pendingQueue = pendingQueue.enqueue(operation)
  }

}

object BinaryTreeNode {
  trait Position
  case object Left extends Position
  case object Right extends Position

  case class CopyTo(treeNode: ActorRef)
  case object CopyFinished

  def props(elem: Int, initiallyRemoved: Boolean) = Props(classOf[BinaryTreeNode], elem, initiallyRemoved)
}

class BinaryTreeNode(val elem: Int, initiallyRemoved: Boolean) extends Actor {
  import BinaryTreeNode._
  import BinaryTreeSet._

  var subtrees = Map[Position, ActorRef]()
  var removed = initiallyRemoved

  def receive = normal

  /** Handles `Operation` messages and `CopyTo` requests. */
  val normal: Receive = {
    case cont @ Contains(requester, id, elem) =>
      search(cont)(
        finalAction = _  => requester ! ContainsResult(id, false),
        foundAction = () => requester ! ContainsResult(id, !removed))

    case ins @ Insert(requester: ActorRef, id: Int, elem: Int) =>
      search(ins)(
        finalAction = (p: Position) => {
          subtrees += p -> context.actorOf(BinaryTreeNode.props(elem, initiallyRemoved = false))
          requester ! OperationFinished(id)
        },
        foundAction = () => {
          removed = false
          requester ! OperationFinished(id)
        })
        
    case rem @ Remove(requester, id, elem) =>
      search(rem)(
          finalAction = _  => requester ! OperationFinished(id),
          foundAction = () => {
            removed = true
            requester ! OperationFinished(id)
          })
      
    case CopyTo(node) => {
      if (!removed) node ! Insert(self, elem, elem)
      
      val children = subtrees.values.toSet
      if (removed && children.isEmpty) copyFinished()
      else {
        children foreach { _ ! CopyTo(node) }
        context become copying(children, insertConfirmed = removed)
      }
    }    
  }

  /**
   * `expected` is the set of ActorRefs whose replies we are waiting for,
   * `insertConfirmed` tracks whether the copy of this node to the new tree has been confirmed.
   */
  def copying(expected: Set[ActorRef], insertConfirmed: Boolean): Receive = {
    case CopyFinished =>
      val rest = expected - sender
      if (rest.isEmpty && insertConfirmed) context.parent ! CopyFinished
      else context become copying(rest, insertConfirmed)
      
    case OperationFinished(_) =>
      if (expected.isEmpty) context.parent ! CopyFinished
      else context become copying(expected, true)
  }

  private def search(operation: Operation)(finalAction: Position => Unit, foundAction: () => Unit) {
    val elem = operation.elem
    val requester = operation.requester

    if (elem > this.elem) check(Right)
    if (elem < this.elem) check(Left)
    if (elem == this.elem) foundAction()

    def check(p: Position) {
      subtrees.get(p) match {
        case Some(subTree) => subTree ! operation
        case None          => finalAction(p)
      }
    }
  }  
  
  private def copyFinished() {
    context.parent ! CopyFinished
    context.stop(self)
  }
}
