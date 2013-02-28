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
    <div class="offset3 span9">
        <p>There has been a problem with the server. If this problem persists, please contact <a href="${(aboutUri)}/help/contact-us">support</a>.</p>
    </div>
</div>
<div class="hide">
    ${(exception.message)!}
</div>
</@public>