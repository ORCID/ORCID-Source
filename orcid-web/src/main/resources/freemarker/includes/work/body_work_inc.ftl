<ul ng-hide="!works.length" class="workspace-publications workspace-body-list bottom-margin-medium" ng-cloak>        
    <li class="bottom-margin-small" ng-repeat="work in works | orderBy:['-publicationDate.year', '-publicationDate.month', '-publicationDate.day']">            	
		<#include "all_info_work_inc.ftl"/>
        <#if !(isPublicProfile??)>
        	<div class="pull-right" style="right: 140px; top: 20px; width: 15px;"><a href ng-click="deleteWork(work.putCode.value)" class="icon-trash orcid-icon-trash grey"></a></div>
			<div class="pull-right" style="width: 130px;">
				<@orcid.privacyToggle angularModel="work.visibility.visibility" 
					publicClick="setPrivacy(work.putCode.value, 'PUBLIC', $event)" 
                	limitedClick="setPrivacy(work.putCode.value, 'LIMITED', $event)" 
                	privateClick="setPrivacy(work.putCode.value, 'PRIVATE', $event)"
                	popoverStyle="left: -255px;"
                	arrowStyle="left: 261px;" />
			</div>
		</#if>
		<div style="width: 530px;">
        <h3 class="work-title">
        	<strong ng-bind-html="work.workTitle.title.value"></strong><span class="work-subtitle" ng-show="work.workTitle.subtitle.value" ng-bind-html="':&nbsp;'.concat(work.workTitle.subtitle.value)"></span>
        	<span ng-show="work.publicationDate.month">{{work.publicationDate.month}}-</span><span ng-show="work.publicationDate.year">{{work.publicationDate.year}}</span>
        </h3>
        </div>
        
        
		<div  style="width: 680px;" class="work-metadata">
            <span ng-repeat='ie in work.workExternalIdentifiers'>
            	<span ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length'></span>
            </span>
            <span ng-show="work.url.value" style=" display: inline-block;">URL: <a href="{{work.url.value | urlWithHttp}}" target="_blank">{{work.url.value}}</a></span>
        </div>
        
        <div ng-show="work.shortDescription" ng-bind-html="work.shortDescription.value" style="width: 680px; white-space: pre-wrap;"></div>
        <div ng-show="work.citationForDisplay" class="citation {{work.workCitation.workCitationType.toLowerCase()}}" ng-bind-html="work.citationForDisplay" style="width: 680px;"></div>
    </li>           
</ul>
<div ng-show="numOfWorksToAdd==null || (numOfWorksToAdd > works.length)" class="text-center">
    <i class="icon-spinner icon-4x icon-spin  green"></i>
</div>
<div ng-show="numOfWorksToAdd==0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} <a ng-click="addWorkModal()">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a></#if></strong>
</div>
    