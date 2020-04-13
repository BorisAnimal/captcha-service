package captcha.controllers


import captcha.instances.Captcha.ID
import captcha.instances.Checker
import captcha.instances.Checker._
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._

object AnswerChecker {

  //  implicit def resultEncoder: EntityEncoder[IO, Checker] = ???
  object IdQueryParameterMatcher extends QueryParamDecoderMatcher[ID]("id")

  object AnswerQueryParameterMatcher extends QueryParamDecoderMatcher[Answer](name = "answer")

  val checkerService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "check" :? IdQueryParameterMatcher(id) +& AnswerQueryParameterMatcher(answer) =>
      Ok(Checker.checkAnswer(id, answer))
  }
}
