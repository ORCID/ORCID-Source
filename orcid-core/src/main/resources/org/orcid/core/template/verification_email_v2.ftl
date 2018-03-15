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
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<#if isPrimary?? && isPrimary>
    <@emailMacros.msg "email.verify.primary_reminder_v2" /><@emailMacros.space />
</#if>
<#if isReminder?? && isReminder>    
    <@emailMacros.msg "email.verify.click_link" />
<#else>
    <@emailMacros.msg "email.verify.thank_you" />    
</#if>
    
${verificationUrl}?lang=${locale}

<@emailMacros.msg "email.verify.1" /><@emailMacros.space />${orcid}<@emailMacros.msg "email.verify.2" /><@emailMacros.space />${baseUri}/${orcid}?lang=${locale}<@emailMacros.space /><@emailMacros.msg "email.verify.primary_email_1" /><@emailMacros.space />${primaryEmail}<@emailMacros.msg "email.verify.primary_email_2" />.
 
<@emailMacros.msg "email.verify.if_you_did_not" />

<@emailMacros.msg "email.common.did_you_know" /><@emailMacros.space />${baseUri}/blog

<@emailMacros.msg "email.common.need_help.description.1" /><@emailMacros.space /><@emailMacros.msg "email.common.need_help.description.1.text" /><@emailMacros.space /><@emailMacros.msg "email.common.need_help.description.2" /><@emailMacros.space /><@emailMacros.msg "email.common.need_help.description.2.text" />

<@emailMacros.msg "email.common.kind_regards.simple" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
