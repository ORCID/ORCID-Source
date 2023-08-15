<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.deactivate.gdpr_you_have_asked.1" />
${baseUri}/${orcid}?lang=${locale}

<@emailMacros.msg "email.deactivate.you_have_requested.2" />

${baseUri}${deactivateUrlEndpoint}?lang=${locale}

<@emailMacros.msg "email.deactivate.gdpr_if_you_do_not" />

<@emailMacros.msg "email.deactivate.please_note.1" /> <@emailMacros.msg "email.deactivate.please_note.2" /> (https://en.wikipedia.org/wiki/Cryptographic_hash_function) <@emailMacros.msg "email.deactivate.please_note.3" />

<@emailMacros.msg "email.deactivate.reactivate_anytime" /> <a href="https://support.orcid.org/hc/articles/360006973813" rel="noopener noreferrer" target="_blank">https://support.orcid.org/hc/articles/360006973813</a>

<#include "email_footer.ftl"/>
