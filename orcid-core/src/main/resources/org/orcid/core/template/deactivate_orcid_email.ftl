<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.deactivate.you_have_asked" />
(${baseUri}/${orcid}?lang=${locale})

<@emailMacros.msg "email.deactivate.please_click_the_link" />

${baseUri}${deactivateUrlEndpoint}?lang=${locale}

<@emailMacros.msg "email.deactivate.please_note" />

<@emailMacros.msg "email.deactivate.if_you_no_longer_want" />

<@emailMacros.msg "email.deactivate.after_your_account_is_deactivated" />

<@emailMacros.msg "email.deactivate.reactivate_anytime" /> <a href="https://support.orcid.org/hc/articles/360006973813" rel="noopener noreferrer" target="_blank">https://support.orcid.org/hc/articles/360006973813</a>

<#include "email_footer.ftl"/>
