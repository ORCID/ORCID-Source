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
<@base>
<div class="container">
<div class="row">
    <div class="offset3 span6">
        <div class="alert">${springMacroRequestContext.getMessage("session_expired.labelsessionexpired")} <a href="<@spring.url '/signin'/>">${springMacroRequestContext.getMessage("header.signin")}</a> ${springMacroRequestContext.getMessage("session_expired.labeltryagain")}</p>
    </div>
</div>
</div>
</@base>