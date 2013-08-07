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

<@emailMacros.msg "email.email_removed.the_primary" /> 

<@emailMacros.msg "email.email_removed.while.1" />${baseUri}?lang=${locale}<@emailMacros.msg "email.email_removed.while.2" />

<@emailMacros.msg "email.email_removed.please_click" />
${baseUri}/account?lang=${locale}

<@emailMacros.msg "email.email_removed.important" />

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />