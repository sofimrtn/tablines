package es.ctic.miningTree

import es.ctic.tabels._
import weka.classifiers.trees.J48
import weka.core.{Instances, Attribute, FastVector, Instance}
import java.io.File
import grizzled.slf4j.Logging
import scala.Array
import es.ctic.tabels.Point
import es.ctic.tabels.NamedResource

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
  * trainingData <File> : should be the directory path where the trainning sample is located.
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

  def classifyAdapter(inputAdapter: DataAdapter):scala.collection.mutable.LinkedHashMap[String,Array[Array[(Any,String, NamedResource,CellStyle)]]]  = {

    val classValues = new FastVector()
    classValues.addElement("BLANK")
    classValues.addElement("DATA_MATRIX")
    classValues.addElement("BOX_HEAD")
    classValues.addElement("STUB_HEAD")
    val classificationTable =  new scala.collection.mutable.LinkedHashMap[String,Array[Array[(Any,String, NamedResource,CellStyle)]]]
    inputAdapter.getTabs.foreach(tab =>{
      val rowArray =  new Array[Array[(Any,String, NamedResource,CellStyle)]](inputAdapter.getRows(tab))
      (0 until inputAdapter.getRows(tab)).foreach(row =>{
        val colArray = new Array[(Any,String, NamedResource,CellStyle)](inputAdapter.getCols(tab))
        (0 until inputAdapter.getCols(tab)).foreach(col=>{

          val point =  Point(inputAdapter.uri,tab,col,row)
          val cell = inputAdapter.getValue(point)

          colArray(col)=(cell.getContent.value,classifyCell(inputAdapter, point),cell.getContent.rdfType,cell.getStyle)
          logger.debug("TUPLA: "+ colArray(col)._1+" - "+ colArray(col)._2+" - "+ colArray(col)._3)

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
    classValues.addElement("BLANK")
    classValues.addElement("DATA_MATRIX")
    classValues.addElement("BOX_HEAD")
    classValues.addElement("STUB_HEAD")

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