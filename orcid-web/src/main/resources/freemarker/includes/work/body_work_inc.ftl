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
<ul ng-hide="!works.length" class="workspace-publications workspace-body-list bottom-margin-medium" id="body-work-list" ng-cloak>
        
    <li class="bottom-margin-small" ng-repeat="work in works | orderBy:['-publicationDate.year', '-publicationDate.month', '-publicationDate.day']">        
		<div class="row">
			<!-- Info -->
			<div class="col-md-8 col-sm-8">
		        <h3 class="work-title">
		        	<strong ng-bind-html="work.workTitle.title.value"></strong><span class="work-subtitle" ng-show="work.workTitle.subtitle.value" ng-bind-html="':&nbsp;'.concat(work.workTitle.subtitle.value)"></span>
		        	<span ng-show="work.publicationDate.month">{{work.publicationDate.month}}-</span><span ng-show="work.publicationDate.year">{{work.publicationDate.year}}</span>
		        </h3> 
	        </div>
	        <!-- Settings -->
	        <div class="col-md-4 col-sm-4 workspace-toolbar">
	        	<!-- More info -->	        					
	        	<#include "work_more_info_inc.ftl"/>
	        	<#if !(isPublicProfile??)>
	        		<!-- Trash can -->
					<a href ng-click="deleteWork(work.putCode.value)" class="glyphicon glyphicon-trash grey"></a>
	        	</#if>
	        	<#if !(isPublicProfile??)>
	        		<!-- Privacy bar -->
					<ul class="workspace-private-toolbar">
						<li>
						<@orcid.privacyToggle angularModel="work.visibility" 
						    questionClick="toggleClickPrivacyHelp(work.putCode.value)"
						    clickedClassCheck="{'popover-help-container-show':privacyHelp[work.putCode.value]==true}"
							publicClick="setPrivacy(work.putCode.value, 'PUBLIC', $event)" 
		                	limitedClick="setPrivacy(work.putCode.value, 'LIMITED', $event)" 
		                	privateClick="setPrivacy(work.putCode.value, 'PRIVATE', $event)"/>
		                </li>			
					</ul>				
				</#if>				
			</div>
        </div>
    </li><!-- bottom-margin-small -->
</ul>
<div ng-show="worksSrvc.loading == true" class="text-center" id="workSpinner">
	<i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i><!-- Hidden with a CSS hack on IE 7 only -->
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-show="worksSrvc.loading == false && works.length == 0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} <a ng-click="addWorkModal()">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a></#if></strong>
</div>
    