<#macro base>
    <!DOCTYPE html>
    <html class="no-js oldie" lang="en">
    <#include "/common/html-head.ftl" />
    <body>
    <root-cmp>
        <style type="text/css">
            body, html {
                height: 100%;
            }
            .app-loading {
                position: relative;
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                height: 100%;
            }
        </style>        
        <noscript>
            <div class="alert alert-banner">
                 ${springMacroRequestContext.getMessage("common.browser-checks.functionalityofthissite")} <a href="http://www.enable-javascript.com/" target="common.browser-checks.instructionhowtoadd">
                 ${springMacroRequestContext.getMessage("common.browser-checks.instructionhowtoadd")}</a>.<br>
                 ${springMacroRequestContext.getMessage("common.cookies.orcid_uses")} <a href="${baseUri}/privacy-policy#TrackingTechnology" target="common.cookies.learn_more">
                 ${springMacroRequestContext.getMessage("common.cookies.learn_more")}</a>.
            </div>
            <style type="text/css"> 
            .app-loading {
                display: none;
            }
            </style>
        </noscript>            
        <div class="app-loading">
            <i class="glyphicon glyphicon-refresh spin green x8" id="spinner"></i>
            <!--[if lt IE 8]>    
                <img src="${staticCdn}/img/spin-big.gif" class="spinner" width="85" height ="85"/>
            <![endif]-->
        </div>
    </root-cmp>
    <#nested />
    <!--Content from this template (below) or other templates inside base macro tags appears here-->
    <#include "/common/scripts.ftl" />
    </body>
    </html>
</#macro>

<#macro nav></#macro>

<#macro footer></#macro>

<#macro public css=[] js=[] classes=[] other=[] nav="" >
    <@base>
    
        <@orcid.checkFeatureStatus 'COOKIE_BANNER'>
            <#include "/includes/ng2_templates/alert-banner-ng2-template.ftl">  
            <alert-banner-ng2></alert-banner-ng2>
        </@orcid.checkFeatureStatus>
        <!--OAUTH SCREEN HEADER-->
        <#if (RequestParameters['oauth'])??>
            <div class="container">
                <div id="main" role="main">
                    <div class="row top-header">
                        <div class="col-md-6 col-md-offset-3 centered logo topBuffer">
                            <a href="https://orcid.org" alt="ORCID logo">
                                <script type="text/ng-template" id="oauth-header-ng2-template">
                                    <img src="{{assetsPath}}/img/orcid-logo-208-64.png" width="208px" height="64px" alt="ORCID logo">
                                </script>
                                <oauth-header-ng2></oauth-header-ng2>
                            </a>
                        </div>       
                    </div> 
        </#if>
        <!--NON-OAUTH HEADER-->
        <!--hide header if oauth login-->
        <#if !(RequestParameters['oauth'])??>
            <div class="container">
                <#include "/includes/ng2_templates/header-ng2-template.ftl">
                <#include "/includes/ng2_templates/language-ng2-template.ftl">
                <div class="header center">
                    <header-ng2></header-ng2>
                </div><!-- .header -->
                <div id="main" role="main" class="main">
        </#if>
                <script type="text/ng-template" id="maintenance-message-ng2-template">
                    <div *ngIf="visible" class="row">
                        <div class="maintenance-header">              
                           <p>{{maintenanceMessage}}</p>
                        </div>
                    </div>    
                </script>
                <maintenance-ng2></maintenance-ng2>
                    
                <#nested>
                <!--Content from other templates inside public macro tags appears here-->
                </div>
            </div><!-- .container -->
        <!--FOOTER-->
        <!--hide footer if oauth login-->
        <#if !(RequestParameters['oauth'])??>
            <script type="text/ng-template" id="footer-ng2-template">
                <@orcid.checkFeatureStatus 'NEW_FOOTER'>
                    <footer class="footer-main">
                        <div class="container">
            	            <span id="noop"><!-- For automated tests --> </span>
                            <div class="row">
                                <div class="col-md-2">
                                    <p>
                                        <a href="{{aboutUri}}" alt="ORCID logo">
                                            <img src="{{assetsPath}}/img/orcid-logo.svg" width="108px" alt="ORCID logo">
                                        </a>
                                    </p>
                                    <nav>
                                        <ul class="inline-list">
                                            <li><a class="social-button" href="https://twitter.com/orcid_org" target="social-twitter"><span class="social social-twitter"></span></a></li>
                                            <li><a class="social-button" href="http://orcid.org/blog/feed" target="social-rss"><span class="social social-rss"></span></a></li>
                                            <li><a class="social-button" href="https://github.com/ORCID" target="social-github"><span class="social social-github"></span></a></li>
                                        </ul>
                                    </nav>
                                </div>
                                <div class="col-md-10">
                                    <p>
                                        <a rel="license" target="footer.copyright_cc0" href="http://creativecommons.org/publicdomain/zero/1.0/"><img src="{{assetsPath}}/img/cc0_80x15.png" style="border-style: none;" alt="CC0" /></a> <@orcid.msg 'footer.copyright_cc0_1'/> <a rel="license" target="footer.copyright_cc0" href="http://creativecommons.org/publicdomain/zero/1.0/"><@orcid.msg 'footer.copyright_cc0_2'/></a></p>
                                    <nav>
                                        <ul class="inline-list">
                                            <li><a href="{{aboutUri}}/footer/privacy-policy"><@orcid.msg 'public-layout.privacy_policy'/></a></li>
                                            <li><a href="{{aboutUri}}/content/orcid-terms-use"><@orcid.msg 'public-layout.terms_of_use'/></a></li>
                                            <li><a href="https://orcid.org/orcid-dispute-procedures">Dispute procedures</a></li>
                                            <li><a href="{{aboutUri}}/help/contact-us"><@orcid.msg 'public-layout.contact_us'/></a></li>
                                            <li><a href="https://orcid.org/trademark-and-id-display-guidelines">Trademark &amp; iD display guidelines</a></li>
                                        </ul>
                                    </nav>
                                </div>
                            </div>
                        </div>
                    </footer>
                </@orcid.checkFeatureStatus>
                <@orcid.checkFeatureStatus featureName='NEW_FOOTER' enabled=false>
                    <div id="footer" class="footer clear-fix">
                        <span id="noop"><!-- For automated tests --> </span>
                        <div class="container">
                            <div class="row">
                                <div class="col-md-11 col-md-offset-1">
                                    <ul class="col-md-11 col-md-offset-1">
                                        <li class=""><a href="{{aboutUri}}/help/contact-us"><@orcid.msg 'public-layout.contact_us'/></a></li>
                                        <li class=""><a href="{{aboutUri}}/footer/privacy-policy"><@orcid.msg 'public-layout.privacy_policy'/></a></li>
                                        <li class=""><a href="{{aboutUri}}/content/orcid-terms-use"><@orcid.msg 'public-layout.terms_of_use'/></a></li>
                                        <li class=""><a href="{{aboutUri}}/open-source-license"><@orcid.msg 'footer.openSource'/></a></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </@orcid.checkFeatureStatus>
                <form action="{{getBaseUri()}}">
                    <input id="imageUrl" type="hidden" value="{{assetsPath}}/images">                
                </form>
            </script>
            <footer-ng2></footer-ng2>
        </#if>
    </@base>
</#macro>

