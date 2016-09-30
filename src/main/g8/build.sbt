import sbt._
import Keys._
import org.scalajs.sbtplugin.cross.CrossProject

enablePlugins(ScalaJSPlugin)
shellPrompt in ThisBuild := Sensible.colorPrompt

val monocleVersion = "1.2.2"
val scalazVersion = "7.2.6"
val specs2Version = "3.8.5"
val testDependencies = libraryDependencies ++= Seq(
  "org.typelevel"  %% "discipline"                % "0.7"          % "test",
  "com.github.julien-truffaut" %% "monocle-law"   % monocleVersion % "test",
  "org.scalaz"     %% "scalaz-scalacheck-binding" % scalazVersion  % "test",
  "org.typelevel"  %% "scalaz-specs2"             % "0.4.0"        % "test",
  "org.specs2"     %% "specs2-core"               % specs2Version  % "test" force(),
  "org.specs2"     %% "specs2-scalacheck"         % specs2Version  % "test" force(),
  // `scalaz-scalack-binding` is built with `scalacheck` 1.12.5 so we are stuck with that version
  "org.scalacheck" %% "scalacheck"                % "1.12.5"       % "test" force()
)

lazy val standardSettings = Seq(

  scalaJSUseRhino in Global := false,
  scalaVersion := "2.11.8",
  logBuffered in Compile := false,
  logBuffered in Test := false,
  outputStrategy := Some(StdoutOutput),
  updateOptions := updateOptions.value.withCachedResolution(true),
  autoCompilerPlugins := true,
  autoAPIMappings := true,
  exportJars := true,
  organization := "org.adamchandra",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    "JBoss repository" at "https://repository.jboss.org/nexus/content/repositories/",
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
    "bintray/non" at "http://dl.bintray.com/non/maven"),

  addCompilerPlugin("org.spire-math" %% "kind-projector"   % "0.9.0"),
  addCompilerPlugin("org.scalamacros" % "paradise"         % "2.1.0" cross CrossVersion.full),
  addCompilerPlugin("com.milessabin"  % "si2712fix-plugin" % "1.2.0" cross CrossVersion.full),


  console <<= console in Test, // console alias test:console

  libraryDependencies ++= Seq(
    "com.slamdata"               %%% "matryoshka-core" % "0.12.0",
    "com.lihaoyi"                %% "scalatags" % "0.6.0",
    "com.lihaoyi"                %% "acyclic" % "0.1.4" % "provided",
    "com.lihaoyi"                %% "ammonite-ops" % "0.7.7",
    // "com.typesafe.play"          %% "play-json" % "2.5.8",
    // "com.github.scopt"           %% "scopt" % "3.5.0",
    "com.github.julien-truffaut" %%% "monocle-core" % monocleVersion % "compile, test",
    "org.scalaz"                 %%% "scalaz-core"  % scalazVersion  % "compile, test",
    "com.github.mpilquist"       %%% "simulacrum"   % "0.9.0"        % "compile, test"
  )
)

lazy val allSettings = standardSettings ++ Sensible.settings

lazy val root = Project("root", file("."))
  .settings(allSettings:_*)
  .settings(Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false))
  .settings(name := "root")
  .aggregate(coreJVM, coreJS)


lazy val core = crossProject.in(file("core"))
  .settings(allSettings:_*)
  .settings(name := "core")
  .jvmSettings(testDependencies)

lazy val coreJVM = core.jvm
lazy val coreJS = core.js
