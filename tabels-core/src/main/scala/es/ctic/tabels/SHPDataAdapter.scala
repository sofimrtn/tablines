package es.ctic.tabels

import java.io._
import grizzled.slf4j.Logging
import java.util
import util.zip.ZipFile
import collection.mutable
import org.geotools.data._


/**
 * Created with IntelliJ IDEA.
 * User: Guillermo Gonzalez-Moriyon
 * Date: 7/30/12
 * Time: 12:51 PM
 */

/** Handles SHP directory. Looks for dbf inside zip and processes it using DBFDataAdapter. Useful for shp zipped files.
 *
 *
 * @author Guillermo Gonzalez-Moriyon
 *
 */
class SHPDataAdapter(file: File) extends DataAdapter with Logging {

  logger.trace("Starting to process file: "+file.getAbsolutePath)
  logger.trace("Tmp files are in " + System.getProperty("java.io.tmpdir"))

  // First we need to extract to a temp dir
  val extracted = new ZipDeflater().deflate(new ZipFile(file),true)

  // Find .shp
  val found = extracted.list().find(fileName => fileName.endsWith(".shp"))
  logger.trace("we found this "+found)


  // FIXME what should we do if no shp is found?
  val store = FileDataStoreFinder.getDataStore(new File(extracted,found.get))
  val featureSource = store.getFeatureSource()

  // headers
  val schema = featureSource.getSchema
  val headers = (1 until schema.getAttributeCount).map(index => schema.getAttributeDescriptors.get(index).getName)
  trace("headers read: "+headers)

  // datamatrix
 /* val featuresIterator:Iterable[org.opengis.feature.simple.SimpleFeature] = JavaConversions.collectionAsScalaIterable(featureSource.getFeatures)
  val dataMatrix = (featuresIterator map (feature => feature.getAttributes())) toArray
  */

  var dataMatrix = new mutable.MutableList[IndexedSeq[java.lang.Object]]()
  val featuresIterator:org.geotools.data.simple.SimpleFeatureIterator = featureSource.getFeatures.features
  while (featuresIterator.hasNext) {
    val feature:org.opengis.feature.simple.SimpleFeature = featuresIterator.next()
    // trace("dM before insertion: "+dataMatrix)
     dataMatrix += 1 until feature.getAttributeCount map (index => feature.getAttribute(index))            // this returns an Object[]
    // val row = 1 until feature.getAttributeCount map (index => feature.getAttribute(index))            // this returns an Object[]
    // trace("dM after insertion: "+dataMatrix)

  }
  featuresIterator.close

  override val uri = file.getCanonicalPath()
  override def getTabs(): Seq[String] = Seq("")
  override def getRows(tabName: String = ""): Int = dataMatrix.length + 1
  override def getCols(tabName: String = ""): Int = headers.length

  /** Gets the value of a cell as a CellValue.
   * @param point current cell coordinates
   * @return the cell as CellValue
   */
  override def getValue(point: Point): CellValue = {
    logger.trace("Getting value at " + point)
    try {


      // If header use the header information
      if (point.row == 0) {
        trace("is header row")
        return SHPCellValue(headers(point.col))
      } else {

        // FIXME If cell is null default as double, this code should consider different types: String, Double, Integer based
        // on the attribute type
        val cell =  Option(dataMatrix(point.row - 1) apply point.col).getOrElse(new java.lang.Double(0.0))
        trace("cell: "+cell)
        logger.trace ("cell type is "+cell.getClass.getCanonicalName+" but field is "+schema.getAttributeDescriptors.get(point.col+1).toString )
        return SHPCellValue(cell)
      }
    } catch {
      case e => throw new IndexOutOfBounds(point)
    }
  }


  /** Contains the value of a cell for a DBF file.
   *
   *
   */
  case class SHPCellValue(cell: Object) extends CellValue with Logging {

    override def getContent: Literal = {

      logger.trace("Parsing cell "+cell)
      cell match {

        case cell:java.lang.String => Literal(cell.trim, XSD_STRING)
        case cell:java.lang.Integer => Literal(cell, XSD_INTEGER)
        case cell:java.lang.Long => Literal(cell, XSD_INTEGER)
        case cell:java.lang.Double => Literal(cell, XSD_DOUBLE)
        case cell:java.util.Date => Literal(cell.toString, XSD_DATE)
        case cell:java.lang.Boolean => Literal(cell, XSD_BOOLEAN)
        case x =>
          logger.info("Unrecognized cell format: '" + x + "'")
          autodetectFormat(cell.toString)
      }
    }
  }

}


