organization  := "com.example"

version       := "0.1"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.4.16"
  val sprayV = "1.3.3"
  val akkaHttpV = "10.0.1"

  Seq(
    "com.typesafe.akka"   %% "akka-http-core" % akkaHttpV,
    "com.typesafe.akka"   %% "akka-http"      % akkaHttpV,
    "com.typesafe.akka"   %% "akka-http-spray-json" % akkaHttpV,
    "io.spray"            %%  "spray-json" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test"
  )
}

Revolver.settings
