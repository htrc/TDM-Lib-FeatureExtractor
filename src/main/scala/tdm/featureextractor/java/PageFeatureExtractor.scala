package tdm.featureextractor.java

import org.hathitrust.htrc.textprocessing.runningheaders.PageStructure
import tdm.featureextractor.features.SectionFeatures
import tdm.featureextractor.java.features.PageFeatures

import scala.collection.JavaConverters._

object PageFeatureExtractor {
  private def sectionFeaturesToJava(f: SectionFeatures): tdm.featureextractor.java.features.SectionFeatures =
    new tdm.featureextractor.java.features.SectionFeatures(
      f.tokenCount,
      f.lineCount,
      f.emptyLineCount,
      f.sentenceCount.map(_.asInstanceOf[Integer]).orNull,
      f.capAlphaSeq,
      f.beginCharCount.mapValues(_.asInstanceOf[Integer]).asJava,
      f.endCharCount.mapValues(_.asInstanceOf[Integer]).asJava,
      f.tokenPosCount.mapValues(_.mapValues(_.asInstanceOf[Integer]).asJava).asJava
    )

  /**
    * Wrapper for generating Java-friendly feature objects
    * @see PageStructure for details
    *
    * @param page The page to perform feature extraction on
    * @return The extracted PageFeatures
    */
  def extractPageFeatures(page: PageStructure): PageFeatures = {
    val features = tdm.featureextractor.PageFeatureExtractor.extractPageFeatures(page)

    new PageFeatures(
      features.version,
      features.language.orNull,
      features.tokenCount,
      features.lineCount,
      features.emptyLineCount,
      features.sentenceCount.map(_.asInstanceOf[Integer]).orNull,
      features.header.map(sectionFeaturesToJava).orNull,
      features.body.map(sectionFeaturesToJava).orNull,
      features.footer.map(sectionFeaturesToJava).orNull
    )
  }
}
