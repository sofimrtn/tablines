package es.ctic.tabels
import scala.collection.mutable.HashMap

class WorkArea {
	def mapUnDisambiguted = new scala.collection.mutable.ListMap[Resource, ResourceUnDisambiguated]
}

case class ResourceUnDisambiguated(label:String,  numResults:Int, candidateList:Seq[Resource], source:String, strategy: String){
  
}