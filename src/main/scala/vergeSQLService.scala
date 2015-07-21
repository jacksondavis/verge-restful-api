package vergedatabaseSQL

import akka.actor._
import akka.util.Timeout
import play.api.libs.json._
import scala.concurrent.duration._
import spray.routing._
import spray.util.LoggingContext

trait AkkaVergeService extends MyRoutes {
  def system: ActorSystem

  class VergeServiceActor extends Actor with VergeService with akka.actor.ActorLogging {
    log.info("Starting")

    implicit def actorRefFactory = context

    def receive = runRoute(myRoute)(ExceptionHandler.default, RejectionHandler.Default, context, RoutingSettings.default, LoggingContext.fromActorRefFactory)
  }
}

trait MyRoutes extends ArticleRepositoryComponent {

  trait VergeService extends HttpService {
    import XmlParser._
    implicit def dispatcher = actorRefFactory.dispatcher
    implicit val timeout: Timeout = Timeout(15.seconds)

    val myRoute = {
      get {
        path("ping") {
          complete {
            "pong"
          }
        }
      }~
      get {
        path("articles") {
          complete {
            val arts = articleRepository.queriedArticles
            val res = articlesToJson(arts)
            Json.prettyPrint(res)
          } 
        }
      }~
      get {
        path("title") {
          parameter('q.as[String]) {
            title => {
              complete {
                val arts = articleRepository.queriedArticles
                val newList = titleSearch(title, arts)
                val titleJson = articlesToJson(newList)
                Json.prettyPrint(titleJson)
              }
            }
          }
        }
      }~
      get {
        path("content") {
          parameter('q.as[String]) {
            content => {
              complete {
                val arts = articleRepository.queriedArticles
                val newList = contentSearch(content, arts)
                val contentJson = articlesContentToJson(newList)
                Json.prettyPrint(contentJson)
              }
            }
          }
        }
      }~
      get {
        path("article") {
          parameter('id.as[String]) {
            idVal => {
              complete {
                val arts = articleRepository.queriedArticles
                val newList = idSearch(idVal, arts)
                val idJson = articlesToJson(newList)
                Json.prettyPrint(idJson)
              }
            }
          }
        }
      }~
      get {
        path("dictionary") {
          complete {
            Json.prettyPrint(Json.obj("dictionary" -> articleRepository.wordDifference))
          }
        }
      }
    }
  }
}