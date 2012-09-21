package es.ctic.tabels

import collection.JavaConversions._
import java.io._
import grizzled.slf4j.Logging
import java.util.zip.ZipFile
import collection.mutable
import org.geotools.data._
import org.geotools.data.simple._
import es.ctic.maplab.sld2googlemaps.Sld2GmapsConverter
import es.ctic.maplab.shp2kml.Shp2KmlConverter



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
class SHPMaplabDataAdapter(file: File) extends DataAdapter with Logging {

  // 0 - ZIP extraction
  logger.trace("Starting to process file: " + file.getAbsolutePath)
  logger.trace("Tmp files are in " + System.getProperty("java.io.tmpdir"))

  // First we need to extract to a temp dir
  val extracted = new ZipDeflater().deflate(new ZipFile(file), true)

  // Find .shp
  val shpFound = extracted.list().find(fileName => fileName.endsWith(".shp"))
  logger.trace("we found this shp in zip entries: " + shpFound)

  // 1 - DBF Handling

  // FIXME what should we do if no shp is found?
  val shpFile = new File(extracted, shpFound.get)
  val store = FileDataStoreFinder.getDataStore(shpFile)
  val featureSource = store.getFeatureSource()

  // headers
  val schema = featureSource.getSchema
  val dbfHeaders = ((1 until schema.getAttributeCount).map(index => schema.getAttributeDescriptors.get(index).getName.toString)).toList ::: List("geometry","kml")
  trace("DBF headers read: " + dbfHeaders)

  // 2 - SHP Handling: geometries are appended as last column so we need to generate KML before we calculate datamatrix
  val kmlConverter = new Shp2KmlConverter()
  val geometryType = kmlConverter.getGeometryType(shpFile)
  val convertedKmlDir = new File(extracted,"kml")
  convertedKmlDir.mkdir()
  val kmlConversionResults = kmlConverter.convert(shpFile,convertedKmlDir)

  // datamatrix: dbf + geometry + kml
  var dataMatrix = new mutable.MutableList[List[java.lang.Object]]()
  val featuresIterator: org.geotools.data.simple.SimpleFeatureIterator = featureSource.getFeatures.features
  while (featuresIterator.hasNext) {
    val feature: org.opengis.feature.simple.SimpleFeature = featuresIterator.next()
    // trace("dM before insertion: "+dataMatrix)
    // FIXME why does this until start in 1: suggestion: attribute(0) is the geometry
    val currentAttributesInRow = 1 until feature.getAttributeCount map (index => feature.getAttribute(index)) // each row is an Object[]

    // get kml path
    val currentKmlPath = kmlConversionResults.get(feature.getID).getAbsolutePath

    val currentRow = currentAttributesInRow.toList ::: List(geometryType,currentKmlPath)
    dataMatrix += currentRow
    // trace("dM after insertion: "+dataMatrix)
  }
  featuresIterator.close

  // 3 - SLD Handling
  // Find .sld
  val sldFound = extracted.list().find(fileName => fileName.endsWith(".sld"))
  logger.trace("we found this sld in zip entries: " + sldFound)
  val styleMatrix = if (sldFound == null) List() else {

    val convertedSldDir = new File(extracted,"sld")
    convertedSldDir.mkdir()
    val converter = new Sld2GmapsConverter()
    val sldFile = new File(extracted, sldFound.get)
    val sldMap = converter.convert(sldFile,convertedSldDir)

    val firstAttributeInMap = sldMap.keySet().iterator().next()

    val indexOfStyleAttributeInHeaders = dbfHeaders.indexOf(firstAttributeInMap)
    trace("trying to find "+firstAttributeInMap+ " in "+dbfHeaders + ": "+indexOfStyleAttributeInHeaders)
    val styleForFirstAttributeMap = sldMap.get(firstAttributeInMap)
    val zippedMap=(0 until styleForFirstAttributeMap.size) zip styleForFirstAttributeMap

    (0 until zippedMap.length) map (index => Seq(indexOfStyleAttributeInHeaders,zippedMap(index)._2._1,zippedMap(index)._2._2))
  }


  override val uri = file.getCanonicalPath()

  override def getTabs(): Seq[String] = Seq("dbf","sld")

  override def getRows(tabName: String = "dbf"): Int = {

    tabName match {
      case "dbf" => dataMatrix.length + 1
      case "sld" => styleMatrix.length
      case _ => throw new InvalidInputTab(tabName)
    }
  }

  override def getCols(tabName: String = "dbf"): Int = {

    tabName match {

      case "dbf" => dbfHeaders.length
      case "sld" => 3
      case _ => throw new InvalidInputTab(tabName)
    }
  }

  /** Gets the value of a cell as a CellValue.
    * @param point current cell coordinates
    * @return the cell as CellValue
    */
  override def getValue(point: Point): CellValue = {
    logger.trace("Getting value at " + point)


      point.tab match {

        case "dbf" => {

          try {
            // If header use the header information
            if (point.row == 0) {
              trace("is header row")
              return SHPCellValue(dbfHeaders(point.col))
            } else {

              // FIXME If cell is null default as double, this code should consider different types: String, Double, Integer based
              // on the attribute type
              val cell = Option(dataMatrix(point.row - 1) apply point.col).getOrElse(new java.lang.Double(0.0))
              trace("cell: " + cell)
              return SHPCellValue(cell)
            }
          } catch {
            case e => throw new IndexOutOfBounds(point)
          }
        }
        case "sld" => {
          return SHPCellValue((styleMatrix(point.row) apply point.col).asInstanceOf[AnyRef])
          // return SHPCellValue(styleMatrix(point.row) apply point.col)
        }
        case _ => throw new IndexOutOfBounds(point)
      }
  }


  /** Contains the value of a cell for a DBF file.
    *
    *
    */
  case class SHPCellValue(cell: Object) extends CellValue with Logging {

    override def getContent: Literal = {

      logger.trace("Parsing cell " + cell)
      cell match {

        case cell: java.lang.String => Literal(cell.trim, XSD_STRING)
        case cell: java.lang.Integer => Literal(cell, XSD_INT)
        case cell: java.lang.Long => Literal(cell, XSD_INT)
        case cell: java.lang.Double => Literal(cell, XSD_DECIMAL)
        case cell: java.util.Date => Literal(cell.toString, XSD_DATE)
        case cell: java.lang.Boolean => Literal(cell, XSD_BOOLEAN)
        case x =>
          logger.info("Unrecognized cell format: '" + x + "'")
          autodetectFormat(cell.toString)
      }
    }
  }

}


