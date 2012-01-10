grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal()
        mavenRepo "http://wopr.fundacionctic.org:8081/nexus/content/groups/public"
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "http://www.scala-tools.org/repo-releases/"
    }
    plugins {
        runtime ':tapinos-js:1.1',
                ':uploadr:0.5.0',
                ':highcharts:2.1.0' // indirect dependency
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.13'
        compile('es.ctic.tabels:tabels-core_2.9.1:0.1') {
            excludes 'slf4j-api'
        }
        compile 'commons-lang:commons-lang:2.6',
                'commons-httpclient:commons-httpclient:3.0',
                'org.fundacionctic.su4j:su4j-endpoint:0.5.1',
                'org.fundacionctic.ext:pubby:0.3.3.1'
    }
}
