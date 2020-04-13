package captcha.instances

import captcha.instances.Captcha.ID
import captcha.instances.Checker.Result

case class Checker(result: Result)

object Checker {
  type Result = String
  type Answer = String

  def checkAnswer(id:ID, answer: Answer): Result = {
    true.toString
  }
}