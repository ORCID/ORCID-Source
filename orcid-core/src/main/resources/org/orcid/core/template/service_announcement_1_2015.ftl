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
<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" />${emailName},

<@emailMacros.msg "email.service_announcement.body_intro" /><@emailMacros.msg "email.service_announcement.privacy_link" /><@emailMacros.msg "email.service_announcement.dot_bottom" />


<@emailMacros.msg "email.service_announcement.body_inbox_title" />

<@emailMacros.msg "email.service_announcement.body_inbox1" />
<@emailMacros.msg "email.service_announcement.inbox_link" />
<@emailMacros.msg "email.service_announcement.body_inbox2" />
<@emailMacros.msg "email.service_announcement.inbox_about_link" />
<@emailMacros.msg "email.service_announcement.body_inbox3" />


<@emailMacros.msg "email.service_announcement.body_permission_title" />

<@emailMacros.msg "email.service_announcement.body_permission1" />
<@emailMacros.msg "email.service_announcement.crossref_link" />
<@emailMacros.msg "email.service_announcement.body_permission2" />
<@emailMacros.msg "email.service_announcement.datacite_link" />
<@emailMacros.msg "email.service_announcement.body_permission3" />
<@emailMacros.msg "email.service_announcement.updates_link" />
<@emailMacros.msg "email.service_announcement.body_permission4" />
<@emailMacros.msg "email.service_announcement.inbox_about_link" />
<@emailMacros.msg "email.service_announcement.body_permission5" />


<@emailMacros.msg "email.service_announcement.body_privacy_policy_title" />

<@emailMacros.msg "email.service_announcement.body_privacy_policy" />

<#if verificationUrl??>

<@emailMacros.msg "email.service_announcement.verify_account" />

${verificationUrl}

</#if>

<@emailMacros.msg "email.service_announcement.body_updates1" />


<@emailMacros.msg "email.service_announcement.steamlined_link" /><@emailMacros.msg "email.service_announcement.body_updates2" />

<@emailMacros.msg "email.service_announcement.body_updates3" /><@emailMacros.msg "email.service_announcement.wizard_link" /><@emailMacros.msg "email.service_announcement.body_updates4" />

<@emailMacros.msg "email.service_announcement.qr_link" />

<@emailMacros.msg "email.service_announcement.language_link" /><@emailMacros.msg "email.service_announcement.body_updates5" />

<@emailMacros.msg "email.service_announcement.body_updates6" />

<@emailMacros.msg "email.service_announcement.body_updates7" /><@emailMacros.msg "email.service_announcement.here_link" /><@emailMacros.msg "email.service_announcement.body_updates8" />


<@emailMacros.msg "email.service_announcement.regards" />

<@emailMacros.msg "email.service_announcement.orcid_team" />

<@emailMacros.msg "email.service_announcement.support_id" />


<@emailMacros.msg "email.service_announcement.footer_text1_title" />

<@emailMacros.msg "email.service_announcement.footer_text1" />[<@emailMacros.msg "email.service_announcement.footer_frequency_link" />]
<@emailMacros.msg "email.service_announcement.footer_text2" />[<@emailMacros.msg "email.service_announcement.footer_account_link" />]

<@emailMacros.msg "email.service_announcement.footer_orcid_id" />${baseUri}${orcid}