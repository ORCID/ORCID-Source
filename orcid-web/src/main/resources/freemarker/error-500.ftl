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
<@public classes=['home'] nav="signin">

<div class="row">
    <div class="col-md-offset-3 col-md-9 col-sm-12">
        <p>${springMacroRequestContext.getMessage("error-500.hasbeenproblemwithserver")} <a href="${(aboutUri)}/help/contact-us">${springMacroRequestContext.getMessage("error-500.support")}</a>.</p>
    </div>
</div>
<div class="hide">
    ${(exception.message)!}
</div>
</@public>