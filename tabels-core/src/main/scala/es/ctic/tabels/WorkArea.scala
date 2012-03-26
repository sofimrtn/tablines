package es.ctic.tabels
import scala.collection.mutable.HashMap
import es.ctic.tabels.Dimension._

class WorkArea {
  
	 
    var originalPos = (0,0)
    def originalRow = originalPos _1
	def originalColumn = originalPos _2
    
	var actualPos = (0,0)
	def actualRow = actualPos _1
	def actualColumn = actualPos _2
	
	var finalPos = (-1,-1)
	def mapUnDisambiguted = new scala.collection.mutable.ListMap[Resource, ResourceUnDisambiguated]
    
    def isDimensionInWorkArea(dimension : Dimension, value:Point):Boolean = {
	    dimension match {
	        case Dimension.rows =>  finalPos._1 <= value.row
		    case Dimension.cols => finalPos._2 <= value.col
		    case _ => true
		    }
	 }
    
    def actualPosActualize(dimValue : Point , dimension:Dimension)= {
	  dimension match{
	    case Dimension.rows =>  actualPos = (dimValue.row-originalRow+1, actualColumn )
	    case Dimension.cols => actualPos = (actualRow , actualColumn+1 )
	    case _ => actualPos = (0, 0 )
	  }
	}
    
    def actualPosIncrementUnactiveDimension(dimension:Dimension)= {
	  dimension match{
	    case Dimension.rows =>  originalPos = (actualRow + originalRow, actualColumn + originalColumn +1 )
	    actualPos=(0,0)
	    case Dimension.cols => originalPos = (actualRow + originalRow +1, actualColumn+ originalColumn)
	    actualPos=(0,0)
	    case _ => 
	  }
	}
    /***************************************/
    def actualPosIncrementUnactive(dimension:Dimension)= {
	  dimension match{
	    case Dimension.rows =>  originalPos = (originalRow,originalColumn +1 )
	   
	    case Dimension.cols => originalPos = (actualRow + originalRow +1, actualColumn+ originalColumn)
	    actualPos=(0,0)
	    case _ => 
	  }
	}
     def originalPosActualice(dimension:Dimension)= {
	  dimension match{
	     case Dimension.rows =>  originalPos = (actualRow + originalRow, actualColumn + originalColumn )
	    actualPos=(0,0)
	    case Dimension.cols => originalPos = (actualRow + originalRow, actualColumn+ originalColumn)
	    actualPos=(0,0)
	    case _ => 
	  }
	}
     /*******************************************/
    def actualPosDecrementUnactiveDimension(dimension:Dimension)= {
	  dimension match{
	    case Dimension.rows =>  actualPos = (actualRow + originalRow, actualColumn + originalColumn -1 )
	    case Dimension.cols => actualPos = (actualRow + originalRow-1, actualColumn + originalColumn)
	    case _ => 
	  }
	}
    def actualPosActualize(dimension:Dimension)= {
	  dimension match{
	    case Dimension.rows =>  actualPos = (actualRow , actualColumn)
	    case Dimension.cols => actualPos = (actualRow , actualColumn)
	    case _ => actualPos = (0, 0 )
	  }
	}
    
    def finalPosActualize = {
	
    	finalPos = (actualRow , actualColumn )
	  
	}
    
    def setActualDimensionValue(dimValue : Any , dimension:Dimension) = {
	  dimension match{
	    case Dimension.rows =>  actualPos = (dimValue.asInstanceOf[Int], actualPos _2)
	    case Dimension.cols => actualPos = (actualPos _1, dimValue.asInstanceOf[Int] )
	    case _ => actualPos = (0, 0 )
	  }
	}
	
	 
	 def getActualDimension(dimension : Dimension,value:String):String ={
	   
	   dimension match {
	        case Dimension.rows =>  actualRow.toString
		    case Dimension.cols => actualColumn.toString
		    case _ => value
		    }
	 }
}


case class ResourceUnDisambiguated(label:String,  numResults:Int, candidateList:Seq[Resource], source:String, strategy: String){
  
}