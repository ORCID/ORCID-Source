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
<@emailMacros.msg "email.common.dear" />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "admin.profile_deprecation.email.deprecated_account.message.1" />${deprecatedAccount}<@emailMacros.msg "admin.profile_deprecation.email.deprecated_account.message.2" />
<@emailMacros.msg "admin.profile_deprecation.email.deprecated_account.message.3" />${primaryAccount}<@emailMacros.msg "admin.profile_deprecation.email.deprecated_account.message.4" />

<@emailMacros.msg "email.common.you_have_received_this_email" />
