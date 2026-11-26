<#import "delegate_invitation_macros.ftl" as m>
<#escape x as x?html>
<!DOCTYPE html>
<html><head><title>${subject}</title></head><body>
<#if key?starts_with("created.sender.")>
<p><@m.msg "created.sender.prefix"/> <strong>${display}</strong><#if !invitedByEmail> (<a href="${recordUrl}">${recordUrl}</a>)</#if> <@m.msg "created.sender.suffix"/></p>
<p><@m.msg "created.sender.deadline"/> <em><@m.msg "recipient.expired"/></em></p>
<#elseif actionable>
<p><strong>${display}</strong> (<a href="${recordUrl}">${recordUrl}</a>) <@m.msg "recipient.intro"/></p>
<p><@m.msg "recipient.info"/> <a href="https://support.orcid.org/hc/articles/360006973613">https://support.orcid.org/hc/articles/360006973613</a></p>
<p><@m.msg "recipient.deadline"/> <em><@m.msg "recipient.expired"/></em></p>
<h3><@m.msg "recipient.acceptHeading"/></h3>
<p><@m.msg "recipient.acceptDescription.prefix"/> ${display} <@m.msg "recipient.acceptDescription.suffix"/></p>
<p><a href="${acceptUrl}" style="display:inline-block;background:#285c75;color:#fff;padding:12px 16px;border-radius:4px;text-decoration:none;"><@m.msg "accept"/></a></p>
<hr style="border:0;border-top:1px solid #eee;margin:30px 0;">
<h3><@m.msg "recipient.declineHeading"/></h3>
<p><@m.msg "recipient.declineDescription.prefix"/> ${display} <@m.msg "recipient.declineDescription.suffix"/></p>
<p><a href="${declineUrl}" style="display:inline-block;background:#c43c39;color:#fff;padding:12px 16px;border-radius:4px;text-decoration:none;"><@m.msg "decline"/></a></p>
<#elseif key == "accepted.sender">
<p><strong>${display}</strong> (<a href="${recordUrl}">${recordUrl}</a>) <@m.msg "accepted.sender.body"/></p>
<p><@m.msg "accepted.sender.info"/></p>
<#elseif key == "accepted.recipient">
<p><@m.msg "accepted.recipient.prefix"/> <strong>${display}</strong> (<a href="${recordUrl}">${recordUrl}</a>).</p>
<p><@m.msg "accepted.recipient.info.prefix"/> ${display}<@m.msg "accepted.recipient.info.suffix"/></p>
<p><@m.msg "recipient.info"/> <a href="https://support.orcid.org/hc/articles/360006973613">https://support.orcid.org/hc/articles/360006973613</a></p>
<#elseif key?starts_with("declined.sender.")>
<p><strong>${display}</strong><#if !invitedByEmail> (<a href="${recordUrl}">${recordUrl}</a>)</#if> <@m.msg "declined.sender.body"/></p>
<#else>
<p><@m.msg "declined.recipient.prefix"/> <strong>${display}</strong> (<a href="${recordUrl}">${recordUrl}</a>). <@m.msg "declined.recipient.suffix"/></p>
</#if>
</body></html>
</#escape>
