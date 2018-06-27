<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;"><small><@emailMacros.msg "tips.disclaimer.1" /></small></p>
<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;"><small><@emailMacros.msg "tips.disclaimer.2" /> <a href="<@emailMacros.msg "tips.disclaimer.2.url.link" />"><@emailMacros.msg "tips.disclaimer.2.url.text" /></a>.</small></p>
<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;"><small><@emailMacros.msg "tips.disclaimer.3" /> <a href="<@emailMacros.msg "tips.disclaimer.3.url.link" />"><@emailMacros.msg "tips.disclaimer.3.url.text" /></a>.</small></p>
</#escape>