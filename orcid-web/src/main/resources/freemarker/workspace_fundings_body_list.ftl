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

<#include "includes/funding/del_funding_inc.ftl"/>

<#include "includes/funding/add_funding_inc.ftl"/>
<div ng-controller="FundingCtrl">
	<!-- Funding -->
	<div id="workspace-fundings" class="workspace-accordion-item workspace-accordion-active" >
		<div class="workspace-accordion-header"><a name='workspace-fundings' />
		    <a href="" ng-click="workspaceSrvc.toggleFunding()" class="toggle-text">
		  		<i class="glyphicon-chevron-down glyphicon x0" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayFunding==false}"></i></a>
		   	</a>		   	
			<a href="" ng-click="workspaceSrvc.toggleFunding()" class="toggle-text"><@orcid.msg 'workspace.Funding'/></a>
			<#if fundingImportWizards?size != 0>
				<a class="label btn-primary" ng-click="showTemplateInModal('import-funding-modal')"><@orcid.msg 'workspace.import_funding'/></a>
			</#if>			
			<a href="" class="label btn-primary" ng-click="addFundingModal()"><@orcid.msg 'manual_funding_form_contents.add_grant_manually'/></a>
		</div>
		<div ng-show="workspaceSrvc.displayFunding" class="workspace-accordion-content">
			<#include "includes/funding/body_funding_inc.ftl" />
		</div>
	</div>		
</div>