/*trait ArticleRepositoryComponent {
  def articleRepository: ArticleRepository
}

trait ArticleRepository {
  def addArticle(article: Article): Article
  def getArticleByTitle(title: String): Article
}

trait PostgresArticleRepositoryComponent extends ArticleRepositoryComponent {
  def articleRespository: ArticleRepository = new PostgresArticleRepository{}
}

trait PostgresArticleRepository extends ArticleRepository {
  def addArticle(article: Article): Article = ...
  def getArticleByTitle(title: String): Article = ...
}


trait VergeService extends ArticleRepositoryComponent {

  def someMethod(adf: adfasd): asdfas = 
    articleRepository.getArticleByTitle("some title")

}


object Main extends VergeService with AnotherDBArticleRepositoryComponent*/