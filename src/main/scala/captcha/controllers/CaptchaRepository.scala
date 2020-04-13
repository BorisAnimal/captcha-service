package captcha.controllers

import captcha.instances.Captcha._
import captcha.instances.Checker.Answer
import scala.collection.mutable

// TODO: To handle concurrency here, wrap into cats.Resource is enough?
// How to apply IO monad here in correct way?


// It can be DB access layer in future
object CaptchaRepository {
  val id2answer: mutable.Map[ID, Answer] = mutable.Map[ID, Answer]()

  private var idCounter: ID = 0

  def registerNew(answer: Answer): ID = {
    idCounter += 1
    id2answer.addOne(idCounter, answer)
    idCounter
  }

  def delete(id: ID): Unit = {
    id2answer.subtractOne(id)
  }

  def get(id: ID): Answer = {
    id2answer(id)
  }
}
