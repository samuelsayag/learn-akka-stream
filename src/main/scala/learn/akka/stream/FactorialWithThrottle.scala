package learn.akka.stream

import akka.stream.scaladsl.Source
import akka.stream.{Materializer, ThrottleMode}
import akka.{Done, NotUsed}
import learn.akka.stream.helper.Runner

import scala.concurrent.Future

object FactorialWithThrottle extends App with Runner[Done] {

  def toExec(m: Materializer): Future[Done] = {

    /**
      * Create the source: The first parameter (Int) is the data provided by the stream
      * Second parameter is the materializer
      * Creating the stream does not run it !
      */
    val source: Source[Int, NotUsed] = Source(1 to 100)

    val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

    import scala.concurrent.duration._
//
    val result: Future[Done] =
      factorials
        .zipWith(Source(0 to 100))((num, idx) ⇒ s"$idx! = $num")
        .throttle(3, 1 second, 1, ThrottleMode.shaping)
        .runForeach(println)(m)

    result
  }

  justExec
}
