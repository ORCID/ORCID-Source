<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.email_removed.the_primary" /> 

<@emailMacros.msg "email.email_removed.while.1" /><@emailMacros.space />${baseUri}/home?lang=${locale}<@emailMacros.msg "email.email_removed.while.2" />

<@emailMacros.msg "email.email_removed.please_click" />
${baseUri}/account?lang=${locale}

<@emailMacros.msg "email.email_removed.important" />

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
