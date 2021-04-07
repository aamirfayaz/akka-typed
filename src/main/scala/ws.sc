import akka.actor.typed.ActorRef
import akka.actor.typed.internal.AdaptMessage
import akka.dispatch.ExecutionContexts

import scala.concurrent.Future
import scala.util.Try

sealed trait Command
case class Response(message: String)
case class OpenThePodBayDoorsPlease(replyTo: ActorRef[Response]) extends Command

val x: ActorRef[Response] => OpenThePodBayDoorsPlease = OpenThePodBayDoorsPlease

val y: ActorRef[Response] => OpenThePodBayDoorsPlease = ref => OpenThePodBayDoorsPlease(ref)


case class Employee(age: Int)

val empF: Int => Employee = Employee
val empF2: Int => Employee = ref => Employee(ref)
