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

<@emailMacros.msg "email.api_record_creation.creaded_an_account.1" />${creatorName}<@emailMacros.msg "email.api_record_creation.creaded_an_account.2" />

<@emailMacros.msg "email.api_record_creation.what" />

<@emailMacros.msg "email.api_record_creation.within.1" />${creatorName}<@emailMacros.msg "email.api_record_creation.within.2" />

${verificationUrl}

<@emailMacros.msg "email.api_record_creation.what_happens" />

<@emailMacros.msg "email.api_record_creation.if_you_take_no.1" />${creatorName}<@emailMacros.msg "email.api_record_creation.if_you_take_no.2" />

<@emailMacros.msg "email.api_record_creation.what_is_orcid" />

<@emailMacros.msg "email.api_record_creation.launched.1" />${baseUri}<@emailMacros.msg "email.api_record_creation.launched.2" />

<@emailMacros.msg "email.api_record_creation.read_privacy.1" />${baseUri}<@emailMacros.msg "email.api_record_creation.read_privacy.1" />

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}

<@emailMacros.msg "email.api_record_creation.you_have_received.1" />${baseUri}<@emailMacros.msg "email.api_record_creation.you_have_received.2" />
