package vergedatabaseSQL

import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.Session
import org.squeryl.SessionFactory

trait PostgresArticleRepository extends Schema with ArticleRepository {
  println("Repository Accessed")
  val articles = table[Article]("articles")
  var wordDifference = List[String]()

  def addArticles(arts: List[Article]) {
    val newArticles = (arts.toSet -- queriedArticles.toSet).toList
    println(newArticles)
    println(queriedArticles)
    transaction{
      for(i <- newArticles) {
        articles.insert(Article(i.id, i.title, i.author, i.pub, i.updated, i.abs, i.content))
      }
    }
    calculateWordDifference
    println("New Articles Added")
  }

  def queriedArticles: List[Article] = {
    transaction {
      from(articles)(e => org.squeryl.PrimitiveTypeMode.select(e)).toList
    }
  }

  def calculateWordDifference {
    val contentList = queriedArticles.map(i => i.content)
    val currentWords: List[String] = contentList.flatMap(i => i.split(" ")).toList
    wordDifference = (currentWords.toSet -- Boot.unixWords).toList
    println("Word Diff Calculated")
  }
}

trait PostgresArticleRepositoryComponent extends ArticleRepositoryComponent {
  val articleRepository: ArticleRepository = new PostgresArticleRepository{}

  val username = "octzvyvbzslreg"
  val password = "D8gpmkSMnehO_QVxXVtpxfML6K"
  val database = "jdbc:postgresql://ec2-54-225-134-223.compute-1.amazonaws.com/d524m6mren75sr?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
  Class.forName("org.postgresql.Driver")
  SessionFactory.concreteFactory = Some(()=> {
    Session.create(java.sql.DriverManager.getConnection(database, username, password), new PostgreSqlAdapter())
    }
  )
}