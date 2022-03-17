name := """SOEN6441"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

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

// Compile the project before generating Eclipse files, so
// that generated .scala or .class files for views and routes are present
//EclipseKeys.preTasks := Seq(compile in Compile, compile in Test)

// Java project. Don't expect Scala IDE
//EclipseKeys.projectFlavor := EclipseProjectFlavor.Java

// Use .class files instead of generated .scala files for views and routes
//EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)
