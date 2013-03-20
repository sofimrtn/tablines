package es.ctic.miningTree

import grizzled.slf4j.Logging
import java.io.File
import collection.immutable.HashMap
import java.awt.Dimension

class TableRecognition(sourcePath:String, outputPath: Option[String] = None ) extends Logging{

  //FIXME: Just reading Excel files
  //Read files from input dir
  val files = new File(sourcePath).listFiles

  var cellMap = new HashMap[(String ,String,String,String),Seq[Any]]


}
