package daniel

import akka.actor.typed.{ActorSystem}
import akka.actor.typed.scaladsl.Behaviors

object Example1 extends App {


  trait ShoppingCartMessage
  case class AddItem(item: String) extends ShoppingCartMessage
  case class RemoveItem(item: String) extends ShoppingCartMessage
  case object ValidateCart extends ShoppingCartMessage

  /**
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
    "simpleshoppingactor"
  )
  //shoppingRootActor ! "HelloActor" //compile time error: yeah yeah, cool
  shoppingRootActor ! ValidateCart //works
}