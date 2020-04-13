package captcha.controllers

import captcha.instances.Captcha
import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._

object CaptchaGenerator {

  implicit def captchaEncoder: EntityEncoder[IO, Captcha] = ???

  val captchaService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "generate" =>
      Ok(Captcha.registerRandomCaptcha())
  }
}
