package com.mzinnovations.topl.dijkstra

import com.mzinnovations.topl.domain.DijkstraGraph._
import com.mzinnovations.topl.domain.TrafficData.{Measurement, TrafficRecords}
import com.mzinnovations.topl.utils.BestRouteNotFound
import com.mzinnovations.topl.utils.Implicits.WeightWrapper
import scala.annotation.tailrec

object DijkstraGraphOps {

  def buildTrafficDijkstraGraphModel(records: TrafficRecords): Graph[Node] =
    records.trafficMeasurements.flatMap(_.measurements).foldLeft(Map[Node, Set[Node]]()) {
      case (map, Measurement(startAvenue, startStreet, _, endAvenue, endStreet)) =>
        (mkNode(startAvenue, startStreet), mkNode(endAvenue, endStreet)) match {
          case (Right(startNode), Right(endNode)) => map + (startNode -> (map.getOrElse(startNode, Set()) + endNode))
          case _ => map
        }
    }

  def buildTrafficDijkstraWeightsModel(records: TrafficRecords): WeightGraph[Node] =
    records.trafficMeasurements.flatMap(_.measurements).foldLeft(Map[(Node, Node), List[Double]]()) {
      case (map, Measurement(startAvenue, startStreet, transitTime, endAvenue, endStreet)) =>
        (mkNode(startAvenue, startStreet), mkNode(endAvenue, endStreet)) match {
          case (Right(startNode), Right(endNode)) => map + ((startNode, endNode) -> (transitTime :: map.getOrElse((startNode, endNode), List())))
          case _ => map
        }
    }


  def findBestRoute(model: DijkstraModel[Node], source: Node, destination: Node):Either[BestRouteNotFound, BestPath[Node]] = {

    @tailrec
    def findBestRouteTailrec(expanding: Set[Node], visited: Set[Node], costs: Map[Node, (Double, List[Node])]): Map[Node, (Double, List[Node])] = {
      if (expanding.isEmpty) costs
      else {
        val node = expanding.minBy(n => costs(n)._1)
        val neighboursCosts: Map[Node, (Double, List[Node])] = model.graph.getOrElse(node, Set()).map { neighbour =>
          val currentCost = costs.getOrElse(neighbour, (Double.MaxValue, List(neighbour)))
          val neighborCost = model.weightGraph((node, neighbour)).average
          val tentativeCost = costs(node)._1 + neighborCost
          if (tentativeCost < currentCost._1) {
            (neighbour, (tentativeCost, neighbour :: costs(node)._2))
          } else (neighbour, currentCost)
        }.toMap
        val neighboursUnvisited = neighboursCosts.keySet.filterNot(visited)
        findBestRouteTailrec(expanding - node ++ neighboursUnvisited, visited + node, costs ++ neighboursCosts)
      }
    }
    val initialCosts = setInitialCosts(model.graph, source)
    val nodesRoutesMap = findBestRouteTailrec(Set(source), Set(), initialCosts)
    if (nodesRoutesMap.contains(destination)){
      nodesRoutesMap(destination) match {
        case (score: Double, route: List[Node]) => Right(BestPath(score, source :: route.reverse, source, destination))
        case _ => Left(BestRouteNotFound(source, destination))
      }
    } else Left(BestRouteNotFound(source, destination))

  }

  def setInitialCosts(graph: Graph[Node], source: Node): Map[Node, (Double, List[Node])] = graph.keySet.map((_, (Double.MaxValue, List.empty[Node]))).toMap + (source -> (0.0, List.empty[Node]))
}
