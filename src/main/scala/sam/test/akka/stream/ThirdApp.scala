package sam.test.akka.stream

import akka.stream.Materializer
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import sam.test.akka.stream.helper.Runner

import scala.concurrent.Future

object ThirdApp extends App with Runner[Done] {

  def toExec(m: Materializer): Future[Done] = {
    final case class Author(handle: String)

    final case class Hashtag(name: String)

    final case class Tweet(author: Author,
                           timestamp: Long,
                           body: String) {

      def hashtags: Set[Hashtag] = body.split(" ").collect {
        case t if t.startsWith("#") ⇒ Hashtag(t.replaceAll("[^#\\w]", ""))
      }.toSet

    }

    val akkaTag = Hashtag("#akka")

    val tweets: Source[Tweet, NotUsed] = Source(
      Tweet(Author("rolandkuhn"), System.currentTimeMillis, "#akka rocks!") ::
        Tweet(Author("patriknw"), System.currentTimeMillis, "#akka !") ::
        Tweet(Author("bantonsson"), System.currentTimeMillis, "#akka !") ::
        Tweet(Author("drewhk"), System.currentTimeMillis, "#akka !") ::
        Tweet(Author("ktosopl"), System.currentTimeMillis, "#akka on the rocks!") ::
        Tweet(Author("mmartynas"), System.currentTimeMillis, "wow #akka !") ::
        Tweet(Author("akkateam"), System.currentTimeMillis, "#akka rocks!") ::
        Tweet(Author("bananaman"), System.currentTimeMillis, "#bananas rock!") ::
        Tweet(Author("appleman"), System.currentTimeMillis, "#apples rock!") ::
        Tweet(Author("drama"), System.currentTimeMillis, "we compared #apples to #oranges!") ::
        Nil)


    val result: Future[Done] = tweets
      .map(_.hashtags) // Get all sets of hashtags ...
      .reduce(_ ++ _) // ... and reduce them to a single set, removing duplicates across all tweets
      .mapConcat(identity) // Flatten the stream of tweets to a stream of hashtags
      .map(_.name.toUpperCase) // Convert all hashtags to upper case
      .runWith(Sink.foreach(println))(m) // Attach the Flow to a Sink that will finally print the hashtags

    result
  }

  exec(toExec, None)
}
