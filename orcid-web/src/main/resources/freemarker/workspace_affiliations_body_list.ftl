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
	<!-- Education -->
	<div id="workspace-education" class="workspace-accordion-item workspace-accordion-active" >
		<div class="workspace-accordion-header">
			<div class="row">
				<div class="col-md-6 col-sm-6 col-xs-12">
					<a name='workspace-educations' />
				    <a href="" ng-click="workspaceSrvc.toggleEducation()" class="toggle-text">
				  		<i class="glyphicon-chevron-down glyphicon x0" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayEducation==false}"></i></a>
				   	</a>
				   	<a href="" ng-click="workspaceSrvc.toggleEducation()" class="toggle-text"><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.education'/></a> 
			    </div>
			    <div class="col-md-6 col-sm-6 col-xs-12" ng-show="workspaceSrvc.displayEducation">
					<a href="" class="action-option manage-button" ng-click="addAffiliationModal('education')">
						<span class="glyphicon glyphicon-plus"></span>
						<@orcid.msg 'manual_affiliation_form_contents.add_education_manually'/>
					</a>
				</div>
			</div>
		</div>
		<div ng-show="workspaceSrvc.displayEducation" class="workspace-accordion-content">
			<#include "includes/affiliate/edu_body_inc.ftl" />
		</div>
	</div>
	<!-- Employment -->
	<div id="workspace-employment" class="workspace-accordion-item workspace-accordion-active" >
		<div class="workspace-accordion-header">
			<div class="row">
				<div class="col-md-6 col-sm-6 col-xs-12">
					<a name='workspace-employments' />
				    <a href="" ng-click="workspaceSrvc.toggleEmployment()" class="toggle-text">
				  		<i class="glyphicon-chevron-down glyphicon x0" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayEmployment==false}"></i></a>
				   	</a>
				   	<a href="" ng-click="workspaceSrvc.toggleEmployment()" class="toggle-text"><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.employment'/></a>
				</div>
				<div class="col-md-6 col-sm-6 col-xs-12" ng-show="workspaceSrvc.displayEmployment">
					<a href="" class="action-option manage-button" ng-click="addAffiliationModal('employment')">
						<span class="glyphicon glyphicon-plus"></span>
						<@orcid.msg 'manual_affiliation_form_contents.add_employment_manually'/>
					</a>				
				</div>
			</div>
		</div>
		<div ng-show="workspaceSrvc.displayEmployment" class="workspace-accordion-content">
			<#include "includes/affiliate/emp_body_inc.ftl" />
		</div>
	</div>
	<!-- Affiliations -->
	<!-- It section still requires styling -->
	<div ng-show='affiliationsSrvc.affiliations.length != 0' id="workspace-affiliations" class="workspace-accordion-item workspace-accordion-active" ng-cloak>	
		<div class="workspace-accordion-header">
			<div class="row">
				<div class="col-md-6 col-sm-6 col-xs-12">
					<a name='workspace-affiliations' />
				    <a href="" ng-click="workspaceSrvc.toggleAffiliations()" class="toggle-text">
				  		<i class="glyphicon-chevron-down glyphicon x0" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayAffiliations==false}"></i></a>
				   	</a> 
				    <a href="" ng-click="workspaceSrvc.toggleAffiliations()" class="toggle-text"><@orcid.msg 'workspace_bio.Affiliations'/></a>
				 </div>
		    </div>
		</div>
		<div ng-show="workspaceSrvc.displayAffiliations" class="workspace-accordion-content">
			<#include "includes/affiliate/aff_body_inc.ftl" />		
		</div>
	</div>
</div>
    