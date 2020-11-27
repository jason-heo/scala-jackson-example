lazy val root = (project in file(".")).
  settings(
    version := "0.1",
    scalaVersion := "2.12.10"
  )

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-core" % "2.11.2",

  // JSR310 의 필요성: https://perfectacle.github.io/2018/01/16/jackson-local-date-time-serialize/
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.11.2",

  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.11.2"
)

assemblyJarName in assembly := "scala-jackson-example.jar"

assemblyMergeStrategy in assembly := {
  case "META-INF/MANIFEST.MF" => MergeStrategy.discard
  case _ => MergeStrategy.first
}

