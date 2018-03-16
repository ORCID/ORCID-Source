<ul id="employments-list" ng-hide="!affiliationsSrvc.employments.length" class="workspace-affiliations workspace-body-list bottom-margin-medium" ng-cloak>
	<li class="bottom-margin-small workspace-border-box affiliation-box card" ng-repeat="group in affiliationsSrvc.employments | orderBy:sortState.predicate:sortState.reverse" employment-put-code="{{group.getActive().putCode.value}}">  
	    <#include "aff_row_inc_v3.ftl" />
	</li>
</ul>
<div ng-if="affiliationsSrvc.loading" class="text-center">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-if="affiliationsSrvc.loading == false && affiliationsSrvc.employments.length == 0" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noemploymentaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyemployment")} <a ng-click="addAffiliationModal('employment')">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
</div>
