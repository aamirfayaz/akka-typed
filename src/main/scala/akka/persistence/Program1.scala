package akka.persistence

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import akka.persistence.typed.{PersistenceId, RecoveryCompleted, RecoveryFailed}
import com.typesafe.config.ConfigFactory


object Program1 extends App {

 object PersistentDomain {
   sealed trait Command
   final case class Add(data: String) extends Command
   case object Clear extends Command

   sealed trait Event
   final case class Added(data: String) extends Event
   case object Cleared extends Event
   final case class State(history: List[String] = Nil)

   case class Initialize(id: String)
 }


object HandleErrorSignal {


  }

  object MyPersistentBehavior {
   import PersistentDomain._

    def apply(id: String): Behavior[Command] =
      EventSourcedBehavior[Command, Event, State](
        persistenceId = PersistenceId.ofUniqueId(id),
        emptyState = State(Nil),
        //the current State and the incoming Command.
        //commandHandler = (state, cmd) => throw new NotImplementedError("TODO: process the command & return an Effect"),
        commandHandler = commandHandle,
        //When an event has been persisted successfully by commandHandler, the new state is created by applying the event to the current state with the eventHandler.
        //eventHandler = (state, evt) => throw new NotImplementedError("TODO: process the event return the next state"))
        eventHandler = eventHandler
      ).receiveSignal {
        case (state, RecoveryCompleted) =>
          println("recovery done : " + state)
        case (state, RecoveryFailed(ex)) =>
          println("recovery failed")
          println(ex.getMessage)

      }

    val commandHandle:(State, Command) => Effect[Event, State] = { (state, command) =>
       command match  {
         case Add(data) => {
           println(s"persisting event: Added($data)")
           Effect.persist(Added(data))
         }
         case Clear => Effect.persist(Cleared)
       }
    }

    val eventHandler: (State, Event) => State = { (state, event) =>
      event match {
        case Added(data) => {
           println("Event persisted or recovered, now updating state")
          state.copy(data :: state.history)
        }//.take(5)
        case Cleared => State(Nil)
        case _ =>
          println("some random messge")
          state
      }
    }
  }

  object UserGuardian {
   import PersistentDomain._

    def apply(id: String): Behavior[Command] = {
      Behaviors.setup(context => {
        val persistentActor: ActorRef[Command] = context.spawn(MyPersistentBehavior(id), "mypersisbehavior")
        Behaviors.receiveMessage({ message =>
          persistentActor ! message
          Behaviors.same
        })
      })
    }
  }

  val config = ConfigFactory.load().getConfig("postgresStore")
  val as = ActorSystem(UserGuardian("firsttypedpersistenceexample"), "pas", config)

  import PersistentDomain._

 as ! Add("one")


}