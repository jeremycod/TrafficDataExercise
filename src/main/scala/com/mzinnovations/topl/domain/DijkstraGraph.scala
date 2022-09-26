package com.mzinnovations.topl.domain

import cats.implicits._
import com.mzinnovations.topl.utils.InvalidData
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.numeric.Interval.OpenClosed
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype

object DijkstraGraph {
  type Avenue = String Refined MatchesRegex["^[A-Z]{1}"]
  object Avenue extends RefinedTypeOps[Avenue, String]

  type Intersection = String Refined MatchesRegex["^[A-Z][0-9]{1,2}"]
  object Intersection extends RefinedTypeOps[Intersection, String]

  type Street = Int Refined OpenClosed[0,100]
  object Street extends RefinedTypeOps[Street, Int]

  @newtype case class Node(value: Intersection)

  type Graph[T] = Map[T, Set[T]]
  type WeightGraph[T] = Map[(T, T), List[Double]]

  case class DijkstraModel[T](graph: Graph[T], weightGraph: WeightGraph[T])
  case class BestPath[T](averageTransitTime: Double, route: List[T], source: Node, destination: Node)

  def mkNode(av: String, str: Int): Either[InvalidData, Node] = Intersection.from(s"$av$str").toEitherNel.map(Node.apply)
    .leftMap(InvalidData.apply)
  def mkNode(avenue: Avenue, street: Street): Either[InvalidData, Node] = mkNode(avenue.value, street.value)
}
