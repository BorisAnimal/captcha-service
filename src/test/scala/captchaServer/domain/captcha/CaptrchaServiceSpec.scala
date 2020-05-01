package captchaServer.domain.captcha

import captchaServer.domain.CaptchaNotFoundError
import cats.effect.IO
import cats.effect.testing.specs2.CatsIO
import cats.implicits._
import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import org.mockito.specs2.Mockito
import org.specs2.mutable.Specification

class CaptrchaServiceSpec extends Specification with CatsIO with Mockito {
  implicit val scheduler: SchedulerService = Scheduler.singleThread("Testing")

  //  def init(): (CaptchaRepositoryAlgebra[Task], AnswerGenerator, CaptchaService[Task], Captcha) = {
  //    val captcha = Captcha("aa", Some(0))
  //    val rep = mock[CaptchaRepositoryAlgebra[Task]]
  //    rep.create(any[Captcha]).returns(captcha.pure[Task])
  //    rep.get(0).returns(captcha.some.pure[Task])
  //    val gen = AnswerGenerator(Set("a"), 2, None)
  //    val service = CaptchaService[Task](rep, gen)
  //
  //    (rep, gen, service, captcha)
  //  }

  "CaptchaService" should {
    "generateCaptcha" in IO {
      //      val (rep, gen, service, captcha) = init()
      val captcha = Captcha("aa", Some(0))
      val rep = mock[CaptchaRepositoryAlgebra[Task]]
      rep.create(any[Captcha]).returns(captcha.pure[Task])
      rep.get(0).returns(captcha.some.pure[Task])
      val gen = AnswerGenerator(Set("a"), 2, None)
      val service = CaptchaService[Task](rep, gen)

      val generated: Captcha = service.generateCaptcha.runSyncUnsafe()
      generated mustEqual captcha
    }

    "check correct Captcha" in IO {
      //      val (rep, gen, service, captcha) = init()

      val captcha = Captcha("aa", Some(0))
      val rep = mock[CaptchaRepositoryAlgebra[Task]]
      rep.create(any[Captcha]).returns(captcha.pure[Task])
      rep.get(0).returns(captcha.some.pure[Task])
      val gen = AnswerGenerator(Set("a"), 2, None)
      val service = CaptchaService[Task](rep, gen)

      service.checkCaptcha(captcha.id.get, captcha.answer).value.runSyncUnsafe() must beRight(true)
    }

    "decline incorrect Captcha" in IO {
      //      val (rep, gen, service, captcha) = init()

      val captcha = Captcha("aa", Some(0))
      val rep = mock[CaptchaRepositoryAlgebra[Task]]
      rep.create(any[Captcha]).returns(captcha.pure[Task])
      rep.get(0).returns(captcha.some.pure[Task])
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
