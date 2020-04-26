package captchaServer

import captchaServer.domain.captcha.Captcha
import captchaServer.infrastructure.repository.{CaptchaRepositoryInMemory, IdGenerator}
import cats.Id
import cats.effect.IO
import cats.effect.testing.specs2.CatsIO
import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import org.specs2.mutable.Specification
import cats.syntax.option._

class CaptchaRepositorySpec extends Specification with CatsIO {
  //  Test for generator

  "SimpleIdGenerator" should {
    "increase inner counter" in IO {
      import IdGenerator._
//      implicit val increase: Int => Int = _ + 1
      val gen: IdGenerator[Id, Int] = IdGenerator.simpleGenerator
      gen.next mustEqual gen.next - 1
    }
  }

  "CaptchaRepositoryInMemory" should {
    "keep created elements" in IO {

      implicit val scheduler: SchedulerService = Scheduler.singleThread("Testing")

      val total = 3
      val gen = new CaptchaRepositoryInMemory[Task]

      val captchas = for (i <- 1 to total)
        yield Captcha(answer = i.toString, id = Some(i - 1)).some
      for (k <- captchas)
        gen.create(k.get)
      val stored = (0 until total).map(gen.get(_).runSyncUnsafe())

      gen.size mustNotEqual 0

      captchas mustEqual stored
    }
  }
}
