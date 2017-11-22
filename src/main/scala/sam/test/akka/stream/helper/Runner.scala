package sam.test.akka.stream.helper

import akka.Done
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.Future

trait Runner[T] {

  def toExec(m: Materializer): Future[T]

  def exec[O](f: (Materializer) => Future[T],
              toDo: Option[(T) => O]): Unit = {

    implicit val system = ActorSystem("QuickStart")
    val mat: Materializer = ActorMaterializer()
    val ec = system.dispatcher

    f(mat).onComplete(res => {
      toDo.flatMap(t => res.toOption.map(t))
      system.terminate()
    })(ec)
  }

  def justExec = exec(toExec, None)
}
