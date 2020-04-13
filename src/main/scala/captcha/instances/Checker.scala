package captcha.instances

import captcha.instances.Captcha.ID
import captcha.instances.Checker.Result
import org.http4s.EntityEncoder
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._

import scala.concurrent.ExecutionContext.Implicits.global

case class Checker(result: Result)

object Checker {
  type Result = String
  type Answer = String

//  implicit def resultEncoder: EntityEncoder[IO, Checker] = ???

  def checkAnswer(id:ID, answer: Answer): Result = {
    true.toString
  }

  object IdQueryParameterMatcher extends QueryParamDecoderMatcher[ID]("id")

  object AnswerQueryParameterMatcher extends QueryParamDecoderMatcher[Answer](name="answer")

  val checkerService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "check" :? IdQueryParameterMatcher(id) +& AnswerQueryParameterMatcher(answer) =>
      Ok(checkAnswer(id, answer))}
}