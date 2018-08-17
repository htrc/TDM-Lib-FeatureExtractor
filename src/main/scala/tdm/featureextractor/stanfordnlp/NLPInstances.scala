package tdm.featureextractor.stanfordnlp

import java.util.{Locale, Properties, concurrent}

import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.slf4j.LoggerFactory
import tdm.featureextractor.Helper._
import tdm.featureextractor.PageFeatureExtractor

import scala.util.{Failure, Success}

object NLPInstances {
  private val logger = LoggerFactory.getLogger(getClass)
  private val instances = new concurrent.ConcurrentHashMap[Locale, StanfordCoreNLP]()

  private def createInstance(locale: Locale): StanfordCoreNLP = {
    val lang = locale.getLanguage
    val langProps = s"/nlp/config/$lang.properties"
    logger.debug(s"Loading ${locale.getDisplayLanguage} settings from $langProps")
    val props = loadPropertiesFromClasspath(langProps) match {
      case Success(p) => p
      case Failure(e) => throw e
    }

    new StanfordCoreNLP(props)
  }

  val whitespaceTokenizer: StanfordCoreNLP = {
    val props = new Properties()
    props.put("annotators", "tokenize")
    props.put("tokenize.language", "Whitespace")
    new StanfordCoreNLP(props)
  }

  def forLanguage(lang: String): Option[StanfordCoreNLP] = forLocale(Locale.forLanguageTag(lang))

  def forLocale(locale: Locale): Option[StanfordCoreNLP] = {
    if (PageFeatureExtractor.supportedLanguages.contains(locale.getLanguage))
      Option(instances.get(locale)).orElse {
        this.synchronized {
          Option(instances.get(locale)).orElse {
            val instance = createInstance(locale)
            instances.putIfAbsent(locale, instance)
            Some(instance)
          }
        }
      }
    else None
  }
}