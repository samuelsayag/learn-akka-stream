package sam.test.akka.stream

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, IOResult, Materializer, ThrottleMode}
import sam.test.akka.stream.helper.Runner

import scala.concurrent.Future

object FifthApp extends App with Runner[Done] {

  def toExec(m: Materializer): Future[Done] = {

    /**
      * Create the source: The first parameter (Int) is the data provided by the stream
      * Second parameter is the materializer
      * Creating the stream does not run it !
      */
    val source: Source[Int, NotUsed] = Source(1 to 100)

    val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

    import scala.concurrent.duration._

    val result: Future[Done] =
      factorials
        .zipWith(Source(0 to 100))((num, idx) ⇒ s"$idx! = $num")
        .throttle(3, 1 second, 1, ThrottleMode.shaping)
        .runForeach(println)(m)

    result
  }

  exec(toExec, None)
}
