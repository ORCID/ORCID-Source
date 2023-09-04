<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.reset_password_not_found.we_could_not_find" /><@emailMacros.space />${submittedEmail}
<@emailMacros.msg "email.reset_password_not_found.registered_using_another_email" /><@emailMacros.space /><a href="https://orcid.org/reset-password"><@emailMacros.msg "email.reset_password_not_found.try_another_email" /></a><@emailMacros.space /><@emailMacros.msg "email.reset_password_not_found.many_users_have" />
<@emailMacros.msg "email.reset_password_not_found.id_associated_with_email_no_longer_access" /><@emailMacros.space /><a href="https://support.orcid.org/?ticket_form_id=360003481994"><@emailMacros.msg "email.reset_password_not_found.contact_us" /></a>
<@emailMacros.msg "email.reset_password_not_found.not_registered_using_other_email" /><@emailMacros.space /><a href="https://orcid.org/register"><@emailMacros.msg "email.reset_password_not_found.register_for_an_orcid" /></a>


<#include "email_footer.ftl"/>
