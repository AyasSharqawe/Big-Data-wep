## Kafka Tweet Processor Application
# Overview 
The Kafka Tweet Processor Application is a real-time pipeline that ingests tweets from a Kafka topic, processes them to extract valuable metadata, analyzes their sentiment, and stores them in a MongoDB database for future analysis. This system can be used to monitor social media trends, sentiment, and geospatial activity in real time. 

# Features 
- Kafka Producer: Simulates a tweet stream by reading tweets from a JSON file and publishing them to Kafka.
- Tweet Ingestion: Reads tweets from a Kafka topic.
- Hashtag Extraction: Extracts hashtags from tweet content using regex.
- Sentiment Analysis: Determines the sentiment (e.g., Positive, Negative, Neutral) of tweets using the Stanford CoreNLP library.
- MongoDB Storage: Stores tweets along with metadata in a MongoDB database.
- Indexing: Creates indexes on MongoDB collections for efficient querying based on sentiment, hashtags, timestamps, and geospatial data.

# Architecture 
The system consists of the following components: 
  1. Kafka Tweet Producer:
     - Reads tweets from a JSON file.
     - Publishes tweets to a Kafka topic for downstream processing.
  2. Kafka Tweet Processor:
     - Consumes tweets from Kafka.
     - Extracts metadata such as hashtags, user information, location, and sentiment.
     - Stores processed tweets in MongoDB.
  3. MongoDB Handler:
     - Manages MongoDB operations, including saving tweets and creating indexes.
  4. Sentiment Analyzer:
     - Uses Stanford CoreNLP to analyze the sentiment of tweets.
  5. MongoDB Integration:
     - Stores tweets, along with metadata such as sentiment and hashtags, in a MongoDB database.

# Project Structure
  ** KafkaTweetProcessorApp  
      Reads tweets from Kafka.
      Processes tweets to extract metadata, analyze sentiment, and store in MongoDB.

  ** TwitterKafkaProducerApp
     - Reads tweets from a JSON file.
     - Publishes tweets to Kafka for downstream processing.

  ** MongoDBHandler
    - Handles MongoDB operations such as inserting tweets and creating indexes.
  ** SentimentAnalyzer
    - Analyzes tweet sentiment using Stanford CoreNLP.
  ** HashtagExtractor
    - Extracts hashtags from tweet content using regex.

# Requirements
- Scala 2.12+
- Apache Kafka 2.7.0+
- MongoDB 4.4+
- Stanford CoreNLP 4.5.1+
- sbt 1.5.0+

# Setup and Installation 
1. Install Dependencies
   Ensure you have the following installed:
   - Scala
   - sbt
   - Apache Kafka
   - MongoDB
   - Stanford CoreNLP
2. Configure Kafka
   Start Kafka and create a topic named tweet-stream:
    - kafka-topics.sh --create --topic tweet-stream --bootstrap-server localhost:9092
3. Configure MongoDB
   - Ensure MongoDB is running locally on mongodb://localhost:27017 and create a database named tweets_db.
4. Run the Producer Application
  1. Place your JSON file with geolocated tweets (boulder_flood_geolocated_tweets.json) in the project directory.
  2. Run the producer application to send tweets to Kafka:
    - sbt run
5. Run the Consumer Application
  - Consume and process tweets from Kafka:
   -  sbt run
# Contributors 
Ayas Sharqawi 


