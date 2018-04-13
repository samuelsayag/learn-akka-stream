package learn.akka.stream

import akka.stream._
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import learn.akka.stream.helper.Runner

import scala.concurrent._

object IntSourceOnly extends App with Runner[Done] {

  override def explanation: Seq[String] =
    Seq(
      """Illustrate the "most" simple stream app""",
      """By creating just a simple Int source that materialize to a Stream""",
      """and then is printed."""
    )

  def toExec(m: Materializer): Future[Done] = {

    // the source is an indexedSeq of Int
    // the is NO materialized value (NotUsed)
    val source: Source[Int, NotUsed] = Source(1 to 100)
    // the stream is materialized with the runForeach (sugar of toMat())
    val done: Future[Done] = source.runForeach(i => print(s"$i "))(m)

    done
  }

  exec(toExec, Some((d: Done) => println()))

}
