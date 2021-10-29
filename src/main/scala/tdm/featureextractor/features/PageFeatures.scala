package tdm.featureextractor.features

object PageFeatures {
  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def apply(version: String,
            language: Option[String],
            header: Option[SectionFeatures],
            body: Option[SectionFeatures],
            footer: Option[SectionFeatures]): PageFeatures = {
    val allSections = List(header, body, footer)
    val nonEmptySections = allSections.collect { case Some(s) => s }

    val tokenCount = nonEmptySections.foldLeft(0)(_ + _.tokenCount)
    val lineCount = nonEmptySections.foldLeft(0)(_ + _.lineCount)
    val emptyLineCount = nonEmptySections.foldLeft(0)(_ + _.emptyLineCount)
    val sentenceCount =
      if (nonEmptySections.forall(_.sentenceCount.isDefined))
        Some(nonEmptySections.foldLeft(0)(_ + _.sentenceCount.get))
      else None

    new PageFeatures(
      version = version,
      language = language,
      tokenCount = tokenCount,
      lineCount = lineCount,
      emptyLineCount = emptyLineCount,
      sentenceCount = sentenceCount,
      header = header,
      body = body,
      footer = footer
    )
  }

  def unapply(arg: PageFeatures): Option[(String, Option[String], Int, Int, Int, Option[Int], Option[SectionFeatures], Option[SectionFeatures], Option[SectionFeatures])] =
    Some(arg.version, arg.language, arg.tokenCount, arg.lineCount, arg.emptyLineCount, arg.sentenceCount, arg.header, arg.body, arg.footer)
}

/**
  * Object recording aggregate features at the page level
  *
  * @param version        The MD5 hash of the page content
  * @param language       The identified page language (if any)
  * @param tokenCount     The total token count for the page
  * @param lineCount      The total line count for the page
  * @param emptyLineCount The empty line count for the page
  * @param sentenceCount  The sentence count for the page
  * @param header         The page header features
  * @param body           The page body features
  * @param footer         The page footer features
  */
class PageFeatures(val version: String,
                   val language: Option[String],
                   val tokenCount: Int,
                   val lineCount: Int,
                   val emptyLineCount: Int,
                   val sentenceCount: Option[Int],
                   val header: Option[SectionFeatures],
                   val body: Option[SectionFeatures],
                   val footer: Option[SectionFeatures]) extends BasicFeatures