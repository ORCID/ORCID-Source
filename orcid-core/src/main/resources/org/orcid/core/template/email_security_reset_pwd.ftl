<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.welcome.your_id.id" /> ${orcid}
<@emailMacros.msg "email.welcome.your_id.link" />:<@emailMacros.space /><a href="${baseUri}/${orcid}" target="orcid.blank">${baseUri}/${orcid}</a>

<@emailMacros.msg "email.reset_password.security.your_orcid_account_password" />

<@emailMacros.msg "email.security.if_you_did_not_make_these_changes" /> <a href="https://orcid.org/reset-password" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.update_your_password" /></a>.

<#include "email_footer_security.ftl"/>

