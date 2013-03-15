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
<@protected>
<div class="alert alert-error">
    ${springMacroRequestContext.getMessage("wrong_user.Wronguser")}
</div>
<div><a href="<@spring.url '/signout'/>">${springMacroRequestContext.getMessage("public-layout.sign_out")}</a> ${springMacroRequestContext.getMessage("wrong_user.andtryagain")}</div>
</@protected>
