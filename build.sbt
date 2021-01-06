name := "ReqRes"

version := "0.1"

scalaVersion := "2.13.4"

lazy val settings = Seq(
  libraryDependencies ++= Seq (
    "org.scala-lang" % "scala-reflect" % scalaVersion.value
  )
)

//lazy val paradise = Seq(
//  resolvers += Resolver.sonatypeRepo("releases"),
//    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
//)

lazy val ReqRes: Project = project in file(".")

lazy val reqres: Project = (project in file("./reqres")).settings(settings).aggregate(json, macros)

lazy val caller: Project = (project in file("./reqres/caller")).settings(settings).dependsOn(json, macros)

lazy val json: Project = (project in file("./reqres/json")).settings(settings)

lazy val macros: Project = (project in file("./reqres/macros")).settings(settings).dependsOn(json)

lazy val reqer: Project = (project in file("reqer")).dependsOn(reqres ,json, macros, caller)