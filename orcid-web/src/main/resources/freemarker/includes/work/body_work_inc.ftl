<ul ng-hide="!worksSrvc.groups.length" class="workspace-publications workspace-body-list bottom-margin-medium" id="body-work-list" ng-cloak>
    <li class="bottom-margin-small" ng-repeat="group in worksSrvc.groups | orderBy:['-dateSortString', 'title']">        
		<div class="row" ng-repeat="work in group.activities | orderBy:['-dateSortString', 'title.value']"> 
			<!-- Info -->
			<div class="col-md-8 col-sm-8">
		        <h3 class="work-title">
		        	<strong ng-bind="work.title.value"></strong><span class="work-subtitle" ng-show="work.subtitle.value" ng-bind="':&nbsp;'.concat(work.subtitle.value)"></span>
		        	<span ng-show="work.publicationDate.year">{{work.publicationDate.year}}</span><span ng-show="work.publicationDate.month">-{{work.publicationDate.month}}</span>
		        </h3> 
	        </div>
	        <!-- Settings -->
	        <div class="col-md-4 col-sm-4 workspace-toolbar">
	        	<!-- More info -->
	        	<#include "work_more_info_inc.ftl"/>
	        	<#if !(isPublicProfile??)>
	        		<!-- Trash can -->
					<a href ng-click="deleteWorkConfirm(work.putCode.value, false)" class="glyphicon glyphicon-trash grey"></a>
	        	</#if>
	        	<#if !(isPublicProfile??)>
	        		<!-- Privacy bar -->
					<ul class="workspace-private-toolbar">
						<li>
						<@orcid.privacyToggle angularModel="work.visibility.visibility" 
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
<div ng-show="worksSrvc.loading == false && worksSrvc.groups.length == 0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} <a ng-click="addWorkModal()">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a></#if></strong>
</div>
    