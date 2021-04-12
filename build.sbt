name := "akka-typed"

version := "0.1"

scalaVersion := "2.13.3"

val AkkaVersion = "2.6.13"

libraryDependencies ++= Seq (
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream-typed" % AkkaVersion,

"com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % AkkaVersion,

  "org.iq80.leveldb" % "leveldb" % "0.12",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",

  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
