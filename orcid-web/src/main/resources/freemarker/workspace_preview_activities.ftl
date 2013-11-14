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
<#if RequestParameters['affiliations']??>
    <h3 class="workspace-header-public">${springMacroRequestContext.getMessage("org.orcid.jaxb.model.message.AffiliationType.education")}</h3>
    <div ng-controller="PublicEduAffiliation">
    	<#include "includes/affiliate/edu_body_inc.ftl" />
    </div>
    <h3 class="workspace-header-public">${springMacroRequestContext.getMessage("org.orcid.jaxb.model.message.AffiliationType.employment")}</h3>
    <div ng-controller="PublicEmpAffiliation">
    	<#include "includes/affiliate/emp_body_inc.ftl" />
	</div>
</#if>
    <h3 class="workspace-header-public">${springMacroRequestContext.getMessage("workspace_preview_activities.Publications")}</h3>
    <#include "includes/work/public_works_body_list.ftl" />
    <h3 class="workspace-header-public">${springMacroRequestContext.getMessage("workspace_preview_activities.Grants")}</h3>
	<#include "workspace_grants_body_list.ftl" />
    <h3 class="workspace-header-public">${springMacroRequestContext.getMessage("workspace_preview_activities.Patents")}</h3>
	<#include "workspace_patents_body_list.ftl" />
