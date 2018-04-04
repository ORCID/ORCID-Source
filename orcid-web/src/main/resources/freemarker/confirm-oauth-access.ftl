<@base>
<@orcid.checkFeatureStatus 'ANGULAR2_QA'> 
    <#include "/includes/ng2_templates/switch-user-ng2-template.ftl">
    <oauth-authorization-ng2></oauth-authorization-ng2>
</@orcid.checkFeatureStatus>
<@orcid.checkFeatureStatus featureName='ANGULAR1_LEGACY' enabled=false>
    <!-- colorbox-content -->
    <div class="container top-green-border confirm-oauth-access oneStepWidth" ng-controller="OauthAuthorizationController">     
        <!-- /Freemarker and GA variables -->
        <@security.authorize access="hasRole('ROLE_USER')">
            <div class="row top-header">
                <div class="col-md-4 col-sm-12 col-xs-12">
                    <div class="logo">
                        <h1><a href="${aboutUri}"><img src="${staticCdn}/img/orcid-logo.png" alt="ORCID logo" /></a></h1>
                    </div>      
                </div>
                
                <div class="col-md-8 col-sm-12 col-xs-12">
                     <#include "includes/mini_id_banner.ftl"/>                
                </div>      
            </div>  
            <div class="row">
                <div class="col-md-12"> 
                    <div class="app-client-name"> 
                        <h3 ng-click="toggleClientDescription()">{{requestInfoForm.clientName}}
                            <a class="glyphicon glyphicon-question-sign oauth-question-sign"></a>               
                        </h3>
                    </div>
                    <div class="app-client-description">
                        <p ng-show="showClientDescription">
                            <span class="uppercase gray-bold-about"><@orcid.msg 'oauth_sign_in.about'/></span> {{requestInfoForm.clientDescription}}
                        </p>
                    </div>
                    <div>
                        <p><@orcid.msg 'orcid.frontend.oauth.have_asked'/></p>
                    </div>
                    <div>
                        <#include "includes/oauth/scopes.ftl"/>
                    </div>
                    <div>
                        <@orcid.checkFeatureStatus 'GDPR_UI'> 
                            <p><@orcid.msg 'confirm-oauth-access.thisApplicationWillNot'/>&nbsp;<a href="${baseUri}/account#manage-permissions" target="confirm-oauth-access.accountSettings"><@orcid.msg 'confirm-oauth-access.accountSettings'/></a>.</p>
                        </@orcid.checkFeatureStatus>
                        <@orcid.checkFeatureStatus featureName='GDPR_UI' enabled=false> 
                            <p><@orcid.msg 'orcid.frontend.web.oauth_is_secure'/>.&nbsp;<a href="${aboutUri}/footer/privacy-policy" target="public-layout.privacy_policy"><@orcid.msg 'public-layout.privacy_policy'/></a>.</p>
                        </@orcid.checkFeatureStatus>
                    </div>          
                    <div id="login-buttons" ng-init="loadAndInitAuthorizationForm()">
                        <div class="row">
                            <div>
                                <button id="authorize" class="btn btn-primary topBuffer bottomBuffer" name="authorize" value="<@orcid.msg 'confirm-oauth-access.Authorize'/>" ng-click="authorize()">
                                    <@orcid.msg 'confirm-oauth-access.Authorize' />
                                </button>
                                <a class="oauth-deny-link topBuffer" name="deny" value="<@orcid.msg 'confirm-oauth-access.Deny'/>" ng-click="deny()">
                                    <@orcid.msg 'confirm-oauth-access.Deny' />
                                </a>
                            </div>                  
                        </div>
                    </div>
                </div>      
            </div>
        </@security.authorize>
    </div>
</@orcid.checkFeatureStatus>
</@base>
