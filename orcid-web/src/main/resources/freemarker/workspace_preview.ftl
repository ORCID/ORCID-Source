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
<div class="row">
    <div class="span12">
        <div class="pull-left">
            <#if isPreview??>
                <h3>${springMacroRequestContext.getMessage("workspace_preview.ORCIDRecordfor")} ${(profile.orcidBio.personalDetails.creditName.content)!}</h3>
                <div class="alert alert-block">
                    <h4 class="alert-heading">${springMacroRequestContext.getMessage("workspace_preview.Preview")}</h4>
                    <p>${springMacroRequestContext.getMessage("workspace_preview.whatvisitors")}  <a href="<@spring.url '/account'/>">${springMacroRequestContext.getMessage("workspace_preview.ManageORCIDRecord")}</a> ${springMacroRequestContext.getMessage("workspace_preview.page")}</p>
                </div>
            </#if>
        </div>
        <div class="pull-right">
        </div>
    </div>
</div>
<div class="row">
    <div class="span12 top-margin">
       <#include "workspace_bio.ftl"/>
    </div>
</div>
