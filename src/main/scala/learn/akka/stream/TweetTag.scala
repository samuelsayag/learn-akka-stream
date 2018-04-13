package learn.akka.stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}

object TweetTag {

  final case class Author(handle: String)

  final case class Hashtag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
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

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("reactive-tweets")
    implicit val materializer = ActorMaterializer()

    val sink = Sink.foreach(println)

    val authors: Flow[Tweet, Author, NotUsed] =
      Flow[Tweet].filter(_.hashtags.contains(akkaTag))
        .fold(Set.empty[Author])(_ + _.author).mapConcat(identity)

    val tags: Flow[Tweet, Hashtag, NotUsed] =
      Flow[Tweet].map(_.hashtags).reduce(_ ++ _).
        mapConcat(identity)

    val g = RunnableGraph.fromGraph(
      GraphDSL.create(tweets, authors, tags, sink, sink)((_, _, _, _, _)) {
        implicit b =>
          (twts, auth, tag, ps1, ps2) =>

            import akka.stream.scaladsl.GraphDSL.Implicits._
            val bcast = b.add(Broadcast[Tweet](2))

            twts ~> bcast.in
            bcast.out(0) ~> auth ~> ps1
            bcast.out(1) ~> tag ~> ps2

            ClosedShape
      })

    // This flow is simpler but it does not showcase how to pass graph (Source, Sink, Flow...)
    // to the create graph function
    
    //    val g: RunnableGraph[NotUsed] = RunnableGraph.fromGraph(
    //      GraphDSL.create() {
    //      implicit b =>
    //        import akka.stream.scaladsl.GraphDSL.Implicits._
    //        val bcast = b.add(Broadcast[Tweet](2))
    //
    //        tweets ~> bcast.in
    //        bcast.out(0) ~> authors ~> sink
    //        bcast.out(1) ~> tags ~> sink
    //
    //        ClosedShape
    //    })
    g.run()

    system.terminate()

  }


}
