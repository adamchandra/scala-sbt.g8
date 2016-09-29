import sbt._
import Keys._
import org.scalajs.sbtplugin.cross.CrossProject

val monocleVersion = "1.2.1"
val scalazVersion = "7.2.6"
val specs2Version = "3.7"

val testDependencies = libraryDependencies ++= Seq(
  "org.typelevel"  %% "discipline"                % "0.4"          % "test",
  "com.github.julien-truffaut" %% "monocle-law"   % monocleVersion % "test",
  "org.scalaz"     %% "scalaz-scalacheck-binding" % scalazVersion  % "test",
  "org.typelevel"  %% "scalaz-specs2"             % "0.4.0"        % "test",
  "org.specs2"     %% "specs2-core"               % specs2Version  % "test" force(),
  "org.specs2"     %% "specs2-scalacheck"         % specs2Version  % "test" force(),
  // `scalaz-scalack-binding` is built with `scalacheck` 1.12.5 so we are stuck
  // with that version
  "org.scalacheck" %% "scalacheck"                % "1.12.5"       % "test" force()
)

lazy val standardSettings = Seq(
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
  addCompilerPlugin("org.spire-math" %% "kind-projector"   % "0.7.1"),
  addCompilerPlugin("org.scalamacros" % "paradise"         % "2.1.0" cross CrossVersion.full),
  addCompilerPlugin("com.milessabin"  % "si2712fix-plugin" % "1.2.0" cross CrossVersion.full),

  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xfuture",
    // "-Xlint",
    "-Yno-adapted-args",
    "-Yno-imports",
    // "-Ywarn-dead-code", // N.B. doesn't work well with the ??? hole
    // "-Ywarn-numeric-widen",
    "-Ywarn-unused-import",
    "-Ywarn-value-discard"),
  scalacOptions in (Compile,doc) ++= Seq("-groups", "-implicits"),
  scalacOptions in (Test, console) --= Seq("-Yno-imports", "-Ywarn-unused-import"),

  // wartremoverErrors in (Compile, compile) ++= warts, // Warts.all,

  console <<= console in Test, // console alias test:console

  libraryDependencies ++= Seq(
    "com.lihaoyi"                %% "scalatags" % "0.6.0",
    "com.lihaoyi"                %% "acyclic" % "0.1.4" % "provided",
    "com.lihaoyi"                %% "ammonite-ops" % "0.7.7",
    "com.typesafe.play"          %% "play-json" % "2.5.8",
    "com.github.scopt"           %% "scopt" % "3.5.0",
    "com.github.julien-truffaut" %%% "monocle-core" % monocleVersion % "compile, test",
    "org.scalaz"                 %%% "scalaz-core"  % scalazVersion  % "compile, test",
    "com.github.mpilquist"       %%% "simulacrum"   % "0.7.0"        % "compile, test"
  )
)


lazy val root = Project("root", file("."))
  .settings(standardSettings)
  .settings(Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false))
  .settings(name := "root")
  .aggregate(coreJVM, coreJS)

lazy val core = crossProject.in(file("core"))
  .settings(standardSettings)
  .settings(name := "core")
  .jvmSettings(testDependencies)

lazy val coreJVM = core.jvm
lazy val coreJS = core.js
