package captchaServer.domain.captcha

import captchaServer.domain.CaptchaNotFoundError
import cats.effect.IO
import cats.effect.testing.specs2.CatsIO
import cats.implicits._
import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import org.specs2.mutable.Specification

class CaptrchaServiceSpec extends Specification with CatsIO {
  implicit val scheduler: SchedulerService = Scheduler.singleThread("Testing")

  "CaptchaService" should {
    "generateCaptcha" in IO {
      //      val (rep, gen, service, captcha) = init()
      val captcha = Captcha("aa", Some(0))
      
      val rep = new CaptchaRepositoryAlgebra[Task] {
        override def create(captcha: Captcha): Task[Captcha] = captcha.copy(id = Some(0)).pure[Task]
        override def delete(id: Int): Task[Option[Captcha]] = ???
        override def get(id: Int): Task[Option[Captcha]] = ???
        override def size: Int = ???
      }

      val gen = AnswerGenerator(Set("a"), 2, None)
      val service = CaptchaService[Task](rep, gen)

      val generated: Captcha = service.generateCaptcha.runSyncUnsafe()
      generated mustEqual captcha
    }

    "check correct Captcha" in IO {
      //      val (rep, gen, service, captcha) = init()

      val captcha = Captcha("aa", Some(0))
      
      val rep = new CaptchaRepositoryAlgebra[Task] {
        override def create(captcha: Captcha): Task[Captcha] = ???
        override def delete(id: Int): Task[Option[Captcha]] = ???
        override def get(id: Int): Task[Option[Captcha]] = id match {
          case 0 => captcha.some.pure[Task]
          case _ => None.pure[Task]
        }
        override def size: Int = ???
      }
      
      val gen = AnswerGenerator(Set("a"), 2, None)
      val service = CaptchaService[Task](rep, gen)

      service.checkCaptcha(captcha.id.get, captcha.answer).value.runSyncUnsafe() must beRight(true)
    }

    "decline incorrect Captcha" in IO {
      //      val (rep, gen, service, captcha) = init()

      val captcha = Captcha("aa", Some(0))
      
      val rep = new CaptchaRepositoryAlgebra[Task] {
        override def create(captcha: Captcha): Task[Captcha] = ???
        override def delete(id: Int): Task[Option[Captcha]] = ???
        override def get(id: Int): Task[Option[Captcha]] = id match {
          case 0 => captcha.some.pure[Task]
          case _ => None.pure[Task]
        }
        override def size: Int = ???
      }
      
      val gen = AnswerGenerator(Set("a"), 2, None)
      val service = CaptchaService[Task](rep, gen)

      // wrong id
      val wrongIdResponse = service.checkCaptcha(1, captcha.answer).value.runSyncUnsafe()
      wrongIdResponse must beLeft(CaptchaNotFoundError(1))

      // wrong answer
      val wrongAnswerResponse = service.checkCaptcha(0, "").value.runSyncUnsafe()
      wrongAnswerResponse must beRight(false)
    }
  }
}
