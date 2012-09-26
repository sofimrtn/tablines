package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.zip.ZipFile
import org.junit.Assert._

/**
 * Created with IntelliJ IDEA.
 * User: Guillermo Gonzalez-Moriyon
 * Date: 9/24/12
 * Time: 4:49 PM
 */
class ZipDeflaterTest extends JUnitSuite {

  val filename1 : String = this.getClass.getResource("/es/ctic/tabels/2012-NL-Gemeentegrenzen.zip").getFile.replace("%20"," ")

  var deflated : File= null
  @Before def setUp {
    deflated = ZipDeflater.deflate(new ZipFile(new File(filename1)))
  }

  @Test def deleteDir {

    val deflatedPath = deflated.getAbsolutePath
    ZipDeflater.deleteDir(deflated)
    assertFalse(new File(deflatedPath).exists())

  }

}
