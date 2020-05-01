package captchaServer.infrastructure.endpoint

import captchaServer.domain.captcha.{AnswerGenerator, Captcha, CaptchaService}
import captchaServer.infrastructure.repository.CaptchaRepositoryInMemory
import cats.effect.IO
import cats.effect.testing.specs2.CatsIO
import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import org.http4s.{HttpRoutes, Request, Response, Uri}
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.specs2.mutable.Specification
import org.http4s.implicits._
import cats.syntax.option._
import cats.implicits._

class CaptchaEndpointsSpec extends Specification
  with CatsIO
  with Http4sDsl[Task]
  with Http4sClientDsl[Task] {
  implicit val scheduler: SchedulerService = Scheduler.singleThread("Testing")


  def init(appendants: Seq[Captcha] = Seq()) = {
    val rep = new CaptchaRepositoryInMemory[Task]()
    appendants.map(rep.create)
    val gen = AnswerGenerator(Set("a"), 2, None)
    val service = CaptchaService[Task](rep, gen)
    val endp = CaptchaEndpoints.endpoints[Task](service)
    Router("/" -> endp).orNotFound
  }

  val captchas = Seq(Captcha("aa", Some(0)), Captcha("aaa", Some(1)), Captcha("", Some(2)))

  "CaptchaEndpoints" should {
    "generateCaptcha" in IO {
      val endpoints = init()

      for {
        request <- GET(Uri.unsafeFromString("/generate"))
        response <- endpoints.run(request)
      } yield {
        response.status shouldEqual Ok
      }
    }

    "checkCaptcha is Ok on correct ids" in IO {
      val endpoints = init(captchas)

      captchas.map(c =>
        for {
          request <- GET(Uri.unsafeFromString(s"/check?id=${c.id.get}&answer=${c.answer}"))
          response <- endpoints.run(request)
        } yield {
          response.status shouldEqual Ok
        }
      ).map(_.runSyncUnsafe())
    }

    "checkCaptcha is Not Ok on incorrect ids" in IO {
      val endpoints = init()

      captchas.map(c =>
        for {
          request <- GET(Uri.unsafeFromString(s"/check?id=${c.id.get}&answer=${c.answer}"))
          response <- endpoints.run(request)
        } yield {
          response.status shouldEqual NotFound
        }
      ).map(_.runSyncUnsafe())
    }
  }
}