lazy val cpJarsForDocker = taskKey[Unit]("prepare for building Docker image")

val akkaVersion = "2.5.2"

lazy val akka = Seq(
  "com.typesafe.akka" %% "akka-cluster",
  "com.typesafe.akka" %% "akka-cluster-metrics",
  "com.typesafe.akka" %% "akka-slf4j",
  "com.typesafe.akka" %% "akka-remote"
).map(_ % akkaVersion)

name := "akka-seeds"

version := "0.1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.lightbend.akka" %% "akka-management-cluster-http" % "0.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.github.romix.akka" %% "akka-kryo-serialization" % "0.5.1"
) ++ akka

exportJars := true

cpJarsForDocker := {

  val dockerDir = (target in Compile).value / "docker"

  val jar = (packageBin in Compile).value
  IO.copyFile(jar, dockerDir / "app" / jar.name)

  (dependencyClasspath in Compile).value.files.foreach { f => IO.copyFile(f, dockerDir / "libs" / f.name )}

  (mainClass in Compile).value.foreach { content => IO.write( dockerDir / "mainClass", content ) }
  IO.write( dockerDir / "tag", akkaVersion )

  IO.copyFile(baseDirectory.value / "Dockerfile", dockerDir / "Dockerfile")
}

fork in run in Global := true
