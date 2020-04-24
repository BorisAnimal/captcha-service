package captchaServer.domain.captcha

case class Captcha(answer: String,
                   //secret: String,
                   id: Option[Int] = None)
