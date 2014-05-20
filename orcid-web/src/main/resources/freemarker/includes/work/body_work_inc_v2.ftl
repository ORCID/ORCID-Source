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
<ul ng-hide="!worksSrvc.works.length" class="workspace-publications workspace-body-list bottom-margin-medium" id="body-work-list" ng-cloak>
    <li class="bottom-margin-small" ng-repeat="work in worksSrvc.works | orderBy:['-dateSortString', 'workTitle.title.value']">        
		<div class="row"> 
			<!-- Main title -->
			<div class="col-md-9 col-sm-9 col-xs-12">
		        <h3 class="work-title">
		        	<strong ng-bind="work.workTitle.title.value"></strong><span class="work-subtitle" ng-show="work.workTitle.subtitle.value" ng-bind="':&nbsp;'.concat(work.workTitle.subtitle.value)"></span>		        			        	
		        </h3>
		        <div class="info-date-detail">
		        	<span ng-show="work.publicationDate.year">{{work.publicationDate.year}}</span><span ng-show="work.publicationDate.month">-{{work.publicationDate.month}}</span>
		        </div>		        
	        </div>
	        <!-- Settings -->
	        <div class="col-md-3 col-sm-3 col-xs-12 workspace-toolbar">
	        	
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
        
        <!-- Identifiers / URL / Validations / Versions -->
		<div class="row bottomBuffer">
			<div class="col-md-9">
				<ul class="id-details">				
					<li><strong>DOI:</strong> <a href="">10.6084/M9.FIGSHARE.841742</a></li>
					<li><strong>URL:</strong> <a href="">http://www.ibridgenetwork.org</a></li>
				</ul>
			</div>
			<div class="col-md-3">
				<ul class="validations-versions">
					<li><span class="glyphicon glyphicon-ok green"></span><strong>Validated</strong> (2)</li>
					<li><span class="glyphicon glyphicon-file green"></span>Versions (3)</li> <!-- for non versions use class 'opaque' instead green -->
				</ul>
			</div>
		</div>        
       	
       	<!-- More info -->
       	<#include "work_more_info_inc_v2.ftl"/>      
        
        <!-- More info tabs -->
        <div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<div class="show-more-info-tab">			
					<a href="" ng-show="!moreInfo[work.putCode.value]" ng-click="showDetailsMouseClick(work.putCode.value,$event);"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'manage.developer_tools.show_details'/></a>
					<a href="" ng-show="moreInfo[work.putCode.value]" ng-click="showDetailsMouseClick(work.putCode.value,$event);"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details'/></a>
				</div>
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
{{works.length}}
<div ng-show="worksSrvc.loading == false && worksSrvc.works.length == 0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} <a ng-click="addWorkModal()">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a></#if></strong>
</div>
    