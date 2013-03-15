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
<#if devSandboxUrl != ''>
    <div class="alert alert-error readme">
        <h3>${springMacroRequestContext.getMessage("sandbox_warning.labelWelcometotheORCID")}</h3>
        <p class="emphasis small-top-margin">${springMacroRequestContext.getMessage("sandbox_warning.labelSee")} <a href="http://dev.orcid.org/release-notes#CurrentSandbox" target="_blank">${springMacroRequestContext.getMessage("sandbox_warning.labelReleaseNotes")}</a>. ${springMacroRequestContext.getMessage("sandbox_warning.labelcommentandfeedback")} <a href="mailto:devsupport@orcid.org">${springMacroRequestContext.getMessage("sandbox_warning.labelorg")}</a></p>
        <p>${springMacroRequestContext.getMessage("sandbox_warning.sandboxproviders")}</p>
        <p>${springMacroRequestContext.getMessage("sandbox_warning.sandboxdoesnotrepresent")}</p>
        <a href="${devSandboxUrl}">${springMacroRequestContext.getMessage("sandbox_warning.readmefirst")}</a>
    </div>
</#if>