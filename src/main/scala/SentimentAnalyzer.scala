import java.util.Properties
import edu.stanford.nlp.pipeline._
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations

object SentimentAnalyzer {
  // Specify the path to the Stanford CoreNLP model directory
  private val props: Properties = {
    val properties = new Properties()
    properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,sentiment")
    properties.setProperty("ner.applyNumericClassifiers", "false")
    properties.setProperty("coref.algorithm", "neural")
    properties
  }
  private val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props)

  // Method to analyze sentiment of a given text
  def analyzeSentiment(text: String): String = {
    val annotation = new Annotation(text)
    pipeline.annotate(annotation)

    val sentences = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])
    if (!sentences.isEmpty) {
      val sentiment = sentences.get(0).get(classOf[SentimentCoreAnnotations.SentimentClass])
      sentiment
    } else {
      "Neutral"
    }
  }
}
