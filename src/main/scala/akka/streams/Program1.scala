package akka.streams

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.typed.scaladsl.ActorSource

trait Protocol
case class Message(msg: String) extends Protocol
case object Complete extends Protocol
case class Fail(ex: Exception) extends Protocol

object Program1 extends App {

  implicit val system = ActorSystem(Behaviors.empty, "aa")
  val source: Source[Protocol, ActorRef[Protocol]] = ActorSource.actorRef[Protocol](completionMatcher = {
    case Complete =>
  }, failureMatcher = {
    case Fail(ex) => ex
  }, bufferSize = 8, overflowStrategy = OverflowStrategy.fail)

  val ref = source
    .collect {
      case Message(msg) => msg
    }
    .to(Sink.foreach(println))
    .run()

  ref ! Message("msg1")
  // ref ! "msg2" Does not compile
}