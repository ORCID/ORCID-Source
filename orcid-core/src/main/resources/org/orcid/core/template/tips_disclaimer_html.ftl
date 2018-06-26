<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<p><@emailMacros.msg "tips.disclaimer.1" /></p>
<p><@emailMacros.msg "tips.disclaimer.2" /> <a href="<@emailMacros.msg "tips.disclaimer.2.url.link" />"><@emailMacros.msg "tips.disclaimer.2.url.text" /></a>.</p>
<p><@emailMacros.msg "tips.disclaimer.3" /> <a href="<@emailMacros.msg "tips.disclaimer.3.url.link" />"><@emailMacros.msg "tips.disclaimer.3.url.text" /></a></p>
