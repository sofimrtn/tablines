package es.ctic.tabels

import java.io.PrintStream
import scala.xml.Elem
import grizzled.slf4j.Logging

class Debugger extends Logging {
    
    val maxRows = 25
    
    val inlineCss = """
    .recentlyBound { background-color: lightyellow; }
    .var { font-weight: bold; }
    .value { color: violet; }
    .spreadsheetColHeader { background-color: lightgrey; }
    .spreadsheetRowHeader { background-color: lightgrey; }
    .odd { background-color: #D0EED0; }
    .even { background-color: #E0FFE0; }
    """
    
    def serializeInterpreterTrace(trace : InterpreterTrace, printStream : PrintStream) {
        val spreadsheets = serializeSpreadsheets(trace)
        val head = <head>
            <title>Tabels debugger</title>
            <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js" type="text/javascript"></script>
            <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js" type="text/javascript"></script>
            <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/south-street/jquery-ui.css" rel="stylesheet"></link>
            <script src="http://imakewebthings.com/deck.js/modernizr.custom.js" type="text/javascript"></script>
        	<link rel="stylesheet" id="style-theme-link" href="http://imakewebthings.com/deck.js/themes/style/web-2.0.css"></link>
        	<link rel="stylesheet" id="transition-theme-link" href="http://imakewebthings.com/deck.js/themes/transition/horizontal-slide.css"></link>
            <script src="http://imakewebthings.com/deck.js/core/deck.core.js" type="text/javascript"></script>
            <script src="http://imakewebthings.com/deck.js/extensions/status/deck.status.js" type="text/javascript"></script>
            <script src="http://imakewebthings.com/deck.js/extensions/navigation/deck.navigation.js" type="text/javascript"></script>
            <style type="text/css">{inlineCss}</style>
        </head>
        val initializationScript =
        	"""$(function() {
        		$( '.tabs' ).tabs();
        		$.deck('.slide');
                $.extend(true, $.deck.defaults, {
                   classes: {
                      navDisabled: 'deck-nav-disabled'
                   },

                   selectors: {
                      nextLink: '.deck-next-link',
                      previousLink: '.deck-prev-link'
                   }
                });
                $.extend(true, $.deck.defaults, {
                   selectors: {
                      statusCurrent: '.deck-status-current',
                      statusTotal: '.deck-status-total'
                   },

                   countNested: true
                });
                $(document).bind('deck.change', function(event, from, to) {
                   $('.spreadsheetCell').stop(true).css('background-color','').css('color','').attr('title','');
                   eval('event' + to + '()'); // FIXME: avoid eval
                });
                event0();
            });"""
        val events = serializeEvents(trace)
        val body = <body><script>{initializationScript}</script><h1>Tabels debugger</h1>{spreadsheets}{events}</body>
        val htmlDoc = <html>{head}{body}</html>
        printStream.print(htmlDoc.toString)
    }
    
    def serializeSpreadsheets(trace : InterpreterTrace) : Elem = {
        return <div>
            <div id="fileTabs" class="tabs">
                <ul>
                    { trace.dataSource.filenames.map(filename => <li><a href={"#fileTab"+filename.hashCode}>{filename}</a></li>) }
                </ul>
            {
                trace.dataSource.filenames.map(filename =>
                    <div id={"fileTab"+filename.hashCode} class="tabs">
                        <ul> { trace.dataSource.getTabs(filename).map(tabName =>
                            <li><a href={"#tabTab" + tabName.hashCode}>{tabName}</a></li>
                        ) } </ul>
                        { trace.dataSource.getTabs(filename).map(tabName =>
                            <div id={"tabTab"+tabName.hashCode}>
                                { serializeTab(trace, filename, tabName) }
                            </div>
                        )}
                    </div>
                )
            }
            </div>            
        </div>
    }
    
    def serializeTab(trace : InterpreterTrace, filename : String, tabName : String) : Elem = {
        val rows = trace.dataSource.getRows(filename, tabName)
        val cols = trace.dataSource.getCols(filename, tabName)
        logger.info("Tab " + tabName + " of file " + filename + " has " + rows + " rows and " + cols + " cols")
        if (rows * cols == 0) {
            return <div/>
        } else {
            val tabCode = "file" + filename.hashCode + "-tab" + tabName.hashCode
            return <table class="spreadsheet" id={"spreadsheet-" + tabCode} border="1">
                <thead>
                    <tr class="spreadsheetColHeader">
                        <th></th>
                        { 0.to(cols-1).map(col => <th>{columnConverter.intToAlpha(col)}</th>) }
                    </tr>
                </thead>
                <tbody>
                { 0.to(rows.min(maxRows)-1).map(row =>
                    <tr class={"spreadsheetRow " + (if(row%2==0) "odd" else "even")} id={tabCode + "-row" + row}>
                        <td class="spreadsheetRowHeader">{row+1}</td>
                        { 0.to(cols-1).map{col => 
                            val point = Point(filename, tabName, col, row)
                            val value = trace.dataSource.getValue(point).getContent.value
                            <td class="spreadsheetCell" id={"point" + point.hashCode}>{value}</td>
                        } }
                    </tr>                
                ) }
                </tbody>
            </table>
        }
    }
    
    def serializeEvents(trace : InterpreterTrace) : Elem = {
        return <div>
        	<p class="deck-status">
                <a href="#" class="deck-prev-link" title="Previous">Prev</a>
        		<span class="deck-status-current"></span>
        		/
        		<span class="deck-status-total"></span>
            	<a href="#" class="deck-next-link" title="Next">Next</a>
        	</p>
            <article class="deck-container" style="position: relative">
                { trace.events.view.zipWithIndex.map{case (event,index) => serializeEvent(event, index)} }
            </article>
        </div>
    }
    
    def serializeEvent(event : Event, index : Int) : Elem = {
        val actions = for ((variable, binding) <- event.bindings.bindingsMap) yield
            ("var $cell = $('#point" + binding.point.hashCode + "'); " +
             "$cell.attr('title',$cell.attr('title')+' " + variable + "');" +
             (if (event.lastBoundVariables contains variable)
                "$cell.css('background-color','yellow').css('color','white').animate({'background-color': 'green'});" else
                "$cell.css('background-color','green');"
             )
            )
        val eventFunction = "function event" + index + "() { " + actions.mkString + "}"
        return <section class="slide" id={"#event"+index}>
            { for ((variable, binding) <- event.bindings.bindingsMap) yield
                <li class={if (event.lastBoundVariables contains variable) "recentlyBound" else ""}><span class="var">{variable}</span> = <span class="value">{binding.value match { case l : Literal => l.value.toString
                    case r : Resource => r.toString}}</span> (at {columnConverter.intToAlpha(binding.point.col)}{binding.point.row+1})</li>
            }
            <script>{eventFunction}</script>
        </section>
    }
    
}