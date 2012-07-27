package es.ctic.tabels

import org.apache.commons.lang.{StringUtils,WordUtils}
import scala.collection.mutable.ListBuffer
import grizzled.slf4j.Logging
import es.ctic.tabels.CommonNamespaces._
import es.ctic.tabels.TripleTemplate._   // implicit conversions


trait Autogenerator extends Logging {

    def autogenerateProgram(dataSource : DataSource) : S

    protected def hasHeaderRow(dataSource : DataSource, filename : String, sheet : String) : Boolean = {
        val firstRow = dataSource.getRow(filename, sheet, 0)
        val secondRow = dataSource.getRow(filename, sheet, 1)
        val firstRowFormats = firstRow map (_.rdfType)
        val secondRowFormats = secondRow map (_.rdfType)
        val differentFormatsCount = firstRowFormats zip secondRowFormats count { case (x,y) => !(x == y) }
        logger.debug("Different formats in " + differentFormatsCount + " cols")
        return differentFormatsCount > 0
    }
    
    protected def toLowerCamelCase(str : String) : String = StringUtils.uncapitalize(WordUtils.capitalize(str))
    
    protected def literalToLocalName(literal : Literal) : Option[String] = {
        val normalizedString = toLowerCamelCase(literal.value.toString).replaceAll("[^a-zA-Z0-9_]","")
        if (normalizedString == "")
            return None
        else if (normalizedString(0).isDigit)
            return Some("n" + normalizedString)
        else Some(normalizedString)
    }
    
    protected def literalsToUniqueLocalNames(literals : Seq[Literal], prefix : String) : Seq[String] =
        literals.map(literalToLocalName _).zipWithIndex.foldLeft (Nil : List[String]){
            case (xs,(Some(ln),_)) if (!xs.contains(ln)) => ln :: xs
            case (xs,(_,i)) => (prefix + (i+1).toString) :: xs
        }.reverse
    
}

class BasicAutogenerator(defaultNamespace : Namespace = EX, projectId: String = "DefaultTabels") extends Autogenerator with Logging {

	implicit val evaluationContext = EvaluationContext()
	
    val prefixes = Seq(("project", defaultNamespace()), ("my", defaultNamespace("resource/")), ("rdf", RDF()), ("rdfs", RDFS()),("dcat", DCAT()), ("dct", DCT()), ("foaf", FOAF()))

    override def autogenerateProgram(dataSource : DataSource) : S = {
        if (dataSource.filenames.isEmpty) {
            return S()
        }
        val statements = new ListBuffer[TabelsStatement]
        val tripleTemplates = new ListBuffer[TripleTemplate]
        val filename = dataSource.filenames(0)
        val sheet = dataSource.getTabs(filename)(0)
        logger.info("Autogenerating Tabels program for project "+ projectId +", file " + filename + ", sheet " + sheet)
        
        val hasHeader = hasHeaderRow(dataSource, filename, sheet)
        lazy val headerRow = dataSource.getRow(filename, sheet, 0)
        val cols = dataSource.getCols(filename, sheet)
        val variables : Seq[Variable] =
            if (hasHeader) (literalsToUniqueLocalNames(headerRow, "v") map (ln => Variable("?" + ln)))
            else for (col <- List.range(1, cols+1)) yield Variable("?v" + col)
        val properties : Seq[Resource] =
            if (hasHeader) (literalsToUniqueLocalNames(headerRow, "prop") map (ln => defaultNamespace(ln)))
            else for (col <- List.range(1, cols+1)) yield defaultNamespace("attr" + col)
        val tuple = Tuple(variables)
        val resource = Variable("?resource")
        val rowId = Variable("?rowId")
        
        val matchStmt = MatchStatement(tuple)
        val letStmt = LetStatement(resource, ResourceExpression(VariableReference(rowId), defaultNamespace()), Some(matchStmt))
        val forStmt = IteratorStatement(Dimension.rows, variable = Some(rowId), filter = if (hasHeader) Some(GetRowExpression(rowId)) else None, nestedStatement = Some(letStmt))
        val inSheetStmt = SetInDimensionStatement(Dimension.sheets, fixedDimension = sheet, nestedStatement = Some(forStmt))
        val inFileStmt = SetInDimensionStatement(Dimension.files, fixedDimension = filename, nestedStatement = Some(inSheetStmt))
        statements += inFileStmt
        
        val resourceClass = defaultNamespace("SomeResource")
        tripleTemplates += TripleTemplate(resource, RDF_TYPE, resourceClass)
        tripleTemplates ++= properties zip variables map { case (p,v) => TripleTemplate(resource, p, v) }
        val template = Template(tripleTemplates)
        val classDefinitionTemplate = Template(Seq(TripleTemplate(resourceClass, RDF("type"), RDFS("Class"))))
        val propertyDefinitionTemplate = Template(properties map (p => TripleTemplate(p,RDF("type"),RDF("Property"))))
       
        val templates = List(dcatTriples.dataSetMetadataTemplate(defaultNamespace,projectId), dcatTriples.dataSetRDFDistributionMetadataTemplate(defaultNamespace,projectId),dcatTriples.dataSetTurtleDistributionMetadataTemplate(defaultNamespace,projectId), dcatTriples.dataSetN3DistributionMetadataTemplate(defaultNamespace,projectId), template, classDefinitionTemplate, propertyDefinitionTemplate)
        
        val program = S(Directives(), prefixes, statements, templates)
        logger.info("Autogenerated Tabels program: " + program)
        return program
    }
    
}

