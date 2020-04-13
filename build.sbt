name := "captcha-service"

version := "0.1"

scalaVersion := "2.13.1"

val http4sVersion = "0.21.2"
//// Only necessary for SNAPSHOT releases
//resolvers += Resolver.sonatypeRepo("snapshots")


addCompilerPlugin(
  ("org.typelevel" %% "kind-projector" % "0.11.0").cross(CrossVersion.full),
)



libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.1.1",

  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,

  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "com.github.pureconfig" %% "pureconfig" % "0.12.3",
  "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.12.3"
)

//scalacOptions ++= Seq("-Ypartial-unification")
