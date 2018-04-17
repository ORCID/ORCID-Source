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
      
        <#if (peerReviewIdsJson)??>       
        orcidVar.PeerReviewIds = JSON.parse("${peerReviewIdsJson}");
        </#if>      
      
        <#if (showLogin)??>
            orcidVar.showLogin = ${showLogin};
        </#if>

        orcidVar.orcidId = '${(effectiveUserOrcid)!}';
        orcidVar.lastModified = '${(lastModifiedTime)!}';
        orcidVar.orcidIdHash = '${(orcidIdHash)!}';
        orcidVar.realOrcidId = '${realUserOrcid!}';
        orcidVar.resetParams = '${(resetParams)!}';
        orcidVar.jsMessages = JSON.parse("${jsMessagesJson}");
        orcidVar.searchBaseUrl = "${searchBaseUrl}";
        orcidVar.isPasswordConfirmationRequired = ${isPasswordConfirmationRequired?c};
        orcidVar.emailVerificationManualEditEnabled = ${emailVerificationManualEditEnabled?c};        
        orcidVar.knowledgeBaseUri = "${knowledgeBaseUri}";
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
    </script>

    <#include "/macros/orcid_ga.ftl">
    
    <link rel="stylesheet" href="${staticLoc}/css/noto-sans-googlefonts.css"/> <!-- Src: //fonts.googleapis.com/css?family=Noto+Sans:400,700 -->
    <link rel="stylesheet" href="${staticLoc}/css/glyphicons.css"/>
    <link rel="stylesheet" href="${staticLoc}/css/social.css"/>
    <link rel="stylesheet" href="${staticLoc}/css/filetypes.css"/>    
    
    <!-- Always remember to remove Glyphicons font reference when bootstrap is updated -->
    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.3.6/css/bootstrap.min.css"/>
    
    <#if locale?? && (locale == 'rl' || locale == 'ar' )>
    <!-- just a prototype to show what RTL, expect to switch the cdn to ours -->
    <!-- Load Bootstrap RTL theme from RawGit -->
    <link rel="stylesheet" href="${staticCdn}/css/bootstrap-rtl.min.css"> <!-- Src: //cdn.rawgit.com/morteza/bootstrap-rtl/v3.3.4/dist/css/bootstrap-rtl.min.css -->
    </#if>

    <link rel="stylesheet" href="${staticCdn}/css/orcid.new.css"/>
    <link rel="stylesheet" href="${staticCdn}/css/idpselect.css" />
    
    <#if springMacroRequestContext.requestUri?contains("/print")>
    <link rel="stylesheet" href="${staticCdn}/css/orcid-print.css"/>
    </#if>

    <link rel="stylesheet" href="${staticCdn}/css/jquery-ui-1.10.0.custom.min.css"/>
    
    <!-- this is a manually patched version, we should update when they accept our changes -->
    <script src="${staticCdn}/javascript/respond.src.js"></script>
    
    <!-- Respond.js proxy on external server -->
    <link href="${staticCdn}/html/respond-proxy.html" id="respond-proxy" rel="respond-proxy" />
    <link href="${staticCdn}/img/respond.proxy.gif" id="respond-redirect" rel="respond-redirect" />
    <script src="${staticCdn}/javascript/respond.proxy.js"></script>
        
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


    <@orcid.checkFeatureStatus 'ANGULAR2_DEV'> 
        <!-- NG2: Under development -->
        <#include "/includes/ng2_templates/reset-password-ng2-template.ftl">
        <#include "/includes/ng2_templates/client-edit-ng2-template.ftl">
        <#include "/includes/ng2_templates/notifications-ng2-template.ftl">
        
        <#if springMacroRequestContext.requestUri?contains("/my-orcid") >
            <#include "/includes/ng2_templates/works-form-ng2-template.ftl">
            <#include "/includes/ng2_templates/works-ng2-template.ftl">
        </#if>

        <!-- Probably this one wont be needed -->
        <#if springMacroRequestContext.requestUri?contains("/my-orcid") 
            || springMacroRequestContext.requestUri?contains("/print")
            || (isPublicProfile??)>
            <#include "/includes/ng2_templates/personal-info-ng2-template.ftl">
        </#if>
    </@orcid.checkFeatureStatus> 

    <!-- NG2: QA -->
    <@orcid.checkFeatureStatus 'ANGULAR2_QA'>
        <#include "/includes/ng2_templates/header-ng2-template.ftl">
        <#include "/includes/ng2_templates/language-ng2-template.ftl">
        <#include "/includes/ng2_templates/oauth-authorization-ng2-template.ftl">
        <#include "/includes/ng2_templates/request-password-reset-ng2-template.ftl">
        <#include "/includes/ng2_templates/social-2FA-ng2-template.ftl">
        
        <#if springMacroRequestContext.requestUri?contains("/social") ||  springMacroRequestContext.requestUri?contains("/shibboleth/signin") || (RequestParameters['linkRequest'])??>
            <#include "/includes/ng2_templates/link-account-ng2-template.ftl">
        </#if>
        
        <#if springMacroRequestContext.requestUri?contains("/my-orcid") >
            <#include "/includes/ng2_templates/also-known-as-ng2-template.ftl">
            <#include "/includes/ng2_templates/also-known-as-form-ng2-template.ftl">
            <#include "/includes/ng2_templates/claim-thanks-ng2-template.ftl">
            <#include "/includes/ng2_templates/country-form-ng2-template.ftl">
            <#include "/includes/ng2_templates/country-ng2-template.ftl">
            <#include "/includes/ng2_templates/funding-ng2-template.ftl">     
            <#include "/includes/ng2_templates/websites-ng2-template.ftl">
            <#include "/includes/ng2_templates/websites-form-ng2-template.ftl">
            <#include "/includes/ng2_templates/websites-form-ng2-template.ftl">
            
            <#include "/includes/ng2_templates/workspace-summary-ng2-template.ftl">
            <#include "/includes/ng2_templates/external-identifier-ng2-template.ftl">
        </#if>

    </@orcid.checkFeatureStatus> 

    <@orcid.checkFeatureStatus 'DISPLAY_NEW_AFFILIATION_TYPES'> 
        <#if springMacroRequestContext.requestUri?contains("/my-orcid") || (isPublicProfile??)>
            <#include "/includes/ng2_templates/affiliation-ng2-template.ftl">
            <#include "/includes/ng2_templates/affiliation-delete-ng2-template.ftl">
            <#include "/includes/ng2_templates/affiliation-form-ng2-template.ftl"> 
        </#if>
    </@orcid.checkFeatureStatus> 
    
    <#if springMacroRequestContext.requestUri?contains("/account") >
        <#include "/includes/ng2_templates/deactivate-account-ng2-template.ftl">
        <#include "/includes/ng2_templates/password-edit-ng2-template.ftl">
        <#include "/includes/ng2_templates/social-networks-ng2-template.ftl">
        <#include "/includes/ng2_templates/twoFA-state-ng2-template.ftl">
        <#include "/includes/ng2_templates/works-privacy-preferences-ng2-template.ftl">
    </#if>

    <#if springMacroRequestContext.requestUri?contains("/account") || springMacroRequestContext.requestUri?contains("/my-orcid")>
        <#include "/includes/ng2_templates/deprecate-account-ng2-template.ftl">
        <#include "/includes/ng2_templates/email-frequency-ng2-template.ftl">
        <#include "/includes/ng2_templates/emails-form-ng2-template.ftl">
        <#include "/includes/ng2_templates/emails-ng2-template.ftl">
    </#if>

    <#if springMacroRequestContext.requestUri?contains("/my-orcid") >        
        <#include "/includes/ng2_templates/biography-ng2-template.ftl">        
        <#include "/includes/ng2_templates/email-unverified-warning-ng2-template.ftl">
        <#include "/includes/ng2_templates/email-verification-sent-messsage-ng2-template.ftl">
        <#include "/includes/ng2_templates/keywords-form-ng2-template.ftl">
        <#include "/includes/ng2_templates/keywords-ng2-template.ftl">
        <#include "/includes/ng2_templates/modal-ng2-template.ftl">
        <#include "/includes/ng2_templates/thanks-for-registering-ng2-template.ftl">
        <#include "/includes/ng2_templates/thanks-for-verifying-ng2-template.ftl">
    </#if>

    <#if springMacroRequestContext.requestUri?contains("/account") || springMacroRequestContext.requestUri?contains("/developer-tools") || springMacroRequestContext.requestUri?contains("/inbox") || springMacroRequestContext.requestUri?contains("/my-orcid")>
        <#include "/includes/ng2_templates/name-ng2-template.ftl">
        <#include "/includes/ng2_templates/privacy-toggle-ng2-template.ftl">
        <#include "/includes/ng2_templates/widget-ng2-template.ftl">
    </#if>

    <!-- Ng2 Templates - END -->
    <!-- ***************************************************** -->
</head>
