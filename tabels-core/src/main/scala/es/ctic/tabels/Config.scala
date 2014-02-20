package es.ctic.tabels

import grizzled.slf4j.Logging
import org.apache.commons.io.FileUtils
import org.apache.commons.configuration.CompositeConfiguration
import org.apache.commons.configuration.SystemConfiguration
import java.io.File

class Config extends Logging {
    
    private val configuration = new CompositeConfiguration()
    configuration.addConfiguration(new SystemConfiguration())
//    configuration.addConfiguration(new PropertiesConfiguration("tabels.properties"))
    
    val tabelsPath = configuration.getString("tabels.path")
    val proxyHost = configuration.getString("tabels.proxyHost")
    val proxyPort = configuration.getString("tabels.proxyPort")
    val maxFileSizeReadFromConfiguration = configuration.getString("tabels.maxFileSize")
    val allowedExtensionsReadFromConfiguration = configuration.getString("tabels.allowedExtensions")

    // val localTomcatWritablePath = configuration.getString("tabels.localTomcatWritablePath")
    val publicTomcatWritablePath = configuration.getString("tabels.publicTomcatWritablePath")

    val tabelsDir = if (tabelsPath != null) new File(tabelsPath) else new File(FileUtils.getTempDirectory(), "tabels")

    val localTomcatWritablePath = tabelsDir + "/projects"

    // default: 12MB in bytes, updated to 20MB
    val maxFileSize = if(maxFileSizeReadFromConfiguration != null) maxFileSizeReadFromConfiguration else 20971520//12582912
    val allowedExtensions = if(allowedExtensionsReadFromConfiguration != null) allowedExtensionsReadFromConfiguration else "owl,rdf,nt,n3,ttl,px,zip,csv,tsv,tab,ods,xls,xlsx,html"
        
}

object Config extends Config {
    
}