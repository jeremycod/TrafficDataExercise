package com.mzinnovations.topl.utils


import com.mzinnovations.topl.domain.DijkstraGraph.{Avenue, Street}
import cats.data.NonEmptyList
import cats.implicits._
import eu.timepit.refined.api.RefinedTypeOps
import scala.io.StdIn.readLine

trait Command[A, B] {
  val validator: RefinedTypeOps[A, B]
  val message: String
  val errorMessage: String
  def cast(input: String): B
  def convertInput(input: String): Either[NonEmptyList[String], A] = try {
    validator.from(cast(input)).toValidatedNel.toEither
  } catch {
    case e: IllegalArgumentException => Left(NonEmptyList("Error while trying to convert input", List(e.getMessage)))
  }
  def executeCommand: Either[NonEmptyList[String], A] = {
    println(message)
    readLine() match {
      case input: String if(convertInput(input).isRight) =>
        convertInput(input)
      case _ =>
        println(errorMessage)
        executeCommand
    }
  }
}
object Commands {
  def StartAvenueCommand = new Command[Avenue, String] {
    override val message: String = "Please provide start avenue"
    override val validator: RefinedTypeOps[Avenue, String] = Avenue
    override val errorMessage: String = "Wrong input. Start avenue has to be capital letter A-Z. Please try again."
    override def cast(input: String): String = input
  }
  def StartStreetCommand = new Command[Street, Int] {
    override val message: String = "Please provide start street"
    override val validator: RefinedTypeOps[Street, Int] = Street
    override val errorMessage: String = "Wrong input. Start street has to be number between 1 and 99. Please try again."
    override def cast(input: String): Int = input.toInt
  }
  def EndAvenueCommand = new Command[Avenue, String] {
    override val message: String = "Please provide end avenue"
    override val validator: RefinedTypeOps[Avenue, String] = Avenue
    override val errorMessage: String = "Wrong input. Start avenue has to be capital letter A-Z. Please try again."
    override def cast(input: String): String = input
  }
  def EndStreetCommand = new Command[Street, Int] {
    override val message: String = "Please provide end street"
    override val validator: RefinedTypeOps[Street, Int] = Street
    override val errorMessage: String = "Wrong input. Start street has to be number between 1 and 99. Please try again."
    override def cast(input: String): Int = input.toInt
  }
}
