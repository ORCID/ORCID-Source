<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /> ${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg 'email.institutional_connection.1' /> ${clientName}.<br />
<@emailMacros.msg 'email.institutional_connection.2' /> <a href="${authorization_url}"><@emailMacros.msg 'email.institutional_connection.here' /></a> <@emailMacros.msg 'email.institutional_connection.3' />

<@emailMacros.msg "email.common.warm_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email_opt_out.1" />${baseUri}/home?lang=${locale}<@emailMacros.msg "email.common.you_have_received_this_email_opt_out.2" />
<#include "email_footer.ftl"/>
