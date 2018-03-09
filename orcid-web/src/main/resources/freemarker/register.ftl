<@protected classes=['manage'] nav="settings">
<div>
    <div class="row">        
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <#if (RequestParameters['linkRequest'])??>
                <div ng-controller="LinkAccountController" ng-init="setEntityId('${RequestParameters.providerId!}')">
                    <div ng-hide="loadedFeed" class="text-center">
                        <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                        <!--[if lt IE 8]>
                            <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
                        <![endif]-->
                    </div>
                    <div ng-show="loadedFeed">
                        <h2>${springMacroRequestContext.getMessage("social.link.title.1")} <span ng-bind="idpName"></span> ${springMacroRequestContext.getMessage("social.link.title.2")}</h2>
                        <h4>${springMacroRequestContext.getMessage("social.link.you_are")} <span ng-bind="idpName"></span> ${springMacroRequestContext.getMessage("social.link.as")} ${RequestParameters.accountId!}</h4>
                        <p> 
                            ${springMacroRequestContext.getMessage("social.link.to_finish.register.1")}<span ng-bind="idpName"></span>${springMacroRequestContext.getMessage("social.link.to_finish.register.2")}
                        </p>
                        <p>
                            <i>${springMacroRequestContext.getMessage("social.link.you_will_only.1")}<span ng-bind="idpName"></span> ${springMacroRequestContext.getMessage("social.link.you_will_only.2")} <a href="${knowledgeBaseUri}/articles/892920" target="social.link.visit_knowledgebase_link" >${springMacroRequestContext.getMessage("social.link.visit_knowledgebase_link")}</a></i>
                        </p>
                    </div>
                    <div>                                                   
                        <#if "shibboleth" == (RequestParameters['linkRequest']!)><a class="reg" href="<@orcid.rootPath '/shibboleth/signin'/>"><#else><a class="reg" href="<@orcid.rootPath '/social/access'/>"></#if>${springMacroRequestContext.getMessage("social.link.link_this_account")}</a> &nbsp;&#124;&nbsp; ${springMacroRequestContext.getMessage("login.registerOrcidId")} &nbsp;&#124;&nbsp; <a class="reg" href="<@orcid.rootPath '/signin'/>">${springMacroRequestContext.getMessage("social.link.return_to_signin")}</a>
                        <hr />
                    </div> 
                </div>
                     
            <#else>
                <h2>${springMacroRequestContext.getMessage("register.labelRegisterforanORCIDiD")}</h2>
                <p>${springMacroRequestContext.getMessage("register.labelORCIDprovides")}</p>
            </#if>
            <p>
                ${springMacroRequestContext.getMessage("register.labelClause_1")}
                <a href="http://orcid.org/content/orcid-terms-use" target="terms_and_conditions">
                ${springMacroRequestContext.getMessage("register.labelClause_2")}
                </a>
                ${springMacroRequestContext.getMessage("register.labelClause_3")}
                <br /><br />
            </p>
            <#include "/common/browser-checks.ftl" />
            <#if ((RequestParameters['linkRequest'])?? && (RequestParameters['firstName'])?? && (RequestParameters['lastName'])?? && (RequestParameters['emailId'])??)>
                <div ng-controller="OauthAuthorizationController" id="RegistrationForm" ng-init="oauth2ScreensLoadRegistrationForm('${RequestParameters.firstName?js_string}', '${RequestParameters.lastName?js_string}', '${RequestParameters.emailId?js_string}', '${RequestParameters.linkRequest?js_string}')">
            <#else>
                <div ng-controller="OauthAuthorizationController" id="RegistrationCtr" ng-init="oauth2ScreensLoadRegistrationForm()">
            </#if>
    		  <#include "/includes/register_inc.ftl" />
            </div>
        </div>
    </div>
</div>
</@protected>