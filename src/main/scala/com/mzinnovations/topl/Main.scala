package com.mzinnovations.topl

import com.mzinnovations.topl.dijkstra.DijkstraGraphOps
import com.mzinnovations.topl.domain.DijkstraGraph
import com.mzinnovations.topl.domain.DijkstraGraph.{DijkstraModel, Node}
import com.mzinnovations.topl.domain.TrafficData.TrafficRecords
import com.mzinnovations.topl.json.Decoders.trafficRecords
import com.mzinnovations.topl.json.JsonFileReader
import com.mzinnovations.topl.utils.{BestRouteNotFound, Commands, FileDecodeError, LocalResources}
import com.mzinnovations.topl.utils.Implicits.CaseInsensitiveRegex
import java.io.File
import scala.io.StdIn.readLine

object Main {
  def main(args: Array[String]): Unit = {
    appInitContext()
  }


  def appInitContext(): Unit = {
    println("Please select sample traffic data by providing full path to your file." +
      "\nIf you want to use default sample (sample-data.json), press ENTER. " +
      "\nYou can close application by entering \"Q\". ")
    readLine() match {
      case ci"q"  =>
        println("Good bye!")
        System.exit(0)
      case input: String if input.isEmpty => loadDataModelContext(None)
      case path: String => loadDataModelContext(Some(path))
      case _ => println("Unknown command. Please try again.")
    }
  }

  def loadDataModelContext(path: Option[String]) = {
    loadDataModelFromFile(path) match {
      case Right(dataModel) => routesSearchContext(dataModel)
      case Left(_) =>
        println("Error happened. Check your data model file and try again.")
        appInitContext()
    }
  }

  def routesSearchContext(dataModel: DijkstraModel[Node]): Unit = {
    val res = for {
      startAvenue <- Commands.StartAvenueCommand.executeCommand
      startStreet <- Commands.StartStreetCommand.executeCommand
      endAvenue <- Commands.EndAvenueCommand.executeCommand
      endStreet <- Commands.EndStreetCommand.executeCommand
      startNode <- DijkstraGraph.mkNode(startAvenue, startStreet)
      endNode <- DijkstraGraph.mkNode(endAvenue, endStreet)
      result <- DijkstraGraphOps.findBestRoute(dataModel, startNode, endNode)
    } yield (result)
    res match {
      case Right(result) =>
        println(s"Optimal route between source: ${result.source} and destination: ${result.destination} is:")
        println(s"Average time spent on this route is: ${result.averageTransitTime}")
        println(s"${result.route.mkString(" -> ")}")
      case Left(nfe: BestRouteNotFound) =>
        println(s"No route between nodes ${nfe.source} and ${nfe.destination}. Check if selected node exist.")
      case Left(_) =>
        println(s"No route between found between selected nodes. Check if selected node exist.")
    }
    println("===============================")
    println("Try again!")
    routesSearchContext(dataModel)

  }

  def loadDataModelFromFile(path: Option[String]): Either[FileDecodeError, DijkstraModel[Node]] = {
    val sampleFile = "sample-data.json"
    val recordsFile = if (path.isEmpty){
      LocalResources.resourceToLocal(sampleFile)
    } else new File(path.getOrElse(getClass.getResource(s"/$sampleFile").getPath))

    JsonFileReader.readAs[TrafficRecords](recordsFile)
    match {
      case Right(trafficRecords) => {
        val graph = DijkstraGraphOps.buildTrafficDijkstraGraphModel(trafficRecords)
        val weightGraph = DijkstraGraphOps.buildTrafficDijkstraWeightsModel(trafficRecords)
        Right(DijkstraModel(graph, weightGraph))
      }
      case Left(er) =>
        println(s"Error while loading file")
        println(s"${er.cause} ${er.path}")
        Left(er)

    }
  }
}
