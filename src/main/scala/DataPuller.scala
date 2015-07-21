package vergedatabaseSQL

import akka.actor._
import org.apache.http.client.methods.HttpGet
import org.apache.http._
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import scala.xml._

case object PullRequest

trait DataPuller extends ArticleRepositoryComponent {
  
  class VergeActor extends Actor {
    import context.dispatcher
    import XmlParser._ 
    private var scheduler: Cancellable = _
   
    override def preStart(): Unit = {
      import scala.concurrent.duration._
      scheduler = context.system.scheduler.schedule(
        initialDelay = 0 seconds,
        interval = 5 minutes,
        receiver = self,
        message = PullRequest
      )
    }

    override def postStop(): Unit = {
      scheduler.cancel()
    }

    def receive = {
      case PullRequest => {
        val client = new DefaultHttpClient
        val request = new HttpGet("http://www.theverge.com/rss/frontpage.xml")
        println("Requesting")
        val response = client.execute(request)
        val entity = response.getEntity()
        val content = EntityUtils.toString(entity)
        val xmlData = XML.loadString(content)
        val data = (xmlData \\ "entry").map(dataMaker).toList
        articleRepository.addArticles(data)
      }
    }
  }
}