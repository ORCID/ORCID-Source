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
	<#include "/common/browser-checks.ftl" />
	<div class="col-md-6 col-sm-12 margin-top-bottom-box" ng-controller="OauthAuthorizationController">
		<#if RequestParameters['OneStep']??>	
			<div class="page-header">
				${client_name} - ${client_group_name}&nbsp;
				<a ng-show="!showClientDescription" ng-click="toggleClientDescription()" class="glyphicon glyphicon-chevron-down"></a>
				<a ng-show="showClientDescription" ng-click="toggleClientDescription()" class="glyphicon glyphicon-chevron-up"></a>
			</div>
			<div>
				<span ng-show="showClientDescription">
					${client_description}
				</span>
			</div>
			<div>
				<#list scopes as scope>
					<div><span class="mini-orcid-icon"></span><@spring.message "${scope.declaringClass.name}.${scope.name()}"/></div>
         		</#list>				
			</div>	
			<#assign denyOnClick = " orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Authorize_Deny', 'OAuth " + client_group_name?js_string + " - " + client_name?js_string + "']);"> 
			<div id="authForm">            		
	            <input ng-model="authorizationForm.scope.value" name="scopes" ng-init="${scopesString}" type="hidden"/>
	            <input ng-model="authorizationForm.clientId.value" name="client_id" ng-init="${client_id}" type="hidden"/>
	            <input ng-model="authorizationForm.responseType.value" name="response_type" ng-init="${response_type}" type="hidden"/>
	            <input ng-model="authorizationForm.redirectUri.value" name="redirect_uri" ng-init="${redirect_uri}" type="hidden"/>	            
	            <div>
	                <label for="userId">${springMacroRequestContext.getMessage("oauth_sign_in.labelemailorID")}</label>
	                <div class="relative">
	                   <input type="text" id="userId" ng-model="authorizationForm.userName.value" name="userId" value="${userId}" placeholder="Email or iD" class="input-xlarge">
	                </div>
	            </div>
	            <div id="passwordField">
	                <label for="password">${springMacroRequestContext.getMessage("oauth_sign_in.labelpassword")}</label>
	                <div class="relative">
	                   <input type="password" id="password" ng-model="authorizationForm.password.value" name="password" value="" placeholder="Password" class="input-xlarge">
	                </div>
	            </div>
	            <div id="buttons">                     		            		               					
					<button class="btn btn-primary" name="authorize" value="${springMacroRequestContext.getMessage('confirm-oauth-access.Authorize')}" ng-click="authorize()">
						<@orcid.msg 'confirm-oauth-access.Authorize' />
					</button>		                 	            
					<button class="btn btn-primary" name="deny" value="${springMacroRequestContext.getMessage('confirm-oauth-access.Deny')}" ng-click="deny()">
						<@orcid.msg 'confirm-oauth-access.Deny' />
					</button>
	            </div>
        	</div>
		<#else>			    
		    <div class="page-header">
			    <h3>${springMacroRequestContext.getMessage("oauth_sign_in.h3signin")}</h3>
			</div>
	        <form id="loginForm" action="<@spring.url '/signin/auth'/>" method="post">
	            
	            <@spring.bind "loginForm" />
	            <@spring.showErrors "<br/>" "error" />
				<input type="hidden" name="client_name" value="${client_name}" />
				<input type="hidden" name="client_group_name" value="${client_group_name}" />
	            <div>
	                <label for="userId">${springMacroRequestContext.getMessage("oauth_sign_in.labelemailorID")}</label>
	                <div class="relative">
	                   <input type="text" id="userId" name="userId" value="${userId}" placeholder="Email or iD" class="input-xlarge">
	                </div>
	            </div>
	            <div id="passwordField">
	                <label for="password">${springMacroRequestContext.getMessage("oauth_sign_in.labelpassword")}</label>
	                <div class="relative">
	                   <input type="password" id="password" name="password" value="" placeholder="Password" class="input-xlarge">
	                </div>
	            </div>
	            <div id="buttons">
	                <div class="relative">
	                    <button class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("oauth_sign_in.h3signin")}</button>
	                    <span id="ajax-loader" class="hide"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x2 green"></i></span>
	                </div>
	                <div class="relative margin-top-box">
	                	<a href="<@spring.url '/reset-password'/>">${springMacroRequestContext.getMessage("oauth_sign_in.forgottenpassword")}</a>
	                </div>
	            </div>
	        </form>	    
		</#if>
	</div>	   
