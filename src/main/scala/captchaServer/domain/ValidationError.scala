package captchaServer.domain

sealed trait ValidationError extends Product with Serializable

case class CaptchaNotFoundError(id: Int) extends ValidationError {
  override def toString: String = s"Captcha with id=$id does not exist."
}
