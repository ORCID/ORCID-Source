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
<ul ng-hide="!affiliationsSrvc.employments.length" class="workspace-affiliations workspace-body-list bottom-margin-medium" ng-cloak>
	<li class="bottom-margin-small" ng-repeat="affiliation in affiliationsSrvc.employments | orderBy:['-startDate.year', '-startDate.month', '-startDate.day', '-endDate.year', '-endDate.month', '-endDate.day', 'affiliationName.value']"> 
		<#include "aff_row_inc.ftl" />
	</li>
</ul>
<div ng-show="affiliationsSrvc.loading == true;" class="text-center">
    <i class="glyphicon glyphicon-refresh spin x4 green"></i>
</div>
<div ng-show="affiliationsSrvc.loading == false && affiliationsSrvc.employments.length == 0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noemploymentaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyemployment")} <a ng-click="addAffiliationModal('employment')">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
</div>
