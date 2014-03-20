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

<@emailMacros.msg "email.claim_reminder.this_is_a_reminder.1" />${creatorName}<@emailMacros.msg "email.claim_reminder.this_is_a_reminder.2" />${daysUntilActivation}<@emailMacros.msg "email.claim_reminder.this_is_a_reminder.3" />${creatorName}<@emailMacros.msg "email.claim_reminder.this_is_a_reminder.4" />

<@emailMacros.msg "email.claim_reminder.what_do_you" />

<@emailMacros.msg "email.claim_reminder.within_the_next.1" />${daysUntilActivation}<@emailMacros.msg "email.claim_reminder.within_the_next.2" />${creatorName}<@emailMacros.msg "email.claim_reminder.within_the_next.3" />

${verificationUrl}?locale_v3=${locale}

<@emailMacros.msg "email.api_record_creation.what_is_orcid" />

<@emailMacros.msg "email.api_record_creation.launched.1" />${baseUri}/?locale_v3=${locale}<@emailMacros.msg "email.api_record_creation.launched.2" />

<@emailMacros.msg "email.api_record_creation.read_privacy.1" />${baseUri}/privacy-policy/?locale_v3=${locale}<@emailMacros.msg "email.api_record_creation.read_privacy.2" />

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}/?locale_v3=${locale}

<@emailMacros.msg "email.api_record_creation.you_have_received.1" />${baseUri}/?locale_v3=${locale}S<@emailMacros.msg "email.api_record_creation.you_have_received.2" />
<#include "email_footer.ftl"/>