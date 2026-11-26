<#escape x as x?html>
<!DOCTYPE html>
<html><head><title>${subject}</title></head><body>
<#if bodyHtml??>
<p><#noescape>${bodyHtml}</#noescape></p>
<#else>
<p>${body}</p>
</#if>
<#if actionable>
<p><a href="${acceptUrl}">${acceptLabel}</a> | <a href="${declineUrl}">${declineLabel}</a></p>
</#if>
</body></html>
</#escape>
