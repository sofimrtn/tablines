package es.ctic.miningTree

import weka.core.Attribute
import weka.core.FastVector
import weka.core.Instance
import weka.core.Instances
import es.ctic.tabels.{Point, DataAdapter}
import grizzled.slf4j.Logging

/**
 * Created with IntelliJ IDEA.
 * User: alfonso
 * Date: 21/03/13
 * Time: 9:21
 * To change this template use File | Settings | File Templates.
 */
class DataAdapterToArff extends Logging{

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

  def generateArff(adapters: Seq[DataAdapter]): Instances={

    val inputGenerator= InputGenerator()

    adapters.foreach(adapter =>
      adapter.getTabs.foreach(tab =>
        (0 until adapter.getRows(tab)).foreach(row =>
          (0 until adapter.getCols(tab)).foreach(col=>{

            val point =  Point(adapter.uri,tab,col,row)

            val cell = adapter.getValue(point)

            val vals = new Array[Double](7)

            vals(0) = booleanValues.indexOf(inputGenerator.unmatchesStyle(adapter, cell, point).toString())

            vals(1) = booleanValues.indexOf(inputGenerator.unmatchesType(adapter, cell, point).toString())

            vals(2) = booleanValues.indexOf(inputGenerator.isText(cell).toString())

            vals(3) = booleanValues.indexOf(inputGenerator.hasBlankSurround(adapter, cell, point).toString())

            vals(4) = booleanValues.indexOf(inputGenerator.isBorderLineCell(adapter, point).toString())

            vals(5) = booleanValues.indexOf(inputGenerator.isBlank(cell).toString())
            if(vals(5)==1) vals(6) = classValues.indexOf("BLANK")
            else if(row==0||row==1) vals(6) = classValues.indexOf("BOX_HEAD")
              //else if(col==0) vals(6) = classValues.indexOf("STUB_HEAD")
                 else vals(6) = classValues.indexOf("DATA_MATRIX")

            dataSet.add(new Instance(1.0, vals))

          }
          )
        )
      )
    )
    logger.info(dataSet)
    dataSet

  }

}
