<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.march_2019.paragraph_1" />

<@emailMacros.msg "email.march_2019.paragraph_2" />

<@emailMacros.msg "email.march_2019.paragraph_3.part_1" /><@emailMacros.msg "email.march_2019.paragraph_3.part_2" /><@emailMacros.space />(https://orcid.org/signin)<@emailMacros.space /><@emailMacros.msg "email.march_2019.paragraph_3.part_3" /><@emailMacros.msg "email.march_2019.paragraph_3.part_4" /><@emailMacros.space />(https://support.orcid.org/hc/en-us/articles/360006971213-Account-email-addresses)<@emailMacros.space /><@emailMacros.msg "email.march_2019.paragraph_3.part_5" />

<@emailMacros.msg "email.march_2019.paragraph_4.part_1" /><@emailMacros.msg "email.march_2019.paragraph_4.part_2" /><@emailMacros.space />(https://support.orcid.org/hc/en-us/articles/360006894494-Visibility-preferences)<@emailMacros.space /><@emailMacros.msg "email.march_2019.paragraph_4.part_3" />

<@emailMacros.msg "email.march_2019.paragraph_5.part_1" /><@emailMacros.msg "email.march_2019.paragraph_5.part_2" /><@emailMacros.space />(https://orcid.org/blog/2019/03/20/don%E2%80%99t-lose-access-your-orcid-record)

<@emailMacros.msg "email.march_2019.thanks" />

<@emailMacros.msg "email.march_2019.director_name" />

<@emailMacros.msg "email.march_2019.director_title" />

<@emailMacros.msg "email.march_2019.note.part_1" /><@emailMacros.msg "email.march_2019.note.part_2" /><@emailMacros.space />(${unsubscribeLink})<@emailMacros.space /><@emailMacros.msg "email.march_2019.note.part_3" />

----
<#include "email_footer.ftl"/>  