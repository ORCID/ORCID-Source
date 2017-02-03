<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#escape x as x?html>
<small>
<a href="${baseUri}/account" style="color: #338caf;"><@emailMacros.msg "email.common.email.preferences" /></a> 
| <a href="${baseUri}/privacy-policy" style="color: #338caf;"><@emailMacros.msg "email.common.privacy_policy" /></a> 
| <@emailMacros.msg "email.common.address1" /> | <@emailMacros.msg "email.common.address2" /> 
| <a href="${baseUri}" style="color: #338caf;">ORCID.org</a>
</small>
</#escape>
