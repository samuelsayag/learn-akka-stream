package learn.akka.stream

import akka.stream.Materializer
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}
import learn.akka.stream.helper.Runner

import scala.concurrent.Future

object MinimalSourceSink1 extends App with Runner[Int] {

  def toExec(m: Materializer): Future[Int] = {

    val source = Source(1 to 10)
    val sink = Sink.fold[Int, Int](0)(_ + _)

    // connect the Source to the Sink, obtaining a RunnableGraph
    val runnable: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)
    // materialize the flow and get the value of the FoldSink
    val sum: Future[Int] = runnable.run()(m)

    //    // more simply the graph may be directly materialize with the sink
    //    // Note: in this case, the last materialized value is always kept.
    //    val sum: Future[Int] = source.runWith(sink)(m)

    sum
  }


  exec(toExec,
    Some((i: Int) =>
      println(s"This is the fold result: $i")))

}
