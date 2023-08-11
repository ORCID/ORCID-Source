<#escape x as x?html>
<p>
<pre style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
<@emailMacros.msg "email.common.warm_regards" />
<a href="https://support.orcid.org/hc/en-us" target="_blank" style="color: #2E7F9F;">https://support.orcid.org</a>
</pre>
</p>
<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
<@emailMacros.msg "email.common.received_email_as_service" />
</p>
<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
<small>
<a href="${baseUri}/account" target="_blank" style="color: #2E7F9F;"><@emailMacros.msg "email.common.email.preferences" /></a>
| <a href="${baseUri}/privacy-policy" target="_blank" style="color: #2E7F9F;"><@emailMacros.msg "email.common.privacy_policy" /></a>
| <@emailMacros.msg "email.common.address1" /><@emailMacros.space />|<@emailMacros.space /><@emailMacros.msg "email.common.address2" />
| <a href="${baseUri}" target="_blank" style="color: #2E7F9F;">ORCID.org</a>
</small>
</p>
</#escape>
