package es.ctic.tabels

import java.io.PrintStream
import scala.xml.Elem
import grizzled.slf4j.Logging

class Debugger extends Logging {
    
    val maxRows = 25
    
    val inlineCss = """
    .recentlyBound { background-color: blue; }
    """
    
    def serializeInterpreterTrace(trace : InterpreterTrace, printStream : PrintStream) {
        val spreadsheets = serializeSpreadsheets(trace)
        val head = <head>
            <title>Tabels debugger</title>
            <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js" type="text/javascript"></script>
            <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js" type="text/javascript"></script>
            <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet"></link>
            <style type="text/css">{inlineCss}</style>
        </head>
        val initializationScript =
        	"""$(function() {
        		$( '.tabs' ).tabs();
        	});"""
        val events = serializeEvents(trace)
        val body = <body><script>{initializationScript}</script><h1>Tabels debugger</h1>{spreadsheets}{events}</body>
        val htmlDoc = <html>{head}{body}</html>
        printStream.print(htmlDoc.toString)
    }
    
    def serializeSpreadsheets(trace : InterpreterTrace) : Elem = {
        return <div>
            <h2>Input files</h2>
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
            return <table border="1">
                <tr>
                    <th></th>
                    { 0.to(cols-1).map(col => <th>Column {columnConverter.intToAlpha(col)}</th>) }
                </tr>
                { 0.to(rows.min(maxRows)-1).map(row =>
                    <tr>
                        <td>{row+1}</td>
                        { trace.dataSource.getRow(filename, tabName, row).map(literal => <td>{literal.value}</td>) }
                    </tr>                
                ) }
            </table>
        }
    }
    
    def serializeEvents(trace : InterpreterTrace) : Elem = {
        return <div>
            <h2>Events</h2>
            { trace.events.map(event => serializeEvent(event)) }
        </div>
    }
    
    def serializeEvent(event : Event) : Elem = {
        return <div id={"#event"+event.hashCode}>
            <p>Evento</p>
            { for ((variable, binding) <- event.bindings.bindingsMap) yield
                <li class={if (event.lastBoundVariables contains variable) "recentlyBound" else ""}>{variable} = {binding.value} (at {binding.point})</li>
            }
        </div>
    }
    
}