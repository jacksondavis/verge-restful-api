package vergedatabaseSQL

import akka.actor._
import akka.dispatch._
import akka.event.Logging
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import java.io._
import java.util.concurrent.Executors
import scala.collection.mutable.Set
import scala.concurrent.duration._
import scala.io.Source
import spray.can.Http
import util.Properties

object Boot extends App with PostgresArticleRepositoryComponent with AkkaVergeService with DataPuller {
  val unixWords = (for{line <- Source.fromURL(getClass.getResource("/words")).getLines()} yield line).toSet

  implicit val timeout = Timeout(5.seconds)

  implicit val system = ActorSystem("verge-data-service")
  val puller = system.actorOf(Props(new VergeActor), "puller")
  val service = system.actorOf(Props(new VergeServiceActor), "verge-service")

  val myPort = Properties.envOrElse("PORT", "8080").toInt

  IO(Http) ? Http.Bind(service, interface = "localhost", port = myPort)
}
