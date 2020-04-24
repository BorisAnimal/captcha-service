package captchaServer.domain.captcha

import captchaServer.domain.CaptchaNotFoundError
import cats.Monad
import cats.data.EitherT
import cats.implicits._

class CaptchaService[F[_] : Monad](repositoryAlgebra: CaptchaRepositoryAlgebra[F],
                                   captchaGenerator: AnswerGenerator) {
  def generateCaptcha: F[Captcha] = for {
    captchaText <- captchaGenerator.generateAnswer[F]
    res <- repositoryAlgebra.create(Captcha(answer = captchaText.mkString("")))
  } yield res

  def checkCaptcha(captchaId: Int, possibleAnswer: String):
  EitherT[F, CaptchaNotFoundError, Boolean] = {
    val answer = for {
      item <- repositoryAlgebra.get(captchaId)
    } yield item.map(_.answer.equals(possibleAnswer))
    EitherT.fromOptionF(answer, CaptchaNotFoundError(captchaId))
  }
}
