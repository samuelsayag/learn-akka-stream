package sam.test.akka.stream

import akka.stream._
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import sam.test.akka.stream.helper.Runner

import scala.concurrent._

object App1SimpleIntSource extends App with Runner[Done] {

  override def explanation: Seq[String] =
    Seq(
      """Illustrate the "most" simple stream app""",
      """By creating just a simple Int source that materialize to a Stream""",
      """and then is printed."""
    )

  def toExec(m: Materializer): Future[Done] = {

    val source: Source[Int, NotUsed] = Source(1 to 100)

    val done: Future[Done] = source.runForeach(i => print(s"$i "))(m)

    done
  }

  exec(toExec, Some((d: Done) => println()))

}
