package captcha.instances

import captcha.instances.Captcha._
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._


case class Captcha(id: ID, secret: Question)

object Captcha {
  type Question = String // TODO: change on image
  type ID = Int

  implicit def captchaEncoder: EntityEncoder[IO, Captcha] = ???

  def registerRandomCaptcha(): Captcha = Captcha(1, "1337")

  val captchaService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "generate" =>
      Ok(registerRandomCaptcha())
  }
}