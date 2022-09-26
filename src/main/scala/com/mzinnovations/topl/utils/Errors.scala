package com.mzinnovations.topl.utils

import cats.data.NonEmptyList
import com.mzinnovations.topl.domain.DijkstraGraph.Node

trait Error extends Throwable
case class FileDecodeError(path: String, cause: String) extends Error {
  override def getMessage: String = s"failed to decode input file $path : $cause"
}
case class ModelLoadError() extends Error {
  override def getMessage: String = "Failed to load Dijkstra graph model"
}

case class InvalidData(messages: NonEmptyList[String]) extends Error {
  override def getMessage: String = s"Invalid data: ${messages.show}"
}

case class RouteNotFound(source: Node, destination: Node) extends Error {
  override def getMessage: String = s"Route between source: ${source.value} and destination: ${destination.value} does not exist"
}

case class InputDataIncorrect() extends Error {
  override def getMessage: String = "Value was not correct. Please try again"
}
case class BestRouteNotFound(source: Node, destination: Node) extends Error {
  override def getMessage: String = s"Best route could not be found between source: ${source.value} and destination: ${destination.value}." +
    s" Check if both nodes exists and if data is provided."
}

