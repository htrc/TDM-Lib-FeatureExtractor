package tdm.featureextractor

import _root_.java.io.FileNotFoundException
import _root_.java.util.{Locale, Properties}

import com.optimaize.langdetect.i18n.LdLocale

import scala.util.{Failure, Try, Using}

object Helper {
  def loadPropertiesFromClasspath(path: String): Try[Properties] = {
    require(path != null && path.nonEmpty)

    Option(getClass.getResourceAsStream(path))
      .map(Using(_) { is =>
        val props = new Properties()
        props.load(is)
        props
      })
      .getOrElse(Failure(new FileNotFoundException(s"$path not found")))
  }

  implicit class LdLocaleDecorator(ldLocale: LdLocale) {
    def toLocale: Locale = {
      val builder = new Locale.Builder()
      builder.setLanguage(ldLocale.getLanguage)
      if (ldLocale.getRegion.isPresent)
        builder.setRegion(ldLocale.getRegion.get())
      if (ldLocale.getScript.isPresent)
        builder.setScript(ldLocale.getScript.get())
      builder.build()
    }
  }
}
