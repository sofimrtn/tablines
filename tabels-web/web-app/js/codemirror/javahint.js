(function () {
  function forEach(arr, f) {
    for (var i = 0, e = arr.length; i < e; ++i) f(arr[i]);
  }
  
  function arrayContains(arr, item) {
    if (!Array.prototype.indexOf) {
      var i = arr.length;
      while (i--) {
        if (arr[i] === item) {
          return true;
        }
      }
      return false;
    }
    return arr.indexOf(item) != -1;
  }
 

  function scriptHint(editor, keywords, getToken) {
    // Find the token at the cursor
    var cur = editor.getCursor(), token = getToken(editor, cur), tprop = token;
    // If it's not a 'word-style' token, ignore the token.
		if (!/^[\w$_]*$/.test(token.string) || !(token.className ="notTabels")) {
      token = tprop = {start: cur.ch, end: cur.ch, string: "", state: token.state,
                       className: /[ ]+/.test(token.string) ? "property":token.className };
    }
    // If it is a property, find out what it is a property of.
    while (tprop.className == "property" || tprop.className == "notTabels") {
      tprop = getToken(editor, {line: cur.line, ch: tprop.start});
      if (! (/[ ]*/.test(tprop.string)|| !(token.className ="notTabels"))) return;
      tprop = getToken(editor, {line: cur.line, ch: tprop.start});
      if (tprop.className == "terminalOperator" || tprop.className=="variable"|| tprop.className=="variable-2"){
    	  
          do {
            tprop = getToken(editor, {line: cur.line, ch: tprop.start});
          } while (tprop.state.lexical.style != "template" && tprop.className != "iteratorStatement" &&  tprop.className != "prevStatement"&& tprop.className != "variableStatement")
    	  if(/[ ]+/.test(tprop.string))tprop = getToken(editor, {line: cur.line, ch: tprop.start});
      }
      if (tprop.string == ',' ||tprop.string == '(' ||(tprop.string == '['&& tprop.state.lexical.style != "template")) {
        
        do {
          tprop = getToken(editor, {line: cur.line, ch: tprop.start});
         
        } while (!((tprop.className == "tabelsExpression" )||(tprop.className == "variableStatement")))
        
      }
      if (tprop.string == ')' || (tprop.string == ']'&& tprop.state.lexical.style != "template") ) {
          var level = 1;
          do {
            tprop = getToken(editor, {line: cur.line, ch: tprop.start});
            switch(tprop.string){
            case ')': level ++; break;
            case '(': level --; break;
            case ']': level ++; break;
            case '[': level --; break;
            default: break;
            }
          } while (level > 0)
          do{
          tprop = getToken(editor, {line: cur.line, ch: tprop.start});
          }while(/[ ]+/.test(tprop.string))
          if (tprop.className == 'tabelsExpression' ) 
        	  do {
                  tprop = getToken(editor, {line: cur.line, ch: tprop.start});
                } while ( tprop.className != "iteratorStatement" && tprop.className != "tabelsExpression"&& tprop.className != "variableStatement" )
          
          
        }
      if((tprop.start == 0)&&(tprop.end == 0)) tprop.className="lineStart";
      
      if (!context) var context = [];
      context.push(tprop);
    }
    return {list: getCompletions(token, context, keywords,editor),
            from: {line: cur.line, ch: token.start},
            to: {line: cur.line, ch: token.end}};
  }

  CodeMirror.javascriptHint = function(editor) {
    return scriptHint(editor, tabelsStatements,
                      function (e, cur) {return e.getTokenAt(cur);});
  }

  var tabelsKeywords = ("@fetch,prefix,cells,rows,cols,sheets,files,horizontal,vetical,at,left,right,top,bottom,while,until,set,in,not windowed,for,let,match,starts when,starts at,filter").split(",");
  var tabelsStatements = ("@fetch,prefix,set,not windowed,for,let,match").split(",");
  var tabelsOperators = ("at,while,until,in,starts when,starts at,filter").split(",");
  var tabelsAtoms = ("cells,rows,cols,sheets,files,horizontal,vetical").split(",");
  var tabelsDimensions = ("cells,rows,cols,sheets,files").split(",");
  var tabelsIndications = ("left of,right of,top of,bottom of").split(",");
  var tabelsExpressions = ("concat,string-join,substring,contains,normalize-space,starts-with,ends-with," +
			"substring-before,substring-after,replace,upper-case,lower-case,matches,compare" +
			"translate,string-length,numeric-add,int-add,numeric-substract,int-substract," +
			"numeric-multiply,int-multiply,numeric-divide,numeric-integer-divide,numeric-nod," +
			"numeric-equal,numeric-greater-than,numeric-less-than,abs,ceiling,floor,round,int," +
			"float,double,levenshtein-distance,first-index-of,last-index-of,trim," +
			"dbpedia-disambiguation,get-row,get-col,resource,literal,setlangtag,not,and,or,if,then,else").split(",");

  /*var coffeescriptKeywords = ("and break catch class continue delete do else extends false finally for " +
                  "if in instanceof isnt new no not null of off on or return switch then throw true try typeof until void while with yes").split(" ");
*/
  function getCompletions(token, context, keywords,editor) {
    var found = [], start = token.string, localVars = [], templateVars = [];
    function maybeAdd(str) {
      if (str.indexOf(start.toLowerCase()) == 0 && !arrayContains(found, str)) found.push(str);
    }
    function gatherCompletions(obj) {
     if (obj.className == "notTabels")
     {
    	 scriptHint(editor, tabelsStatements,function (e, cur) {return e.getTokenAt(cur);});
       }
      else if ( obj.state.lexical.style == "iteratorStatement") forEach(tabelsOperators, maybeAdd);
      else if (obj.state.lexical.style == "variableStatement")
      {
      	forEach(localVars, maybeAdd);
      
      		forEach(tabelsOperators, maybeAdd);
      	
      
      		forEach(tabelsExpressions, maybeAdd);
      }
          //base = "";
        	
      else if (obj.state.lexical.style == "statementOperator")
        //base = "";
      {
      	
      		forEach(tabelsExpressions, maybeAdd);
      
      		forEach(tabelsAtoms, maybeAdd);
      }
      	
      else if (obj.state.lexical.style == "tabelsExpression"){
        //base = 1;
      	forEach(tabelsExpressions, maybeAdd);
      	forEach(localVars, maybeAdd);
      }
    
      else if(obj.state.lexical.style == "template" || obj.state.lexical.style == "base"){
      	for (var v = token.state.context.vars; v; v = v.next) templateVars.push(v.name);
      	forEach(templateVars, maybeAdd);
      }
      
    }
    
    for (var v = token.state.localVars; v; v = v.next) localVars.push(v.name); 
    
 
    if (context) {
        // If this is a property, see if it belongs to some object we can
        // find in the current environment.
        var obj = context.pop(), base;
        if(obj.state.lexical.style == "template"){
    		for (var v = token.state.context.vars; v; v = v.next) templateVars.push(v.name);
    		forEach(templateVars, maybeAdd);
    		
    	}
    	/*else if (obj.className == "lineStart")
        		forEach(keywords, maybeAdd);*/
        else switch (obj.className) {
		        case 'iteratorStatement':{
			        	 /*if(obj.state.lexical.style == "for1"){
			        		maybeAdd("?");
			         		forEach(tabelsDimensions, maybeAdd);
			         	}else*/
		        	
		        	forEach(tabelsOperators, maybeAdd);
		        	
		        	break;
		        	}
		        case 'variableStatement':{
		        	forEach(localVars, maybeAdd);
		        	if(obj.string.toLowerCase() == "match")
		        		forEach(tabelsOperators, maybeAdd);
		    	   	else
		    	   		forEach(tabelsExpressions, maybeAdd);
		        	break;
		        }
		        case 'statementOperator':{
		        	if(obj.string.toLowerCase() == "filter")
		        		forEach(tabelsExpressions, maybeAdd);
		        	else
		        		forEach(tabelsAtoms, maybeAdd);
		        	break;
		        }
		        case 'tabelsExpression':{
		          //base = 1;
		        	forEach(tabelsExpressions, maybeAdd);
		        	forEach(localVars, maybeAdd);
		        	break;
		        }
		        case 'tabelsPosition':{
			          //base = 1;
		        	if(/(bottom|top|righ|left) of/.test(obj.string.toLowerCase()) )
		        		forEach(localVars, maybeAdd);
			        	
		        	break;
			    }
		        case 'lineStart':{
		            forEach(keywords, maybeAdd);
		    	break;
		        }
			    default:{
			    	if(obj.state.lexical.style == "base")
			    	{
			    		for (var v = token.state.context.vars; v; v = v.next) templateVars.push(v.name);
			    		forEach(templateVars, maybeAdd);
			    		
			    	}
			    	break;
		        }
        	}
      }
    else {
      // If not, just look in the window object and any local scope
      // (reading into JS mode internals to get at the local variables)
    	gatherCompletions(token);
    	//forEach(keywords, maybeAdd);
    }
    return found;
  }
})();