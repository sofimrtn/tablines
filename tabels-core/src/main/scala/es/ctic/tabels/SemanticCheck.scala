package es.ctic.tabels

import grizzled.slf4j.Logging
import org.apache.commons.io.FileUtils
import org.apache.commons.configuration.CompositeConfiguration
import org.apache.commons.configuration.SystemConfiguration
import org.apache.commons.configuration.PropertiesConfiguration
import java.io.File
import scala.collection.mutable.Map
import scala.collection.mutable.HashSet

class SemanticCheck() extends Logging {
    
    private val configuration = new CompositeConfiguration()
    configuration.addConfiguration(new SystemConfiguration())
//    configuration.addConfiguration(new PropertiesConfiguration("tabels.properties"))
    
    val tabelsPath = configuration.getString("tabels.path")
    val proxyHost = configuration.getString("http.proxyHost")
    val proxyPort = configuration.getString("http.proxyPort")

    val tabelsDir = if (tabelsPath != null) new File(tabelsPath) else new File(FileUtils.getTempDirectory(), "tabels")
    
    def checkTemplateVars(program : S) {
     
      val usedVars = (program.templateList map(_.variables ) flatten) toSet
      val visGetVars= new VisitorGetVars()
      visGetVars.visit(program)
      val unasignedVars = usedVars--visGetVars.varsContained
      if (!unasignedVars.isEmpty) throw  new SemanticException(unasignedVars) 	
      
    }    
}

object SemanticCheck extends SemanticCheck {
    
}