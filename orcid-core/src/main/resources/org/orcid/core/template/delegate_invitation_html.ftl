<#escape x as x?html>
<!DOCTYPE html>
<html><head><title>${subject}</title></head><body>
<#if actionable>
<p><strong>${senderName}</strong> (<a href="${senderUrl}">${senderUrl}</a>) ${recipientIntro}</p>
<p>${recipientInfo} <a href="https://support.orcid.org/hc/articles/360006973613">https://support.orcid.org/hc/articles/360006973613</a></p>
<p>${recipientDeadline} <em>${recipientExpired}</em></p>
<h3>${acceptHeading}</h3>
<p>${acceptDescription}</p>
<p><a href="${acceptUrl}" style="display:inline-block;background:#285c75;color:#fff;padding:12px 16px;border-radius:4px;text-decoration:none;">${acceptLabel}</a></p>
<hr style="border:0;border-top:1px solid #eee;margin:30px 0;">
<h3>${declineHeading}</h3>
<p>${declineDescription}</p>
<p><a href="${declineUrl}" style="display:inline-block;background:#c43c39;color:#fff;padding:12px 16px;border-radius:4px;text-decoration:none;">${declineLabel}</a></p>
<#elseif bodyHtml??>
<p><#noescape>${bodyHtml}</#noescape></p>
<#else>
<p>${body}</p>
</#if>
</body></html>
</#escape>
