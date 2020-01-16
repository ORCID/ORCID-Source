<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.forgotten_id.the_orcid_associated_email"/> ${submittedEmail} <@emailMacros.msg "email.reset_password.is_colon" />
${baseUri}/${orcid}

<@emailMacros.msg "email.common.if_you_have_any1" /><@emailMacros.space /><@emailMacros.msg "email.common.need_help.description.2.href" /><@emailMacros.msg "email.common.if_you_have_any2" />

<@emailMacros.msg "email.common.warm_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />	

<#include "email_footer.ftl"/>
