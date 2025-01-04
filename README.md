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

# Key Components
  ** KafkaTweetProcessorApp  
      Reads tweets from Kafka.
      Processes tweets to extract metadata, analyze sentiment, and store in MongoDB.

  ** TwitterKafkaProducerApp
      Reads tweets from a JSON file.
      Publishes tweets to Kafka for downstream processing.

  ** MongoDBHandler
      Handles MongoDB operations such as inserting tweets and creating indexes.
  ** SentimentAnalyzer
      Analyzes tweet sentiment using Stanford CoreNLP.
  ** HashtagExtractor
      Extracts hashtags from tweet content using regex.

