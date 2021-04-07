package akka.clustering

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}

/**
 * It is common for sharding to be used with persistence however any Behavior can be used with sharding e.g. a basic counter:
 */
object Counter {

  sealed trait Command

  case object Increment extends Command

  final case class GetValue(replyTo: ActorRef[Int]) extends Command

  def apply(entityId: String): Behavior[Command] = {
    def updated(value: Int): Behavior[Command] = {
      Behaviors.receiveMessage[Command] {
        case GetValue(replyTo) =>
          replyTo ! value
          Behaviors.same

        case Increment =>
          updated(value + 1)
      }
    }

    updated(0)
  }
}

object TestCounter extends App {
    Behaviors.setup[Nothing](context => new Main(context))
}

class Main(context: ActorContext[Nothing])
  extends AbstractBehavior[Nothing](context) {
  val system = context.system

  val sharding = ClusterSharding(system)

  val Typekey = EntityTypeKey[Counter.Command]("Counter")

  val shardRegion = sharding.init(Entity(Typekey)(createBehavior = entityContext => Counter(entityContext.entityId)))

  val counterOne = sharding.entityRefFor(Typekey, "counter-1")
  counterOne ! Counter.Increment

  override def onMessage(msg: Nothing): Behavior[Nothing] = this
}