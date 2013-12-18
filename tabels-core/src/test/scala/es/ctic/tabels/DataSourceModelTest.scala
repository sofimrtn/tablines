package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._      
import java.io.File

class DataAdaptersDelegateTest extends JUnitSuite {

    @Test def filenames() {        
        val baseDir = new File("src/it/resources")  
        val filename = "simple-grid.xls"
        val relativePathToFile = "src/it/resources/" + filename              
        val files = Seq(new File(relativePathToFile))
        val dataAdaptersDelegate = new DataAdaptersDelegate(files, Some(baseDir))     
        assertEquals(1, dataAdaptersDelegate.filenames.size)
        assertEquals(filename, dataAdaptersDelegate.filenames(0))
    }
    
}

//TODO: Add test for autodetect format method (double with comma or dot, etc.)
