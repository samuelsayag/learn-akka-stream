package learn.akka.stream.helper

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.Future
import scala.util.Try

trait Runner[T] {

  def explanation: Seq[String] = Seq()

  private def buildExplanation = {
    val frameSize = Try(explanation.map(_.length).max).
      toOption.getOrElse(0)
    val frame = Seq.fill(frameSize)("*").mkString("")

    frame :: explanation.toList ::: List(frame)
  }

  def printExplanation =
    println(buildExplanation.mkString("\n"))


  def toExec(m: Materializer): Future[T]

  def exec[O](f: (Materializer) => Future[T],
              toDo: Option[(T) => O]): Unit = {

    implicit val system: ActorSystem = ActorSystem("QuickStart")
    val mat: Materializer = ActorMaterializer()
    val ec = system.dispatcher

    printExplanation

    f(mat).onComplete(res => {
      toDo.flatMap(t => res.toOption.map(t))
      system.terminate()
    })(ec)
  }

  def justExec = exec(toExec, None)
}
