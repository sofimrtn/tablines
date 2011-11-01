package es.ctic.tabels

import java.io.{File,FileReader}
import es.ctic.tabels.Dimension._
import grizzled.slf4j.Logging
import scala.xml._
import parsing._
import org.xml.sax.InputSource

class HTMLDataAdapter(fl : File) extends DataAdapter with Logging {
    
    private val root = HTML5Parser.loadXML(new InputSource(new FileReader(fl)))

	override val uri = fl.getCanonicalPath()
	override def getValue(point : Point) : CellValue = new HtmlCellValue() // FIXME
	override def getTabs() : Seq[String] = Seq() // FIXME
	override def getRows(tabName : String) : Int = 0 // FIXME
	override def getCols(tabName : String) : Int = 0 // FIXME
	
}

object HTML5Parser extends NoBindingFactoryAdapter {

  override def loadXML(source : InputSource, _p: SAXParser) = {
    loadXML(source)
  }

  def loadXML(source : InputSource) = {
    import nu.validator.htmlparser.{sax,common}
    import sax.HtmlParser
    import common.XmlViolationPolicy

    val reader = new HtmlParser
    reader.setXmlPolicy(XmlViolationPolicy.ALLOW)
    reader.setContentHandler(this)
    reader.parse(source)
    rootElem
  }
}

class HtmlCellValue() extends CellValue with Logging {
	
	override def getContent : Literal = Literal("FIXME") // FIXME

}
