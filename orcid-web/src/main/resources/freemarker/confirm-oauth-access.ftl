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
<@base>
<#assign displayName = "">
<#if client_name??>
	<#assign displayName = client_name>
</#if>
<!-- colorbox-content -->
<div class="container top-green-border confirm-oauth-access" ng-controller="OauthAuthorizationController">		
	<!-- Freemarker and GA variables -->
	<#assign user_id = "">			
		<#if userId??>
			<#assign user_id = userId>
		</#if>
	<#assign authOnClick = "">		        
	<#assign denyOnClick = " orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Authorize_Deny', 'OAuth " + client_group_name?js_string + " - " + client_name?js_string + "']);">	    	
	<!-- /Freemarker and GA variables -->
	<@security.authorize ifAnyGranted="ROLE_USER">
	<div class="row top-header">
		<div class="col-md-2 col-sm-6 col-xs-12">
			<div class="logo">
	        	<h1 class="oauth_h1_margin"><a href="${aboutUri}"><img src="${staticCdn}/img/orcid-logo.png" alt="ORCID logo" /></a></h1>
	        	<p>${springMacroRequestContext.getMessage("confirm-oauth-access.connectingresearchandresearchers")}</p>
	        </div>		
		</div>
		
	    <div class="col-md-4 col-sm-6 col-xs-12">	        
	        <div class="row">
	            <#include "includes/mini_id_banner.ftl"/>
	        </div>      
	    </div>	    
	</div>
	<div class="row">
		<div class="col-md-6">	
		<div class="app-client-name">
			<h3 ng-click="toggleClientDescription()">${client_name} - ${client_group_name}
				<a class="glyphicon glyphicon-question-sign"></a>				
			</h3>
		</div>
		<div class="app-client-description">
			<p ng-show="showClientDescription">
				<span class="uppercase gray-bold-about"><@orcid.msg 'oauth_sign_in.about'/></span> ${client_description}
			</p>
		</div>
		<div>
			<p><@orcid.msg 'orcid.frontend.oauth.have_asked'/></p>
		</div>
		<ul class="oauth-scopes">
			<#list scopes as scope>
				<li>				
					<#assign authOnClick = authOnClick + " orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'Authorize_" + scope.name()?replace("ORCID_", "") + "', 'OAuth " + client_group_name?js_string + " - " + client_name?js_string + "']);">
					<#if scope.value()?ends_with("/create")>
						<span class="mini-icon glyphicon glyphicon-cloud-download green"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/>
					<#elseif scope.value()?ends_with("/update")>
						<span class="mini-icon glyphicon glyphicon-refresh green"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/>
					<#elseif scope.value()?ends_with("/read-limited")>
						<span class="mini-icon glyphicon glyphicon-eye-open green"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/>
					<#else>
						<span class="mini-orcid-icon oauth-bullet"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/>
					</#if>	
				</li>
	    	</#list>				
		</ul>	
		<div>
			<p><@orcid.msg 'orcid.frontend.web.oauth_is_secure'/>.&nbsp;<a href="${aboutUri}/footer/privacy-policy" target="_blank"><@orcid.msg 'public-layout.privacy_policy'/></a>.</p>
		</div>			
		<div id="login-buttons" ng-init="loadAndInitAuthorizationForm('${scopesString}','${redirect_uri}','${client_id}','${response_type}')">
			<div class="row">
	            <div class="col-md-12">                     		            		               					
					<button class="btn btn-primary pull-right" name="authorize" value="<@orcid.msg 'confirm-oauth-access.Authorize'/>" ng-click="authorize()" onclick="${authOnClick} orcidGA.gaFormSumbitDelay(this); return false;">
						<@orcid.msg 'confirm-oauth-access.Authorize' />
					</button>		                 	            
					<a class="oauth_deny_link pull-right" name="deny" value="<@orcid.msg 'confirm-oauth-access.Deny'/>" ng-click="deny()" onclick="${denyOnClick} orcidGA.gaFormSumbitDelay(this); return false;">
						<@orcid.msg 'confirm-oauth-access.Deny' />
					</a>
				</div>					
			</div>
		</div>
	</div>
	</@security.authorize>
</div>
</@base>