<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.email_removed.the_primary" /> 

<@emailMacros.msg "email.email_removed.while.1" /><@emailMacros.space />${baseUri}/${orcid}<@emailMacros.msg "email.email_removed.while.2" />

<#include "email_footer.ftl"/>
