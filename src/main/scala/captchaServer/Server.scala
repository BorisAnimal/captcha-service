package captchaServer

import captchaServer.config.ServiceConf
import captchaServer.domain.captcha.{AnswerGenerator, CaptchaService, CaptchaToImageTransformer}
import captchaServer.infrastructure.dataset.DatasetLoader
import captchaServer.infrastructure.endpoint.CaptchaEndpoints
import captchaServer.infrastructure.repository.CaptchaRepositoryInMemory
import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}
import pureconfig.{ConfigReader, ConfigSource, Derivation}
import pureconfig.generic.auto._
import cats.effect._
import cats.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import pureconfig.error.ConfigReaderException
import tofu.Raise
import tofu.syntax.raise._
import tofu.logging.Logs
// If configs raise miserable errors on implicits, import erased: import pureconfig.generic.auto._
import pureconfig.generic.auto._

object Server extends TaskApp {
  def parseConfig[F[_] : Sync, A](
                                   implicit
                                   raise: Raise[F, ConfigReaderException[A]],
                                   reader: Derivation[ConfigReader[A]]): F[A] = {
    Sync[F].delay(ConfigSource.default.load[A]) >>=
      (_.leftMap(ConfigReaderException.apply).toRaise[F])
  }


  // http://localhost:8080/check?id=0&answer=1337
  // http://localhost:8080/generate
  override def run(args: List[String]): Task[ExitCode] =
    for {
      conf <- parseConfig[Task, ServiceConf] // This config parsing make ugly whole comprehension
      repository = new CaptchaRepositoryInMemory[Task]()
      dataset = DatasetLoader.loadDataset(conf.dataset.dir)
      transformer = CaptchaToImageTransformer[Task](dataset)
      generator = AnswerGenerator(dataset.getAlphabet.toSet, conf.server.captchaLen, None)
      captchaService = CaptchaService[Task](repository, generator)
      services = CaptchaEndpoints.endpoints(captchaService, transformer)
      httpApp = Router("/" -> services).orNotFound
      server <- BlazeServerBuilder[Task]
        .bindHttp(conf.server.port, conf.server.host)
        .withHttpApp(httpApp)
        .resource
        .use(_ => Task.never)
        .as(ExitCode.Success)
    } yield server

}
