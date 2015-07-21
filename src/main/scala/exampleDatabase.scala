/*import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.annotations.Column
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.PostgreSqlAdapter
import java.util.Calendar

case class Email(@Column("sender") sender: String, 
        @Column("recipient") recipient: String, 
        @Column("content") content: String,
        @Column("timestamp") val timeStamp:java.util.Date = Calendar.getInstance().getTime()
)

object EmailSystem extends Schema {
  val emails = table[Email]("emails")

  def printEmail(e: Email) {
    println(e.sender,e.recipient,e.content,e.timeStamp)
  }

  
  def add = {
    transaction{
      EmailSystem.emails.insert(Email("Joe", "John", "Hello John"))
      EmailSystem.emails.insert(Email("Ping", "Pong", "0ms response"))
    }
  }

  transaction {
    val queriedEmails: List[Email] = from(EmailSystem.emails)(e => select(e)).toList

    queriedEmails.foreach(printEmail)
  }
}

object main extends App {
  Class.forName("org.postgresql.Driver")
  SessionFactory.concreteFactory = Some(()=>
    Session.create(
        java.sql.DriverManager.getConnection("jdbc:postgresql://localhost/exampleDatabase", "jackson", "jaxmitch"),
        new PostgreSqlAdapter()))

  EmailSystem.add
}*/