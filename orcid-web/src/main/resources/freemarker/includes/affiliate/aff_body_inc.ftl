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
<div  class="alert">We are migrating to only Education and Employment affiliations. Please delete any affiliations shown here.</div>
<ul ng-hide="!affiliationsSrvc.affiliations.length" class="workspace-affiliations workspace-body-list bottom-margin-medium" ng-cloak>        
    <li class="bottom-margin-small" ng-repeat="affiliation in affiliationsSrvc.affiliations | orderBy:['-dateSortString', 'affiliationName']">            	
		<#if request.requestURI?ends_with("my-orcid2")>
		    <#include "aff_row_inc_v2.ftl" />
		<#else>
		    <#include "aff_row_inc.ftl" />
		</#if>
    </li>           
</ul>
<div ng-show="affiliationsSrvc.loading == true" class="text-center">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->    
</div>
<div ng-show="affiliationsSrvc.loading == false && affiliationsSrvc.affiliations.length == 0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noaffilationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyaffiliations")} <a ng-click="addAffiliationModal()">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
</div>
