package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.zip.ZipFile
import org.junit.Assert._
import grizzled.slf4j.Logging

/**
 * Created with IntelliJ IDEA.
 * User: Guillermo Gonzalez-Moriyon
 * Date: 9/24/12
 * Time: 4:49 PM
 */
class ZipDeflaterTest extends JUnitSuite with Logging{

  @Test def testDeflate {
    val filename1 : String = this.getClass.getResource("/es/ctic/tabels/2012-NL-Gemeentegrenzen.zip").getFile.replace("%20"," ")
    val deflated = ZipDeflater.deflate(new ZipFile(new File(filename1)))

    assertTrue(deflated.exists())
    assertTrue(deflated.isDirectory())

  }

  @Test def testDeflateWithDirectory {
    debug("starting fail test")
    val filename1 : String = this.getClass.getResource("/es/ctic/tabels/northwest-spain.zip").getFile.replace("%20"," ")
    val deflated = ZipDeflater.deflate(new ZipFile(new File(filename1)))

    assertTrue(deflated.exists())
    assertTrue(deflated.isDirectory())

  }
}
