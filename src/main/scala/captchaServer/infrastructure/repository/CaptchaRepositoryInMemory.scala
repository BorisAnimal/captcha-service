package captchaServer.infrastructure.repository

import captchaServer.domain.captcha.{Captcha, CaptchaRepositoryAlgebra}
import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import cats.{Id, Monad}

// About Ref:
// https://www.pluralsight.com/tech-blog/scala-cats-effect-ref/
// https://olegpy.com/things-to-store-in-a-ref/


trait IdGenerator[Wrapper[_], T] {
  def next(implicit increment: T => T): Wrapper[T]
}

object IdGenerator {
  implicit val increment: Int => Int = _ + 1

  val simpleGenerator: IdGenerator[Id, Int] = new IdGenerator[Id, Int] {
    private var lastId = 0

    override def next(implicit increment: Int => Int): Id[Int] = {
      lastId = increment(lastId)
      Monad[Id].pure(lastId)
    }
  }

  def atomicGenerator[Wrapper[_] : Sync, T: Numeric]: IdGenerator[Wrapper, T] = new IdGenerator[Wrapper, T] {

    private def ref = Ref[Wrapper].of(Numeric[T].zero)

    override def next(implicit increment: T => T): Wrapper[T] = ref.flatMap(_.modify(x => (increment(x), x)))
  }
}

class CaptchaRepositoryInMemory[Wrapper[_] : Sync] extends CaptchaRepositoryAlgebra[Wrapper] {

  protected val generator: IdGenerator[Wrapper, Int] = IdGenerator.atomicGenerator[Wrapper, Int]

  type MapType = Map[Int, Captcha]

  protected def ref: Wrapper[Ref[Wrapper, MapType]] = Ref[Wrapper].of(Map())

  def create(captcha: Captcha): Wrapper[Captcha] = for {
    newId <- generator.next
    newCaptcha = captcha.copy(id = newId.some)
    _ <- ref.flatMap(_.modify(m => (m.updated(newId, newCaptcha), None)))
  } yield newCaptcha.copy()

  def delete(id: Int): Wrapper[Unit] = for {
    _ <- ref.flatMap(_.modify(m => (m.removed(id), None))) // Here could be error
  } yield ()

  def get(id: Int): Wrapper[Option[Captcha]] = for {
    map <- ref.flatMap(_.get)
    value = map(id)
  } yield value.some
}
