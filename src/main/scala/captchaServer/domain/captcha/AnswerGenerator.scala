package captchaServer.domain.captcha

import cats.Monad


import scala.util.Random

class AnswerGenerator private(alphabet: Vector[String], val questionLen: Int,
                              val randomSeed: Option[Int] = None) {
  val r: Random = randomSeed.map(new scala.util.Random(_)).getOrElse(new scala.util.Random())

  def generateAnswer[F[_]](implicit M: Monad[F]): F[Seq[String]] = M.pure(for (_ <- 0 until questionLen)
    yield alphabet(r.nextInt(alphabet.size)))
}

object AnswerGenerator {
  def apply(alphabet: Set[String], questionLen: Int,
            randomSeed: Option[Int]): AnswerGenerator = {
    assert(questionLen > 0)
    new AnswerGenerator(alphabet.toVector, questionLen, randomSeed)
  }
}
