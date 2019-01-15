<head>
    <meta charset="utf-8" />
    <title>${title!"ORCID"}</title>
    <meta name="description" content="">
    <meta name="author" content="ORCID">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    
    <#if (noIndex)??>
        <meta name="googlebot" content="noindex">
        <meta name="robots" content="noindex">
        <meta name="BaiduSpider" content="noindex">
    </#if>
    
    <#include "/layout/google_analytics.ftl">
    
    <script type="text/javascript">
        var orcidVar = {};
        orcidVar.recaptchaKey = '${recaptchaWebKey}';
        orcidVar.baseDomainRmProtocall = '${baseDomainRmProtocall}';
        orcidVar.baseUri = '${baseUri}';
        orcidVar.baseUriHttp = '${baseUriHttp}';
        orcidVar.pubBaseUri = '${pubBaseUri}';
		orcidVar.staticCdn = '${staticCdn}'
	        
        <#if (workIdsJson)??>
        orcidVar.workIds = JSON.parse("${workIdsJson}");
        </#if>
      
        <#if (affiliationIdsJson)??>
        orcidVar.affiliationIdsJson = JSON.parse("${affiliationIdsJson}");
        </#if>
      
        <#if (fundingIdsJson)??>
        orcidVar.fundingIdsJson = JSON.parse("${fundingIdsJson}");
        </#if>
      
        <#if (showLogin)??>
            orcidVar.showLogin = ${showLogin};
        </#if>

        orcidVar.orcidId = '${(effectiveUserOrcid)!}';
        orcidVar.lastModified = '${(lastModifiedTime?datetime)!}';
        orcidVar.orcidIdHash = '${(orcidIdHash)!}';
        orcidVar.realOrcidId = '${realUserOrcid!}';
        orcidVar.resetParams = '${(resetParams)!}';
        orcidVar.emailToReactivate = '${(email)!}';
        orcidVar.jsMessages = JSON.parse("${jsMessagesJson}");
        orcidVar.searchBaseUrl = "${searchBaseUrl}";
        orcidVar.isPasswordConfirmationRequired = ${isPasswordConfirmationRequired?c};       
        orcidVar.features = JSON.parse("${featuresJson}");
        orcidVar.providerId = '${(providerId)!}';
        
        <#if (oauth2Screens)??>
        orcidVar.oauth2Screens = true;
        <#else>
        
        orcidVar.oauth2Screens = false;
        </#if>
      
        <#if (originalOauth2Process)??>
        orcidVar.originalOauth2Process = true;
        <#else>
        orcidVar.originalOauth2Process = false;
        </#if>     
      
        orcidVar.oauthUserId = "${(oauth_userId?js_string)!}";
        orcidVar.firstName = "${(RequestParameters.firstName?js_string)!}";
        orcidVar.lastName = "${(RequestParameters.lastName?js_string)!}"; 
        orcidVar.emailId = "${(RequestParameters.emailId?js_string)!}";
        orcidVar.linkRequest = "${(RequestParameters.linkRequest?js_string)!}";
        orcidVar.memberSlug = "${(memberSlug?js_string)!}";
        
        orcidVar.loginId = "${(request.getParameter('loginId'))!}";
        
        <#if verifiedEmail??>
            orcidVar.loginId = "${verifiedEmail}";
        </#if>
        
        <#if (developerToolsEnabled)??>            
            orcidVar.developerToolsEnabled = ${developerToolsEnabled?c};            
        <#else>
            orcidVar.developerToolsEnabled = false;
        </#if>
    </script>

    <#include "/macros/orcid_ga.ftl">
    
    <link rel="stylesheet" href="${staticLoc}/css/noto-sans-googlefonts.css"/> <!-- Src: //fonts.googleapis.com/css?family=Noto+Sans:400,700 -->
    <link rel="stylesheet" href="${staticLoc}/css/glyphicons.css"/>
    <link rel="stylesheet" href="${staticLoc}/css/social.css"/>
    <link rel="stylesheet" href="${staticLoc}/css/filetypes.css"/>    
    
    <!-- Always remember to remove Glyphicons font reference when bootstrap is updated -->
    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.3.6/css/bootstrap.min.css"/>

    <link type="text/css" rel="stylesheet" href="${staticCdn}/css/nova-light/theme.css"/>
    <link type="text/css" rel="stylesheet" href="${staticCdn}/css/primeicons.css"/>
    <link type="text/css" rel="stylesheet" href="${staticCdn}/css/primeng.min.css"/>
    <link rel="stylesheet" href="${staticCdn}/css/orcid.new.css"/>
    <link rel="stylesheet" href="${staticCdn}/css/idpselect.css" />
    
    <#if springMacroRequestContext.requestUri?contains("/print")>
        <link rel="stylesheet" href="${staticCdn}/css/orcid-print.css"/>
    </#if>

    <link rel="stylesheet" href="${staticCdn}/css/jquery-ui-1.10.0.custom.min.css"/>
    
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
    <link rel="stylesheet" href="${staticLoc}/css/noto-font.css"/> 

    <!-- ***************************************************** -->
    <!-- Ng2 Templates - BEGIN -->
    <#include "/includes/ng2_templates/modal-ng2-template.ftl">
    <#include "/includes/ng2_templates/ext-id-popover-ng2-template.ftl">
    <#if springMacroRequestContext.requestUri?contains("/account") || springMacroRequestContext.requestUri?contains("/developer-tools") || springMacroRequestContext.requestUri?contains("/inbox") || springMacroRequestContext.requestUri?contains("/my-orcid")> 
        <#include "/includes/ng2_templates/privacy-toggle-ng2-template.ftl">
    </#if>

    <!-- Ng2 Templates - END -->
    <!-- ***************************************************** -->
</head>