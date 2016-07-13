name := "protected-resource"

organization := "com.github.ybr"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.6")

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.1",
  "org.specs2" %% "specs2-core" % "3.8.4" % "test"
)

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials("Sonatype Nexus Repository Manager",
                            "oss.sonatype.org",
                            System.getenv.get("SONATYPE_USER"),
                            System.getenv.get("SONATYPE_PASS"))

pomExtra := (
  <url>https://github.com/ybr/protected-resource</url>
  <licenses>
    <license>
      <name>MIT</name>
      <url>https://opensource.org/licenses/MIT</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:ybr/protected-resource.git</url>
    <connection>scm:git:git@github.com:ybr/protected-resource.git</connection>
  </scm>
  <developers>
    <developer>
      <id>ybr</id>
      <name>Yohann Bredoux</name>
      <url>http://ybr.github.io</url>
    </developer>
  </developers>)