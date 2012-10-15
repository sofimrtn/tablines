<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="Grails" /></title>
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:javascript library="application" />
        
		<g:javascript library="jquery" plugin="jquery"/>
		<link rel="stylesheet" href="${resource(dir:'css',file:'codemirror.css')}">
		<link rel="stylesheet" href="${resource(dir:'css',file:'codemirror-default-theme.css')}">
		<link rel="stylesheet" href="${resource(dir:'css',file:'simple-hint.css')}">
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="stylesheet" href="${resource(dir:'css',file:'buttons.css')}" />
        <g:layoutHead />
		<r:layoutResources/>
    </head>
    <body>
     <div id="wrapper">
        <div id="spinner" class="spinner" style="display:none;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
        </div>
		<div id="header">
			<h1><g:link controller="project" action="home"><g:message code="product.name"/><img src="${resource(dir:'images',file:'beta.png')}" alt="Beta" class="beta"/></g:link></h1>
			<p class="undertitle"><g:message code="product.slogan" /></p>			
		</div>
		<div id="content">
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
		<div id="push"></div>
		</div>
		
		<div id="footer">
			<span class="helpIcon">
				<g:link controller="project" action="home">Home</g:link>
			</span> -
			
			<span class="contact">
				<g:link action="contact" > <!--<img src="${resource(dir:'images',file:'contact.png')}" alt="${message(code:'product.contact',default:'loading...')}" />--><g:message code="product.contact"/></g:link></span> - 
			<span class="disclaimer"><g:link action="disclaimer" ><!--<img src="${resource(dir:'images',file:'disclaimer.png')}" alt="${message(code:'product.contact',default:'loading...')}" />-->Disclaimer</g:link></span> - 
			<span class="helpIcon">
				<g:link controller="docs" action="guide"> 
				<!--<img src="${resource(dir:'images',file:'help.png')}" alt="${message(code:'product.documentation',default:'loading...')}" />-->
				<g:message code="product.help"/> <span class="smallText">(Spanish)</span></g:link>
			</span><br/>
			<span>&copy; 2012 <a href="http://www.fundacionctic.org/en" title="CTIC">CTIC</a> All Rights Reserved</span>
		</div>
    </body>
</html>