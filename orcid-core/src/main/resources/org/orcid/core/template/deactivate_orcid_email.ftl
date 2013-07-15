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
<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /> ${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.deactivate.you_have_requested.1" /> 
${baseUri}/${orcid} <@emailMacros.msg "email.deactivate.you_have_requested.2" />
${baseUri}${deactivateUrlEndpoint}


<@emailMacros.msg "email.deactivate.once_an_account" />

<@emailMacros.msg "email.deactivate.if_you_did" />

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}

<@emailMacros.msg "email.common.you_have_received_this_email" />