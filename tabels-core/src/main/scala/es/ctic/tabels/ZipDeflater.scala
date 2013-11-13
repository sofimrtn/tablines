package es.ctic.tabels

import grizzled.slf4j.Logging
import java.util.zip.ZipFile
import java.io._
import scala.collection.JavaConversions._

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
    trace("Starting deflate operation on "+zipFile)
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

    // Extract all entries that are not directories at first level
    if (isTraceEnabled) {
      trace("entries on zip: ")
      zipFile.entries().foreach(e => {
        trace(e.getName)
      })
    }

    // create intermediate dirs
    zipFile.entries().filter(e => e.isDirectory).foreach(e => {

      val intermediateDir = new File(extractedZipDir,e.getName)
      if (!intermediateDir.exists)
        intermediateDir.mkdirs()
    })


    zipFile.entries().filter(e => !e.isDirectory).foreach(e => {
      trace("Extracting "+e.getName+"...")
      val extractedFile = new File(extractedZipDir,e.getName)

      stream(zipFile.getInputStream(e), new FileOutputStream(extractedFile))
      trace("Extracting "+e.getName+"...done.")
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