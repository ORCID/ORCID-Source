<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.amend.thought_you.1" /><@emailMacros.space />${amenderName}<@emailMacros.space /><@emailMacros.msg "email.amend.thought_you.2" />

<@emailMacros.msg "email.amend.please_click" />

    ${baseUri}/my-orcid?lang=${locale}

<@emailMacros.msg "email.common.warm_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email_opt_out.1" /><@emailMacros.space />${baseUri}/home?lang=${locale}<@emailMacros.msg "email.common.you_have_received_this_email_opt_out.2" />
<#include "email_footer.ftl"/>
