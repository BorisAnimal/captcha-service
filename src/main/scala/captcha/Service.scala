package captcha

//import cats.effect._, org.http4s._, org.http4s.dsl.io._, scala.concurrent.ExecutionContext.Implicits.global

import captcha.config.ServiceConf
import captcha.controllers.{AnswerChecker, CaptchaGenerator}
import cats.effect.{Blocker, ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._


object Service extends IOApp {


  //  http://localhost:8080/check?id=1&answer=1337
  //
  val services = AnswerChecker.checkerService <+> CaptchaGenerator.captchaService

  val httpApp = Router("/" -> services).orNotFound


  override def run(args: List[String]): IO[ExitCode] =
    for {
      conf <- Blocker[IO].use(ServiceConf.parseConfig)
      server <- BlazeServerBuilder[IO]
        .bindHttp(conf.server.port, conf.server.host)
        .withHttpApp(httpApp)
        .resource
        .use(_ => IO.never)
        .as(ExitCode.Success)
    } yield server

}
