package daniel

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.ConfigFactory

object Example1 extends App {


  sealed trait ShoppingCartMessage
  case class AddItem(item: String) extends ShoppingCartMessage
  case class RemoveItem(item: String) extends ShoppingCartMessage
  case object ValidateCart extends ShoppingCartMessage

  /**
   * Actor typed is widely praised for bringing compile-time checks to actor and the whole new actor API
   * Now If I want to create an actor for ShoppingCart, I would need to spawn a Behaviour for it,
   * because in akka type API, an actor is defined by its behavior
   *
   * Now, we are constrained to the type of the message we can send to an actor.
   *
   * Think of an actor now, as receiving a message from a given hierarchy , i.e shoppingcartmessage here.
   * this leads to object-oriented type structure of messages which is, so we don't have messages of any types sitting
   * around and getting passed, and also we won't be having mixed behaviours in our system
   *
   * I can use Any type, but that defeats the purpose.
   *
   */

  /**
   *   create a behaviour for root guardian of the actor system, top level actor in whole actor system
       and this will have a behaviour, constructed using many factory pattern methods e.g
   */
  val shoppingRootActor: ActorSystem[ShoppingCartMessage] = ActorSystem(
    Behaviors.receiveMessage[ShoppingCartMessage]{ message: ShoppingCartMessage =>
      message match {
        case AddItem(item) => println(s"I'm adding an item to the cart")
        case RemoveItem(item) => println(s"I'm removing an item to the cart")
        case ValidateCart =>println(" The Card is good")
      }
      Behaviors.same
    },
    "simpleshoppingactor",
    ConfigFactory.load().getConfig("postgresStore")
  )
  //shoppingRootActor ! "HelloActor" //compile time error: yeah yeah, cool

  shoppingRootActor ! ValidateCart //works
}

//mutable state
object Example2 extends App {
  sealed trait ShoppingCartMessage
  case class AddItem(item: String) extends ShoppingCartMessage
  case class RemoveItem(item: String) extends ShoppingCartMessage
  case object ValidateCart extends ShoppingCartMessage

  val shoppingRootActorMutable: ActorSystem[ShoppingCartMessage] = ActorSystem(
    Behaviors.setup[ShoppingCartMessage] { context =>
      var items: Set[String] = Set()//still able to use mutable state in our actors
      println(s"address : ${context.self.path}") /** akka://simpleshoppingactor/user */
      //spawn next actor using context
      Behaviors.receiveMessage[ShoppingCartMessage]{ message: ShoppingCartMessage =>
        message match {
          case AddItem(item) =>
            println(s"I'm adding an item to the cart")
            items += item
          case RemoveItem(item) =>
            println(s"I'm removing an item to the cart")
            items -= item
          case ValidateCart =>
             println(" The Card is good")
            context.log.info("logging from context....")
            context.log.info(context.self.toString)
            context.log.info(context.self.path.toString)
            context.log.info(context.self.path.name)
            println(s"address : ${context.self.path}")/** akka://simpleshoppingactor/user */
        }
        Behaviors.same
      }
    }
   ,
    "simpleshoppingactor"
  )
  //shoppingRootActor ! "HelloActor" //compile time error: yeah yeah, cool

  shoppingRootActorMutable ! ValidateCart //works
}

object Example3 extends App {
 //using multiple behaviors unlike mutable state
 sealed trait ShoppingCartMessage
  case class AddItem(item: String) extends ShoppingCartMessage
  case class RemoveItem(item: String) extends ShoppingCartMessage
  case object ValidateCart extends ShoppingCartMessage

  val shoppingRootActorMutable: ActorSystem[ShoppingCartMessage] = ActorSystem(
    Behaviors.setup[ShoppingCartMessage] { context =>
      shoppingBehavior(Set())
    }
    ,
    "simpleshoppingactor"
  )

  shoppingRootActorMutable ! ValidateCart

   def shoppingBehavior(items: Set[String]): Behavior[ShoppingCartMessage] = Behaviors.receiveMessage[ShoppingCartMessage] {
         case AddItem(item) =>
           println(s"I'm adding an item to the cart")
           shoppingBehavior(items + item)
         case RemoveItem(item) =>
           println(s"I'm removing an item to the cart")
           shoppingBehavior(items - item)
         case ValidateCart =>
           println(" The Card is good")
           Behaviors.same
   }
}

sealed trait ShoppingCartMessage
case class AddItem(item: String) extends ShoppingCartMessage
case class RemoveItem(item: String) extends ShoppingCartMessage
case object ValidateCart extends ShoppingCartMessage
object ShoppingCart {

  def apply(): Behavior[ShoppingCartMessage] = {
    shoppingBehavior(Set.empty)
  }

  def shoppingBehavior(items: Set[String]): Behavior[ShoppingCartMessage] = Behaviors.receive[ShoppingCartMessage] { (context, message) =>
    message match {
      case AddItem(item) =>
        println(s"I'm adding an item to the cart")
        shoppingBehavior(items + item)
      case RemoveItem(item) =>
        println(s"I'm removing an item to the cart")
        shoppingBehavior(items - item)
      case ValidateCart =>
        println(s"child: ${context.self.path}")
        println("The Card is good")
        Behaviors.same
    }
  }

}

object ShoppingCartMain extends App {


  val userActorBehavior: Behavior[ShoppingCartMessage] =
    Behaviors.setup[ShoppingCartMessage] { context =>
      println(s"path1: ${context.self.path}")
      val shoppingCartActor: ActorRef[ShoppingCartMessage] = context.spawn(ShoppingCart(), "shoppingcartactor")
      Behaviors.receiveMessage({ message =>
        println(s"path2: ${shoppingCartActor.path}")
        shoppingCartActor ! message
        Behaviors.same
      })
    }

  //or something like
  val rootOnlineStoreActor = ActorSystem(
    Behaviors.setup[ShoppingCartMessage] { ctx =>
      ctx.spawn(ShoppingCart(), "")
      Behaviors.empty
    },
    "test"
  )


  val system = ActorSystem(userActorBehavior, "actor-system")
  system ! ValidateCart

}

/**
 * Hierarchy:
 * one of the common anti-pattern of old actor API was spawning flat hierarchies using system.actorOf which
   destroyed the great benefit of akk fault tolerance.
 --> An anti-pattern is a common response to a recurring problem that is usually ineffective and risks being highly counterproductive.
 * Now, we are forced to think about actor hierarchies and how root guardian will manage them at the very beginning.
 * So we can only spawn child actors from a root actor.
 */
