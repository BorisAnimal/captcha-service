package captcha

import pureconfig._
import pureconfig.generic.auto._


import pureconfig.module.catseffect.syntax._
import cats.effect.{ Blocker, ContextShift, IO }

case class DBConf(url: String,
                  user: String,
                  password: String,
                  driver: String)

case class ServerConf(
                       host: String,
                       port: Int
                     )

case class ServiceConf(db: DBConf, server: ServerConf)

object ServiceConf {
  def parseConfig(blocker: Blocker)(implicit cs: ContextShift[IO]): IO[ServiceConf] = {
    ConfigSource.default.loadF[IO, ServiceConf](blocker)
  }
}