package captchaServer.config

import cats.effect.{Blocker, ContextShift, IO}

//import pureconfig.generic.auto._ // if implicit error, add this import
import pureconfig.generic.auto._
import pureconfig._
import pureconfig.module.catseffect.syntax._

case class DBConf(url: String,
                  user: String,
                  password: String,
                  driver: String)

case class ServerConf(
                       host: String,
                       port: Int,
                       captchaLen:Int,
                     )

case class ServiceConf(db: DBConf, server: ServerConf)

object ServiceConf {
  def parseConfig(blocker: Blocker)(implicit cs: ContextShift[IO]): IO[ServiceConf] = {
    ConfigSource.default.loadF[IO, ServiceConf](blocker)
  }
}