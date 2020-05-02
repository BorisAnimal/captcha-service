package captchaServer.domain.captcha

import scala.util.Random

case class SymbolPicker[T](collection: Seq[T]) {
  val r: Random = new scala.util.Random()

  def pick: T = collection(r.nextInt(collection.size))
}

case class Dataset[V](private val dict: Map[String, SymbolPicker[V]]) {
  def pick(symbol: String): V = {
    dict(symbol).pick
  }

  def getAlphabet: Iterable[String] =
    dict.keys
}
