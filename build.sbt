name := """SOEN6441"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava).configs(Javadoc).settings(javadocSettings: _*)

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  guice,
  ws,
  caffeine,
  "com.typesafe.akka" %% "akka-testkit" % "2.6.18" % Test,
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test
)

javaOptions in Test ++= Seq(
  "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9998",
  "-Xms512M",
  "-Xmx1536M",
  "-Xss1M",
  "-XX:MaxPermSize=384M"
)

lazy val Javadoc = config("genjavadoc") extend Compile

lazy val javadocSettings = inConfig(Javadoc)(Defaults.configSettings) ++ Seq(
  addCompilerPlugin("com.typesafe.genjavadoc" %% "genjavadoc-plugin" % "0.18" cross CrossVersion.full),
  scalacOptions += s"-P:genjavadoc:out=${target.value}/java",
  Compile / packageDoc := (Javadoc / packageDoc).value,
  Javadoc / sources :=
    (target.value / "java" ** "*.java").get ++
    (Compile / sources).value.filter(_.getName.endsWith(".java")),
  Javadoc / javacOptions := Seq("-private"),
  Javadoc / packageDoc / artifactName := ((sv, mod, art) =>
    "" + mod.name + "_" + sv.binary + "-" + mod.revision + "-javadoc.jar")
)

