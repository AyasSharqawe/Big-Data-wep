ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "final One",
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-native" % "4.0.3",
      "org.apache.kafka" % "kafka-clients" % "3.1.0",
      "com.typesafe.akka" %% "akka-actor" % "2.6.18",
      "com.typesafe.akka" %% "akka-stream" % "2.6.18",
      "com.typesafe.akka" %% "akka-slf4j" % "2.6.18",
      "com.typesafe.akka" %% "akka-http" % "10.2.7",
        "org.mongodb.scala" %% "mongo-scala-driver" % "4.5.0",
        "io.circe" %% "circe-core" % "0.14.1",
        "io.circe" %% "circe-generic" % "0.14.1",
        "io.circe" %% "circe-parser" % "0.14.1",
      "edu.stanford.nlp" % "stanford-corenlp" % "4.5.4",
      "edu.stanford.nlp" % "stanford-corenlp" % "4.5.4" classifier "models",
      "com.typesafe.akka" %% "akka-http" % "10.2.7",

    )
  )
dependencyOverrides += "com.typesafe.play" %% "play-json" % "2.9.2"

resolvers += Resolver.typesafeRepo("releases")
