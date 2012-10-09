package es.ctic.tabels

import grizzled.slf4j.Logging
import java.util.zip.ZipFile
import java.io.{OutputStream, InputStream, FileOutputStream, File}
import scala.collection.JavaConversions._
import org.apache.commons.io.FileUtils

/**
 * Created with IntelliJ IDEA.
 * User: Guillermo Gonzalez-Moriyon
 * Date: 8/1/12
 * Time: 3:11 PM
 */

class ZipDeflater() extends Logging {

  def deflate(zipFile: ZipFile, deflateOnParent: Boolean = false): File =
  {
    // Create temp dir

    // Create on parent dir in case flag deflateOnParent is on (we are working on webapp)
    // zipFile.getName returns the complete path of zipFile
    val pathSplitted = zipFile.getName.split("/")
    val fileName = pathSplitted.last + "-"+System.currentTimeMillis()+"-temp"
    trace(".zip.shp input file name: "+fileName)
    val pathOnly = if(deflateOnParent) pathSplitted.dropRight(2) else pathSplitted.dropRight(1)
    val path = pathOnly.mkString("/") + "/" + fileName
    trace("to be extracted on path: "+path)
    val extractedZipDir =  new File(path)
    extractedZipDir.mkdir()

    zipFile.entries().foreach(e => {
      val extractedFile = new File(extractedZipDir,e.getName)
      stream(zipFile.getInputStream(e), new FileOutputStream(extractedFile))
      logger.trace("Extracting "+e.getName+"...done.")
    })

    return extractedZipDir
  }

  def stream(inputStream: InputStream, outputStream: OutputStream) =
  {
    val buffer = new Array[Byte](1024)

    def doStream(total: Int = 0): Int = {
      val n = inputStream.read(buffer)
      if (n == -1)
        total
      else {
        outputStream.write(buffer, 0, n)
        doStream(total + n)
      }
    }

    doStream()
  }

}

object ZipDeflater extends ZipDeflater {

}