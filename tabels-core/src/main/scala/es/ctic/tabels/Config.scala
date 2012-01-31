package es.ctic.tabels

import grizzled.slf4j.Logging
import org.apache.commons.io.FileUtils
import java.io.File

class Config extends Logging {
    
    val tabelsDir = new File(FileUtils.getTempDirectory(), "tabels")
    
    val tabelsPath = scala.sys.env.getOrElse("tabels.dir", null)
    val proxyHost = scala.sys.env.getOrElse("http.proxyHost", "")
    val proxyPort = scala.sys.env.getOrElse("http.proxyPort", "")

}

object Config extends Config {
    
}