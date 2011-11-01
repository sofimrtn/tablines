package es.ctic.tabels

import java.io.{File,FileReader}
import es.ctic.tabels.Dimension._
import grizzled.slf4j.Logging
import scala.xml._
import parsing._
import org.xml.sax.InputSource

class HTMLDataAdapter(fl : File) extends DataAdapter with Logging {
    
    private val root = HTML5Parser.loadXML(new InputSource(new FileReader(fl)))
    private val tables : Seq[Node] = root \\ "table"
    
    private def getTable(tabName : String) : Node = tables(tabName.toInt)

	override val uri = fl.getCanonicalPath()
	override def getValue(point : Point) : CellValue = new HtmlCellValue(((getTable(point.tab) \\ "tr")(point.row) \ "td")(point.col))
	override def getTabs() : Seq[String] = List.range(0, tables.size) map (_.toString()) // FIXME: use @id when available
	override def getRows(tabName : String) : Int = (getTable(tabName) \\ "tr").size
	override def getCols(tabName : String) : Int = ((getTable(tabName) \\ "tr")(0) \ "td").size // FIXME: considers only the first row, beware of headers
	
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

class HtmlCellValue(node : Node) extends CellValue with Logging {
	
	override def getContent : Literal = Literal(node.text)

}
