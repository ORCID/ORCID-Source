<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.reactivation.thank_you" />

<@emailMacros.msg "email.reactivation.to_reactivate" />

${reactivationUrl}

<@emailMacros.msg "email.reactivation.after" />

<#include "email_footer.ftl"/>
