package captcha.instances

import captcha.instances.Captcha._


case class Captcha(id: ID, secret: Question)

object Captcha {
  type Question = String // TODO: change on image
  type ID = Int//Numeric[Int]

//  implicit def converter(v: Int): Numeric[Int] = Numeric.apply.fromInt(v)
}