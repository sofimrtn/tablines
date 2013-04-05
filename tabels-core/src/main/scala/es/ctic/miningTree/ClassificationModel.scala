package es.ctic.miningTree

import es.ctic.tabels.{CellValue, Point, DataAdapter}
import weka.classifiers.trees.J48
import weka.core.{Instances, Attribute, FastVector, Instance}
import java.io.File
import grizzled.slf4j.Logging

object ClassificationModel extends Logging{

  //Creating classifier
  val options = new Array[String](1)
  options(0) = "-U"               // unpruned tree
  val classificationTree = new J48()         // new instance of tree
  classificationTree.setOptions(options)     // set the options

  def createModel(trainingData:File)={
    val files = trainingData.listFiles.filter(_.getName.endsWith(".xls"))
    val adapters = files.map(file => DataAdapter.createAdapter(file.getCanonicalPath))
    logger.debug("training set: " + (new DataAdapterToArff).generateArff(adapters))
    classificationTree.buildClassifier(new DataAdapterToArff generateArff(adapters))
  }

  def classifyAdapter(inputAdapter: DataAdapter)  = {

    val classValues = new FastVector()
    classValues.addElement("NO_DATA")
    classValues.addElement("DATA_MATRIX")
    classValues.addElement("BOX_HEAD")
    classValues.addElement("STUB_HEAD")

    inputAdapter.getTabs.foreach(tab =>
      (0 until inputAdapter.getRows(tab)).foreach(row =>
        (0 until inputAdapter.getCols(tab)).foreach(col=>{

         val point =  Point(inputAdapter.uri,tab,col,row)
         val cell = inputAdapter.getValue(point)

         classifyCell(inputAdapter, cell,point)
         logger.error("CELDA CLASIFICADA: "+ classifyCell(inputAdapter, cell,point))

        }
        )
      )
    )

  }
  def classifyCell(inputAdapter:DataAdapter,cell :CellValue, point: Point):String = {

    val inputGenerator = new InputGenerator()

    val booleanValues = new FastVector( )
    booleanValues.addElement("false")
    booleanValues.addElement("true")
    val classValues = new FastVector()
    classValues.addElement("NOT_RELEVANT")
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

    vals(0) = booleanValues.indexOf(inputGenerator.unmatchesStyle(inputAdapter, cell, point).toString())

    vals(1) = booleanValues.indexOf(inputGenerator.unmatchesType(inputAdapter, cell, point).toString())

    vals(2) = booleanValues.indexOf(inputGenerator.isText(cell).toString())

    vals(3) = booleanValues.indexOf(inputGenerator.hasBlankSurround(inputAdapter, cell, point).toString())

    vals(4) = booleanValues.indexOf(inputGenerator.isBorderLineCell(inputAdapter, point).toString())

    vals(5) = booleanValues.indexOf(inputGenerator.isBlank(cell).toString())

    vals(6) = Instance.missingValue()

    dataSet.add(new Instance(1.0, vals))
    classValues.elementAt(classificationTree.classifyInstance(dataSet.lastInstance()).toInt).toString
  }

}