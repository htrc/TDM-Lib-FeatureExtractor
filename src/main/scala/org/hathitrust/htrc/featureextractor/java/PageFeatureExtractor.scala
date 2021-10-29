package org.hathitrust.htrc.featureextractor.java

import org.hathitrust.htrc.featureextractor.features.{SectionFeatures => ScalaSectionFeatures}
import org.hathitrust.htrc.featureextractor.java.features.{PageFeatures => JavaPageFeatures}
import org.hathitrust.htrc.featureextractor.java.features.{SectionFeatures => JavaSectionFeatures}
import org.hathitrust.htrc.featureextractor.{PageFeatureExtractor => ScalaPageFeatureExtractor}
import org.hathitrust.htrc.textprocessing.runningheaders.PageStructure

import scala.jdk.CollectionConverters._

object PageFeatureExtractor {
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  private def sectionFeaturesToJava(f: ScalaSectionFeatures): JavaSectionFeatures =
    new JavaSectionFeatures(
      f.tokenCount,
      f.lineCount,
      f.emptyLineCount,
      f.sentenceCount.map(_.asInstanceOf[Integer]).orNull,
      f.capAlphaSeq,
      f.beginCharCount.map { case (k, v) => k -> v.asInstanceOf[Integer] }.asJava,
      f.endCharCount.map { case (k, v) => k -> v.asInstanceOf[Integer] }.asJava,
      f.tokenPosCount.map { case (k, v) => k -> v.map { case (k, v) => k -> v.asInstanceOf[Integer] }.asJava }.asJava
    )

  /**
    * Wrapper for generating Java-friendly feature objects
    * @see PageStructure for details
    *
    * @param page The page to perform feature extraction on
    * @return The extracted PageFeatures
    */
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def extractPageFeatures(page: PageStructure): JavaPageFeatures = {
    val features = ScalaPageFeatureExtractor.extractPageFeatures(page)

    new JavaPageFeatures(
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
