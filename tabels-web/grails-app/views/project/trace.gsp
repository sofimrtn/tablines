<html>
    <head>
        <title>Tabels trace</title>
        <meta name="layout" content="main" />
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js" type="text/javascript"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js" type="text/javascript"></script>
        <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/south-street/jquery-ui.css" rel="stylesheet"></link>
        <script src="http://imakewebthings.com/deck.js/modernizr.custom.js" type="text/javascript"></script>
    	<link rel="stylesheet" id="style-theme-link" href="http://imakewebthings.com/deck.js/themes/style/web-2.0.css"></link>
    	<link rel="stylesheet" id="transition-theme-link" href="http://imakewebthings.com/deck.js/themes/transition/horizontal-slide.css"></link>
        <script src="http://imakewebthings.com/deck.js/core/deck.core.js" type="text/javascript"></script>
        <script src="http://imakewebthings.com/deck.js/extensions/status/deck.status.js" type="text/javascript"></script>
        <script src="http://imakewebthings.com/deck.js/extensions/navigation/deck.navigation.js" type="text/javascript"></script>
        <style type="text/css">
        .recentlyBound { background-color: lightyellow; }
        .var { font-weight: bold; }
        .value { color: violet; }
        .spreadsheetColHeader { background-color: lightgrey; }
        .spreadsheetRowHeader { background-color: lightgrey; }
        .odd { background-color: #D0EED0; }
        .even { background-color: #E0FFE0; }
        </style>
        
    </head>
    <body>
        <script>
        $(function() {
    		$( '.tabs' ).tabs();
    		$.deck('.slide');
    		$('.spreadsheet').resizable();
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
        });
        </script>
        <h2>Transformation trace</h2>
        <p class="backLink"><g:link action="index"><g:message code="msg.back.to.project.link"/></g:link></p>
        <h3>Spreadsheets</h3>
        <div>
            <div id="fileTabs" class="tabs">
                <ul>
                    <g:each in="${scala.collection.JavaConverters.asJavaCollectionConverter(trace.dataSource.filenames).asJavaCollection()}" var="filename">
                        <li><a href="#fileTab${filename.hashCode()}">${filename}</a></li>
                    </g:each>
                </ul>
                <g:each in="${scala.collection.JavaConverters.asJavaCollectionConverter(trace.dataSource.filenames).asJavaCollection()}" var="filename">
                    <div id="fileTab${filename.hashCode()}" class="tabs">
                        <ul>
                            <g:each in="${scala.collection.JavaConverters.asJavaCollectionConverter(trace.dataSource.getTabs(filename)).asJavaCollection()}" var="tabName">
                                <li><a href="#tabTab${tabName.hashCode()}">${tabName}</a></li>
                            </g:each>
                        </ul>
                        <g:each in="${scala.collection.JavaConverters.asJavaCollectionConverter(trace.dataSource.getTabs(filename)).asJavaCollection()}" var="tabName">
                            <div id="tabTab${tabName.hashCode()}">
                                <g:set var="rows" value="${trace.dataSource.getRows(filename, tabName)}" />
                                <g:set var="cols" value="${trace.dataSource.getCols(filename, tabName)}" />
                                <g:if test="${rows*cols == 0}">
                                    <div/>
                                </g:if>
                                <g:else>
                                    <g:set var="tabCode" value="file${filename.hashCode()}-tab${tabName.hashCode()}" />
                                    <table class="spreadsheet" id="spreadsheet-${tabCode}" border="1">
                                        <thead>
                                            <tr class="spreadsheetColHeader">
                                                <th></th>
                                                <g:each in="${0..(cols-1)}" var="col">
                                                    <th> <g:intToAlpha col="${col}" /> </th>
                                                </g:each>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <g:each in="${0..(Math.min(rows,25)-1)}" var="row">
                                                <tr class="spreadsheetRow ${row%2==0 ? 'odd' : 'even'}" id="tabCode-row${row}">
                                                    <td class="spreadsheetRowHeader">${row+1}</td>
                                                    <g:each in="${0..(cols-1)}" var="col">
                                                        <g:set var="point" value="${new es.ctic.tabels.Point(filename, tabName, col, row)}" />
                                                        <g:set var="value" value="${trace.dataSource.getValue(point).content.value()}" />
                                                        <td class="spreadsheetCell" id="point${point.hashCode()}">${value}</td>
                                                    </g:each>
                                                </tr>                
                                            </g:each>
                                        </tbody>
                                    </table>
                                </g:else>
                            </div>
                        </g:each>
                    </div>
                </g:each>
            </div>            
        </div>
        
        
        <h3>Events</h3>

        <div>
        	<p class="deck-status">
                <a href="#" class="deck-prev-link" title="Previous">Prev</a>
        		<span class="deck-status-current"></span>
        		/
        		<span class="deck-status-total"></span>
            	<a href="#" class="deck-next-link" title="Next">Next</a>
        	</p>
            <article class="deck-container" style="position: relative">
                <g:each in="${scala.collection.JavaConverters.asJavaCollectionConverter(trace.events).asJavaCollection()}" var="event" status="index">
                    <section class="slide" id="#event${index}">
                        <g:each in="${scala.collection.JavaConverters.mapAsJavaMapConverter(event.bindings.bindingsMap).asJava()}" var="variableBindingEntry">
                            <g:set var="variable" value="${variableBindingEntry.key}" />
                            <g:set var="varBinding" value="${variableBindingEntry.value}" />
                            <li class="${event.lastBoundVariables.contains(variable) ? 'recentlyBound' : ''}">
                                <span class="var">${variable}</span> =
                                <span class="value">${varBinding.value}<!-- FIXME {binding.value match { case l : Literal => l.value.toString
                                case r : Resource => r.toString}} --></span>
                                (at <g:intToAlpha col="${binding.point.col}" />${binding.point.row+1})
                            </li>
                        </g:each>
                        <script>
                            function event${index}() {
                                <g:each in="${scala.collection.JavaConverters.mapAsJavaMapConverter(event.bindings.bindingsMap).asJava()}" var="variableBindingEntry">
                                    <g:set var="variable" value="${variableBindingEntry.key}" />
                                    <g:set var="varBinding" value="${variableBindingEntry.value}" />
                                    var $cell = $('#point${varBinding.point.hashCode()}');
                                    $cell.attr('title',$cell.attr('title')+'${variable}');
                                    <g:if test="event.lastBoundVariables.contains(variable)">
                                        $cell.css('background-color','yellow').css('color','white').animate({'background-color': 'green'});
                                    </g:if>
                                    <g:else>
                                        $cell.css('background-color','green');
                                    </g:else>
                                </g:each>
                            }
                        </script>
                    </section>
                </g:each>
            </article>
        </div>
        
    </body>
</html>
