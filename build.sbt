name := "akka-typed"

version := "0.1"

scalaVersion := "2.13.3"

val AkkaVersion = "2.6.13"

libraryDependencies ++= Seq (
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test,
  "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.5.3",
  "org.postgresql" % "postgresql" % "42.2.19",
  "org.iq80.leveldb" % "leveldb" % "0.12",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"

)
