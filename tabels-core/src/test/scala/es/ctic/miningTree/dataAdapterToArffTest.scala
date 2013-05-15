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
    assertEquals("@relation dataAdapter\n\n@attribute unmatchesStyle {false,true}\n@attribute unmatchesType {false,true}\n@attribute isText {false,true}\n@attribute hasBlankSurround {false,true}\n@attribute isBorderLineCell {false,true}\n@attribute isBlank {false,true}\n@attribute isHeader {BLANK,DATA_MATRIX,BOX_HEAD,STUB_HEAD}\n\n@data\ntrue,true,true,false,true,false,BOX_HEAD\ntrue,true,true,false,true,false,BOX_HEAD\nfalse,true,false,false,true,false,STUB_HEAD\ntrue,true,false,false,true,false,DATA_MATRIX\nfalse,true,false,false,true,false,STUB_HEAD\nfalse,true,false,false,true,false,DATA_MATRIX\nfalse,true,false,false,true,false,STUB_HEAD\nfalse,true,false,false,true,false,DATA_MATRIX\nfalse,true,false,false,true,false,STUB_HEAD\nfalse,true,false,false,true,false,DATA_MATRIX\ntrue,true,false,true,true,false,STUB_HEAD\nfalse,false,false,false,true,false,DATA_MATRIX\ntrue,true,false,true,true,true,STUB_HEAD\ntrue,true,false,true,true,false,DATA_MATRIX\ntrue,true,false,true,true,true,STUB_HEAD\ntrue,true,false,true,true,false,DATA_MATRIX\ntrue,true,false,true,true,false,STUB_HEAD\nfalse,true,false,false,true,false,DATA_MATRIX\nfalse,true,true,false,true,false,STUB_HEAD\nfalse,true,false,false,true,false,DATA_MATRIX\nfalse,false,true,false,true,false,STUB_HEAD\nfalse,true,true,false,true,false,DATA_MATRIX\nfalse,false,true,false,true,false,STUB_HEAD\nfalse,false,true,false,true,false,DATA_MATRIX\nfalse,false,true,false,true,false,BOX_HEAD",dataAdapterToArff.generateArff(Seq(dataAdapter)).toString())
  }


}
