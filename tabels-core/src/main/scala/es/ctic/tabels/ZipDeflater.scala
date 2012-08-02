package es.ctic.tabels

import grizzled.slf4j.Logging
import java.util.zip.ZipFile
import java.io.{OutputStream, InputStream, FileOutputStream, File}
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: Guillermo Gonzalez-Moriyon
 * Date: 8/1/12
 * Time: 3:11 PM
 */

class ZipDeflater() extends Logging {

  def deflate(zipFile: ZipFile): File =
  {
    // Create temp dir

    val dirNamePre = zipFile.getName.split("/").last.replace(".","-")

    val dirNameSuf = "-temp-"+ System.currentTimeMillis()+"-shp"
    logger.trace("Creating dir p:"+ dirNamePre+" s:"+dirNameSuf)
    // FIXME Hardcoded temp directory
    val extractedZipDir =  new File(new File("/home/guille/tmp"),dirNamePre+dirNameSuf)
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