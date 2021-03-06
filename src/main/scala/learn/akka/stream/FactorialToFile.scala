package learn.akka.stream

import java.nio.file.Paths

import akka.NotUsed
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString
import learn.akka.stream.helper.Runner

import scala.concurrent._

object FactorialToFile extends App with Runner[IOResult] {

  def toExec(m: Materializer): Future[IOResult] = {

    /**
      * Create the source: The first parameter (Int) is the data provided by the stream
      * Second parameter is the materializer
      * Creating the stream does not run it !
      */
    val source: Source[Int, NotUsed] = Source(1 to 100)

    val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

    val result: Future[IOResult] =
      factorials
        .map(num => ByteString(s"$num\n"))
        .runWith(FileIO.toPath(Paths.get("factorials.txt")))(m)

    result
  }

  justExec
}
