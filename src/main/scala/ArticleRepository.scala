package vergedatabaseSQL

import org.squeryl.annotations.Column

case class Article(@Column("id") id: String, @Column("title") title: String, @Column("author") author: String, @Column("pub") pub: String, @Column("updated") updated: String, @Column("abs") abs: String, @Column("content") content: String) {
  override def toString() = id + title + author + pub + updated + abs
}

trait ArticleRepository {
  def wordDifference: List[String]
  def addArticles(articles: List[Article]): Unit
  def queriedArticles(): List[Article]
  def calculateWordDifference: Unit
}

trait ArticleRepositoryComponent {
  def articleRepository: ArticleRepository
}

