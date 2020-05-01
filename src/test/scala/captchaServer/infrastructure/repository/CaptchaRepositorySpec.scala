package captchaServer.infrastructure.repository

import captchaServer.domain.captcha.Captcha
import cats.Id
import cats.effect.IO
import cats.effect.testing.specs2.CatsIO
import cats.syntax.option._
import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import org.specs2.mutable.Specification

class CaptchaRepositorySpec extends Specification with CatsIO {
  //  Test for generator
  "SimpleIdGenerator" should {
    "increase inner counter" in IO {
      import IdGenerator._

      val gen: IdGenerator[Id, Int] = IdGenerator.simpleGenerator
      gen.next mustEqual gen.next - 1
    }
  }


  "CaptchaRepositoryInMemory" should {
    "return None when get wrong id" in IO {
      import CaptchaRepositorySpec._

      val rep = new CaptchaRepositoryInMemory[Task]

      rep.get(total + 1).runSyncUnsafe() must beNone
    }

    "keep created elements" in IO {
      import CaptchaRepositorySpec._

      val rep = new CaptchaRepositoryInMemory[Task]
      // Add elements into repository
      for (k <- captchas)
        rep.create(k.get)

      // Take elements from repository
      val stored: Seq[Option[Captcha]] = (0 until total).map(rep.get(_).runSyncUnsafe())

      val withIds: Seq[Option[Captcha]] = (captchas zip stored).map {
        case (Some(c), Some(s)) => c.copy(id = s.id).some
        case _ => None
      }

      val allAreSome = stored.map(_.isDefined).reduce(_ && _)

      allAreSome mustEqual true
      withIds mustEqual stored

      val allIdsAreSome = stored.map(_.get.id.isDefined).reduce(_ && _)
      allIdsAreSome mustEqual true
    }

    "return correct size" in IO {
      import CaptchaRepositorySpec._

      val rep = new CaptchaRepositoryInMemory[Task]

      rep.size mustEqual 0

      // Add elements into repository
      for (k <- captchas)
        rep.create(k.get)

      rep.size mustEqual total
    }

    "delete element correctly" in IO {
      import CaptchaRepositorySpec._

      val rep = new CaptchaRepositoryInMemory[Task]
      // Add elements into repository
      for (k <- captchas)
        rep.create(k.get)

      val actual = captchas(0).get.copy(id = Some(0)).some

      rep.get(0).runSyncUnsafe() mustEqual actual
      rep.delete(0).runSyncUnsafe() mustEqual actual
      rep.get(0).runSyncUnsafe() must beNone
    }


  }
}

object CaptchaRepositorySpec {
  implicit val scheduler: SchedulerService = Scheduler.singleThread("Testing")

  val total = 3

  // Initialize elements
  val captchas: Seq[Option[Captcha]] = for (i <- 0 until total)
    yield Captcha(answer = i.toString, id = None).some
}