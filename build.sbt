name := "captchaServer-service"

version := "0.1"

scalaVersion := "2.13.1"

lazy val http4sVersion = "0.21.2"
lazy val pureconfigVersion = "0.12.3"
val CirceVersion = "0.13.0"
val CirceGenericExVersion = "0.13.0"
val CirceConfigVersion = "0.8.0"
val DoobieVersion = "0.9.0"
val EnumeratumCirceVersion = "1.5.23"


//// Only necessary for SNAPSHOT releases
//resolvers += Resolver.sonatypeRepo("snapshots")


addCompilerPlugin(
  ("org.typelevel" %% "kind-projector" % "0.11.0").cross(CrossVersion.full),
)



libraryDependencies ++= Seq(
  "io.monix" %% "monix" % "3.1.0",


  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-literal" % CirceVersion,
  "io.circe" %% "circe-generic-extras" % CirceGenericExVersion,
  "io.circe" %% "circe-parser" % CirceVersion,
  "io.circe" %% "circe-config" % CirceConfigVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,



  "org.typelevel" %% "cats-core" % "2.1.1",

  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,

  "org.scalatest" %% "scalatest" % "3.1.0" % Test,

  "com.github.pureconfig" %% "pureconfig" % pureconfigVersion,
  "com.github.pureconfig" %% "pureconfig-cats" % pureconfigVersion,
  "com.github.pureconfig" %% "pureconfig-cats-effect" % pureconfigVersion,

  "com.typesafe.slick" %% "slick" % "3.3.2",

  "ru.tinkoff" %% "tofu" % "0.7.4",
)

//scalacOptions ++= Seq("-Ypartial-unification")
