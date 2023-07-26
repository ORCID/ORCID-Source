<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.forgotten_id.the_orcid_associated_email"/> ${submittedEmail} <@emailMacros.msg "email.reset_password.is_colon" />
${baseUri}/${orcid}

<#include "email_footer.ftl"/>
