<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.reset_password.orcid_id" /> ${submittedEmail} <@emailMacros.msg "email.reset_password.is" /> ${baseUri}/${orcid}

<@emailMacros.msg "email.reset_password.to_reset" />

${passwordResetUrl}

<@emailMacros.msg "email.reset_password.note" />

<@emailMacros.msg "email.reset_password.if_you_did_not" />

<#include "email_footer.ftl"/>
