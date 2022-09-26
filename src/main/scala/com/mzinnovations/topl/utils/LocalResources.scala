package com.mzinnovations.topl.utils

import java.io.{File, FileOutputStream}
import java.nio.file.{Files, Paths}
import scala.language.postfixOps

object LocalResources {

  def resourceToLocal(resourcePath: String):File = {
    val outPath = "/tmp/" + resourcePath
    if (!Files.exists(Paths.get(outPath))) {
      val resourceFileStream = getClass.getResourceAsStream(s"/$resourcePath")
      val fos = new FileOutputStream(outPath)
      fos.write(
        LazyList.continually(resourceFileStream.read).takeWhile(-1 !=).map(_.toByte).toArray
      )
      fos.close()
    }
    new File(outPath)
  }

}
