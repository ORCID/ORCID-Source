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
        <script src="${staticCdn}/javascript/json3/3.2.4/json3.min.js" type="text/javascript"></script>
        <script type="text/javascript">
        </script>
    <![endif]-->
    
    <#include "/layout/google_analytics.ftl">
    
    <script type="text/javascript">
        var orcidVar = {};
        orcidVar.recaptchaKey = '${recaptchaWebKey}';
        orcidVar.baseDomainRmProtocall = '${baseDomainRmProtocall}';
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
      orcidVar.lastModified = '${(lastModifiedTime)!}';
      orcidVar.orcidIdHash = '${(orcidIdHash)!}';
      orcidVar.realOrcidId = '${realUserOrcid!}';
      orcidVar.jsMessages = JSON.parse("${jsMessagesJson}");
      orcidVar.searchBaseUrl = "${searchBaseUrl}";
      orcidVar.isPasswordConfirmationRequired = ${isPasswordConfirmationRequired?c};
      orcidVar.emailVerificationManualEditEnabled = ${emailVerificationManualEditEnabled?c};
      orcidVar.version = "${ver}";
      orcidVar.knowledgeBaseUri = "${knowledgeBaseUri}";
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
      orcidVar.memberSlug = "${(memberSlug?js_string)!}";
    </script>

	<#include "/macros/orcid_ga.ftl">
    
	<link rel="stylesheet" href="${staticLoc}/css/noto-sans-googlefonts.css?v=${ver}"/> <!-- Src: //fonts.googleapis.com/css?family=Noto+Sans:400,700 -->
    <link rel="stylesheet" href="${staticLoc}/css/glyphicons.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticLoc}/css/social.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticLoc}/css/filetypes.css?v=${ver}"/>    
    
    <!-- Always remember to remove Glyphicons font reference when bootstrap is updated -->
    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.3.6/css/bootstrap.min.css?v=${ver}"/>
    <!--[if lt IE 8]>
        <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.1.0/css/bootstrap-ie7.css?v=${ver}"/>                 
    <![endif]-->
    
    <#if locale?? && (locale == 'rl' || locale == 'ar' )>
    <!-- just a prototype to show what RTL, expect to switch the cdn to ours -->
    <!-- Load Bootstrap RTL theme from RawGit -->
    <link rel="stylesheet" href="${staticCdn}/css/bootstrap-rtl.min.css?v=${ver}"> <!-- Src: //cdn.rawgit.com/morteza/bootstrap-rtl/v3.3.4/dist/css/bootstrap-rtl.min.css -->
    </#if>
    <link rel="stylesheet" href="${staticCdn}/css/orcid.new.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticCdn}/css/idpselect.css" />
    <#if springMacroRequestContext.requestUri?contains("/print")>
    <link rel="stylesheet" href="${staticCdn}/css/orcid-print.css"/>
    </#if>

    <!--[if lt IE 8]>
        <link rel="stylesheet" href="${staticCdn}/css/orcid-ie7.css?v=${ver}"/>
    <![endif]-->
    <link rel="stylesheet" href="${staticCdn}/css/jquery-ui-1.10.0.custom.min.css?v=${ver}"/>
    <!-- this is a manually patched version, we should update when they accept our changes -->
    <script src="${staticCdn}/javascript/respond.src.js?v=${ver}"></script>
    
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
    <link rel="stylesheet" href="${staticLoc}/css/noto-font.css?v=${ver}"/>	

    <!-- Ng2 Templates -->
    <script type="text/ng-template" id="privacy-toggle-ng2-template">
        <div class="relative" class="privacy-bar-impr">
            <ul class="privacyToggle" (mouseenter)="showTooltip(name)" (mouseleave)="hideTooltip(name)" >
                <li class="publicActive" [ngClass]="{publicInActive: dataPrivacyObj[privacyNodeName]?.visibility != 'PUBLIC'}"><a (click)="setPrivacy('PUBLIC')"></a></li>
                <li class="limitedActive limitedInActive" [ngClass]="{limitedInActive: dataPrivacyObj[privacyNodeName]?.visibility != 'LIMITED'}"><a (click)="setPrivacy('LIMITED')"></a></li>
                <li class="privateActive privateInActive" [ngClass]="{privateInActive: dataPrivacyObj[privacyNodeName]?.visibility != 'PRIVATE'}"><a (click)="setPrivacy('PRIVATE')"></a></li>
            </ul>

            <div class="popover-help-container">
                <div class="popover top privacy-myorcid3" [ngClass]="showElement[name] == true ? 'block' : ''">
                    <div class="arrow"></div>
                    <div class="popover-content">
                        <strong><@orcid.msg 'privacyToggle.help.who_can_see' /></strong>
                        <ul class="privacyHelp">
                            <li class="public" style="color: #009900;"><@orcid.msg 'privacyToggle.help.everyone' /></li>
                            <li class="limited" style="color: #ffb027;"><@orcid.msg 'privacyToggle.help.trusted_parties' /></li>
                            <li class="private" style="color: #990000;"><@orcid.msg 'privacyToggle.help.only_me' /></li>
                        </ul>
                        <a href="https://support.orcid.org/knowledgebase/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information"><@orcid.msg 'privacyToggle.help.more_information' /></a>
                    </div>                
                </div>                              
            </div>
        </div>
    </script>    
</head>
