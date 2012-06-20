// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

grails.doc.title = "Documentación Tabels"
grails.doc.images = new File("src/docs/images")
// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = ['es.ctic.data.tapinos.springcontrollers']

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

grails.views.javascript.library="jquery"

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://wopr:8080/${appName}"
    }
    development {
        grails.serverURL = "http://localhost:8080/${appName}"
    }
    test {
        grails.serverURL = "http://localhost:8080/${appName}"
    }

}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
        'null' name:'stacktrace'  // disable stacktrace.log, see http://haxx.sinequanon.net/2008/09/grails-stacktracelog/
        environments {
            production {
                rollingFile name: 'myAppender',
                    maxFileSize: 1024*1024,
                    file: new File(new es.ctic.tabels.Config().tabelsDir, "tabels.log").toString(),
                    layout: pattern(conversionPattern: "%c{2} %m%n")                
            }
            development {
                console name:'myAppender',
                    layout:pattern(conversionPattern: '%c %m%n')                
            }
            test {
                console name:'myAppender',
                    layout:pattern(conversionPattern: '%c{2} %m%n')
            }
        }
    }
    
    root {
        warn 'myAppender'
    }

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate',
           'grails.app.tagLib',
           'grails.util',
           'net.sf.ehcache',
           'org.grails.plugin.resource'

    warn   'org.mortbay.log'
    
    info   'es.ctic.tabels'
    
    debug  'grails.app.services',
            'grails.app.controllers'
    
    warn  'es.ctic.data' // tapinos

}
