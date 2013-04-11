<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<head>
    <meta charset="utf-8" />
    <title>${title!"ORCID"}</title>
    <meta name="description" content="">
    <meta name="author" content="">

    <link rel="stylesheet" href="<@spring.url '/static/ORCID-Fonts-Dot-Com/style.css?v=${ver}'/>"/>
    
	<#if (RequestParameters['lemur'])??>
	
	<#else>
	    <link rel="stylesheet" href="<@spring.url '/static/css/orcid.css?v=${ver}'/>"/>
	    <link rel="stylesheet" href="<@spring.url '/static/css/java.css?v=${ver}'/>"/>
	    <link rel="stylesheet" href="<@spring.url '/static/css/jquery-ui-1.10.0.custom.min.css?v=${ver}'/>"/>
	    <link rel="stylesheet" href="<@spring.url '/static/font-awesome/3.0.2/css/font-awesome.css'/>"/>
	    <!--[if IE 7]>
	    	<link href="/static/font-awesome/3.0.2/css/font-awesome-ie7.css" rel="stylesheet">
		<![endif]-->
		<script src="<@spring.url '/static/javascript/modernizr.js?v=${ver}'/>"></script>
    </#if>
	<style type="text/css">
		/* 
	  	Allow angular.js to be loaded in body, hiding cloaked elements until 
	  	templates compile.  The !important is important given that there may be 
	  	other selectors that are more specific or come later and might alter display.  
		 */
		[ng\:cloak], [ng-cloak], .ng-cloak {
	  		display: none !important;
		}
	</style> 

    <link rel="shortcut icon" href="<@spring.url '/static/img/favicon.ico'/>"/>
    <link rel="apple-touch-icon" href="<@spring.url '/static/img/apple-touch-icon.png'/>" />


    <#include "/layout/google_analytics.ftl">
    <!-- hack in json3 to allow angular js to work in IE7 -->
    <!--[if IE 7]>
    	<script src="//cdnjs.cloudflare.com/ajax/libs/json3/3.2.4/json3.min.js" type="text/javascript"></script>
     <![endif]-->
</head>
