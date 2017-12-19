organization  := "com.example"

version       := "0.1"

scalaVersion  := "2.12.4"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-Ypartial-unification",
  "-language:postfixOps"
)

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

libraryDependencies ++= {
  val akkaV = "2.5.4"
  val sprayV = "1.3.4"
  val akkaHttpV = "10.0.1"

  Seq(
    "org.typelevel" %% "cats-core" % "1.0.0-RC1",
    "com.chuusai" %% "shapeless" % "2.3.2",
    "com.typesafe.akka"   %% "akka-http-core" % akkaHttpV,
    "com.typesafe.akka"   %% "akka-http"      % akkaHttpV,
    "com.typesafe.akka"   %% "akka-http-spray-json" % akkaHttpV,
    "io.spray"            %%  "spray-json" % sprayV,
    "com.typesafe.akka"   %%  "akka-http-testkit" % akkaHttpV  % "test",
    "org.scalatest"       %%  "scalatest"   % "3.0.1" % "test"
  )
}

