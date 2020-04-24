package captchaServer

import captchaServer.config.ServiceConf
import captchaServer.domain.captcha.{AnswerGenerator, CaptchaService}
import captchaServer.infrastructure.endpoint.CaptchaEndpoints
import captchaServer.infrastructure.repository.CaptchaRepositoryInMemory
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IOApp, Resource, Timer}
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler.Implicits.global
import pureconfig.{ConfigReader, ConfigSource, Derivation}
import pureconfig.generic.auto._
import cats.effect._
import cats.implicits._
import org.http4s.server.{Router, Server => H4Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import pureconfig.error.ConfigReaderException
import tofu.Raise
import tofu.syntax.raise._


object Service extends TaskApp {
  def parseConfig[F[_] : Sync, A](
                                   implicit
                                   raise: Raise[F, ConfigReaderException[A]],
                                   reader: Derivation[ConfigReader[A]]): F[A] = {
    Sync[F].delay(ConfigSource.default.load[A]) >>=
      (_.leftMap(ConfigReaderException.apply).toRaise[F])
  }


//  def createServer[F[_] : ConcurrentEffect : ContextShift : Timer] = for {
//    conf <- parseConfig[F, ServiceConf]
//    repository = new CaptchaRepositoryInMemory[F]()
//    alphabet = Set("2", "1", "3") // TODO: Make it loading and make implicit for Seq[anyVal] to CaptchaAnswer parsing
//    generator = AnswerGenerator(alphabet, conf.server.captchaLen, None)
//    captchaService = new CaptchaService[F](repository, generator)
//    services = CaptchaEndpoints.endpoints(captchaService)
//    httpApp = Router("/" -> services).orNotFound
//    server <- BlazeServerBuilder[F]
//      .bindHttp(conf.server.port, conf.server.host)
//      .withHttpApp(httpApp)
//        .resource
//  } yield server


  // http://localhost:8080/check?id=1&answer=1337
  // http://localhost:8080/generate
  override def run(args: List[String]): Task[ExitCode] =
//    createServer[Task].use(_ => Task.never).as(ExitCode.Success)

    for {
      conf <- parseConfig[Task, ServiceConf]
      repository = new CaptchaRepositoryInMemory[Task]()
      alphabet = Set("2", "1", "3") // TODO: Make it loading and make implicit for Seq[anyVal] to CaptchaAnswer parsing
      generator = AnswerGenerator(alphabet, conf.server.captchaLen, None)
      captchaService = new CaptchaService[Task](repository, generator)
      services = CaptchaEndpoints.endpoints(captchaService)
      httpApp = Router("/" -> services).orNotFound
      server <- BlazeServerBuilder[Task]
        .bindHttp(conf.server.port, conf.server.host)
        .withHttpApp(httpApp)
        .resource
          .use(_ => Task.never)
          .as(ExitCode.Success)
      } yield server

}
