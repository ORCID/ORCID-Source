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

	<script type="text/ng-template" id="delete-work-modal">
		<div style="padding: 20px;">
			<h3 style="margin-bottom: 0px;">${springMacroRequestContext.getMessage("manage.deleteWork.pleaseConfirm")}</h3>
			{{fixedTitle}}<br />
			<br />
	    	<div class="btn btn-danger" ng-click="deleteByIndex()">
	    		${springMacroRequestContext.getMessage("manage.deleteWork.delete")}
	    	</div>
	    	<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteWork.cancel")}</a>
	    <div>; 
	</script>
	
	 
	<ul ng-hide="!works.length" class="workspace-publications workspace-body-list bottom-margin-medium" ng-cloak>        
            <li class="bottom-margin-small" ng-repeat="work in works | orderBy:['-publicationDate.year', '-publicationDate.month', '-publicationDate.day']">            	
                <div class="pull-right" style="right: 145px; top: 20px; width: 15px;"><a href ng-click="deleteWork($index)" class="icon-trash orcid-icon-trash grey"></a></div>
				<div style="width: 530px;">
                <h3 class="work-title">
                	<strong ng-bind-html="work.workTitle.title.value"></strong><span class="work-subtitle" ng-show="work.workTitle.subtitle.value" ng-bind-html="':&nbsp;'.concat(work.workTitle.subtitle.value)"></span>
                	<span ng-show="work.publicationDate.month">{{work.publicationDate.month}}-</span><span ng-show="work.publicationDate.year">{{work.publicationDate.year}}</span>
                </h3>
                </div>
                <div class="pull-right" style="width: 130px;">
				<@orcid.privacyToggle "work.visibility.visibility" "setPrivacy($index, 'PUBLIC', $event)" 
		                    	  "setPrivacy($index, 'LIMITED', $event)" "setPrivacy($index, 'PRIVATE', $event)" />
				</div>
				<div  style="width: 680px;" class="work-metadata">
	                <span ng-repeat='ie in work.workExternalIdentifiers'>
	                	<span ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length'></span>
	                </span>
	                <span ng-show="work.url.value" style=" display: inline-block;">URL: <a href="{{work.url.value | urlWithHttp}}" target="_blank">{{work.url.value}}</a></span>
	            </div>
                
                <div ng-show="work.shortDescription" ng-bind-html="work.shortDescription.value" style="width: 680px;"></div>
                <div ng-show="work.citationForDisplay" class="citation {{work.workCitation.workCitationType.toLowerCase()}}" ng-bind-html="work.citationForDisplay" style="width: 680px;"></div>
            </li>           
	</ul>
	<div ng-show="numOfWorksToAdd==null || (numOfWorksToAdd > works.length)" class="text-center">
	    <i class="icon-spinner icon-4x icon-spin  green"></i>
	</div>
    <div ng-show="numOfWorksToAdd==0" class="alert alert-info" ng-cloak>
        <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} <a ng-click="addWorkModal()">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a></#if></strong>
    </div>
    