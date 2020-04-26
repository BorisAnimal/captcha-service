package captchaServer.domain.captcha

trait CaptchaRepositoryAlgebra[Wrapper[_]] {
  def create(captcha: Captcha): Wrapper[Captcha]

  def delete(id: Int): Wrapper[Option[Captcha]]

  def get(id: Int): Wrapper[Option[Captcha]]

  def size: Int
}
