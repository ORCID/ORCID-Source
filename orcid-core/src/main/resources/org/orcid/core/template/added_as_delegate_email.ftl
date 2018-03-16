<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /> ${emailNameForDelegate}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.added_as_delegate.you_have.1" /><@emailMacros.space />${grantingOrcidName}<@emailMacros.space /><@emailMacros.msg "email.added_as_delegate.you_have.2" />${baseUri}/${grantingOrcidValue}?lang=${locale}<@emailMacros.msg "email.added_as_delegate.you_have.3" /><@emailMacros.space />${grantingOrcidName}<@emailMacros.msg "email.added_as_delegate.you_have.4" />

<@emailMacros.msg "email.added_as_delegate.for_a_tutorial" />

<@emailMacros.msg "email.added_as_delegate.if_you_have.1" />${grantingOrcidName}<@emailMacros.space /><@emailMacros.msg "email.added_as_delegate.if_you_have.2" /><@emailMacros.space />${grantingOrcidEmail}<@emailMacros.msg "email.added_as_delegate.if_you_have.3" />

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
