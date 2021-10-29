package org.hathitrust.htrc.featureextractor

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import org.apache.commons.codec.digest.DigestUtils
import org.hathitrust.htrc.featureextractor.stanfordnlp.NLPInstances
import org.hathitrust.htrc.textprocessing.runningheaders.{Lines, Page, PageStructure}
import LanguageDetector.detectLanguage
import org.hathitrust.htrc.featureextractor.features.{PageFeatures, SectionFeatures}

import scala.jdk.CollectionConverters._
import scala.util.matching.Regex

object PageFeatureExtractor {
  val supportedLanguages: Set[String] = Set("ar", "zh", "en", "fr", "de", "es")

  private val hyphenWordRegex: Regex = """(\S*\p{L})-\n(\p{L}\S*)\s?""".r
  private val punctBeforeRegex: Regex = """(?<=^|\s)(\p{P}+)(?=\p{L})""".r
  private val punctAfterRegex: Regex = """(?<=\p{L})(\p{P}+)(?=\s|$)""".r
  private val zeroWidthSpaceRegex: Regex = "\u200b".r
  private val maxTokenChars: Int = 200
  private val posTagUnknown: String = "UNK"

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  private[featureextractor] def countLongestAlphaSequenceOfCapitalizedLines(lines: Lines): Int = {
    import _root_.java.lang.Math.max

    var maxSeqCount, curSeqCount = 0
    var lastChar: Option[Char] = None

    for (c <- lines.withFilter(_.head.isUpper).map(_.head)) {
      lastChar match {
        case Some(char) if c >= char => curSeqCount += 1
        case None => curSeqCount = 1
        case _ =>
          maxSeqCount = max(curSeqCount, maxSeqCount)
          curSeqCount = 1
      }

      lastChar = Some(c)
    }

    maxSeqCount = max(curSeqCount, maxSeqCount)

    maxSeqCount
  }

  def extractBasicSectionFeatures(lines: Lines, nlp: StanfordCoreNLP): Option[SectionFeatures] = {
    // trim lines, replace zero-width space, and filter out empty lines
    val nonEmptyLines = lines.map(l => zeroWidthSpaceRegex.replaceAllIn(l, " ").trim).filterNot(_.isEmpty)
    val emptyLineCount = lines.size - nonEmptyLines.size

    // create the character distribution for begin and end characters on each line
    val beginCharCount = nonEmptyLines.groupBy(_.head.toString).map { case (k, v) => k -> v.length }
    val endCharCount = nonEmptyLines.groupBy(_.last.toString).map { case (k, v) => k -> v.length }

    // find the count of the longest sequence of lines starting with a capital letter in alphabetic order
    val longestAlphaSeq = countLongestAlphaSequenceOfCapitalizedLines(nonEmptyLines)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val text = {
      var s = nonEmptyLines.mkString("\n")
      s = hyphenWordRegex.replaceAllIn(s, "$1$2\n") // combine hyphenated words occurring at end of line
      s = punctBeforeRegex.replaceAllIn(s, "$1 ") // separate punctuation at beginning of words
      s = punctAfterRegex.replaceAllIn(s, " $1") // separate punctuation at end of words
      s
    }

    if (text.isEmpty)
      return None

    val annotatedText: Annotation = {
      val annotation = new Annotation(text)
      nlp.annotate(annotation)
      annotation
    }

    val tokenPos =
      annotatedText
        .get(classOf[CoreAnnotations.TokensAnnotation])
        .iterator()
        .asScala
        .map(token => token.originalText().take(maxTokenChars) -> posTagUnknown)
        .toList

    val tokenPosCount = tokenPos.groupBy { case (token, _) => token }.map {
      case (token, tokenPosArr) =>
        token ->
          tokenPosArr
            .map { case (_, pos) => pos }
            .groupBy(identity)
            .map { case (k, v) => k -> v.length }
    }

    Some(SectionFeatures(
      tokenCount = tokenPos.length,
      lineCount = lines.length,
      emptyLineCount = emptyLineCount,
      sentenceCount = None,
      capAlphaSeq = longestAlphaSeq,
      beginCharCount = beginCharCount,
      endCharCount = endCharCount,
      tokenPosCount = tokenPosCount
    ))
  }

  def extractFullSectionFeatures(lines: Lines, nlp: StanfordCoreNLP): Option[SectionFeatures] = {
    // trim lines, replace zero-width space, and filter out empty lines
    val nonEmptyLines = lines.map(l => zeroWidthSpaceRegex.replaceAllIn(l, " ").trim).filterNot(_.isEmpty)
    val emptyLineCount = lines.size - nonEmptyLines.size

    // create the character distribution for begin and end characters on each line
    val beginCharCount = nonEmptyLines.groupBy(_.head.toString).map { case (k, v) => k -> v.length }
    val endCharCount = nonEmptyLines.groupBy(_.last.toString).map { case (k, v) => k -> v.length }

    // find the count of the longest sequence of lines starting with a capital letter in alphabetic order
    val longestAlphaSeq = countLongestAlphaSequenceOfCapitalizedLines(nonEmptyLines)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val text = {
      var s = nonEmptyLines.mkString("\n")
      s = hyphenWordRegex.replaceAllIn(s, "$1$2\n") // combine hyphenated words occurring at end of line
      s
    }

    if (text.isEmpty)
      return None

    val annotatedText: Annotation = {
      val annotation = new Annotation(text)
      nlp.annotate(annotation)
      annotation
    }

    val sentenceCount = annotatedText.get(classOf[CoreAnnotations.SentencesAnnotation]).size()

    val tokenPos =
      annotatedText
        .get(classOf[CoreAnnotations.TokensAnnotation])
        .iterator()
        .asScala
        .map(token => token.originalText().take(maxTokenChars) -> token.tag())
        .toList

    val tokenPosCount = tokenPos.groupBy { case (token, _) => token }.map {
      case (token, tokenPosArr) => token -> tokenPosArr.map { case (_, pos) => pos }.groupBy(identity).map { case (k, v) => k -> v.length }
    }

    Some(SectionFeatures(
      tokenCount = tokenPos.length,
      lineCount = lines.length,
      emptyLineCount = emptyLineCount,
      sentenceCount = Some(sentenceCount),
      capAlphaSeq = longestAlphaSeq,
      beginCharCount = beginCharCount,
      endCharCount = endCharCount,
      tokenPosCount = tokenPosCount
    ))
  }

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def extractPageFeatures(page: PageStructure): PageFeatures = {
    val text = page.asInstanceOf[Page].text
    val locale = detectLanguage(text)
    val version = DigestUtils.md5Hex(text)
    val (header, body, footer) =
      locale.flatMap(NLPInstances.forLocale(_)) match {
        case Some(nlp) =>
          val headerFeatures = extractFullSectionFeatures(page.headerLines, nlp)
          val bodyFeatures = extractFullSectionFeatures(page.bodyLines, nlp)
          val footerFeatures = extractFullSectionFeatures(page.footerLines, nlp)

          (headerFeatures, bodyFeatures, footerFeatures)

        case None =>
          val nlp = NLPInstances.whitespaceTokenizer
          val headerFeatures = extractBasicSectionFeatures(page.headerLines, nlp)
          val bodyFeatures = extractBasicSectionFeatures(page.bodyLines, nlp)
          val footerFeatures = extractBasicSectionFeatures(page.footerLines, nlp)

          (headerFeatures, bodyFeatures, footerFeatures)
      }

    PageFeatures(
      version = version,
      language = locale.map(_.getLanguage),
      header = header,
      body = body,
      footer = footer
    )
  }
}
