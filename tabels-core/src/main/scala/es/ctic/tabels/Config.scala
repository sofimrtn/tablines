package es.ctic.tabels

import grizzled.slf4j.Logging
import org.apache.commons.io.FileUtils
import java.io.File

object Config extends Logging {
    
    val tabelsDir = new File(FileUtils.getTempDirectory(), "tabels")

}