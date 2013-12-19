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
<ul ng-hide="!grantsSrvc.grants.length" class="workspace-grants workspace-body-list bottom-margin-medium" ng-cloak>
	<li class="bottom-margin-small" ng-repeat="grant in grantsSrvc.grants | orderBy:['-startDate.year', '-startDate.month', '-startDate.day', '-endDate.year', '-endDate.month', '-endDate.day', 'title.value']"> 
		<div class="row">        
			<!-- Information -->
			<div class="col-md-8 col-sm-8">
				<h3 class="affiliation-title">
					<strong ng-show="grant.title">{{grant.title.value}}:</strong>
					<span ng-bind-html="grant.fundingName.value"></span>
					
					<span class="grant-date" ng-show="grant.startDate && !grant.endDate">
						(<span ng-show="grant.startDate.month">{{grant.startDate.month}}-</span><span ng-show="grant.startDate.year">{{grant.startDate.year}}</span>
					    <@orcid.msg 'workspace_grants.dateSeparator'/>
					    <@orcid.msg 'workspace_grants.present'/>)
					</span>
					<span class="grant-date" ng-show="grant.startDate && grant.endDate">
						(<span ng-show="grant.startDate.month">{{grant.startDate.month}}-</span><span ng-show="grant.startDate.year">{{grant.startDate.year}}</span>
						<@orcid.msg 'workspace_grants.dateSeparator'/><span ng-show="grant.endDate.month">{{grant.endDate.month}}-</span><span ng-show="grant.endDate.year">{{grant.endDate.year}}</span>)
					</span>
					<span class="grant-date" ng-show="!grant.startDate && grant.endDate">
						     (<span ng-show="grant.endDate.month">{{grant.endDate.month}}-</span><span ng-show="grant.endDate.year">{{grant.endDate.year}}</span>)
					</span>					
				</h3>
				<div class="grant-details" ng-show="grant.url">
					<span ng-bind-html="grant.url.value"></span>
				</div>
			</div>
			
			
			
			<!-- Privacy Settings -->
	        <div class="col-md-4 col-sm-4 workspace-toolbar">
	        	<#include "grant_more_info_inc.ftl"/>
	        	<#if !(isPublicProfile??)>
	        		<a href ng-click="deleteGrant(grant)" class="glyphicon glyphicon-trash grey"></a>
	        		<ul class="workspace-private-toolbar">
						<@orcid.privacyToggle  angularModel="grant.visibility.visibility"
						questionClick="toggleClickPrivacyHelp(grant.putCode.value)"
						clickedClassCheck="{'popover-help-container-show':privacyHelp[grant.putCode.value]==true}" 
						publicClick="setPrivacy(grant, 'PUBLIC', $event)" 
	                	limitedClick="setPrivacy(grant, 'LIMITED', $event)" 
	                	privateClick="setPrivacy(grant, 'PRIVATE', $event)" />			        
		        	</ul>
		        </#if>
			</div>
			
			
			
			
		</div>
	</li>
</ul>
<div ng-show="grantsSrvc.loading == true;" class="text-center">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-show="grantsSrvc.loading == false && grantsSrvc.grants.length == 0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true><@orcid.msg 'workspace_grants_body_list.nograntaddedyet' /><#else><@orcid.msg 'workspace_grants.havenotaddaffiliation' /><a ng-click="addGrantModal()"> <@orcid.msg 'workspace_grants_body_list.addsomenow'/></a></#if></strong>
</div>






















