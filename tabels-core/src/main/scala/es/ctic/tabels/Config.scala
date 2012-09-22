package es.ctic.tabels

import grizzled.slf4j.Logging
import org.apache.commons.io.FileUtils
import org.apache.commons.configuration.CompositeConfiguration
import org.apache.commons.configuration.SystemConfiguration
import org.apache.commons.configuration.PropertiesConfiguration
import java.io.File

class Config extends Logging {
    
    private val configuration = new CompositeConfiguration()
    configuration.addConfiguration(new SystemConfiguration())
//    configuration.addConfiguration(new PropertiesConfiguration("tabels.properties"))
    
    val tabelsPath = configuration.getString("tabels.path")
    val proxyHost = configuration.getString("tabels.proxyHost")
    val proxyPort = configuration.getString("tabels.proxyPort")

    val localTomcatWrittablePath = configuration.getString("tabels.localTomcatWrittablePath")
    val publicTomcatWrittablePath = configuration.getString("tabels.publicTomcatWrittablePath")

    val tabelsDir = if (tabelsPath != null) new File(tabelsPath) else new File(FileUtils.getTempDirectory(), "tabels")
        
}

object Config extends Config {
    
}