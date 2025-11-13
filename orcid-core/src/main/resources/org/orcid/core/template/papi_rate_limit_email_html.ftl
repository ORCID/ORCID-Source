<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>	
		<title>${subject}</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 10px; width: 700px; margin: auto;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		    <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;" ><@emailMacros.msg "email.common.dear" /> ${emailName},</span>
		    <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;"><@emailMacros.msg "papi.rate.limit.important.msg" /><@emailMacros.space /><a href="https://info.orcid.org/ufaqs/what-are-the-api-limits/" target="_blank"><@emailMacros.msg "papi.rate.limit.daily.usage.limit" /></a><@emailMacros.space /><@emailMacros.msg "papi.rate.limit.your.integration" /></p>
		    <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;"><@emailMacros.msg "papi.rate.limit.client.name" /><@emailMacros.space /> ${clientName}</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;"><@emailMacros.msg "papi.rate.limit.client.id" /><@emailMacros.space />${clientId}</p>
			<br/>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;"><@emailMacros.msg "papi.rate.limit.please.remember" /><@emailMacros.space /><a href="https://info.orcid.org/public-client-terms-of-service/" target="_blank"><@emailMacros.msg "papi.rate.limit.public.api.terms" /></a>.<@emailMacros.space /><@emailMacros.msg "papi.rate.limit.by.non.commercial" /></p>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;"><@emailMacros.msg "papi.rate.limit.based.on.your" /><@emailMacros.space /><a href="https://info.orcid.org/what-is-orcid/services/member-api/" target="_blank"><@emailMacros.msg "papi.rate.limit.member.api" /></a>.<@emailMacros.space /><@emailMacros.msg "papi.rate.limit.not.only" /></p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;"><@emailMacros.msg "papi.rate.limit.to.minimize" />
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>
 </#escape>
