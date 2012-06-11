CodeMirror.defineMode("tabelscomplete", function(config) {
  var indentUnit = config.indentUnit;
  var curPunc;

  function wordRegexp(words) {
    return new RegExp("^(?:" + words.join("|") + ")$", "i");
  }
  
  var ops = wordRegexp(["str", "lang", "langmatches","datatype", "bound", "sameterm", "isiri", "isuri",
                        "isblank", "isliteral"/*, "a"*/]);
  var keywords = wordRegexp(["in", "prefix", "cells", "rows", "cols", "for", "sheets",
                             "files", "ignore", "blanks", "filter", "horizontal", "vertical", "let", "at", "match", "left",
                             "right", "top", "bottom", "while", "until", "set", "match", "starts","at", "when", "@fetch", "not windowed"]);
  
  var terminals = wordRegexp(["cells", "rows", "cols", "sheets", "files",
                              "ignore blanks", 
                              "horizontal", "vertical" ]);
  
  var operators = wordRegexp(("in,filter,while,until,starts at,starts when,at").split(","));
  var expressions = wordRegexp(("expression").split(","));
  
  var tabelsExpressions = wordRegexp(("concat,string-join,substring,contains,normalize-space,starts-with,ends-with," +
  									"substring-before,substring-after,replace,upper-case,lower-case,matches,compare" +
  									"translate,string-length,numeric-add,int-add,numeric-substract,int-substract," +
  									"numeric-multiply,int-multiply,numeric-divide,numeric-integer-divide,numeric-nod," +
  									"numeric-equal,numeric-greater-than,numeric-less-than,abs,ceiling,floor,round,int," +
  									"float,double,levenshtein-distance,first-index-of,last-index-of,trim," +
  									"dbpedia-disambiguation,get-row,get-col,resource,literal,setlangtag,not,and,or,if,then,else").split(","));
 
  var iteratorStatements = wordRegexp(["set", "not windowed","for"]);
  var variableStatements = wordRegexp(["let", "match"]);
  var prevStatements = wordRegexp(["prefix", "@fetch"]);
  var operatorChars = /[*+\-<>=&|]/;
  var tabelsPosition = /[A-Z]+[0-9]+|(bottom |top |left |right )of/;
  var statementLimit = null

  function tokenBase(stream, state) {
    var ch = stream.next();
    curPunc = null;
    statementLimit =false;
    if(stream.eol())
  	  if (state.context && state.context.type != "pattern") statementLimit = true;
    
  	  
    if (ch == "$" || ch == "?") {
      stream.match(/^[\w\d]*/);
      curPunc = stream.current();
      return "variable-2";
    }
    else if (ch == "<" && !stream.match(/^[\s\u00a0=]/, false)) {
      stream.match(/^[^\s\u00a0>]*>?/);
      return "atom";
    }
    else if (ch == "\"" || ch == "'") {
      state.tokenize = tokenLiteral(ch);
      return state.tokenize(stream, state);
    }
    else if(/[\n]/.test(ch)){
        curPunc = ch;
        return null;
      }
    else if (/[{}\(\),\.;\[\]]/.test(ch)) {
      curPunc = ch;
      return null;
    }
    else if (ch == "#") {
      stream.skipToEnd();
      return "comment";
    }
    else if (ch == "/") {
       if (stream.eat("/")) {
        stream.skipToEnd();
        return "comment";
      }else 
      if(stream.eat("*")) {
        state.tokenize = tokenComment;
        return tokenComment(stream, state);
      }
    
    }
    else if (operatorChars.test(ch)) {
      stream.eatWhile(operatorChars);
      curPunc = stream.current();
      return "operator";
    }
    else if (ch == ":") {
      stream.eatWhile(/[\w\d\._\-]/);
      curPunc = stream.current(); /*******************/
      return "atom";
    }
    else {
      stream.eatWhile(/[_\w\d\-]/); 
      if (stream.eat(":")) {
    	if(/[\w\d_\-]/.test(stream.peek()))
    	{
	        stream.eatWhile(/[\w\d_\-]/);
	        curPunc = stream.current();
	        return "atom" ;
        }
    	curPunc = stream.current();
        return "variable" ;
      }
      var word = stream.current(), type;
      if (ops.test(word))
        return null;
      else if (iteratorStatements.test(word)){
    	  curPunc = word;
          return "iteratorStatement";}
      else if (variableStatements.test(word)){
    	  curPunc = word;
          return "variableStatement";}
      else if (prevStatements.test(word)){
    	  curPunc = word;
          return "prevStatement";}
      else if(operators.test(word)){
    	curPunc = word;
      	return "statementOperator";}
      else if(terminals.test(word)){
      	curPunc = word;
        	return "terminalOperator";}
      else if(tabelsExpressions.test(word)){
        	curPunc = word;
          	return "tabelsExpression";}
      else if(tabelsPosition.test(word)){
      	curPunc = word;
        	return "tabelsPosition";}
      else if (keywords.test(word)){
    	    var streampos=stream.pos;
    	    stream.eatSpace();
    	    stream.eatWhile(/[^ ]/);
    	  	word= stream.current();
    	  	if(operators.test(word)){
    	  		curPunc = word;
    	  		return "statementOperator";}
    	  	else if(tabelsPosition.test(word)){
    	  		curPunc = word;
    	  		return "tabelsPosition";}
    	  	else if(iteratorStatements.test(word)){
        	  		curPunc = word;
        	  		return "iteratorStatement";}
    	  	
    	    stream.pos=streampos;
    	    curPunc = stream.current();
    	  	return "keyword";
      
      }
      else
    	if(/[\d]+/.test(word)) return "number";
      	curPunc = stream.current();
        return "notTabels";
    }
  }

  function tokenLiteral(quote) {
    return function(stream, state) {
      var escaped = false, ch;
      while ((ch = stream.next()) != null) {
        if (ch == quote && !escaped) {
          state.tokenize = tokenBase;
          break;
        }
        escaped = !escaped && ch == "\\";
      }
      return "string";
    };
  }
  //function from https://code.google.com/p/tidej/source/browse/war/CodeMirror-2.16/mode/clike/clike.js?spec=svn6734953c22005945d627da187ce48ca42666fd62&name=6734953c22&r=6734953c22005945d627da187ce48ca42666fd62
  function tokenComment(stream, state) {
    var maybeEnd = false, ch;
    while (ch = stream.next()) {
      if (ch == "/" && maybeEnd) {
        state.tokenize = tokenBase;
        break;
      }
      maybeEnd = (ch == "*");
    }
    return "comment";
  }
  var defaultVars = null;
  var patternVars = null;
  
  function register(state,varname) {
	   
	    if (state.context) {
	     
	      for (var v = state.localVars; v; v = v.next)
	        if (v.name == varname) return;
	      state.localVars = {name: varname, next: state.localVars};
	      
	      for (var v = patternVars; v; v = v.next)
		        if (v.name == varname) return;
	      patternVars = {name: varname, next: patternVars};
	    }
	  }
  
  
  function pushLocalVar(state) {
	  if (!state.context) state.localVars = defaultVars;
    state.context = {prev: state.context.prev, indent: state.context.indent, col: state.context.col, type: state.context.type, vars: state.localVars};
  }
  /*function pushContext(state, type, col) {
	  if (!state.context) state.localVars = defaultVars;
    state.context = {prev: state.context, indent: state.indent, col: col, type: type, vars: state.localVars};
  }
  function popContext(state) {
    state.indent = state.context.indent;
    state.localVars = state.context.vars;
    state.context = state.context.prev;
    
  }*/
  /**************************/ 
  var atomicTypes = {"uri":true, "atom": true, "number": true, "variable": true, "string": true, "regexp": true, "keyword":true };
  var cx = {state: null, column: null, marked: null, cc: null};
  var type, content;
 
  function JSLexical(indented, column, style, align, prev, info) {
	    this.indented = indented;
	    this.column = column;
	    this.style = style;
	    this.prev = prev;
	    this.info = info;
	    if (align != null) this.align = align;
	}
  
  function pass() {
    for (var i = arguments.length - 1; i >= 0; i--) cx.cc.push(arguments[i]);
  }
  function cont() {
    pass.apply(null, arguments);
    return true;
  }
  function pushContext() {
	    if (!cx.state.context) cx.state.localVars = defaultVars;
	    cx.state.context = {prev: cx.state.context, vars: cx.state.localVars};
	  }
  function popContext() {
		if (cx.state.context){
			cx.state.localVars = cx.state.context.vars;
			cx.state.context = cx.state.context.prev;
		}
	  }
  
  function pushPatternContext() {
	    if (!cx.state.context) cx.state.localVars = defaultVars;
	    cx.state.context = {prev: cx.state.context, vars: patternVars};
	  }
  function pushlex(style, info) {
	    var result = function() {
	      var state = cx.state;
	      state.lexical = new JSLexical(state.indented, cx.stream.column(), style, null, state.lexical, info)
	      
	    };
	    result.lex = true;
	    return result;
	  }
  function poplex() {
	    var state = cx.state;
	    if (state.lexical.prev) {
	      if (state.lexical.style == ")")
	        state.indented = state.lexical.indented;
	      state.lexical = state.lexical.prev;
	    }
	  }
	  poplex.lex = true;
  
  function statement(style,value,state) {
  	
  	    if (value == "{") return cont(pushContext,pushlex("}"), block, poplex, popContext);
  	    if (value == ".") return cont();
  	    if (value == "construct") return cont(pushPatternContext,pushlex("template"), expect("{"), templateBlock, poplex, popContext);
  	    if (style == "prevStatement") return cont(pushlex("prevStatement"),/*pushContext,*/vardef1,poplex,statement/*,popContext*/);
  	    if (style == "iteratorStatement") return pass(pushlex("iteratorStatement"), /*pushContext,*/iterationspec, poplex, statement/*, popContext*/);
  	    if (style == "variableStatement") return pass(pushlex("variableStatement"), /*pushContext,*/varspec, poplex, statement/*, popContext*/);
  	    if (value == ";") return pass();
  	
  	    return pass(pushlex("stat"), expression, poplex);
  	  }
  
  function expression(style, value) {
	    if (atomicTypes.hasOwnProperty(style)) return cont(maybeoperator);
	   // if (type == "keyword c") return cont(maybeexpression);
	    if (style == "statementOperator") return pass (pushlex("statementOperator"), statementSpec, poplex) 
	    if (style == "terminalOperator") return cont (pushlex("teminalOperator"), mayvardef, poplex) 
	   // if (curPunc == "(") return cont(pushlex(")"), expression, expect(")"), poplex);
	    if (style == "operator") return cont(expression);
	  //  if (curPunc == "[") return cont(pushlex("]"), commasep(vardef1, "]"), poplex, maybeoperator);
	  //  if (curPunc == "{") return cont(pushlex("}"), commasep(objprop, "}"), poplex, maybeoperator);
	    if (style == "tabelsExpression") return cont (pushlex("tabelsExpression"), expect("("), commasep(expression,")"),poplex)
	    if (value == "}") return pass();
	   
	    return cont();
	  }
  function statementSpec(style, value) {
	    if (value == "in") return cont(expression);
	    if (value == "filter") return cont(expression);
	    if (value == "while") return cont(pushlex("statementOperator"),expression, poplex);
	    if (value == "until") return cont(expression);
	    if (value == "starts when") return cont(expression);
	    if (value == "starts at") return cont(positionSpec);
	    if (value == "at") return cont(positionSpec);
	    return cont();
	  }
  function positionSpec(style, value) {
	    if (/[a-z]+[0-9]+/.test(value)) return cont();
	    if (style == "number") return cont(positionRelativeSpec,vardef1);
	    return cont();
	  }
  function positionRelativeSpec (style,value){
	  	if(/|(bottom |top |left |right )of/.test(value)) return cont();
	  	return pass();
  }
  function maybeoperator(type, value) {
	    if (type != "operator" ) return pass();
	    
	  }
  function iterationspec(style, value) {
	    if (value == "for") return cont(pushlex("for1"),forspec1,poplex);
	    if (value == "set") return cont(forspec1);
	    return cont();
	  }
  function forspec1(style) {
	    if (/variable/.test(style)) return pass(pushlex("for2"),vardef1,forspec2,poplex);
	    return pass(forspec2);
	  }
  function forspec2(style, value) {
	    if (style == "statementOperator") return pass(expression,forspec2);
	    return pass();
	  }
  function varspec(style, value) {
	    if (value == "let") return cont(vardef1);
	    if (value == "match") return cont(expect("["),commasep(vardef1, "]"), forspec2);
	    return pass();
	  }
  function mayvardef(style, value, state) {
	    if (/variable/.test(style)){if(inScope(state,value)) return pass(vardef1); }
	    return pass();
	  }
  function vardef1(style, value, state) {
	    if (/variable/.test(style)){register(state,value); return cont(vardef2); }
	    return cont();
	  }
  function vardef2(style, value) {
	    if (value == "=") return cont(expression);/**/
	    return pass();
	  }
  function commasep(what, end) {
	  return function proceed(type, value) {
		  if (value == ",") return cont(what, proceed);
	      if (value == end) return cont();
	      return pass(what, proceed);
	    }
  }
  function block(style, value) {
	    if (value == "}") return cont();
	    return pass(statement,separatorSpec, block);
	  }
  function templateBlock(style, value) {
	    if (value == "}") return cont();
	    return pass(tripleSpec,statementSeparatorSpec,templateBlock);
	  }
  function tripleSpec(style,value){
	  if(value =="[") return cont(propertySpec,ObjectSpec);
	  if(value =="[]") return cont(propertySpec,ObjectSpec); 
	  if (value == ";") return cont(propertySpec,ObjectSpec);
	  if (/variable/.test(style)) return pass(subjectSpec,propertySpec,ObjectSpec) ;
	  if(style == "atom") return pass(subjectSpec,propertySpec,ObjectSpec) ;
	  return cont();
  }
  function subjectSpec(style, value) {
	  if (/variable/.test(style)) return cont() ;
	  if(style == "atom") return cont() ; 
	    return pass();
	  }
 
  function propertySpec(style, value) {
	  if (value== "a" ) return cont();
	  if(style == "atom") return cont();
	    return pass();
	  }
  function ObjectSpec(style, value) {
	  if (/variable/.test(style)) return cont();
	  if (atomicTypes.hasOwnProperty(style)) return cont();
	  return pass();
	  }
  function separatorSpec(style,value){
	  	if(value=="}") return pass();
	  	if(value==";") return cont(popContext,pushContext);
	  	if(value==".") return cont();
	  	return pass();
	  	
  	  }
  function statementSeparatorSpec(style,value){
	    if(value=="]") return cont(statementSeparatorSpec)
	  	if(value=="}") return pass();
	  	if(value==";") return pass();
	  	if(value==".") return cont();
	  	return pass();
	  	
	  }

  function inScope(state, varname) {
	    for (var v = state.localVars; v; v = v.next)
	      if (v.name == varname) return true;
	  }
  
  function parseJS(state, style, content, stream) {
	    var cc = state.cc;
	    // Communicate our context to the combinators.
	    // (Less wasteful than consing up a hundred closures on every call.)
	    cx.state = state; cx.stream = stream; cx.marked = null, cx.cc = cc;
	  
/*	    if (!state.context.align)
	    	state.context.align = true;
*/
	    while(true) {
	      var combinator = cc.length ? cc.pop() : statement;
	      if (combinator(style, content,state)) {
	        while(cc.length && cc[cc.length - 1].lex)
	          cc.pop()();
	        if (cx.marked) return cx.marked;
	        if (style == "variable" && inScope(state, content)) return "variable-2";
	        return style;
	      }
	    } 
	  }
  function expect(wanted) {
	    return function expecting(type,value) {
	      if (value == wanted) return cont();
	      else if (wanted == ".") return pass();
	      else return cont(arguments.callee);
	    };
	  }
  /**********************/
 
  return {
    startState: function(base) {
      return {tokenize: tokenBase,
              context: {prev: null, vars: patternVars},
              indent: 0,
              cc: [],
         //     indented, column, style, align, prev, info
              lexical: new JSLexical((base || 0) - indentUnit, 0, "base", false, null),
              col: 0};
    },

    token: function(stream, state) {
     
      if (stream.sol()) {
        if (state.context && state.context.align == null) state.context.align = false;
        state.indent = stream.indentation();
      }
      
      if (stream.eatSpace()) return null;
      var style = state.tokenize(stream, state);

      if (style != "comment" && state.context && state.context.align == null && state.context.type != "pattern") {
        state.context.align = true;
      }
      if (curPunc) curPunc=curPunc.toLowerCase();
      return parseJS(state, style,  curPunc , stream);
  
    },

    indent: function(state, textAfter) {
      var firstChar = textAfter && textAfter.charAt(0);
      var context = state.context;
      if (/[\]\}]/.test(firstChar))
        while (context && context.type == "pattern") context = context.prev;
      
      var closing = context && firstChar == context.type;
      if (!context)
        return 0;
      else if (context.type == "pattern")
        return context.col;
      else if (context.align)
        return context.col + (closing ? 0 : 1);
      else
        return context.indent + (closing ? 0 : indentUnit);
    }
  };
});

CodeMirror.defineMIME("application/x-tabels", "tabels");
