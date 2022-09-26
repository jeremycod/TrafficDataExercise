package com.mzinnovations.topl.domain

object TrafficData {
  case class Measurement(startAvenue: String, startStreet: Int, transitTime: Double, endAvenue: String, endStreet: Int)

  case class TrafficMeasurement(measurementTime: Long, measurements: List[Measurement])

  case class TrafficRecords(trafficMeasurements: List[TrafficMeasurement])

}
