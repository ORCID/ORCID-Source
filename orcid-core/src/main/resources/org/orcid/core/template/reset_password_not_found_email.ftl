<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.hello" /><@emailMacros.msg "email.common.dear.comma" />


<@emailMacros.msg "email.reset_password_not_found.thank_you" />


<@emailMacros.msg "email.reset_password_not_found.email_provided" /><@emailMacros.msg "email.common.dear.comma" /> ${submittedEmail}<@emailMacros.msg "email.common.dear.comma" /> 
<@emailMacros.msg "email.reset_password_not_found.not_registered" />


<@emailMacros.msg "email.reset_password_not_found.another_email_reset" /> <@emailMacros.msg "email.common.reset_password.href" />

<@emailMacros.msg "email.reset_password_not_found.unable_to_reset" />


<@emailMacros.msg "email.reset_password_not_found.unsure_whether" /> <@emailMacros.msg "email.common.register.href" />

<@emailMacros.msg "email.reset_password_not_found.we_recommend" />


<@emailMacros.msg "email.common.warm_regards" />
<@emailMacros.msg "email.common.need_help.description.2.href" />


${baseUri}/home?lang=${locale}


<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
