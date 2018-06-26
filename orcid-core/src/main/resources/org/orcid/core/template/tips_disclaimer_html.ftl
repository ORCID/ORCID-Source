<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<tr>
    <td><@emailMacros.msg "tips.disclaimer.1" /></td>
    <td><@emailMacros.msg "tips.disclaimer.2" /> <a href="<@emailMacros.msg "tips.disclaimer.2.url.link" />"><@emailMacros.msg "tips.disclaimer.2.url.text" /></a>.</td>
    <td><@emailMacros.msg "tips.disclaimer.3" /> <a href="<@emailMacros.msg "tips.disclaimer.3.url.link" />"><@emailMacros.msg "tips.disclaimer.3.url.text" /></a></td>
</tr>


