<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
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
    <meta name="_csrf" content="${(_csrf.token)!}"/>
    <meta name="_csrf_header" content="${(_csrf.headerName)!}"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
     <#if (noIndex)??>
        <meta name="googlebot" content="noindex">
        <meta name="robots" content="noindex">
        <meta name="BaiduSpider" content="noindex">
     </#if>
    <!-- hack in json3 to allow angular js to work in IE7 -->
    <!-- we also need this JSON parser for orcidVar -->
    <!--[if IE 7]>
    	<script src="//cdnjs.cloudflare.com/ajax/libs/json3/3.2.4/json3.min.js" type="text/javascript"></script>
    	<script type="text/javascript">
			if (typeof JSON == 'undefined') {
    			document.write(unescape("%3Cscript src='${staticCdn}/javascript/json3/3.2.4/json3.min.js' type='text/javascript'%3E%3C/script%3E"));
			}
		</script>
    <![endif]-->
    
    <#include "/layout/google_analytics.ftl">
    
    <script type="text/javascript">
        var orcidVar = {};
        orcidVar.recaptchaKey = '${recaptchaWebKey}';
        orcidVar.baseUri = '${baseUri}';
        orcidVar.baseUriHttp = '${baseUriHttp}';
        orcidVar.pubBaseUri = '${pubBaseUri}';
      <#if (workIdsJson)??>
        orcidVar.workIds = JSON.parse("${workIdsJson}");
      </#if>
      <#if (affiliationIdsJson)??>
        orcidVar.affiliationIdsJson = JSON.parse("${affiliationIdsJson}");
      </#if>
      <#if (fundingIdsJson)??>
        orcidVar.fundingIdsJson = JSON.parse("${fundingIdsJson}");
      </#if>
      <#if (peerReviewIdsJson)??>      	
      	orcidVar.PeerReviewIds = JSON.parse("${peerReviewIdsJson}");
      </#if>      
      <#if (showLogin)??>
        orcidVar.showLogin = ${showLogin};
      </#if>
      orcidVar.orcidId = '${(effectiveUserOrcid)!}';
      orcidVar.orcidIdHash = '${(orcidIdHash)!}';
      orcidVar.realOrcidId = '${realUserOrcid!}';
      orcidVar.jsMessages = JSON.parse("${jsMessagesJson}");
      orcidVar.searchBaseUrl = "${searchBaseUrl}";
      orcidVar.isPasswordConfirmationRequired = ${isPasswordConfirmationRequired?string};
      orcidVar.version = "${ver}";
    </script>
    
    <link rel="stylesheet" href="${staticLoc}/css/fonts.css?v=${ver}"/>
    <link href='https://fonts.googleapis.com/css?family=Noto+Sans:400,700' rel='stylesheet' type='text/css'>
    
    <link rel="stylesheet" href="${staticLoc}/css/glyphicons.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticLoc}/css/social.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticLoc}/css/filetypes.css?v=${ver}"/>    
	
	<!-- Always remember to remove Glyphicons font reference when bootstrap is updated -->
    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.3.6/css/bootstrap.min.css?v=${ver}"/>
    <!--[if lt IE 8]>
        <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.1.0/css/bootstrap-ie7.css?v=${ver}"/>	                
    <![endif]-->
    
    <#if locale?? && locale = 'rl'>
        <!-- just a prototype to show what RTL, expect to switch the cdn to ours -->
        <!-- Load Bootstrap RTL theme from RawGit -->
        <link rel="stylesheet" href="//cdn.rawgit.com/morteza/bootstrap-rtl/v3.3.4/dist/css/bootstrap-rtl.min.css">
    </#if>
    
    <link rel="stylesheet" href="${staticCdn}/css/orcid.new.css?v=${ver}"/>
    <link rel="stylesheet" type="text/css" href="${staticCdn}/css/idpselect.css" />

    <!--[if lt IE 8]>
    	<link rel="stylesheet" href="${staticCdn}/css/orcid-ie7.css?v=${ver}"/>
    <![endif]-->
    <link rel="stylesheet" href="${staticCdn}/css/jquery-ui-1.10.0.custom.min.css?v=${ver}"/>
    <!-- this is a manually patched version, we should update when they accept our changes -->
    <script src="${staticCdn}/javascript/respond.src.js?v=${ver}"></script>
    
    <!-- Respond.js proxy on external server -->
    <link href="${staticCdn}/html/respond-proxy.html" id="respond-proxy" rel="respond-proxy" />
    <link href="${staticLoc}/img/respond.proxy.gif" id="respond-redirect" rel="respond-redirect" />
    <script src="${staticLoc}/javascript/respond.proxy.js"></script>
        
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
    <link rel="stylesheet" href="${staticCdn}/css/orcid-print.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticCdn}/css/noto-font.css?v=${ver}"/>
</head>
