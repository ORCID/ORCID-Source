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
<ul ng-hide="!affiliationsSrvc.employments.length" class="workspace-affiliations workspace-body-list bottom-margin-medium" ng-cloak>
	<li class="bottom-margin-small workspace-border-box affiliation-box card" ng-repeat="group in affiliationsSrvc.employments | orderBy:sortState.predicate:sortState.reverse"> 
	    <#include "aff_row_inc_v3.ftl" />
	</li>
</ul>
<div ng-show="affiliationsSrvc.loading == true;" class="text-center">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-show="affiliationsSrvc.loading == false && affiliationsSrvc.employments.length == 0" class="" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noemploymentaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyemployment")} <a ng-click="addAffiliationModal('employment')">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
</div>
