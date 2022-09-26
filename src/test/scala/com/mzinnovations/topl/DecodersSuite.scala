package com.mzinnovations.topl

import cats.implicits.catsSyntaxEitherId
import cats.kernel.Eq
import com.mzinnovations.topl.domain.TrafficData.{Measurement, TrafficMeasurement, TrafficRecords}
import com.mzinnovations.topl.json.Decoders._
import io.circe.Encoder
import io.circe.generic.auto.exportEncoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax._
import io.circe.testing.ArbitraryInstances
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks


object Encoders {
  implicit val encodeMeasurement: Encoder[Measurement] = deriveEncoder[Measurement]
  implicit val encodeTrafficMeasurement: Encoder[TrafficMeasurement] = deriveEncoder[TrafficMeasurement]
  implicit val encodeTrafficRecords: Encoder[TrafficRecords] = deriveEncoder[TrafficRecords]
}

object DecodersImplicits extends ArbitraryInstances {
  private lazy val genMeasurement: Gen[Measurement] =
    for {
      sA <- Gen.pick(1, 'A' to 'Z').map(_.mkString)
      sS <- Gen.choose(1, 99)
      eA <- Gen.pick(1, 'A' to 'Z').map(_.mkString)
      eS <- Gen.choose(1, 99)
      time <- Gen.choose[Double](0, Double.MaxValue)
    } yield Measurement(sA, sS, time, eA, eS)

  private lazy val genTrafficMeasurement: Gen[TrafficMeasurement] =
    for {
      mt <- Gen.choose(1, Long.MaxValue)
      measurements <- Gen.listOf(genMeasurement)

    } yield TrafficMeasurement(mt, measurements)

  private lazy val genTrafficRecords: Gen[TrafficRecords] =
    for {
      trafficMeassurements <- Gen.listOf(genTrafficMeasurement)
    } yield TrafficRecords(trafficMeassurements)

  implicit val eqMeasurement: Eq[Measurement] = Eq.fromUniversalEquals
  implicit val arbitraryMeasurment: Arbitrary[Measurement] = Arbitrary(genMeasurement)

  implicit val eqTrafficMeasurement: Eq[TrafficMeasurement] = Eq.fromUniversalEquals
  implicit val arbitraryTrafficMeasurment: Arbitrary[TrafficMeasurement] = Arbitrary(genTrafficMeasurement)

  implicit val eqTrafficRecords: Eq[TrafficRecords] = Eq.fromUniversalEquals
  implicit val arbitraryTraffic: Arbitrary[TrafficRecords] = Arbitrary(genTrafficRecords)
}

class DecodersSuite extends AnyFunSpec with Matchers with ScalaCheckPropertyChecks {
  import DecodersImplicits._
  it("Decode Measurement") {
    forAll { m: Measurement =>
      m.asJson.as[Measurement] shouldEqual m.asRight
    }
  }
  it("Decode TrafficMeasurement") {
    forAll { m: TrafficMeasurement =>
      m.asJson.as[TrafficMeasurement] shouldEqual m.asRight
    }
  }
  it("Decode TrafficRecords") {
    forAll { m: TrafficRecords =>
      m.asJson.as[TrafficRecords] shouldEqual m.asRight
    }
  }
}
