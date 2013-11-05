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
    <meta name="author" content="ORCID">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
         
    <script type="text/javascript">
    	var orcidVar = {};
    	orcidVar.baseUri = '${baseUri}';
    	orcidVar.baseUriHttp = '${baseUriHttp}';
    </script>    
    <#if
		request.requestURI?ends_with("${basePath}signin")||
		request.requestURI?ends_with("${basePath}register")||
		request.requestURI?ends_with("${basePath}reset-password")||
		request.requestURI?ends_with("${basePath}error")||
		request.requestURI?ends_with("${basePath}oauth/signin")||		
		request.requestURI?ends_with("${basePath}oauth/confirm_access")||
		request.requestURI?ends_with("${basePath}my-orcid")||
		request.requestURI?ends_with("${basePath}statistics")||
		request.requestURI?ends_with("${basePath}account")||	
		request.requestURI?matches("(.*)/(?:\\d{4}-){3,}\\d{3}[\\dX]")||
		request.requestURI?ends_with("${basePath}account/manage-bio-settings")||
		request.requestURI?matches("(.*)/[a-zA-Z0-9?=]*")
		
	>    	
	    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.0.0/css/bootstrap.min.css?v=${ver}"/>
	    <!--[if lt IE 8]>
	        <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.0.0/css/bootstrap-ie7.css?v=${ver}"/>	                
	    <![endif]-->
	    <link rel="stylesheet" href="${staticLoc}/css/fonts.css?v=${ver}"/>
	    <link rel="stylesheet" href="${staticCdn}/css/orcid.resp.css?v=${ver}"/>	    
	    <!--[if lt IE 8]>
	    	<link rel="stylesheet" href="${staticCdn}/css/orcid-ie7.css?v=${ver}"/>
	    <![endif]-->
	    <link rel="stylesheet" href="${staticCdn}/css/jquery-ui-1.10.0.custom.min.css?v=${ver}"/>
	    <link rel="stylesheet" href="${staticLoc}/font-awesome/3.2.1/css/font-awesome.css"/>
	    <!--[if IE 7]>
	    	<link href="${staticLoc}/font-awesome/3.0.2/css/font-awesome-ie7.min.css" rel="stylesheet">
		<![endif]-->	
		
	    <script src="${staticCdn}/javascript/respond.min.js?v=${ver}"></script>
    
    <#else>		
		<link rel="stylesheet" href="${staticLoc}/ORCID-Fonts-Dot-Com/style.css?v=${ver}"/>
	    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/2.3.1/css/bootstrap.min.css?v=${ver}"/>
	    <link rel="stylesheet" href="${staticLoc}/css/fonts.css?v=${ver}"/>
	    <link rel="stylesheet" href="${staticCdn}/css/orcid.css?v=${ver}"/>	    
	    <link rel="stylesheet" href="${staticCdn}/css/jquery-ui-1.10.0.custom.min.css?v=${ver}"/>
	    <link rel="stylesheet" href="${staticLoc}/font-awesome/3.2.1/css/font-awesome.css"/>
	    <!--[if IE 7]>
	    	<link href="${staticLoc}/font-awesome/3.0.2/css/font-awesome-ie7.min.css" rel="stylesheet">
		<![endif]-->
    </#if>
    
    <#-- manage bio settings still requires modernizr :-( -->
	    <#if request.requestURI?ends_with("account/manage-bio-settings")>
	        <script src="${staticCdn}/javascript/modernizr.js?v=${ver}"></script>
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

    <link rel="shortcut icon" href="${staticCdn}/img/favicon.ico"/>
    <link rel="apple-touch-icon" href="${staticCdn}/img/apple-touch-icon.png" />


    <#include "/layout/google_analytics.ftl">
    <!-- hack in json3 to allow angular js to work in IE7 -->
    <!--[if IE 7]>
    	<script src="//cdnjs.cloudflare.com/ajax/libs/json3/3.2.4/json3.min.js" type="text/javascript"></script>
     <![endif]-->
     <script type="text/javascript">
		  var _prum={id:"51682c74abe53d6049000000"};
		  var PRUM_EPISODES=PRUM_EPISODES||{};
		  PRUM_EPISODES.q=[];
		  PRUM_EPISODES.mark=function(b,a){PRUM_EPISODES.q.push(["mark",b,a||new Date().getTime()])};
		  PRUM_EPISODES.measure=function(b,a,b){PRUM_EPISODES.q.push(["measure",b,a,b||new Date().getTime()])};
		  PRUM_EPISODES.done=function(a){PRUM_EPISODES.q.push(["done",a])};
		  PRUM_EPISODES.mark("firstbyte");
		  (function(){
		    var b=document.getElementsByTagName("script")[0];
		    var a=document.createElement("script");
		    a.type="text/javascript";a.async=true;a.charset="UTF-8";
		    a.src="//rum-static.pingdom.net/prum.min.js";b.parentNode.insertBefore(a,b)
		  })();
	</script>
</head>
