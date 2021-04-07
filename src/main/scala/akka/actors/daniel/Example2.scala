package akka.actors.daniel

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior

trait SimpleThing
case object EatChocolate extends SimpleThing
case object WashDishes extends SimpleThing
case object LearnAkka extends SimpleThing


object CheckHappiness {

  def apply():Behavior[SimpleThing] = {
    emotionalFunctionalActor()
  }

  def emotionalFunctionalActor(happiness:Int = 0): Behavior[SimpleThing] = Behaviors.receive {
    (context, message) =>
    message match {
        case EatChocolate =>
          println(s"($happiness) eating chocolate")
          emotionalFunctionalActor(happiness + 1)
        case WashDishes =>
          println(s"($happiness) washing dishes, womp womp")
          emotionalFunctionalActor(happiness - 2)
        case LearnAkka =>
          println(s"($happiness) Learning Akka, yes!!")
          emotionalFunctionalActor(happiness + 100)
        case _ =>
            println("Received something i don't know")
            Behaviors.same
  }
}
}

object TestEmotionalActor extends App {

  val emotionalActorSystem = ActorSystem(CheckHappiness(), "happinessas")

    emotionalActorSystem ! EatChocolate
    emotionalActorSystem ! EatChocolate
    emotionalActorSystem ! WashDishes
    emotionalActorSystem ! LearnAkka

    Thread.sleep(1000)
    emotionalActorSystem.terminate()
}
