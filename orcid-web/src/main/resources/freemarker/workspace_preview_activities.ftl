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
    <#if !(affiliationsEmpty)??>
		<!-- Education -->
		<div class="workspace-header-public" ng-controller="PublicEduAffiliation" ng-hide="!affiliationsSrvc.educations.length" ng-cloack>
	    	<h3>${springMacroRequestContext.getMessage("org.orcid.jaxb.model.message.AffiliationType.education")}</h3>
    		<#include "includes/affiliate/edu_body_inc.ftl" />
    	</div>
    	<!-- Employment -->
    	<div class="workspace-header-public" ng-controller="PublicEmpAffiliation" ng-hide="!affiliationsSrvc.employments.length" ng-cloack>
	    	<h3>${springMacroRequestContext.getMessage("org.orcid.jaxb.model.message.AffiliationType.employment")}</h3>		    
	    	<#include "includes/affiliate/emp_body_inc.ftl" />			
		</div>    
    </#if>
    
	<#if !(fundingEmpty)??>
		<!-- Funding -->
		<div class="workspace-header-public" ng-controller="PublicFundingCtrl">      
			<h3>${springMacroRequestContext.getMessage("workspace.Funding")}</h3>		
   			<#include "includes/funding/body_funding_inc.ftl" />
		</div>
	</#if>
		
	<#if !(worksEmpty)??>
		<!-- Works -->
		<div class="workspace-header-public">
    		<h3>${springMacroRequestContext.getMessage("workspace.Works")}</h3>
    		<#include "includes/work/public_works_body_list.ftl" />
    	</div>
    </#if>
    
    
    