package es.ctic.tabels

import scala.collection.mutable.ListBuffer

import grizzled.slf4j.Logging

object Autogenerator extends Logging {
    
    val EX_NS = "http://example.org/ex#"
    val defaultPrefixes = Seq(("ex", ns()))
    
    def autogenerateProgram(dataSource : DataSource) : S = {
        val statements = new ListBuffer[TabelsStatement]
        val tripleTemplates = new ListBuffer[TripleTemplate]
        val filename = dataSource.filenames(0)
        val sheet = dataSource.getTabs(filename)(0)
        logger.info("Autogenerating Tabels program for file " + filename + ", sheet " + sheet)
        
        val cols = dataSource.getCols(filename, sheet)
        val variables = for (col <- List.range(1, cols)) yield Variable("?v" + col)
        val tuple = Tuple(variables)
        
        tripleTemplates += TripleTemplate(Right(Variable("?resource")), Left(RDF_TYPE), Left(ns("SomeResource")))
        tripleTemplates ++= (for (col <- List.range(1, cols)) yield TripleTemplate(Right(Variable("?resource")), Left(ns("attr" + col)), Right(Variable("?v" + col))))
        
        val matchStmt = MatchStatement(tuple)
        val letStmt = LetStatement(Variable("?resource"), ResourceExpression(VariableReference(Variable("?rowId")), ns()), Some(matchStmt))
        val forStmt = IteratorStatement(Dimension.rows, variable = Some(Variable("?rowId")), nestedStatement = Some(letStmt))
        val setSheetStmt = SetInDimensionStatement(Dimension.sheets, fixedDimension = sheet, nestedStatement = Some(forStmt))
        val setFileStmt = SetInDimensionStatement(Dimension.files, fixedDimension = filename, nestedStatement = Some(setSheetStmt))
        val template = Template(tripleTemplates)
        val templates = List(template)
        statements += setFileStmt
        return S(defaultPrefixes, statements, templates)
    }
    
    private def ns(localName : String = "") = Resource(EX_NS + localName)
    
}