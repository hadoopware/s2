import sbt._
import Keys._

import sbtassembly.Plugin._
import AssemblyKeys._ 

object build extends Build {

//  mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => {
//      case PathList("META-INF", "maven","jline","jline", ps) if ps.startsWith("pom") => MergeStrategy.discard
//      case x => old(x)
//    }
//  }

  val Settings = Defaults.defaultSettings ++ Seq(
    retrieveManaged := true,
    version := "0.0.1-SNAPSHOT",
    organization := "com.syspulse.s2",
    scalaVersion := "2.11.8",
    //crossScalaVersions := Seq("2.10.0", "2.11.0"),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-optimize", "-feature", "-Yinline-warnings", "-language:existentials", "-language:implicitConversions", "-language:higherKinds", "-language:reflectiveCalls", "-language:postfixOps"),
    javacOptions ++= Seq("-target", "1.8", "-source", "1.8"),
//    manifestSetting,
//    publishSetting,
    resolvers ++= Seq(Opts.resolver.sonatypeSnapshots, Opts.resolver.sonatypeReleases),
    crossVersion := CrossVersion.binary,
    classpathTypes += "maven-plugin",
    resolvers ++= Seq(
      "spray repo"         at "http://repo.spray.io/",
      "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases/",
      "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      "typesafe repo"      at "http://repo.typesafe.com/typesafe/releases/"
    )
  )


  lazy val root = Project(
    id = "s2",
    base = file("."),
    settings =  Settings //++ assemblySettings
  ).aggregate(s2_core, s2_search,s2_shell,s2_similarity)
//   .settings(
//      mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
//      {
//        case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
//        case x => MergeStrategy.first
//      }
//      }
//    )

  lazy val s2_core = Project(
    id = "s2-core",
    base = file("s2-core"),
    settings = Settings ++ assemblySettings ++ Seq(
      libraryDependencies ++= Seq(
		"org.jline" 					%  "jline" 					% "3.5.1",
        "com.typesafe.akka"   			%% "akka-actor"        		% "2.3.9",
        "com.github.nscala-time" 		%% "nscala-time"       		% "2.6.0",
        "com.typesafe.scala-logging" 	%% "scala-logging" 			% "3.5.0", 
        "ch.qos.logback" 				%  "logback-classic" 		% "1.2.3",
        "org.scalatest" 				%% "scalatest" 				% "3.0.1" % "test",  
		    "com.googlecode.javaewah" 		%  "JavaEWAH" 				% "0.7.3"
      )
    )
  )

  lazy val s2_similarity = Project(
    id = "s2-similarity",
    base = file("s2-similarity"),

    settings = Settings ++ assemblySettings ++ Seq(
      libraryDependencies ++= Seq(
        "org.deeplearning4j" 			% "deeplearning4j-core" 			% "0.9.1",
        "org.deeplearning4j" 			% "deeplearning4j-nlp" 				% "0.9.1",
        "org.nd4j" 						% "nd4j-native" 					% "0.9.1" classifier "" classifier "linux-x86_64",
        "org.nd4j" 						% "nd4j-native-platform" 			% "0.9.1",
         "com.typesafe.scala-logging" 	%% "scala-logging" 					% "3.5.0",
        "ch.qos.logback" 				% "logback-classic" 				% "1.2.3",
        "com.github.nscala-time" 		%% "nscala-time"       				% "2.6.0"
      )
    )
  ).dependsOn(s2_core)


  lazy val s2_search = Project(
    id = "s2-search",
    base = file("s2-search"),
    settings = Settings ++ assemblySettings ++ Seq(
      libraryDependencies ++= Seq(
        "com.typesafe.akka"   			%% "akka-actor"        		% "2.3.9",
        "com.github.nscala-time" 		%% "nscala-time"       		% "2.6.0",
        "com.typesafe.scala-logging" 	%% "scala-logging" 			% "3.5.0", 
        "ch.qos.logback" 				%  "logback-classic" 		% "1.2.3",
        "org.scalatest" 				%% "scalatest" 				% "3.0.1" % "test",  
		"com.googlecode.javaewah" 		%  "JavaEWAH" 				% "0.7.3"
      )
    )
  ).dependsOn(s2_core)

  lazy val s2_shell = Project(
    id = "s2-shell",
    base = file("s2-shell"),
    settings = Settings ++ assemblySettings ++ Seq(
      libraryDependencies ++= Seq(
		"org.jline" 					%  "jline" 					% "3.5.1",
        "com.github.nscala-time" 		%% "nscala-time"       		% "2.6.0",
        "com.typesafe.scala-logging" 	%% "scala-logging" 			% "3.5.0", 
        "ch.qos.logback" 				%  "logback-classic" 		% "1.2.3"
      )
    )
  ).dependsOn(s2_core,s2_search)

}