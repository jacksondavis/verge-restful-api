package vergedatabaseSQL

import org.jsoup._
import play.api.libs.json.{JsNull,Json,JsString,JsValue}
import scala.xml._

case class ArticlePrint(id: String, title: String, author: String, pub: String, updated: String, abs: String)

object XmlParser  {
  def dataMaker(entry: Node):Article = {
    val htmlId = (entry \ "id").text
    val idDoc = Jsoup.parse(htmlId)
    val id = idDoc.body().text
    val title = (entry \ "title").text
    val htmlAuthor = (entry \ "author").text
    val authDoc = Jsoup.parse(htmlAuthor)
    val author = authDoc.body().text
    val pub = (entry \ "published").text
    val updated = (entry \ "updated").text
    val htmlContent = (entry \ "content").text
    val conDoc = Jsoup.parse(htmlContent)
    val con = conDoc.body().text
    val abs = {
      val parts = conDoc.body().text.split(" ")
      if(parts.length >= 7) {
        parts(0) + " " + parts(1) + " " + parts(2) + " " + parts(3) + " " + parts(4) + " " + parts(5) + " " + parts(6) + " " + parts(7) + "..."
      }
      else "..."
    }
    val article = Article(id, title, author, pub, updated, abs, con)
    article
  }

  def articlesToJson(articleList: Seq[Article]) = {
    val printArticles = articleList.map(i => ArticlePrint(i.id, i.title, i.author, i.pub, i.updated, i.abs))
    implicit val articleFormat = Json.format[ArticlePrint]
    Json.obj("articles" -> printArticles) 
  }

  def articlesContentToJson(articleList: Seq[Article]) = {
    implicit val articleFormat = Json.format[Article]
    Json.obj("articles" -> articleList) 
  }

  def titleSearch(tmp: String, arts: Seq[Article]): Seq[Article] = arts.filter(i => i.title.toLowerCase.contains(tmp.toLowerCase))

  def contentSearch(tmp: String, arts: Seq[Article]): Seq[Article] = arts.filter(i => i.content.toLowerCase.contains(tmp.toLowerCase))

  def idSearch(tmp: String, arts: Seq[Article]): Seq[Article] = arts.filter(i => i.id.toLowerCase.contains(tmp.toLowerCase)) 
}