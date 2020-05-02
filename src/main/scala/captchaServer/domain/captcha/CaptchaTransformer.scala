package captchaServer.domain.captcha

import java.awt.image.BufferedImage

import captchaServer.domain.{EmptyImages, ImageError, InternalServerError}
import cats.Applicative
import cats.data.EitherT
import cats.syntax.either._

trait CaptchaTransformer[F[_], T] {
  def concat(items: Seq[T]): EitherT[F, InternalServerError, BufferedImage]

  def transform(answer: String): EitherT[F, InternalServerError, T]
}

class CaptchaToImageTransformer[F[_] : Applicative](dataset: Dataset[BufferedImage])
  extends CaptchaTransformer[F, BufferedImage] {

  val selectedImageType: Int = BufferedImage.TYPE_INT_RGB

  /**
   * Modifies source image
   */
  def putImage(source: BufferedImage, target: BufferedImage, xStart: Int): BufferedImage = {
    for (x <- 0 until target.getWidth)
      for (y <- 0 until target.getHeight)
        source.setRGB(x + xStart, y, target.getRGB(x, y))
    source
  }

  def concatImagesHorizontally(images: Seq[BufferedImage]): Either[ImageError, BufferedImage] =
    images match {
      case Nil => Left(EmptyImages)
      case _ =>
        val newW = images.map(_.getWidth).sum
        val singleH = images.head.getHeight
        val singleW = images.head.getWidth
        val newImg = new BufferedImage(newW, singleH, selectedImageType)

        images.zipWithIndex.foldLeft(newImg) { (largeImage, t) =>
          putImage(largeImage, t._1, t._2 * singleW)
        }.asRight
    }

  override def concat(items: Seq[BufferedImage]): EitherT[F, InternalServerError, BufferedImage] =
    EitherT.fromEither(concatImagesHorizontally(items))

  override def transform(answer: String): EitherT[F, InternalServerError, BufferedImage]
  = {
    concat(answer.map(c => dataset.pick(c.toString)))
  }
}

object CaptchaToImageTransformer {
  def apply[F[_] : Applicative](dataset: Dataset[BufferedImage]):
  CaptchaToImageTransformer[F] =
    new CaptchaToImageTransformer[F](dataset: Dataset[BufferedImage])
}
