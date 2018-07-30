<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.claim_reminder.this_is_a_reminder.1" /><@emailMacros.space />${creatorName}<@emailMacros.space /><@emailMacros.msg "email.claim_reminder.this_is_a_reminder.2" />${daysUntilActivation}<@emailMacros.space /><@emailMacros.msg "email.claim_reminder.this_is_a_reminder.3" /><@emailMacros.space />${creatorName}<@emailMacros.space /><@emailMacros.msg "email.claim_reminder.this_is_a_reminder.4" />

<@emailMacros.msg "email.claim_reminder.what_do_you" />

<@emailMacros.msg "email.claim_reminder.within_the_next.1" /><@emailMacros.space />${daysUntilActivation}<@emailMacros.space /><@emailMacros.msg "email.claim_reminder.within_the_next.2" />${creatorName}<@emailMacros.space /><@emailMacros.msg "email.claim_reminder.within_the_next.3" />

${verificationUrl}?lang=${locale}

<@emailMacros.msg "email.api_record_creation.what_is_orcid" />

<@emailMacros.msg "email.api_record_creation.launched.1" />${baseUri}/home?lang=${locale}<@emailMacros.msg "email.api_record_creation.launched.2" />

<@emailMacros.msg "email.api_record_creation.read_privacy.1" />${baseUri}/privacy-policy/?lang=${locale}<@emailMacros.msg "email.api_record_creation.read_privacy.2" />
<@emailMacros.msg "email.common.if_you_have_any1" /> <@emailMacros.msg "email.common.need_help.description.2.href" /><@emailMacros.msg "email.common.if_you_have_any2" />

<@emailMacros.msg "email.common.warm_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.api_record_creation.you_have_received.1" />${baseUri}/home?lang=${locale}S<@emailMacros.msg "email.api_record_creation.you_have_received.2" />
<#include "email_footer.ftl"/>
