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

<script type="text/ng-template" id="delete-affiliation-modal">
	<div style="padding: 20px;">
		<h3 style="margin-bottom: 0px;">${springMacroRequestContext.getMessage("manage.deleteAffiliation.pleaseConfirm")}</h3>
		{{fixedTitle}}<br />
		<br />
    	<div class="btn btn-danger" ng-click="deleteByIndex()">
    		${springMacroRequestContext.getMessage("manage.deleteAffiliation.delete")}
    	</div>
    	<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteAffiliation.cancel")}</a>
    <div>
</script>

<ul ng-hide="!affiliations.length" class="workspace-publications workspace-body-list bottom-margin-medium" ng-cloak>        
    <li class="bottom-margin-small" ng-repeat="affiliation in affiliations">            	
        <div class="pull-right" style="right: 145px; top: 20px; width: 15px;"><a href ng-click="deleteAffiliation($index)" class="icon-trash orcid-icon-trash grey"></a></div>
		<div style="width: 530px;">
        <h3 class="affiliation-title">
        	<strong ng-bind-html="affiliation.affiliationName"></strong>
        </h3>
        </div>
        <div class="pull-right" style="width: 130px;">
		<@orcid.privacyToggle "affiliation.visibility" "setPrivacy($index, 'PUBLIC', $event)" 
                    	  "setPrivacy($index, 'LIMITED', $event)" "setPrivacy($index, 'PRIVATE', $event)" />
		</div>
    </li>           
</ul>
<div ng-show="numOfAffiliationsToAdd==null || (numOfAffiliationsToAdd > affiliations.length)" class="text-center">
    <i class="icon-spinner icon-4x icon-spin  green"></i>
</div>
<div ng-show="numOfAffiliationsToAdd==0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noaffilationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyaffiliations")} <a ng-click="addAffiliationModal()">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
</div>
    