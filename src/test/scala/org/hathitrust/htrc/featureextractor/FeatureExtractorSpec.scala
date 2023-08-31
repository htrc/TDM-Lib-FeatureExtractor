package org.hathitrust.htrc.featureextractor

import org.hathitrust.htrc.textprocessing.runningheaders.PageStructureParser.StructuredPage
import org.hathitrust.htrc.textprocessing.runningheaders.{Lines, Page, PageStructure}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.{OptionValues, ParallelTestExecution}
import PageFeatureExtractor._

import scala.collection.immutable.HashMap

/**
  * A very basic spec to test the feature extractor code
  */
class FeatureExtractorSpec extends AnyFlatSpec
  with OptionValues
  with should.Matchers
  with ParallelTestExecution {

  val pageLines: Lines = IndexedSeq[String](
    "  This is a title",
    "that spans two lines",
    "  ",
    "Once upon a time there was a fox",
    "that tried to jump, un-  ",
    "successfully, over an all-too-lazy dog.",
    "The dog didn't even notice the fox be-",
    "  cause he was sleeping soundly",
    "in his bed.",
    "I received a notice from the dog,",
    "complaining about this little fox.",
    "Sleeping soundly, the dog ignored the fox.",
    "The fox jumped, again, over the dog.",
    "Not again, complained the dog..."
  )

  val page: StructuredPage = new Page with PageStructure {
    override def textLines: Lines = pageLines

    override def numHeaderLines: Int = 2

    override def numFooterLines: Int = 0
  }

  "countLongestAlphaSequenceOfCapitalizedLines" should "work correctly" in {
    countLongestAlphaSequenceOfCapitalizedLines(pageLines) shouldBe 3
  }

  "extractPageFeatures" should "return the correct set of features" in {
    val pageFeatures = extractPageFeatures(page)
    pageFeatures.language shouldBe Some("en")

    val headerFeatures = pageFeatures.header.value
    val bodyFeatures = pageFeatures.body.value

    headerFeatures.tokenCount shouldBe 8
    headerFeatures.lineCount shouldBe 2
    headerFeatures.emptyLineCount shouldBe 0
    headerFeatures.sentenceCount.value shouldBe 1
    headerFeatures.capAlphaSeq shouldBe 1
    headerFeatures.beginCharCount shouldBe HashMap("t" -> 1, "T" -> 1)
    headerFeatures.endCharCount shouldBe HashMap("e" -> 1, "s" -> 1)
    headerFeatures.tokenPosCount shouldBe HashMap(
      "is" -> HashMap("VBZ" -> 1),
      "This" -> HashMap("DT" -> 1),
      "two" -> HashMap("CD" -> 1),
      "a" -> HashMap("DT" -> 1),
      "that" -> HashMap("WDT" -> 1),
      "spans" -> HashMap("VBZ" -> 1),
      "title" -> HashMap("NN" -> 1),
      "lines" -> HashMap("NNS" -> 1)
    )

    bodyFeatures.tokenCount shouldBe 81
    bodyFeatures.lineCount shouldBe 12
    bodyFeatures.emptyLineCount shouldBe 1
    bodyFeatures.sentenceCount.value shouldBe 6
    bodyFeatures.capAlphaSeq shouldBe 3
    bodyFeatures.beginCharCount shouldBe HashMap(
      "s" -> 1, "N" -> 1, "T" -> 2, "t" -> 1, "I" -> 1, "i" -> 1, "c" -> 2, "O" -> 1, "S" -> 1
    )
    bodyFeatures.endCharCount shouldBe HashMap("x" -> 1, "." -> 6, "y" -> 1, "-" -> 2, "," -> 1)
    bodyFeatures.tokenPosCount shouldBe HashMap(
      "Once" -> HashMap("IN" -> 1),
      "lazy" -> HashMap("JJ" -> 1),
      "this" -> HashMap("DT" -> 1),
      "in" -> HashMap("IN" -> 1),
      "his" -> HashMap("PRP$" -> 1),
      "too" -> HashMap("RB" -> 1),
      "jumped" -> HashMap("VBD" -> 1),
      "soundly" -> HashMap("RB" -> 2),
      "." -> HashMap("." -> 5),
      "jump" -> HashMap("VB" -> 1),
      "all" -> HashMap("RB" -> 1),
      "complained" -> HashMap("VBD" -> 1),
      "Sleeping" -> HashMap("VBG" -> 1),
      "a" -> HashMap("DT" -> 3),
      "because" -> HashMap("IN" -> 1),
      "complaining" -> HashMap("VBG" -> 1),
      "dog" -> HashMap("NN" -> 6),
      "I" -> HashMap("PRP" -> 1),
      "that" -> HashMap("WDT" -> 1),
      "upon" -> HashMap("IN" -> 1),
      "to" -> HashMap("TO" -> 1),
      "bed" -> HashMap("NN" -> 1),
      "-" -> HashMap("HYPH" -> 2),
      "did" -> HashMap("VBD" -> 1),
      "," -> HashMap("," -> 7),
      "was" -> HashMap("VBD" -> 2),
      "there" -> HashMap("EX" -> 1),
      "The" -> HashMap("DT" -> 2),
      "over" -> HashMap("IN" -> 2),
      "notice" -> HashMap("NN" -> 1, "VB" -> 1),
      "unsuccessfully" -> HashMap("RB" -> 1),
      "he" -> HashMap("PRP" -> 1),
      "even" -> HashMap("RB" -> 1),
      "little" -> HashMap("JJ" -> 1),
      "again" -> HashMap("RB" -> 2),
      "from" -> HashMap("IN" -> 1),
      "Not" -> HashMap("RB" -> 1),
      "tried" -> HashMap("VBD" -> 1),
      "an" -> HashMap("DT" -> 1),
      "..." -> HashMap("." -> 1),
      "time" -> HashMap("NN" -> 1),
      "ignored" -> HashMap("VBD" -> 1),
      "sleeping" -> HashMap("VBG" -> 1),
      "about" -> HashMap("IN" -> 1),
      "n't" -> HashMap("RB" -> 1),
      "fox" -> HashMap("NN" -> 5),
      "received" -> HashMap("VBD" -> 1),
      "the" -> HashMap("DT" -> 6)
    )

    pageFeatures.footer shouldBe None

    pageFeatures.tokenCount shouldBe 89
    pageFeatures.lineCount shouldBe 14
    pageFeatures.emptyLineCount shouldBe 1
    pageFeatures.sentenceCount.value shouldBe 7
  }
}
