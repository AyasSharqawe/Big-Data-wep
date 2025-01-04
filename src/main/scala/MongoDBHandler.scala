import org.mongodb.scala._
import io.circe.parser._
import io.circe.generic.auto._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import HashtagExtractor._
import org.mongodb.scala._
import org.mongodb.scala.bson.{BsonNull, BsonString}
import org.mongodb.scala.model._
import org.mongodb.scala.model.Indexes._

object MongoDBHandler {
  val client: MongoClient = MongoClient("mongodb://localhost:27017")
  val database: MongoDatabase = client.getDatabase("tweets_db")
  val collection: MongoCollection[Document] = database.getCollection("tweets")
  def createIndexes(): Unit = {
    // Index on sentiment for fast retrieval of tweets based on sentiment
    collection.createIndex(Indexes.ascending("sentiment")).toFuture()

    // Compound index on hashtags (useful for fast searching by hashtags)
    collection.createIndex(Indexes.ascending("hashtags")).toFuture()

    // Index on created_at for time-based queries
    collection.createIndex(Indexes.ascending("created_at")).toFuture()

    // Index on tweet_hash for deduplication and uniqueness checks
    collection.createIndex(Indexes.ascending("tweet_hash")).toFuture()

    // Full-text index on tweet content for text-based search
    collection.createIndex(Indexes.text("text")).toFuture()

    // Geospatial index on location for location-based queries
    collection.createIndex(Indexes.geo2dsphere("location")).toFuture()
  }

  def saveTweetToMongo(
                        tweetJson: String,
                        sentiment: String,
                        tweetHash: String,
                        hashtags: List[String],
                        location: Option[Map[String, Double]]
                      ): Future[Unit] = {
    // Parse the JSON string to a Json object using Circe
    parse(tweetJson) match {
      case Right(json) =>
        // Extract the tweet text, id, and sentiment from the JSON
        val text = json.hcursor.get[String]("text").getOrElse("")
        val tweetId = json.hcursor.get[Long]("id").getOrElse(0L)
        val createdAt = json.hcursor.get[String]("created_at").getOrElse("")
        val user = json.hcursor.downField("user").as[Map[String, String]].getOrElse(Map.empty)

        // Create the collection name based on tweet ID
        val collectionName = s"tweet_$tweetId"
        val collection: MongoCollection[Document] = database.getCollection(collectionName)
                // Convert the hashtags to a List[String] for MongoDB
                val hashtagList = hashtags.map(_.toString) // Ensure the hashtags are in string format


        // Extract location (optional, String)
                val location = json.hcursor.downField("user").downField("location").as[Option[String]].getOrElse(None)

                // Create a MongoDB Document from extracted fields including sentiment
                val document = Document(
                  "_id" -> tweetId,  // Use tweetId as MongoDB `_id`
                  "created_at" -> createdAt,
                  "text" -> text,
                  "user" -> user.getOrElse("screen_name", "Unknown"),
                  "hashtags" -> hashtagList, // Store hashtags as a List of strings
                  "sentiment" -> sentiment,   // Store the sentiment of the tweet
                  "tweet_hash" -> tweetHash,
                  "location" -> location.map(BsonString.apply)
                )

        // Insert the document into the dynamically created collection asynchronously
        collection.insertOne(document).toFuture().map { _ =>
          println(s"Tweet with ID $tweetId successfully saved to collection '$collectionName' with sentiment '$sentiment'.")
        }

      case Left(error) =>
        Future.successful(println(s"Failed to parse tweet JSON: $error"))
    }
  }
}
