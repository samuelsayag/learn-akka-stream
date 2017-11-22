package sam.test.akka.stream

import akka.stream.Materializer
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}
import sam.test.akka.stream.helper.Runner

import scala.concurrent.Future

object MinimalSourceSink2 extends App with Runner[Int] {

  def toExec(m: Materializer): Future[Int] = {

    val source = Source(1 to 10)
    val sink = Sink.fold[Int, Int](0)(_ + _)

    // connect the Source to the Sink, obtaining a RunnableGraph
    val sum: Future[Int] = source.runWith(sink)(m)

    sum
  }

  exec(toExec,
    Some((i: Int) =>
      println(s"This is the fold result: $i")))

}
