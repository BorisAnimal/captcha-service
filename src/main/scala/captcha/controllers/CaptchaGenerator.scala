package captcha.controllers

import captcha.instances.Captcha
import captcha.instances.Captcha.Question
import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._

class CaptchaGenerator(val questionLen: Int = 4, val randomSeed: Int = 0) {

  import CaptchaGenerator._

  val repository: CaptchaRepository.type = CaptchaRepository
  val r = new scala.util.Random(randomSeed)

  def generate: Question = {
    r.nextString(questionLen)
  }

  def registerRandomCaptcha(): Captcha = {
    val q = generate
    val id = repository.registerNew(q)
    Captcha(id, q)
  }

  val captchaService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "generate" =>
      Ok(registerRandomCaptcha())
  }
}

object CaptchaGenerator {
  implicit def captchaEncoder: EntityEncoder[IO, Captcha] = ???
}
