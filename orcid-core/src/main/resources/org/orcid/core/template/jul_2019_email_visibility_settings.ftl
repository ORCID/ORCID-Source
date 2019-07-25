<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.2019.vis_settings.we_are_contacting_you_about" /><@emailMacros.space />${baseUri}/${orcidId}

<@emailMacros.msg "email.2019.vis_settings.as_an_orcid_user" /><@emailMacros.space /><@emailMacros.msg "email.2019.vis_settings.visibility_settings" /> (https://support.orcid.org/hc/en-us/articles/360006897614-Visibility-settings)<@emailMacros.space /><@emailMacros.msg "email.2019.vis_settings.for_more_information" />

<@emailMacros.msg "email.2019.vis_settings.when_we_first_launched" /><@emailMacros.space /><@emailMacros.msg "email.2019.vis_settings.we_later_changed" /><@emailMacros.space />(https://orcid.org/blog/2013/01/18/orcid-tech-update-default-privacy-settings)<@emailMacros.space /><@emailMacros.msg "email.2019.vis_settings.except_your_email" /><@emailMacros.space /><@emailMacros.msg "email.2019.vis_settings.account_settings" /><@emailMacros.space />(${baseUri}/account)<@emailMacros.msg "email.common.period" />

<@emailMacros.msg "email.2019.vis_settings.we_are_contacting_you_to_thank" />


<@emailMacros.msg "email.2019.vis_settings.changing_who_you_share" />


<@emailMacros.msg "email.2019.vis_settings.for_more_information_please" /><@emailMacros.space /><@emailMacros.msg "email.2019.vis_settings.visibility_settings" />(https://support.orcid.org/hc/en-us/articles/360006897614-Visibility-settings)<@emailMacros.space /><@emailMacros.msg "email.2019.vis_settings.if_you_have" /> 

<@emailMacros.msg "email.2019.vis_settings.thanks_for_your" />


<@emailMacros.msg "email.2019.vis_settings.cheers" /><@emailMacros.msg "email.common.dear.comma" />


<@emailMacros.msg "email.march_2019.director_name" /><@emailMacros.msg "email.common.dear.comma" /><@emailMacros.space /><@emailMacros.msg "email.march_2019.director_title" />


<@emailMacros.msg "email.march_2019.note.part_1" /><@emailMacros.msg "email.march_2019.note.part_2" /> (${unsubscribeLink}) <@emailMacros.msg "email.march_2019.note.part_3" />

----
<#include "email_footer.ftl"/>  