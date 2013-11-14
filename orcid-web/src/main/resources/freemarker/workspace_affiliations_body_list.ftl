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

<#include "includes/affiliate/del_affiliate_inc.ftl"/>

<#include "includes/affiliate/add_affiliate_inc.ftl"/>
<div ng-controller="AffiliationCtrl">
	<div id="workspace-education" class="workspace-accordion-item workspace-accordion-active" >
		<div class="workspace-accordion-header"><a name='workspace-educations' />
		    <a href="" ng-click="toggleDisplayAffiliations()" class="toggle-text">
		  		<i class="icon-caret-down icon" ng-class="{'icon-caret-right':displayAffiliations==false}"></i></a>
		   	</a> 
		    <a href="" ng-click="toggleDisplayAffiliations()" class="toggle-text"><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.education'/></a>
			<a href="" class="label btn-primary" ng-click="addAffiliationModal('education')"><@orcid.msg 'manual_affiliation_form_contents.add_education_manually'/></a>
		</div>
		<div ng-show="displayAffiliations" class="workspace-accordion-content">
			<ul ng-hide="!educations.length" class="workspace-affiliations workspace-body-list bottom-margin-medium" ng-cloak>
				<li class="bottom-margin-small" ng-repeat="affiliation in educations | orderBy:['-startDate.year', '-startDate.month', '-startDate.day', '-endDate.year', '-endDate.month', '-endDate.day', 'affiliationName.value']"> 
					<#include "includes/affiliate/aff_row_inc.ftl" />
				</li>
			</ul>
			<div ng-show="loadingAff == true;" class="text-center">
			    <i class="glyphicon glyphicon-refresh spin green"></i>
			</div>
			<div ng-show="loadingAff == false && educations.length == 0" class="alert alert-info" ng-cloak>
			    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noeducationaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyeducation")} <a ng-click="addAffiliationModal('education')">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
			</div>
		</div>
	</div>	
	<div id="workspace-education" class="workspace-accordion-item workspace-accordion-active" >
		<div class="workspace-accordion-header"><a name='workspace-employments' />
		    <a href="" ng-click="toggleDisplayAffiliations()" class="toggle-text">
		  		<i class="icon-caret-down icon" ng-class="{'icon-caret-right':displayAffiliations==false}"></i></a>
		   	</a> 
		    <a href="" ng-click="toggleDisplayAffiliations()" class="toggle-text"><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.employment'/></a>
			<a href="" class="label btn-primary" ng-click="addAffiliationModal('employment')"><@orcid.msg 'manual_affiliation_form_contents.add_employment_manually'/></a>
		</div>
		<div ng-show="displayAffiliations" class="workspace-accordion-content">
			<ul ng-hide="!employments.length" class="workspace-affiliations workspace-body-list bottom-margin-medium" ng-cloak>
				<li class="bottom-margin-small" ng-repeat="affiliation in employments | orderBy:['-startDate.year', '-startDate.month', '-startDate.day', '-endDate.year', '-endDate.month', '-endDate.day', 'affiliationName.value']"> 
					<#include "includes/affiliate/aff_row_inc.ftl" />
				</li>
			</ul>
			<div ng-show="loadingAff == true;" class="text-center">
			    <i class="glyphicon glyphicon-refresh spin green"></i>
			</div>
			<div ng-show="loadingAff == false && employments.length == 0" class="alert alert-info" ng-cloak>
			    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noemploymentaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyemployment")} <a ng-click="addAffiliationModal('employment')">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
			</div>
		</div>
	</div>	
	<div ng-show='affiliations.length != 0' id="workspace-affiliations" class="workspace-accordion-item workspace-accordion-active" ng-cloak>
		<div class="workspace-accordion-header"><a name='workspace-affiliations' />
		    <a href="" ng-click="toggleDisplayAffiliations()" class="toggle-text">
		  		<i class="icon-caret-down icon" ng-class="{'icon-caret-right':displayAffiliations==false}"></i></a>
		   	</a> 
		    <a href="" ng-click="toggleDisplayAffiliations()" class="toggle-text"><@orcid.msg 'workspace_bio.Affiliations'/></a>
			<a href="" class="label btn-primary" ng-click="addAffiliationModal()"><@orcid.msg 'manual_affiliation_form_contents.add_affiliation_manually'/></a>
		</div>
		<div ng-show="displayAffiliations" class="workspace-accordion-content">
			<div  class="alert">We are migrating to only Education and Employment affiliations. Please delete any affiliations shown here.</div>
			<ul ng-hide="!affiliations.length" class="workspace-affiliations workspace-body-list bottom-margin-medium" ng-cloak>        
			    <li class="bottom-margin-small" ng-repeat="affiliation in affiliations | orderBy:['-startDate.year', '-startDate.month', '-startDate.day', '-endDate.year', '-endDate.month', '-endDate.day', 'affiliationName.value']">            	
					<#include "includes/affiliate/aff_row_inc.ftl" />
			    </li>           
			</ul>
			<div ng-show="loadingAff == true" class="text-center">
			    <i class="glyphicon glyphicon-refresh spin green"></i>
			</div>
			<div ng-show="loadingAff == false && affiliations.length == 0" class="alert alert-info" ng-cloak>
			    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noaffilationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyaffiliations")} <a ng-click="addAffiliationModal()">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
			</div>
		</div>
	</div>
</div>
    