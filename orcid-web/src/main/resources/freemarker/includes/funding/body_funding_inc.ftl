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
<ul ng-hide="!fundingSrvc.fundings.length" class="workspace-fundings workspace-body-list bottom-margin-medium" ng-cloak>
	<li class="bottom-margin-small" ng-repeat="funding in fundingSrvc.fundings | orderBy:['-startDate.year', '-startDate.month', '-startDate.day', '-endDate.year', '-endDate.month', '-endDate.day', 'title.value']"> 
		<div class="row">        
			
			<!-- Information -->
			<div class="col-md-8 col-sm-8 col-xs-8">
				<h3 class="funding-title">
					<strong ng-show="funding.fundingTitle.title.value">{{funding.fundingTitle.title.value}}:</strong>
					<span class="funding-name" ng-bind-html="funding.fundingName.value"></span>
					<span class="funding-date" ng-show="funding.startDate && !funding.endDate">
						(<span ng-show="funding.startDate.month">{{funding.startDate.month}}-</span><span ng-show="funding.startDate.year">{{funding.startDate.year}}</span>
				    	<@orcid.msg 'workspace_fundings.dateSeparator'/>&nbsp;
				    	<@orcid.msg 'workspace_fundings.present'/>)
					</span>
					<span class="funding-date" ng-show="funding.startDate && funding.endDate">
						(<span ng-show="funding.startDate.month">{{funding.startDate.month}}-</span><span ng-show="funding.startDate.year">{{funding.startDate.year}}</span>
						<@orcid.msg 'workspace_fundings.dateSeparator'/>&nbsp;<span ng-show="funding.endDate.month">{{funding.endDate.month}}-</span><span ng-show="funding.endDate.year">{{funding.endDate.year}}</span>)
					</span>
					<span class="funding-date" ng-show="!funding.startDate && funding.endDate">
					     (<span ng-show="funding.endDate.month">{{funding.endDate.month}}-</span><span ng-show="funding.endDate.year">{{funding.endDate.year}}</span>)
					</span>
				</h3>
			</div>
			
			<!-- Privacy Settings -->
	        <div class="col-md-4 col-sm-4">
	        	<#include "funding_more_info_inc.ftl"/>
	        	<#if !(isPublicProfile??)>
	        		<a href ng-click="deleteFunding(funding)" class="glyphicon glyphicon-trash grey"></a>
	        		<ul class="workspace-private-toolbar">
						<@orcid.privacyToggle  angularModel="funding.visibility.visibility"
						questionClick="toggleClickPrivacyHelp(funding.putCode.value)"
						clickedClassCheck="{'popover-help-container-show':privacyHelp[funding.putCode.value]==true}" 
						publicClick="setPrivacy(funding, 'PUBLIC', $event)" 
	                	limitedClick="setPrivacy(funding, 'LIMITED', $event)" 
	                	privateClick="setPrivacy(funding, 'PRIVATE', $event)" />			        
		        	</ul>
		        </#if>
			</div>
		</div>
		<div class="row">			
			<div class="col-md-12 col-sm-12 col-xs-12">
				<div class="funding-details" ng-show="funding.url">
					<span ng-bind-html="funding.url.value"></span>
				</div>
			</div>	
		</div>
	</li>
</ul>
<div ng-show="fundingSrvc.loading == true;" class="text-center">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-show="fundingSrvc.loading == false && fundingSrvc.fundings.length == 0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true><@orcid.msg 'workspace_fundings_body_list.nograntaddedyet' /><#else><@orcid.msg 'workspace_fundings.havenotaddaffiliation' /><a ng-click="addFundingModal()"> <@orcid.msg 'workspace_fundings_body_list.addsomenow'/></a></#if></strong>
</div>






















