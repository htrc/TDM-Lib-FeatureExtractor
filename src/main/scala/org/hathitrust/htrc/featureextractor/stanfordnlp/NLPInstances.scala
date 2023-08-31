package org.hathitrust.htrc.featureextractor.stanfordnlp

import java.net.URL
import java.util.{Locale, Properties, concurrent}
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.hathitrust.htrc.featureextractor.PageFeatureExtractor
import org.slf4j.LoggerFactory
import org.hathitrust.htrc.featureextractor.Helper._

import scala.util.{Failure, Success, Using}

object NLPInstances {
  private val logger = LoggerFactory.getLogger(getClass)
  private val instances = new concurrent.ConcurrentHashMap[Locale, StanfordCoreNLP]()

  private def createInstance(props: Properties): StanfordCoreNLP =
    new StanfordCoreNLP(props)

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  private def getLocalePropertiesFromClasspath(locale: Locale): Properties = {
    val lang = locale.getLanguage
    val langProps = s"/nlp/config/$lang.properties"
    logger.debug(s"Loading ${locale.getDisplayLanguage} settings from $langProps")
    loadPropertiesFromClasspath(langProps) match {
      case Success(p) => p
      case Failure(e) => throw e
    }
  }

  private def getLocalePropertiesFromUrl(locale: Locale, baseUrl: URL): Properties = {
    val lang = locale.getLanguage
    val localePropUrl = new URL(baseUrl, s"$lang.properties")
    logger.debug(s"Loading ${locale.getDisplayLanguage} settings from $localePropUrl")
    val localeProps = new Properties()
    Using.resource(localePropUrl.openStream()) { propStream =>
      localeProps.load(propStream)
    }

    localeProps
  }

  val whitespaceTokenizer: StanfordCoreNLP = {
    val props = new Properties()
    props.put("annotators", "tokenize")
    props.put("tokenize.language", "Whitespace")
    new StanfordCoreNLP(props)
  }

  def forLanguage(lang: String, propBaseUrl: Option[URL] = None): Option[StanfordCoreNLP] = forLocale(Locale.forLanguageTag(lang), propBaseUrl)

  def forLocale(locale: Locale, propBaseUrl: Option[URL] = None): Option[StanfordCoreNLP] = {
    if (PageFeatureExtractor.supportedLanguages.contains(locale.getLanguage))
      Option(instances.get(locale)).orElse {
        this.synchronized {
          Option(instances.get(locale)).orElse {
            val props = propBaseUrl match {
              case None => getLocalePropertiesFromClasspath(locale)
              case Some(baseUrl) => getLocalePropertiesFromUrl(locale, baseUrl)
            }
            val instance = createInstance(props)
            instances.putIfAbsent(locale, instance)
            Some(instance)
          }
        }
      }
    else None
  }

}