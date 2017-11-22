package sam.test.akka.stream

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString
import akka.{Done, NotUsed}
import sam.test.akka.stream.helper.Runner

import scala.concurrent.Future

object FourthApp extends App with Runner[IOResult] {

  def toExec(m: Materializer): Future[IOResult] = {
    /**
      * Create the source: The first parameter (Int) is the data provided by the stream
      * Second parameter is the materializer
      * Creating the stream does not run it !
      */
    val source: Source[Int, NotUsed] = Source(1 to 100)

    val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

    val result: Future[IOResult] =
      factorials.
        map(_.toString).
        runWith(lineSink("factorial2.txt"))(m)
    result
  }

  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
      .map(s ⇒ ByteString(s + "\n"))
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)

  exec(toExec, None)

}
