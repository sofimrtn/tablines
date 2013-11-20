grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.war.file = "target/${appName}.war"
grails.project.docs.output.dir = "web-app/docs"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal()
        grailsHome()
        grailsPlugins()
        grailsCentral()
        
        /*Internal semantic web nexus repository (deployed by Diego Berrueta and now deprecated)*/
        //mavenRepo "http://wopr.fundacionctic.org:8081/nexus/content/groups/public"
        //mavenRepo "http://wopr.fundacionctic.org:8081/nexus/content/groups/public-snapshots"

        /*CTIC official nexus repository*/
        mavenRepo "http://devit.fundacionctic.org:8081/nexus/content/groups/public"
        mavenRepo "http://devit.fundacionctic.org:8081/nexus/content/groups/private"
        
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "http://resources.smile.deri.ie/maven-repository/"
        mavenRepo "http://www.scala-tools.org/repo-releases/"

        //GeoTools repository
        mavenRepo "http://download.osgeo.org/webdav/geotools/"
        
    }
    plugins {
        runtime ':uploadr:0.6.0',// Apache Lincese version 2.0
				//':tapinos-js:1.7',
                ':svn:1.0.2',// Apache Lincese version 2.0
                //Added for tapinos-ws
                ":jquery:1.7.1",//MIT License
                ":fancybox:1.3.4",//MIT License
                ":jquery-geturlparam:2.1",
                ":jquery-datatables:1.8.2",
                ":jquery-tipsy:1.0.0a",
                ":jquery-tooltip:1.3",
                ":jquery-ui:1.8.15",
                ":resources:1.1.6",// Apache Lincese version 2.0
                ":mail:1.0"// Apache Lincese version 2.0
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.13'
        compile('es.ctic.tabels:tabels-core_2.9.1:0.6-SNAPSHOT') {
            excludes 'jena', 'arq', 'xml-apis', 'xml-apis-xerces','slf4j-log4j12'
        }

        compile('es.ctic.undermaps:undermaps-ws:0.5-SNAPSHOT'){
		  excludes 'arq'
		}
		
        compile('es.ctic.understats:tapinos-rdf-ws:2.7'){
            excludes 'arq'
        }
        compile('commons-lang:commons-lang:2.6') // Apache Lincese version 2.0
        compile('commons-io:commons-io:2.1')  // Apache Lincese version 2.0
        compile('commons-httpclient:commons-httpclient:3.0') // Apache Lincese version 2.0
        compile('org.scala-lang:scala-library:2.9.2') //BSD like license
        compile('org.fundacionctic.su4j:su4j-endpoint:1.2'){// Apache Lincese version 2.0
            excludes 'jena', 'arq', 'jena-core', 'jena-arq', 'jena-tdb', 'slf4j-log4j12', 'slf4j-jdk14'
        }
        compile('org.fundacionctic.ext:pubby:0.3.3.1') {
            excludes 'arq'
        }
        compile('com.hp.hpl.jena:jena:2.6.4'){ // BSD License
            excludes 'slf4j-log4j12'
        }
        runtime('com.hp.hpl.jena:arq:2.8.8'){ // BSD like license - transitive dependency, added here because we exclude it above
            excludes 'slf4j-log4j12',  'stax-api'
        }
    }
}
