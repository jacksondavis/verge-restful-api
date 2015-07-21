package vergedatabaseSQL

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.routing.HttpService
import spray.http.StatusCodes._
import akka.actor._
import org.specs2.specification.Scope

trait DummyArticleRepositoryComponent extends ArticleRepositoryComponent {
  val articleRepository = new DummyArticleRepository{}
}

trait DummyArticleRepository extends ArticleRepository {
  val dummyArticleListToSearch = List[Article](Article("1", "one", "author", "publisher", "updated", "article one abs", "article one content"))
  def wordDifference: List[String] = List("one")
  def addArticles(articles: List[Article]): Unit = println
  def queriedArticles(): List[Article] = dummyArticleListToSearch
  def calculateWordDifference: Unit = println
}

object bankingAppTest extends Specification 
with Specs2RouteTest 
with MyRoutes 
with DummyArticleRepositoryComponent {
  import XmlParser._
  
  val router = new VergeService{ def actorRefFactory = system }
  val myRoute = router.myRoute
  val dummySearchList = List[Article](Article("1", "one", "author", "publisher", "updated", "article one abs", "article one content"), Article("2", "two", "author", "publisher", "updated", "article two abs", "article two content"))


  "The service" should {
     "return a 'pong' response for GET requests to /ping" in {
      Get("/ping") ~> myRoute ~> check {
        responseAs[String] === "pong"
      }
    }

    "handle a response for GET requests to /articles" in {
      Get("/articles") ~> myRoute ~> check {
        handled must beTrue
        response.status should be equalTo OK
      }
    }

    "handle a response for GET requests to /title" in {
      Get("/title?q=one") ~> myRoute ~> check {
        handled must beTrue
        response.status should be equalTo OK
      }
    }

    "handle a response for GET requests to /content" in {
      Get("/content?q=one") ~> myRoute ~> check {
        handled must beTrue
        response.status should be equalTo OK
      }
    } 

    "handle a response for GET requests to /article" in {
      Get("/article?id=1") ~> myRoute ~> check {
        handled must beTrue
        response.status should be equalTo OK
      }
    }     
  }

  "When we send a title string and a sequence of articles, titleSearch" should {
    "return a sequence of articles containing the title" in {
      val ans = titleSearch("one", dummySearchList)
      ans must_== Seq[Article](Article("1", "one", "author", "publisher", "updated", "article one abs", "article one content"))
    }
  }

  "When we send a content string and a sequence of articles, contentSearch" should {
    "return a sequence of articles containing the content" in {
      val ans = contentSearch("two", dummySearchList)
      ans must_== Seq[Article](Article("2", "two", "author", "publisher", "updated", "article two abs", "article two content"))
    }
  }

  "When we send an ID string and a sequence of articles, idSearch" should {
    "return a sequence of articles containing the content" in {
      val ans = idSearch("2", dummySearchList)
      ans must_== Seq[Article](Article("2", "two", "author", "publisher", "updated", "article two abs", "article two content"))
    }
  }

  "When we send an invalid ID string and a sequence of articles, idSearch" should {
    "return an empty sequence" in {
      val ans = idSearch("4", dummySearchList)
      ans must_== Seq[Article]()
    }
  }

  "When we send a sequence of articles, articlesToJson" should {
    "return a JSON Object" in {
      val ans = articlesToJson(dummySearchList).toString
      ans must_== """{"articles":[{"id":"1","title":"one","author":"author","pub":"publisher","updated":"updated","abs":"article one abs"},{"id":"2","title":"two","author":"author","pub":"publisher","updated":"updated","abs":"article two abs"}]}"""
    }
  }
}