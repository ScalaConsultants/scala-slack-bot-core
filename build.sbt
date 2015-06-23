name := "slack-scala-bot-core"

version := "0.1"

scalaVersion := "2.11.5"

organization := "io.scalac"

libraryDependencies ++= {
  val akkaVersion = "2.3.9"
  Seq(
    "org.mockito" % "mockito-core" % "1.10.19",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "io.spray" %% "spray-json" % "1.3.1",
    "io.spray" %% "spray-client" % "1.3.1",
    "io.spray" %% "spray-can" % "1.3.2",
    "com.wandoulabs.akka" %% "spray-websocket" % "0.1.4",
    "joda-time" % "joda-time" % "2.7",
    "org.joda" % "joda-convert" % "1.7",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test",
    "log4j" % "log4j" % "1.2.17",
    "org.slf4j" % "slf4j-api" % "1.7.5",
    "org.slf4j" % "slf4j-log4j12" % "1.7.5",
    "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
    "org.scala-lang" % "scala-compiler" % "2.10.2",
    "org.scala-lang" % "jline" % "2.10.2",
    "org.twitter4j" % "twitter4j-core" % "4.0.0",
    "com.typesafe.slick" %% "slick" % "2.1.0",
    "com.h2database" % "h2" % "1.4.186"
  )
}

resolvers += "spray repo" at "http://repo.spray.io"

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
