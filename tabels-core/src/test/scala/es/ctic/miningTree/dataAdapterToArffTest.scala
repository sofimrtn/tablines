package es.ctic.miningTree

import org.scalatest.junit.JUnitSuite
import org.junit.{Test, Before}
import org.junit.Assert._
import es.ctic.tabels.DataAdapter

/**
 * Created with IntelliJ IDEA.
 * User: alfonso
 * Date: 19/02/13
 * Time: 10:51
 * To change this template use File | Settings | File Templates.
 */
class dataAdapterToArffTest extends JUnitSuite {

  val dataAdapterToArff = new DataAdapterToArff
  var dataAdapter : DataAdapter = null
  val filename1 : String = this.getClass.getResource("/es/ctic/miningTree/Test1.xls").getFile.replace("%20"," ")
  val sheet1 = "Hoja1"

  @Before def setUp {

    dataAdapter = DataAdapter.createAdapter(filename1)
  }
  @Test def generateArff{
    assertEquals("@relation dataAdapter\n\n@attribute unmatchesStyle {false,true}\n@attribute unmatchesType {false,true}\n@attribute isText {false,true}\n@attribute hasBlankSurround {false,true}\n@attribute isBorderLineCell {false,true}\n@attribute isBlank {false,true}\n@attribute isHeader {DATA_MATRIX,BOX_HEAD,STUB_HEAD}\n\n@data\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?\nfalse,false,true,false,true,false,?",dataAdapterToArff.generateArff(Seq(dataAdapter)).toString())
  }


}
