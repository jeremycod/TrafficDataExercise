package com.mzinnovations.topl.utils

object Implicits {

  implicit class WeightWrapper(weights: List[Double]) {
    def average: Double = weights.foldLeft((0.0, 1)) { case ((avg, idx), next) => (avg + (next - avg)/idx, idx + 1) }._1
  }

  implicit class CaseInsensitiveRegex(sc: StringContext) {
    def ci = ( "(?i)" + sc.parts.mkString ).r
  }

}
