<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.welcome.your_id.id" /> ${orcid}

<@emailMacros.msg "email.deactivate.your_orcid_account_has_been_deactivated" />

<@emailMacros.msg "email.deactivate.your_orcid_id_will_continue" /> <a href="https://info.orcid.org/privacy-policy/#9_How_long_we_keep_your_data" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.deactivate.privacy_policy" /></a> <@emailMacros.msg "email.deactivate.for_information" />

<#include "email_footer_security.ftl"/>