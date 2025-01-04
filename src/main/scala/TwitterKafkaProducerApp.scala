import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, Callback, RecordMetadata}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import akka.actor.ActorSystem
import MongoDBHandler._ // Assuming MongoDBHandler is handling MongoDB connections
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import scala.io.Source
import scala.concurrent.{Future, Promise}
import SentimentAnalyzer._ // Assuming SentimentAnalyzer is a utility for sentiment analysis

object TwitterKafkaProducerApp {
  implicit val system: ActorSystem = ActorSystem("TwitterStreamSimulator")

  // Case classes for Tweet and User
  case class Tweet(created_at: String, id: Long, text: String, user: User)
  case class User(name: String, screen_name: String, location: Option[String])

  // Method to read the JSON file and parse tweets
  def readTweetsFromFile(filePath: String): List[Tweet] = {
    val tweetSource = Source.fromFile(filePath)
    val lines = tweetSource.getLines().toList

    val tweets = lines.flatMap { line =>
      parse(line) match {
        case Right(json) => json.as[Tweet].toOption
        case Left(_) => None
      }
    }

    tweetSource.close()
    tweets
  }

  // Method to send tweet to Kafka
  def sendTweetToKafka(kafkaProducer: KafkaProducer[String, String], topic: String, tweet: Tweet): Unit = {
    val tweetJson = tweet.asJson.noSpaces
    val record = new ProducerRecord[String, String](topic, tweet.id.toString, tweetJson)

    // Send the tweet JSON to Kafka with a callback
    kafkaProducer.send(record, new Callback {
      def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        if (exception != null) {
          println(s"Error sending tweet with ID ${tweet.id} to Kafka: ${exception.getMessage}")
        } else {
          println(s"Successfully sent tweet with ID ${tweet.id} to Kafka topic '${metadata.topic()}'")
        }
      }
    })
  }

  // Method to process the tweets and produce them to Kafka
  def processTweetsAndProduce(tweets: List[Tweet], kafkaProducer: KafkaProducer[String, String], topic: String): Unit = {
    tweets.foreach { tweet =>
      // Send each tweet to Kafka
      sendTweetToKafka(kafkaProducer, topic, tweet)

      // Optionally, you can also perform sentiment analysis here before sending to Kafka
      val sentiment = SentimentAnalyzer.analyzeSentiment(tweet.text)
      println(s"Sentiment for tweet ${tweet.id}: $sentiment")

      // Optionally, store the tweet in MongoDB
      val tweetHash = tweet.text.hashCode.toString
      val hashtags = HashtagExtractor.extractHashtags(tweet.text)
      MongoDBHandler.saveTweetToMongo(tweet.asJson.noSpaces, sentiment, tweetHash, hashtags, None)
    }
  }

  def main(args: Array[String]): Unit = {
    val kafkaTopic = "tweet-stream"
    val kafkaBroker = "localhost:9092"

    val kafkaProps = new Properties()
    kafkaProps.put("bootstrap.servers", kafkaBroker)
    kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val kafkaProducer = new KafkaProducer[String, String](kafkaProps)

    // Path to the JSON file containing tweet data
    val filePath = "boulder_flood_geolocated_tweets.json"

    // Read tweets from the file
    val tweets = readTweetsFromFile(filePath)

    // Process the tweets and send them to Kafka
    processTweetsAndProduce(tweets, kafkaProducer, kafkaTopic)

    // Close Kafka producer after processing
    kafkaProducer.close()
  }
}
