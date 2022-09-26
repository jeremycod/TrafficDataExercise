package com.mzinnovations.topl.json

import cats.implicits.{toBifunctorOps, toShow}
import com.mzinnovations.topl.utils.FileDecodeError
import io.circe.Decoder
import io.circe.jawn.decodeFile
import java.io.File

case class Fail(message: String, cause: Option[Either[Throwable, Fail]] = None)

object JsonFileReader {
  def readAs[A: Decoder](resource: File): Either[FileDecodeError, A] = {
    decodeFile[A](resource)
      .leftMap(error => FileDecodeError(resource.getPath, error.show))
  }

}
