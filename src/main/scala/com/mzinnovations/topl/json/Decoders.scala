package com.mzinnovations.topl.json

import io.circe.Decoder
import com.mzinnovations.topl.domain.TrafficData._

object Decoders {

  implicit val decodeMeasurement: Decoder[Measurement] = json =>
    for {
      startAvenue <- json.downField("startAvenue").as[String]
      startStreet <- json.downField("startStreet").as[Int]
      transitTime <- json.downField("transitTime").as[Double]
      endAvenue <- json.downField("endAvenue").as[String]
      endStreet <- json.downField("endStreet").as[Int]
    } yield Measurement(startAvenue, startStreet, transitTime, endAvenue, endStreet)

  implicit val decodeTrafficMeasurements: Decoder[TrafficMeasurement] = json =>
    for {
      mt <- json.downField("measurementTime").as[Long]
      measurements <- json.downField("measurements").as[List[Measurement]]
    } yield TrafficMeasurement(mt, measurements)

  implicit val trafficRecords: Decoder[TrafficRecords] = json =>
    for {
      trafficMeasurements <- json.downField("trafficMeasurements").as[List[TrafficMeasurement]]
    } yield TrafficRecords(trafficMeasurements)

}


