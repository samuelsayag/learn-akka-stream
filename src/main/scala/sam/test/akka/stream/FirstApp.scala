package sam.test.akka.stream

import akka.stream._
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import sam.test.akka.stream.helper.Runner

import scala.concurrent._

object FirstApp extends App with Runner[Done] {

  def toExec(m: Materializer): Future[Done] = {

    /**
      * Create the source: The first parameter (Int) is the data provided by the stream
      * Second parameter is the materializer
      * Creating the stream does not run it !
      */
    val source: Source[Int, NotUsed] = Source(1 to 100)

    val done: Future[Done] = source.runForeach(i => println(i))(m)

    done
  }

  justExec

}
