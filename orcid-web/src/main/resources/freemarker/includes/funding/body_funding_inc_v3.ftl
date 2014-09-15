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
<ul ng-hide="!fundingSrvc.groups.length" class="workspace-fundings workspace-body-list bottom-margin-medium" ng-cloak>
	<li class="bottom-margin-small workspace-border-box card ng-scope" ng-repeat="group in fundingSrvc.groups | orderBy:['-dateSortString', 'title']""> 
		<div class="row">        			
			<!-- Information -->
			<div class="col-md-9 col-sm-9">
				<h3 class="workspace-title">
					<strong ng-show="group.getActive().fundingTitle.title.value">{{group.getActive().fundingTitle.title.value}}:</strong>
					<span class="funding-name" ng-bind-html="group.getActive().fundingName.value"></span>					
				</h3>
				
				<div class="info-detail">
					<span class="funding-date" ng-show="group.getActive().startDate && !group.getActive().endDate">
						<span ng-show="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span ng-show="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span>						
				    	<@orcid.msg 'workspace_fundings.dateSeparator'/>
				    	<@orcid.msg 'workspace_fundings.present'/>
					</span>
					<span class="funding-date" ng-show="group.getActive().startDate && group.getActive().endDate">
						<span ng-show="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span ng-show="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span>						
						<@orcid.msg 'workspace_fundings.dateSeparator'/>
						<span ng-show="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span ng-show="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span>
					</span>
					<span class="funding-date" ng-show="!group.getActive().startDate && group.getActive().endDate">
					     <span ng-show="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span ng-show="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span>
					</span>
				</div>
			</div>	
			
			<!-- Privacy Settings -->
	        <div class="col-md-3 col-sm-3 workspace-toolbar">
	        	<#if !(isPublicProfile??)>
	        		<!-- <a href ng-click="deleteFunding(group.getActive())" class="glyphicon glyphicon-trash grey"></a> -->
	        		<ul class="workspace-private-toolbar">
	        			<li>
					 		<a href="" class="toolbar-button edit-item-button">
					 			<span class="glyphicon glyphicon-pencil edit-option-toolbar" title="" ng-click="openEditFunding(group.getActive())"></span>
					 		</a>	
					 	</li>
	        			<li>
							<@orcid.privacyToggle2  angularModel="group.getActive().visibility.visibility"
							questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
							clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}" 
							publicClick="setPrivacy(funding, 'PUBLIC', $event)" 
		                	limitedClick="setPrivacy(funding, 'LIMITED', $event)" 
		                	privateClick="setPrivacy(funding, 'PRIVATE', $event)" />
	                	</li>
		        	</ul>
		        </#if>
			</div>
		</div>
		<div ng-show="group.getActive().externalIdentifiers.length > 0" class="row bottomBuffer" ng-show="funding.externalIdentifiers.length > 0" ng-cloak>
				<div class="col-md-12 col-sm-12">					
					<div>					
						<span ng-repeat='ei in group.getActive().externalIdentifiers'>							
							<span ng-bind-html='ei | externalIdentifierHtml:$first:$last:group.getActive().externalIdentifiers.length'>
							</span>
						</span>
					</div>
				</div>				
	   </div>			
			
       	
       	<!-- More info -->
		<#include "funding_more_info_inc_v3.ftl"/>

       	<!-- Sources -->
       	<#include "funding_sources_inc_v3.ftl"/>       	        
	
	</li>
</ul>

<div ng-show="fundingSrvc.loading == true;" class="text-center">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-show="fundingSrvc.loading == false && fundingSrvc.groups.length == 0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true><@orcid.msg 'workspace_fundings_body_list.nograntaddedyet' /><#else><@orcid.msg 'workspace_fundings.havenotaddaffiliation' /><a ng-click="addFundingModal()"> <@orcid.msg 'workspace_fundings_body_list.addsomenow'/></a></#if></strong>
</div>

