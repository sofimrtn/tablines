<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="Grails" /></title>
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:javascript library="application" />
		<g:javascript library="jquery" plugin="jquery"/>
		<script src="${resource(dir:'js/codemirror',file:'codemirror.js')}"></script>
		<link rel="stylesheet" href="${resource(dir:'css',file:'codemirror.css')}">
		<script src="${resource(dir:'js/codemirror',file:'sparql.js')}"></script>
		<script src="${resource(dir:'js/codemirror',file:'tabels.js')}"></script>
		<link rel="stylesheet" href="${resource(dir:'css',file:'codemirror-default-theme.css')}">
		<r:layoutResources/>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <g:layoutHead />
    </head>
    <body>
        <div id="spinner" class="spinner" style="display:none;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
        </div>
		<div id="header">
			<h1><g:link controller="project">Tabels</g:link></h1>
			<p class="undertitle"><g:message code="product.slogan" /></p>
		</div>
		<div id="body">
		<g:if test="${flash.error}">
			<div class="errorsbox">
				<g:message code="${flash.error}" args="${flash.args}" default="${flash.error.encodeAsHTML()}"/>
			</div>
		</g:if>
		<g:if test="${flash.message}">
			<div class="messagebox">
				<g:message code="${flash.message}" args="${flash.args}" default="${flash.message.encodeAsHTML()}"/>
			</div>
		</g:if>
        <g:layoutBody />
		<r:layoutResources/>
		</div>
    </body>
</html>