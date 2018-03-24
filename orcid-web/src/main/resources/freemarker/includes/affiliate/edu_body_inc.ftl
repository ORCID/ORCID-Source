<ul id="educations-list" ng-hide="!affiliationsSrvc.educations.length" class="workspace-affiliations workspace-body-list bottom-margin-medium" ng-cloak>
	<li class="bottom-margin-small workspace-border-box affiliation-box card ng-scope" ng-repeat="group in affiliationsSrvc.educations | orderBy:sortState.predicate:sortState.reverse" education-put-code="{{group.getActive().putCode.value}}"> 
		    <#include "aff_row_inc_v3.ftl" />
	</li>
</ul>
<div ng-if="affiliationsSrvc.loading" class="text-center">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-if="affiliationsSrvc.loading == false && affiliationsSrvc.educations.length == 0" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noeducationaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyeducation")} <a ng-click="addAffiliationModal('education')">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
</div>		
