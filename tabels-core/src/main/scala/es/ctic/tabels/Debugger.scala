package es.ctic.tabels

import java.io.PrintStream
import scala.xml.Elem
import grizzled.slf4j.Logging

class Debugger extends Logging {
    
    val maxRows = 25
    
    def serializeInterpreterTrace(trace : InterpreterTrace, printStream : PrintStream) {
        val spreadsheets = serializeSpreadsheets(trace)
        val head = <head><title>Tabels debugger</title></head>
        val body = <body><h1>Tabels debugger</h1>{spreadsheets}</body>
        val htmlDoc = <html><head></head>{body}</html>
        printStream.print(htmlDoc.toString)
    }
    
    def serializeSpreadsheets(trace : InterpreterTrace) : Elem = {
        return <div>
            <h2>Input files</h2> {
                trace.dataSource.filenames.map(filename =>
                    <div>
                        <h3>{filename}</h3>
                        { trace.dataSource.getTabs(filename).map(tabName =>
                            <div>
                                <h4>{tabName}</h4>
                                { serializeTab(trace, filename, tabName) }
                            </div>
                        )}
                    </div>
                )
            }            
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
    
}