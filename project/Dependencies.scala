
import sbt._
import sbt.ModuleID

object Dependencies {
  object Versions {
    val circe = "0.14.1"
    val newtype  = "0.4.4"
    val scalatest = "2.2.0"
    val scalacheck = "1.17.0"
  }
  object Libraries {
    def circe(artifact: String): ModuleID = "io.circe" %% artifact % Versions.circe

    val circeCore = circe("circe-core")
    val circeGeneric = circe("circe-generic")
    val circeParser = circe("circe-parser")
    val circeRefined = circe("circe-refined")
    val circeTesting = circe("circe-testing") % Test
    val newtype  = "io.estatico"       %% "newtype"   % Versions.newtype
    val disciplineScalatest = "org.typelevel" %% "discipline-scalatest" % Versions.scalatest % Test
    val scalacheck = "org.scalacheck" %% "scalacheck" % Versions.scalacheck % Test

  }

  val rootDependencies = Seq(
    Libraries.circeCore,
    Libraries.circeParser,
    Libraries.circeGeneric,
    Libraries.circeRefined,
    Libraries.circeTesting,
    Libraries.newtype,
    Libraries.disciplineScalatest,
    Libraries.scalacheck
  )
}
