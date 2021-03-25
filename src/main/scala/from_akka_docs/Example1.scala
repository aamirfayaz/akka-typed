package from_akka_docs

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.event.slf4j.Logger

object TypedActorTest1 extends App {

object HelloWorld {

  final case class Greet(whom: String, replyTo: ActorRef[Greeted])

  final case class Greeted(whom: String, from: ActorRef[Greet])

  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>
   println(s"Hello: ${message.whom}")
    Thread.sleep(1000)
     println(context.self.path) // ==> akka://ActorSystemName/user/greeter
     message.replyTo ! Greeted(message.whom, context.self)
    Behaviors.same
  }
}


  object HelloWorldBot {

    def apply(max: Int): Behavior[HelloWorld.Greeted] = {
      bot(0, max)
    }

    private def bot(greetingCounter: Int, max: Int): Behavior[HelloWorld.Greeted] = {
      Behaviors.receive {
        (context, message) =>
        Thread.sleep(1000)
         println(context.self.path) // ==> akka://ActorSystemName/user/World
         val n = greetingCounter + 1
       println(s"Greeting $n for ${message.whom}")
        if (n == max) Behaviors.stopped else {
          message.from ! HelloWorld.Greet(message.whom, context.self)
          bot(n, max)
        }
      }
    }

  }

  object HelloWorldMain {

    final case class SayHello(name: String)

    def apply(): Behavior[SayHello] = {
      //spawn a user guardian actor
      Behaviors.setup(context => {
        println(context.self.path) // ==> akka://ActorSystemName/user
        val greeter: ActorRef[HelloWorld.Greet] = context.spawn(HelloWorld(), "greeter")
        Behaviors.receiveMessage({ message =>
          val replyTo: ActorRef[HelloWorld.Greeted] = context.spawn(HelloWorldBot(max = 2), message.name)
          greeter ! HelloWorld.Greet(message.name, replyTo)
          Behaviors.same
        })
      })
    }
  }

  val system = ActorSystem(HelloWorldMain(), "ActorSystemName")
  system ! HelloWorldMain.SayHello("World")
  system ! HelloWorldMain.SayHello("Akka")

}