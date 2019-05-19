import _root_.sbt.Keys._

name := "vars"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions := Seq(
  "-encoding",
  "utf8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-target:jvm-1.8",
  "-Ypartial-unification",
  "-language:_",
  "-Xexperimental"
)

val catsVersion       = "1.6.0"
val catsEffectVersion = "1.1.0"


libraryDependencies += "org.typelevel" %% "cats-effect" % catsEffectVersion