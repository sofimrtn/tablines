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
import org.apache.commons.io.FileUtils


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

  // ****************
  // 0 - ZIP extraction
  logger.trace("Starting to process file: " + file.getAbsolutePath)
  logger.trace("Tmp files are in " + System.getProperty("java.io.tmpdir"))

  // First we need to extract to a temp dir
  val extractedZipDir = ZipDeflater.deflate(new ZipFile(file), true)

  // Find .shp
  val shpFound = extractedZipDir.list().find(fileName => fileName.endsWith(".shp"))
  logger.trace("we found this shp in zip entries: " + shpFound)

  // ****************
  // 1 - DBF Handling
  // if no shp is found: throw exception
  val shpFile = new File(extractedZipDir, shpFound.getOrElse(throw new InvalidInputFileNoShpAttached(file.getName)))
  val store = FileDataStoreFinder.getDataStore(shpFile)
  val featureSource = store.getFeatureSource()

  // headers
  val schema = featureSource.getSchema
  val dbfHeaders = ((1 until schema.getAttributeCount).map(index => schema.getAttributeDescriptors.get(index).getName.toString)).toList ::: List("geometry","kml")
  trace("DBF headers read: " + dbfHeaders)

  // ****************
  // 2 - Generate local and public paths
  // public root: Obtain from Config
  val publicUrlRoot = if(Config.publicTomcatWritablePath != null) Config.publicTomcatWritablePath  else  {
    logger.warn("No public URL root specified in tabels.publicTomcatWritablePath, using default http://www.tabels.com/")
    "http://www.tabels.com"
  }

  val localTomcatPath = if(Config.localTomcatWritablePath != null && new File(Config.localTomcatWritablePath).canWrite) Config.localTomcatWritablePath  else  {
    logger.warn("No local tomcat writable path specified in tabels.localTomcatWritablePath or directory not writable, using default "+extractedZipDir.getAbsolutePath)
    extractedZipDir.getAbsolutePath
  }

  // Guess projectId from extractedZipDir path
  val splittedPath = if (file.getAbsolutePath.contains("""\""")) file.getAbsolutePath.split("""\\""")
  					 else file.getAbsolutePath.split("/") 
  val projectId = splittedPath(splittedPath.length-3)
  trace("projectId guessed from zipfile path is "+projectId)

  // final results
  val projectPath = new File(localTomcatPath + "/" + projectId)
  if (!projectPath.exists) {

    val projectPath = new File(localTomcatPath + "/" + projectId)
    projectPath.mkdir
    trace("Created path: "+ projectPath.getAbsolutePath)
  }

  // local writable
  val localWritableDirKml = localTomcatPath + "/" + projectId + "/kml"
  val localWritableDirJson = localTomcatPath + "/" + projectId + "/json"

  // public
  val publicDirKml = publicUrlRoot + "/" + projectId +"/kml/"
  val publicDirJson = publicUrlRoot + "/" + projectId +"/json/"
  val publicDirSvg = publicUrlRoot + "/" + projectId +"/svg/"

  trace("localWritableDirKml: "+ localWritableDirKml)
  trace("localWritableDirJson: "+ localWritableDirJson)
  trace("publicDirKml: "+ publicDirKml)
  trace("publicDirJson: "+ publicDirJson)


  // ****************
  // 3 - SHP Handling: geometries are appended as last column so we need to generate KML before we calculate datamatrix
  val kmlConverter = new Shp2KmlConverter()
  val geometryType = kmlConverter.getGeometryType(shpFile)

  val convertedKmlDir = new File(localWritableDirKml)
  convertedKmlDir.mkdir()
  if (new File(localWritableDirKml).exists()) {
    trace("Created dir to store kml files in: "+convertedKmlDir.getAbsolutePath)
  }  else {
    warn("can not create dir: "+localWritableDirKml)
  }

  val kmlConversionResults = kmlConverter.convert(shpFile,convertedKmlDir)

  // datamatrix: dbf + geometry + kml
  var dataMatrix = new mutable.MutableList[List[java.lang.Object]]()
  val featuresIterator: org.geotools.data.simple.SimpleFeatureIterator = featureSource.getFeatures.features
  while (featuresIterator.hasNext) {
    val feature: org.opengis.feature.simple.SimpleFeature = featuresIterator.next()

    // FIXME why does this "until" start in 1: suggestion: attribute(0) is the geometry
    val currentAttributesInRow = 1 until feature.getAttributeCount map (index => feature.getAttribute(index)) // each row is an Object[]

    // get kml path
    // val currentKmlPath = kmlConversionResults.get(feature.getID).getAbsolutePath
    val currentKmlPublicPath = publicDirKml + kmlConversionResults.get(feature.getID).getName
    val currentRow = currentAttributesInRow.toList ::: List(geometryType,currentKmlPublicPath)

    dataMatrix += currentRow
  }
  featuresIterator.close

  // ****************
  // 4 - SLD Handling
  // Find .sld
  val sldFound = extractedZipDir.list().find(fileName => fileName.endsWith(".sld"))

  // If no sld, warn
  if (sldFound.isEmpty) warn("No SLD found!")

  logger.trace("we found this sld in zip entries: " + sldFound)

  val styleMatrix = if (sldFound.isEmpty) List() else {

    val convertedJsonDir = new File(localWritableDirJson)
    convertedJsonDir.mkdir()
    logger.trace("Created dir to store json files in: "+convertedJsonDir.getAbsolutePath)
    val converter = new Sld2GmapsConverter()
    val sldFile = new File(extractedZipDir, sldFound.get)
    val sldMap = converter.convert(sldFile,convertedJsonDir, publicDirSvg)

    val firstAttributeInMap = sldMap.keySet().iterator().next()

    val indexOfStyleAttributeInHeaders = dbfHeaders.indexOf(firstAttributeInMap)
    trace("trying to find "+firstAttributeInMap+ " in "+dbfHeaders + ": "+indexOfStyleAttributeInHeaders)
    if(indexOfStyleAttributeInHeaders == -1) {
      warn("Attribute '%s' not found in dbf".format(firstAttributeInMap))
    }

    val styleForFirstAttributeMap = sldMap.get(firstAttributeInMap)
    trace("style map: "+styleForFirstAttributeMap)

    val zippedMap=(0 until styleForFirstAttributeMap.size) zip styleForFirstAttributeMap.keySet()
    trace ("new zipped map: " + zippedMap)

    trace("final public url root for json files: "+publicDirJson)
    (0 until zippedMap.length) map (index => Seq(
      indexOfStyleAttributeInHeaders,        // DBF column name associated with style
      zippedMap(index)._2,                   // value
      publicDirJson + styleForFirstAttributeMap.get(zippedMap(index)._2).getName) // public-uri.json
    )

  }
  trace("style matrix [%d,%s]:".format(styleMatrix.length,if(styleMatrix.isEmpty) "-" else ""+styleMatrix(0).length)+styleMatrix)

  // Delete extractedDir
  trace("Removing temp dir: "+ extractedZipDir.getAbsolutePath)
  FileUtils.deleteDirectory(extractedZipDir)
  if(!extractedZipDir.exists)
    trace("Removing temp dir: "+ extractedZipDir.getAbsolutePath + ": removed.")

  override val uri = file.getCanonicalPath()

  override def getTabs(): Seq[String] = if (styleMatrix.isEmpty) Seq("dbf") else Seq("dbf","sld")

  override def getRows(tabName: String = "dbf"): Int = {

    tabName match {
      case "dbf" => dataMatrix.length + 1
      case "sld" => if(styleMatrix.isEmpty) throw new InvalidInputTab(tabName) else styleMatrix.length
      case _ => throw new InvalidInputTab(tabName)
    }
  }

  override def getCols(tabName: String = "dbf"): Int = {

    tabName match {

      case "dbf" => dbfHeaders.length
      case "sld" => if (styleMatrix.isEmpty) throw new InvalidInputTab(tabName) else styleMatrix(0).length
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
              val cell = dbfHeaders(point.col)
              trace("is header row: "+cell)
              SHPCellValue(cell)
            } else {

              // FIXME If cell is null default as double, this code should consider different types: String, Double, Integer based
              // on the attribute type
              val cell = Option(dataMatrix(point.row - 1) apply point.col).getOrElse(new java.lang.Double(0.0))
              trace("cell: " + cell)
              SHPCellValue(cell)
            }
          } catch {
            case e => throw new IndexOutOfBounds(point)
          }
        }
        case "sld" => {
          try {
            val cell = (styleMatrix(point.row) apply point.col).asInstanceOf[AnyRef]
            trace("cell: " + cell)
            SHPCellValue(cell)
          } catch {
            case e => throw new IndexOutOfBounds(point)
          }

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
        case cell: java.lang.Integer => Literal(cell, XSD_INTEGER)
        case cell: java.lang.Long => Literal(cell, XSD_INTEGER)
        case cell: java.lang.Double => Literal(cell, XSD_DOUBLE)
        case cell: java.util.Date => Literal(cell.toString, XSD_DATE)
        case cell: java.lang.Boolean => Literal(cell, XSD_BOOLEAN)
        case x =>
          logger.info("Unrecognized cell format: '" + x + "'")
          autodetectFormat(cell.toString)
      }
    }
  }

}


