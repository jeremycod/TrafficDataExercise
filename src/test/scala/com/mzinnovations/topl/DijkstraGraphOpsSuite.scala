package com.mzinnovations.topl

import cats.implicits._
import com.mzinnovations.topl.dijkstra.DijkstraGraphOps
import com.mzinnovations.topl.domain.DijkstraGraph._
import com.mzinnovations.topl.domain.TrafficData.{Measurement, TrafficMeasurement, TrafficRecords}
import com.mzinnovations.topl.utils.{BestRouteNotFound, InvalidData}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

object TestFixture {
  private val startAvenues = ('A' to 'D')
  private val startStreets = (1 to 4)
  private val endAvenues = ('B' to 'E')
  private val endStreets = (2 to 5)
  private val startNodes = startAvenues.zip(startStreets)
  private val endNodes = endAvenues.zip(endStreets)
  val measurements: List[Measurement] = startNodes.zip(endNodes).map{
    case (st, end) => Measurement(st._1.toString, st._2, end._2 * 10, end._1.toString, end._2)
  }.toList

  val trafficMeasurement: TrafficMeasurement = TrafficMeasurement(100, measurements)
  val trafficRecords: TrafficRecords = TrafficRecords(List(trafficMeasurement))
}

class DijkstraGraphOpsSuite extends AnyFunSpec with Matchers with ScalaCheckPropertyChecks {

  private def mkNode(avenueStreet: String): Either[InvalidData, Node] = Intersection.from(avenueStreet).toEitherNel.map(Node.apply)
    .leftMap(InvalidData.apply)

  private def validateModelNodes(startNode: String, endNode: String, model: Graph[Node]) =
   for {
     sn <- mkNode(startNode)
     en <- mkNode(endNode)
   } yield model.get(sn).exists(_.head == en)

  private def validateModelNodesWeights(startNode: String, endNode: String, weight: Double, model: WeightGraph[Node]) =
    for {
      sn <- mkNode(startNode)
      en <- mkNode(endNode)
    } yield model.get((sn, en)).exists(_.head == weight)

  private def findAndValidateBestRoutes(startNode: String, endNode: String, model: DijkstraModel[Node]) = {
    for {
      st <- mkNode(startNode)
      end <- mkNode(endNode)
      bestRoute <-  DijkstraGraphOps.findBestRoute(model, st, end)
    } yield bestRoute
  }

  it("Builds correct graph model") {
    val model = DijkstraGraphOps.buildTrafficDijkstraGraphModel(TestFixture.trafficRecords)
    assert(model.size == 4)
    val expectedPairs = List(("A1", "B2"), ("B2", "C3"), ("C3", "D4"), ("D4","E5"))
    val result = expectedPairs.foldLeft(true)((res, pair) => {
      validateModelNodes(pair._1, pair._2, model) match {
        case Right(value) => value && res
        case Left(_) => false
      }
    })
    assert(result)
  }

  it("Builds correct weight model") {
    val model = DijkstraGraphOps.buildTrafficDijkstraWeightsModel(TestFixture.trafficRecords)
    assert(model.size == 4)
    val expectedPairs = List(("A1", "B2", 20.0), ("B2", "C3", 30.0), ("C3", "D4", 40.0), ("D4","E5", 50.0))
    val result = expectedPairs.foldLeft(true)((res, pair) => {
      validateModelNodesWeights(pair._1, pair._2, pair._3, model) match {
        case Right(value) => value && res
        case Left(_) => false
      }
    })
    assert(result)
  }

  it("Finds best routes between nodes"){
    val graph = DijkstraGraphOps.buildTrafficDijkstraGraphModel(TestFixture.trafficRecords)
    val weightGraph = DijkstraGraphOps.buildTrafficDijkstraWeightsModel(TestFixture.trafficRecords)
    val model = DijkstraModel(graph, weightGraph)
    val expectedPairs = List(
      ("A1", "C3", "A1, B2, C3", 50.0),
      ("A1", "B2", "A1, B2", 20.0),
      ("A1", "D4", "A1, B2, C3, D4", 90.0),
      ("A1", "E5", "A1, B2, C3, D4, E5", 140.0))
    val result = expectedPairs.foldLeft(true)((res, pair) => {
      findAndValidateBestRoutes(pair._1, pair._2, model) match {
        case Right(value) => {
          value.route.mkString(", ") == pair._3 &&
            value.averageTransitTime == pair._4 &&
            res
        }
        case Left(_) => false
      }
    })
    assert(result)
  }

  it("Returns route not found when destination doesn't exist") {
    val graph = DijkstraGraphOps.buildTrafficDijkstraGraphModel(TestFixture.trafficRecords)
    val weightGraph = DijkstraGraphOps.buildTrafficDijkstraWeightsModel(TestFixture.trafficRecords)
    val model = DijkstraModel(graph, weightGraph)
    val startNode = "A1"
    val endNode = "F6"
    val result = for {
      st <- mkNode(startNode)
      end <- mkNode(endNode)
      bestRoute <-  DijkstraGraphOps.findBestRoute(model, st, end)
    } yield bestRoute
    assert(result.isLeft)
    assert(result match {
      case Left(BestRouteNotFound(_,_)) => true
      case _ => false
    })
  }

}
