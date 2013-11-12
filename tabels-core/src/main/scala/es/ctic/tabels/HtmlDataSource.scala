package es.ctic.tabels

import java.io.{File,FileReader,FileNotFoundException}
import grizzled.slf4j.Logging
import scala.xml._
import parsing._
import org.xml.sax.InputSource
import javax.xml.parsers.DocumentBuilderFactory


class HTMLDataAdapter(fl : File) extends DataAdapter with Logging {
    private val root = try {
      HTML5Parser.loadXML(new InputSource(new FileReader(fl)))


    }
    catch {
            case e : FileNotFoundException =>
                logger.error("While reading HTML file " + fl.getCanonicalPath, e)
                throw new NoInputFiles
            case e : Exception =>
                logger.error("While reading HTML file " + fl.getCanonicalPath, e)
                throw new InvalidInputFileCannotReadHTML(fl.getName)
	   }

	private val tables : Seq[Node] = root \\ "table"
  private def getTable(tabName : String) : Node = tables(tabName.toInt)
    

	override val uri = fl.getCanonicalPath()
	override def getValue(point : Point) : CellValue =
	    try {
        /*  FIXME
          val docFactory = DocumentBuilderFactory.newInstance
          docFactory.setValidating(false)
          val docBuilder = docFactory.newDocumentBuilder
          val doc = docBuilder.parse(fl)   */
	        new HtmlCellValue(((getTable(point.tab) \\ "tr")(point.row) \ "td")(point.col)/*FIXME, doc*/,fl)
        } catch {
            case e : IndexOutOfBoundsException => throw new IndexOutOfBounds(point)
        }
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


class HtmlCellValue(node : Node/*FIXME, domDoc:org.w3c.dom.Document*/,fl:File) extends CellValue with Logging {
	

  override def getContent : Literal = autodetectFormat(node.text)

 /* override def getStyle : CellStyle = {
    /*FIXME*/
    val docDomFactory = DocumentBuilderFactory.newInstance
    docDomFactory.setValidating(false)
    val docDomBuilder = docDomFactory.newDocumentBuilder
    val domDoc = docDomBuilder.parse(fl)
    /**/
    val domStyleMap = cz.vutbr.web.css.CSSFactory.assignDOM(domDoc,new java.net.URL("http://"),"",true)

    val docFactory = DocumentBuilderFactory.newInstance

    val docBuilder = docFactory.newDocumentBuilder

    val doc = docBuilder.newDocument()
    val domNode = new NodeExtras(node).toJdkNode(doc).asInstanceOf[org.w3c.dom.Element]
    val style = domStyleMap.get(domNode)

    logger.debug("Node scala: " +node.toString())
    logger.debug("Node dom: " + domNode.toString)
    logger.debug("Style Map: " +domStyleMap.keySet().toString)

    style.getProperty("border",true)
    CellStyle()
  }  */
}
 /*Convert Scala XML to Java DOM ->http://icodesnip.com/snippet/scala/convert-scala-xml-to-java-dom*/

object XmlHelpers {
  val factoryBuilder= javax.xml.parsers.DocumentBuilderFactory.newInstance()
  factoryBuilder.setIgnoringElementContentWhitespace(true)
  val docBuilder =
    factoryBuilder.newDocumentBuilder()

}

class NodeExtras(n: Node) extends Logging {
  def toJdkNode(doc: org.w3c.dom.Document): org.w3c.dom.Node =
    n match {
      case Elem(prefix, label, attributes, scope, children @ _*) =>
        // XXX: ns

        val r = doc.createElement(label)
        for (a <- attributes) {
          r.setAttribute(a.key, a.value.text)
          logger.debug("atribute: "+ a.key +" = "+ a.value.text)
        }
        for (c <- children) {
          logger.debug("children: "+ c.label)
          r.appendChild(new NodeExtras(c).toJdkNode(doc))
        }
        r
      case Text(text) => logger.debug("text: "+ text)
                        doc.createTextNode(text)

      case Comment(comment) => logger.debug("comment: "+ comment)
                            doc.createComment(comment)
      // not sure
      case a: Atom[_] => logger.debug("ATOM: "+ a.data.toString)
                        doc.createTextNode(a.data.toString)
      // XXX: other types
      //case x => throw new Exception(x.getClass.getName)
    }
}

class ElemExtras(e: Elem) extends NodeExtras(e) {
  override def toJdkNode(doc: org.w3c.dom.Document) =
    super.toJdkNode(doc).asInstanceOf[org.w3c.dom.Element]

  def toJdkDoc = {
    val doc = XmlHelpers.docBuilder.newDocument()
    doc.appendChild(toJdkNode(doc))
    doc
  }
}

//implicit def nodeExtras(n: scala.xml.Node):org.w3c.dom.Node  = new NodeExtras(n)
//implicit def elemExtras(e: Elem) = new ElemExtras(e)

