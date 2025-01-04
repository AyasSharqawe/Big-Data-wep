object HashtagExtractor {
  // Function to extract hashtags from tweet text
  def extractHashtags(text: String): List[String] = {
    // Using regex to find hashtags (e.g., #hashtag)
    val hashtagRegex = "#(\\w+)".r
    hashtagRegex.findAllIn(text).toList
  }
}
