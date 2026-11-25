<#escape x as x?html>
<!DOCTYPE html>
<html><head><title>${subject}</title></head><body>
<p>${body}</p>
<#if actionable>
<p><a href="${acceptUrl}">${acceptLabel}</a> | <a href="${declineUrl}">${declineLabel}</a></p>
</#if>
</body></html>
</#escape>