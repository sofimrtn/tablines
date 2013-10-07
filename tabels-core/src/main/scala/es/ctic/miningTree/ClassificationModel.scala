package es.ctic.miningTree

import es.ctic.tabels._
import weka.classifiers.trees.J48
import weka.core.{Instances, Attribute, FastVector, Instance}
import java.io.File
import grizzled.slf4j.Logging
import scala.{None, Array}
import es.ctic.tabels.Point
import es.ctic.tabels.NamedResource
import scala.collection.mutable.{ArraySeq,LinkedHashMap}

object ClassificationModel extends Logging{

  //Creating classifier
  val options = new Array[String](1)
  // unpruned tree
  options(0) = "-U"
  // new instance of tree
  val classificationTree = new J48()
  // set the options
  classificationTree.setOptions(options)

  /*
  * trainingData <File> : should be the directory path where the training sample is located.
  *
  *
  */
  def createModel(trainingData:File)={
    //FIXME: Trainning just with xls files
    val files = trainingData.listFiles.filter(_.getName.endsWith(".xls"))
    val adapters = files.map(file => DataAdapter.createAdapter(file.getCanonicalPath))
    logger.debug("training set: " + (new DataAdapterToArff).generateArff(adapters))
    classificationTree.buildClassifier(new DataAdapterToArff generateArff(adapters))
  }

  def classifyAdapter(inputAdapter: DataAdapter):LinkedHashMap[String,ArraySeq[ArraySeq[CellHeading]]]  = {

    val classValues = new FastVector()
    classValues.addElement(Classification.blank)
    classValues.addElement(Classification.dataMatrix)
    classValues.addElement(Classification.boxHead)
    classValues.addElement(Classification.stubHead)
    val classificationTable =  new scala.collection.mutable.LinkedHashMap[String,ArraySeq[ArraySeq[CellHeading]]]
    inputAdapter.getTabs.foreach(tab =>{
      val rowArray =  new ArraySeq[ArraySeq[CellHeading]](inputAdapter.getRows(tab))
      (0 until inputAdapter.getRows(tab)).foreach(row =>{
        val colArray = new ArraySeq[CellHeading](inputAdapter.getCols(tab))
        (0 until inputAdapter.getCols(tab)).foreach(col=>{

          val point =  Point(inputAdapter.uri,tab,col,row)
          val cell = inputAdapter.getValue(point)
          val classification = classifyCell(inputAdapter, point)

          //BOX and STUB condition
          val dataPoint = Classification.withName(classification) match{
            case Classification.boxHead => (row+1 until inputAdapter.getRows(tab)).find(line => classifyCell(inputAdapter, Point(inputAdapter.uri,tab,col,line))==Classification.dataMatrix.toString) match{
                                                case Some(dIndex)=> Some(Point(inputAdapter.uri,tab,col,dIndex))
                                                case None => None
                                            }

            case Classification.stubHead => (col+1 until inputAdapter.getCols(tab)).find(line => classifyCell(inputAdapter, Point(inputAdapter.uri,tab,line,row))==Classification.dataMatrix.toString)  match{
                                                case Some(dIndex)=> Some(Point(inputAdapter.uri,tab,col,dIndex))
                                                case None => None
                                              }
            case default => None
          }

          //get the first data cell under this head
          val dataCell = dataPoint match{
            case Some(p)=> Some(inputAdapter.getValue(p))
            case None => None
          }
          //get the type of the first data cell under this head
          val dataCellType = dataCell match{
            case Some(c)=> Some(c.getContent.rdfType)
            case None => None
          }
          //get the style of the first data cell under this head
          val dataCellStyle = dataCell match{
            case Some(c)=> Some(Seq(c.getStyle))
            case None => None
          }

          val cellStructure = new CellBoxHeading(cell.getContent.value,classification,cell.getContent.rdfType,cell.getStyle,dataCellType,dataCellStyle)

          colArray(col)=cellStructure
          logger.debug("CELLSTRUCTURE: "+ cellStructure)

        }//END OF COL ITERATION
        )
        rowArray(row)=colArray
      }// END OF ROW ITERATION
      )
      classificationTable+=(tab -> rowArray)
    }//END OF TAB ITERATION
    )
    classificationTable
  }
  def classifyCell(inputAdapter:DataAdapter, point: Point):String = {

    val inputGenerator = new InputGenerator()

    val booleanValues = new FastVector( )
    booleanValues.addElement("false")
    booleanValues.addElement("true")
    val classValues = new FastVector()
    classValues.addElement(Classification.blank.toString)
    classValues.addElement(Classification.dataMatrix.toString)
    classValues.addElement(Classification.boxHead.toString)
    classValues.addElement(Classification.stubHead.toString)


    val atts = new FastVector()
    atts.addElement(new Attribute("unmatchesStyle",booleanValues))
    atts.addElement( new Attribute("unmatchesType",booleanValues))
    atts.addElement( new Attribute("isText",booleanValues ))
    atts.addElement( new Attribute("hasBlankSurround",booleanValues ))
    atts.addElement( new Attribute("isBorderLineCell",booleanValues ))
    atts.addElement( new Attribute("isBlank",booleanValues ))
    atts.addElement( new Attribute("isHeader",classValues ))

    val dataSet = new Instances("dataAdapter", atts, 0)
    dataSet.setClassIndex(dataSet.numAttributes() - 1)

    val vals = new Array[Double](7)

    vals(0) = booleanValues.indexOf(inputGenerator.unmatchesStyle(inputAdapter,inputAdapter.getValue(point), point).toString())

    vals(1) = booleanValues.indexOf(inputGenerator.unmatchesType(inputAdapter, inputAdapter.getValue(point), point).toString())

    vals(2) = booleanValues.indexOf(inputGenerator.isText(inputAdapter.getValue(point)).toString())

    vals(3) = booleanValues.indexOf(inputGenerator.hasBlankSurround(inputAdapter, inputAdapter.getValue(point), point).toString())

    vals(4) = booleanValues.indexOf(inputGenerator.isBorderLineCell(inputAdapter, point).toString())

    vals(5) = booleanValues.indexOf(inputGenerator.isBlank(inputAdapter.getValue(point)).toString())

    vals(6) = Instance.missingValue()

    dataSet.add(new Instance(1.0, vals))
    classValues.elementAt(classificationTree.classifyInstance(dataSet.lastInstance()).toInt).toString
  }

}