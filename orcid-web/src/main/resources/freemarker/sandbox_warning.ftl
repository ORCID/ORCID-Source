<#if devSandboxUrl = ''>
    <div class="alert alert-error readme">
        <h3>${springMacroRequestContext.getMessage("sandbox_warning.labelWelcometotheORCID")}</h3>
        <p class="emphasis small-top-margin">${springMacroRequestContext.getMessage("sandbox_warning.labelSee")} <a href="http://dev.orcid.org/release-notes#CurrentSandbox" target="sandbox_warning.labelReleaseNotes">${springMacroRequestContext.getMessage("sandbox_warning.labelReleaseNotes")}</a>. ${springMacroRequestContext.getMessage("sandbox_warning.labelcommentandfeedback")} <a href="mailto:devsupport@orcid.org">${springMacroRequestContext.getMessage("sandbox_warning.labelorg")}</a></p>
        <p>${springMacroRequestContext.getMessage("sandbox_warning.sandboxproviders")}</p>
        <p>${springMacroRequestContext.getMessage("sandbox_warning.sandboxdoesnotrepresent")}</p>
        <a href="${devSandboxUrl}">${springMacroRequestContext.getMessage("sandbox_warning.readmefirst")}</a>
    </div>
</#if>