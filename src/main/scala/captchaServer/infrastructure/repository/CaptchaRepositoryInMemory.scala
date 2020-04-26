package captchaServer.infrastructure.repository

import captchaServer.domain.captcha.{Captcha, CaptchaRepositoryAlgebra}
import cats.Monad
import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._

import scala.collection.concurrent.TrieMap

// About Ref:
// https://www.pluralsight.com/tech-blog/scala-cats-effect-ref/
// https://olegpy.com/things-to-store-in-a-ref/


trait IdGenerator[Wrapper[_], T] {
  def next(implicit increment: T => T): Wrapper[T]
}

object IdGenerator {
  implicit val incrementInt: Int => Int = _ + 1
  implicit val incrementLong: Long => Long = _ + 1

  def simpleGenerator[Wrapper[_] : Monad, T: Numeric]: IdGenerator[Wrapper, T] = new IdGenerator[Wrapper, T] {
    private var lastId = Numeric[T].zero

    override def next(implicit increment: T => T): Wrapper[T] = {
      val mem = lastId
      lastId = increment(lastId)
      Monad[Wrapper].pure(mem)
    }
  }

  def atomicGenerator[Wrapper[_] : Sync, T: Numeric]: IdGenerator[Wrapper, T] = new IdGenerator[Wrapper, T] {

    private def ref = Ref[Wrapper].of(Numeric[T].zero)

    override def next(implicit increment: T => T): Wrapper[T] = ref.flatMap(_.modify(x => (increment(x), x)))
  }
}

class CaptchaRepositoryInMemory[Wrapper[_] : Sync] extends CaptchaRepositoryAlgebra[Wrapper] {
  import IdGenerator._

  type MapType = TrieMap[Int, Captcha]
  type Id[A] = A
  protected val generator: IdGenerator[Id, Int] = IdGenerator.simpleGenerator
  protected val cache: MapType = new MapType()

  def create(captcha: Captcha): Wrapper[Captcha] = {
    val newId = generator.next
    val newCaptcha = captcha.copy(id = newId.some)
    cache += (newId -> newCaptcha)
    newCaptcha.pure[Wrapper]
  }

  def delete(id: Int): Wrapper[Option[Captcha]] = cache.remove(id).pure[Wrapper]

  def size: Int = cache.size

  def get(id: Int): Wrapper[Option[Captcha]] = cache.get(id).pure[Wrapper]
}
