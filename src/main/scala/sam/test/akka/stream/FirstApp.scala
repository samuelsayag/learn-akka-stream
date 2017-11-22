package sam.test.akka.stream

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.{Done, NotUsed}

import scala.concurrent._

object FirstApp  extends App {

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  /**
    * Create the source: The first parameter (Int) is the data provided by the stream
    * Second parameter is the materializer
    * Creating the stream does not run it !
    */
  val source: Source[Int, NotUsed] = Source(1 to 100)

  /**
    * Run the stream
    */
  val done: Future[Done] = source.runForeach(i => println(i))(materializer)


  implicit val ec = system.dispatcher
  done.onComplete(_ => system.terminate())
}