class ScovoAutogenerator(defaultNamespace : Namespace = EX, projectId: String  = "DefaultTabels") extends Autogenerator with Logging {
	implicit val evaluationContext = EvaluationContext()
    
    val prefixes = Seq(("project", defaultNamespace()), ("my", defaultNamespace("resource/")), ("scv", SCV()), ("rdf", RDF()), ("rdfs", RDFS()), ("skos", SKOS()),("dcat", DCAT()), ("dct", DCT()), ("foaf", FOAF()))
    
    override def autogenerateProgram(dataSource : DataSource) : S = {
        if (dataSource.filenames.isEmpty) {
            return S()
        }
        val filename = dataSource.filenames(0)
        val sheet = dataSource.getTabs(filename)(0)
        logger.info("Autogenerating Tabels program for file " + filename + ", sheet " + sheet)
        
        val cols = dataSource.getCols(filename, sheet)

        val rawItemValue = Variable("?rawItemValue")
        val itemValue = Variable("?itemValue")
        val item = Variable("?item")
        val rowId = Variable("?rowId")
        val dataset = defaultNamespace("dataset")

        // dimensions
        val dimensionLabelVars : List[Variable] = for (col <- List.range(1, cols)) yield Variable("?dl" + col)
        val headerTuple = Tuple(dimensionLabelVars)
        val dimensionVars : List[Variable] = for (col <- List.range(1, cols)) yield Variable("?d" + col)

        // dimension values
        val dimensionValueLabelsVars : List[Variable] = for (col <- List.range(1, cols)) yield Variable("?dvl" + col)
        val rowTuple = Tuple(dimensionValueLabelsVars :+ rawItemValue)
        val dimensionValueVars : List[Variable] = for (col <- List.range(1, cols)) yield Variable("?dv" + col)        
        
        val terminalStmt : Option[TabelsStatement] = None
        val letDimensionValues : Option[TabelsStatement] = List.range(0, cols-1).foldLeft(terminalStmt)(
            (innerStmt, i) => Some(LetStatement(dimensionValueVars(i),
                                                ResourceExpression(VariableReference(dimensionValueLabelsVars(i)), defaultNamespace()),
                                                nestedStatement = innerStmt))
        )
        
        val letTreatValueStmt = LetStatement(itemValue, NumericFunctions.double.createExpression(VariableReference(rawItemValue)),nestedStatement = letDimensionValues )
        val whenStmt = WhenConditionalStatement(Some(Left(NumericFunctions.canBeDouble.createExpression(VariableReference(rawItemValue)))), nestedStatement = Some(letTreatValueStmt))
        val matchRowStmt = MatchStatement(rowTuple, nestedStatement = Some(whenStmt))
        val letItemStmt = LetStatement(item, ResourceExpression(GetRowExpression(rowId), defaultNamespace()), nestedStatement =Some(matchRowStmt))
        val forStmt : TabelsStatement = IteratorStatement(Dimension.rows, variable = Some(rowId),
            filter = Some(GetRowExpression(rowId)), nestedStatement = Some(letItemStmt))
        
        val letDimensions : TabelsStatement = List.range(0, cols-1).foldLeft(forStmt)(
            (innerStmt, i) => LetStatement(dimensionVars(i),
                                           ResourceExpression(VariableReference(dimensionLabelVars(i)), defaultNamespace()),
                                           nestedStatement = Some(innerStmt))
        )
        
        val matchHeadersStmt = MatchStatement(headerTuple, position = Some(FixedPosition(row = 0, col = 0)), nestedStatement = Some(letDimensions))
        val inSheetStmt = SetInDimensionStatement(Dimension.sheets, fixedDimension = sheet, nestedStatement = Some(matchHeadersStmt))
        val inFileStmt = SetInDimensionStatement(Dimension.files, fixedDimension = filename, nestedStatement = Some(inSheetStmt))

        val templates = new ListBuffer[Template]
        
       
        templates += dcatTriples.dataSetMetadataTemplate(defaultNamespace,projectId)
        templates += dcatTriples.dataSetRDFDistributionMetadataTemplate(defaultNamespace,projectId)
        templates += dcatTriples.dataSetTurtleDistributionMetadataTemplate(defaultNamespace,projectId)
        templates += dcatTriples.dataSetN3DistributionMetadataTemplate(defaultNamespace,projectId)
                 
        val itemTripleTemplates = new ListBuffer[TripleTemplate]
        itemTripleTemplates ++= List(
            (item, RDF_TYPE, SCV("Item")),
            (item, SCV("dataset"), dataset),
            (item, RDF("value"), itemValue)
        )
        itemTripleTemplates ++= (dimensionValueVars map (TripleTemplate(item, SCV("dimension"), _)))
        templates += Template(itemTripleTemplates)
        
        templates ++= (for (dim <- List.range(0, cols-1)) yield Template(List(
            (dimensionValueVars(dim), RDF_TYPE, dimensionVars(dim)),
            (dimensionValueVars(dim), SKOS("prefLabel"), dimensionValueLabelsVars(dim)),
            (dimensionVars(dim), RDFS("subClassOf"), SCV("Dimension")),
            (dimensionVars(dim), SKOS("prefLabel"), dimensionLabelVars(dim))
        )))
        
        templates += Template(List(
            (dataset, RDF_TYPE, SCV("Dataset")),
            (dataset, SKOS("prefLabel"), Literal(projectId + "SKOSDataSet"))
        ))

        val program = S(Directives(), prefixes, Seq(inFileStmt), templates)
        logger.info("Autogenerated Tabels program: " + program)
        return program
    }
    
}
object dcatTriples{
  def catalogMetadataTemplate(defaultNamespace:Namespace = EX, projectId: String) = Template(Seq(TripleTemplate(defaultNamespace("TabelsDataCatalog"),RDF_TYPE,DCAT("Catalog")),
        					TripleTemplate(defaultNamespace("TabelsDataCatalog"),DCT("title"),Literal("Tabels AutoGenerated Catalog")),
        					TripleTemplate(defaultNamespace("TabelsDataCatalog"),RDF("label"),Literal("Tabels AutoGenerated Catalog")),
        					TripleTemplate(defaultNamespace("TabelsDataCatalog"),DCT("publisher"),defaultNamespace("TabelsAutoGenerator")),
        					TripleTemplate(defaultNamespace("TabelsDataCatalog"),DCAT("dataset"),defaultNamespace("DefaultTabelsDataSet"))
        					))
  def catalogPublisherMetadataTemplate (defaultNamespace:Namespace = EX, projectId: String) = Template(Seq(TripleTemplate(defaultNamespace("TabelsAutoGenerator"),RDF_TYPE,FOAF("Project")),
        					TripleTemplate(defaultNamespace("TabelsAutoGenerator"),RDFS("label"),Literal("Tabels autogenerator program")),
        					TripleTemplate(defaultNamespace("TabelsAutoGenerator"),FOAF("homepage"),Literal("http://localhost:8080/tabels-web/") )  
        					))
  def dataSetMetadataTemplate (defaultNamespace:Namespace = EX, projectId: String) = Template(Seq(TripleTemplate(defaultNamespace(projectId+"DataSet"),RDF_TYPE,DCAT("Dataset")),
        					TripleTemplate(defaultNamespace(projectId+"DataSet"),DCT("title"),Literal("Default title for autogenerated tabels data sets")),
        					TripleTemplate(defaultNamespace(projectId+"DataSet"),RDFS("keyword"),Literal("tabels")),
        					TripleTemplate(defaultNamespace(projectId+"DataSet"),DCAT("Distribution"),defaultNamespace(projectId+"DatasetRDF")),
        					TripleTemplate(defaultNamespace(projectId+"DataSet"),DCAT("Distribution"),defaultNamespace(projectId+"DatasetTurtle")),
        					TripleTemplate(defaultNamespace(projectId+"DataSet"),DCAT("Distribution"),defaultNamespace(projectId+"DatasetN3"))
        					))
  def dataSetRDFDistributionMetadataTemplate (defaultNamespace:Namespace = EX, projectId: String) = Template(Seq(TripleTemplate(defaultNamespace(projectId+"DatasetRDF"),RDF_TYPE,DCAT("Distribution")),
        					TripleTemplate(defaultNamespace(projectId+"DatasetRDF"),DCAT("accesURL"),defaultNamespace("data")),
        					TripleTemplate(defaultNamespace(projectId+"DatasetRDF"),DCT("format"),BlankNode(Left("B0"))),
        					TripleTemplate(BlankNode(Left("B0")),RDF_TYPE,DCT("IMT")),
        					TripleTemplate(BlankNode(Left("B0")),RDF("value"),Literal("application/rdf+xml")),
        					TripleTemplate(BlankNode(Left("B0")),RDFS("label"),Literal("RDF+XML"))
        					))
  def dataSetTurtleDistributionMetadataTemplate (defaultNamespace:Namespace = EX, projectId: String) = Template(Seq(TripleTemplate(defaultNamespace(projectId+"DatasetTurtle"),RDF_TYPE,DCAT("Distribution")),
        					TripleTemplate(defaultNamespace(projectId+"DatasetTurtle"),DCAT("accesURL"),defaultNamespace("data?format=ttl")),
        					TripleTemplate(defaultNamespace(projectId+"DatasetTurtle"),DCT("format"),BlankNode(Left("B1"))),
        					TripleTemplate(BlankNode(Left("B1")),RDF_TYPE,DCT("IMT")),
        					TripleTemplate(BlankNode(Left("B1")),RDF("value"),Literal("text/turtle")),
        					TripleTemplate(BlankNode(Left("B1")),RDFS("label"),Literal("TURTLE"))
        					))
 def dataSetN3DistributionMetadataTemplate (defaultNamespace:Namespace = EX, projectId: String) = Template(Seq(TripleTemplate(defaultNamespace(projectId+"DatasetN3"),RDF_TYPE,DCAT("Distribution")),
        					TripleTemplate(defaultNamespace(projectId+"DatasetN3"),DCAT("accesURL"),defaultNamespace("data?format=text")),
        					TripleTemplate(defaultNamespace(projectId+"DatasetN3"),DCT("format"),BlankNode(Left("B2"))),
        					TripleTemplate(BlankNode(Left("B2")),RDF_TYPE,DCT("IMT")),
        					TripleTemplate(BlankNode(Left("B2")),RDF("value"),Literal("text/n3")),
        					TripleTemplate(BlankNode(Left("B2")),RDFS("label"),Literal("N3"))
        					))
        					
        					
        
}