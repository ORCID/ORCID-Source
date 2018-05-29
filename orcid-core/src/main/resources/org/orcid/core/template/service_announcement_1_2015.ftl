<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.service_announcement.dear1" />${emailName}<@emailMacros.msg "email.service_announcement.dear2" />,

<@emailMacros.msg "email.service_announcement.body_intro" /><@emailMacros.msg "email.service_announcement.privacy_link" /> (https://orcid.org/privacy-policy)<@emailMacros.msg "email.service_announcement.dot_bottom" />

<@emailMacros.msg "email.service_announcement.body_inbox_title" />

<@emailMacros.msg "email.service_announcement.body_inbox1" /><@emailMacros.msg "email.service_announcement.inbox_link" /><@emailMacros.msg "email.service_announcement.body_inbox2" /><@emailMacros.msg "email.service_announcement.inbox_about_link" /><@emailMacros.msg "email.service_announcement.body_inbox3" /> (<@emailMacros.knowledgeBaseUri />/knowledgebase/articles/665437)<@emailMacros.msg "email.service_announcement.body_inbox4" />

<@emailMacros.msg "email.service_announcement.body_permission_title" />

<@emailMacros.msg "email.service_announcement.body_permission1" /><@emailMacros.msg "email.service_announcement.crossref_link" /> (http://www.crossref.org/)<@emailMacros.msg "email.service_announcement.body_permission2" /><@emailMacros.msg "email.service_announcement.datacite_link" /> (https://www.datacite.org/)<@emailMacros.msg "email.service_announcement.body_permission3" /><@emailMacros.msg "email.service_announcement.updates_link" /> (http://orcid.org/blog/2015/01/13/new-webinar-metadata-round-trip)<@emailMacros.msg "email.service_announcement.body_permission4" /><@emailMacros.msg "email.service_announcement.inbox_about_link" /><@emailMacros.msg "email.service_announcement.body_permission5" /> (<@emailMacros.knowledgeBaseUri />/knowledgebase/articles/665437)<@emailMacros.msg "email.service_announcement.body_permission6" />


<@emailMacros.msg "email.service_announcement.body_privacy_policy_title" />

<@emailMacros.msg "email.service_announcement.body_privacy_policy" />


<@emailMacros.msg "email.service_announcement.body_updates1" />


* <@emailMacros.msg "email.service_announcement.steamlined_link" /><@emailMacros.msg "email.service_announcement.body_updates2" /> (http://orcid.org/blog/2014/12/11/new-feature-friday-new-orcid-record-interface)

* <@emailMacros.msg "email.service_announcement.body_updates3" /><@emailMacros.msg "email.service_announcement.wizard_link" /><@emailMacros.msg "email.service_announcement.body_updates4" /> (http://orcid.org/blog/2015/06/17/humanists-rejoice-mla-international-bibliography-now-connects-orcid)

* <@emailMacros.msg "email.service_announcement.qr_link" /> (http://orcid.org/blog/2014/11/14/new-functionality-friday-orcid-id-qr-codes)
   
* <@emailMacros.msg "email.service_announcement.language_link" /><@emailMacros.msg "email.service_announcement.body_updates5" /> (http://orcid.org/blog/2014/11/28/new-functionality-friday-orcid-site-%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%B9-and-portugu%C3%AAs)

* <@emailMacros.msg "email.service_announcement.body_updates6" />

* <@emailMacros.msg "email.service_announcement.body_updates7" /><@emailMacros.msg "email.service_announcement.here_link" /><@emailMacros.msg "email.service_announcement.body_updates8" /> (<@emailMacros.knowledgeBaseUri />/knowledgebase/articles/460004)

<@emailMacros.msg "email.service_announcement.regards" />

<@emailMacros.msg "email.service_announcement.orcid_team" />

<@emailMacros.msg "email.service_announcement.support_id" />


<@emailMacros.msg "email.service_announcement.footer_text1_title" />

<@emailMacros.msg "email.service_announcement.footer_text1" />

<@emailMacros.msg "email.service_announcement.footer_text2" />

<@emailMacros.msg "email.service_announcement.footer_text_unsubscribe" /><@emailMacros.msg "email.service_announcement.footer_frequency_link" /> (${emailFrequencyUrl})

<@emailMacros.msg "email.service_announcement.footer_text3" /><@emailMacros.msg "email.service_announcement.footer_account_link" /> (${baseUri}/account) <@emailMacros.msg "email.service_announcement.footer_text4" />

<@emailMacros.msg "email.service_announcement.footer_orcid_id" />${baseUri}/${orcid}

----
<#include "email_footer.ftl"/>
