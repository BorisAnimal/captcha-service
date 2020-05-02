package captchaServer.domain

sealed trait InternalServerError

object DatasetWrongKey extends InternalServerError {
  override def toString: String = "Asked key missed in dataset."
}

sealed trait ImageError extends InternalServerError

object EmptyImages extends ImageError {
  override def toString: String =
    "Zero images passed. Method can't work."
}
