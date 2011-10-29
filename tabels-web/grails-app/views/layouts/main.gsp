<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="Grails" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:javascript library="application" />
		<g:javascript library="jquery" plugin="jquery"/>
		<r:require modules="uploadr"/>
		<script src="${resource(dir:'js/codemirror',file:'codemirror.js')}"></script>
		<link rel="stylesheet" href="${resource(dir:'css',file:'codemirror.css')}">
		<script src="${resource(dir:'js/codemirror',file:'sparql.js')}"></script>
		<link rel="stylesheet" href="${resource(dir:'css',file:'codemirror-default-theme.css')}">
        <g:layoutHead />
		<r:layoutResources/>
    </head>
    <body>
        <div id="spinner" class="spinner" style="display:none;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
        </div>
        <div id="grailsLogo"><a href="http://grails.org"><img src="${resource(dir:'images',file:'grails_logo.png')}" alt="Grails" border="0" /></a></div>
		<g:if test="${flash.error}">
			<div class="errorbox">
				<g:message code="${flash.error}" args="${flash.args}" default="${flash.error}"/>
			</div>
		</g:if>
		<g:if test="${flash.message}">
			<div class="messagebox">
				<g:message code="${flash.message}" args="${flash.args}" default="${flash.message}"/>
			</div>
		</g:if>
        <g:layoutBody />
		<r:layoutResources/>
    </body>
</html>