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
    <h3 class="workspace-header-public">${springMacroRequestContext.getMessage("workspace_preview_activities.Publications")}</h3>
    <#include "workspace_works_body_list.ftl" />
    <h3 class="workspace-header-public">${springMacroRequestContext.getMessage("workspace_preview_activities.Grants")}</h3>
<#include "workspace_grants_body_list.ftl" />
    <h3 class="workspace-header-public">${springMacroRequestContext.getMessage("workspace_preview_activities.Patents")}</h3>
<#include "workspace_patents_body_list.ftl" />
