<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.forgotten_id.could_not_find" /> ${submittedEmail}

<@emailMacros.msg "email.forgotten_id.try_anohter_email" /><@emailMacros.msg "email.forgotten_id.try_anohter_email_link"/><@emailMacros.msg "email.service_announcement.dot_bottom"/>

${baseUri}/reset-password

<@emailMacros.msg "email.forgotten_id.contact_us" /> <@emailMacros.msg "email.common.if_you_have_any.contact_us"/><@emailMacros.msg "email.service_announcement.dot_bottom"/>
https://support.orcid.org/hc/en-us/requests/new

<@emailMacros.msg "email.forgotten_id.certain_not_regiser" /> <@emailMacros.msg "email.forgotten_id.certain_not_regiser_url" /><@emailMacros.msg "email.service_announcement.dot_bottom"/>
${baseUri}/register

<@emailMacros.msg "email.common.warm_regards" />
${baseUri}/home?lang=${locale}
<#include "email_footer_html.ftl"/>
