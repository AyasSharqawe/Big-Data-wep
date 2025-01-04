import java.util.Properties
import org.apache.kafka.clients.consumer.{KafkaConsumer}
import scala.collection.JavaConverters._
import org.apache.kafka.common.serialization.StringDeserializer
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorSystem
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import MongoDBHandler._
import HashtagExtractor._
import SentimentAnalyzer._
import scala.concurrent.Future

object KafkaTweetProcessorApp {
  implicit val system: ActorSystem = ActorSystem("KafkaTweetProcessor")

  val kafkaTopic = "tweet-stream"
  val kafkaBroker = "localhost:9092"

  val consumerProps = new Properties()
  consumerProps.put("bootstrap.servers", kafkaBroker)
  consumerProps.put("group.id", "tweet-consumer-group")
  consumerProps.put("key.deserializer", classOf[StringDeserializer].getName)
  consumerProps.put("value.deserializer", classOf[StringDeserializer].getName)

  val consumer = new KafkaConsumer[String, String](consumerProps)
  consumer.subscribe(List(kafkaTopic).asJava)

  def processTweet(tweetJson: String): Future[Unit] = {
    parse(tweetJson) match {
      case Right(json) =>
        // Extract tweet fields
        val text = json.hcursor.get[String]("text").getOrElse("")
        val tweetId = json.hcursor.get[Long]("id").getOrElse(0L)
        val sentiment = SentimentAnalyzer.analyzeSentiment(text)
        val hashtags = extractHashtags(text)

        // Create a hash for the tweet
        val tweetHash = text.hashCode.toString

        // Extract user info and other fields
        val user = json.hcursor.downField("user").as[Map[String, String]].getOrElse(Map.empty)
        val createdAt = json.hcursor.get[String]("created_at").getOrElse("")
        val location = json.hcursor.downField("location").as[Option[Map[String, Double]]].getOrElse(None)

        // Save tweet to MongoDB
        MongoDBHandler.saveTweetToMongo(tweetJson, sentiment, tweetHash, hashtags, location)

        Future.successful(())
      case Left(error) =>
        println(s"Failed to parse tweet JSON: $error")
        Future.successful(())
    }
  }

  def main(args: Array[String]): Unit = {
    println("Starting Kafka Tweet Processor...")

    // Continuously read from Kafka and process tweets
    while (true) {
      val records = consumer.poll(1000) // Poll Kafka for new messages

      // Process each tweet from the Kafka topic
      records.asScala.foreach { record =>
        val tweetJson = record.value()
        println(s"Received tweet: $tweetJson")

        processTweet(tweetJson)
      }
    }
  }
}
