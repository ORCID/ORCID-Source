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
		    <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">Dear ${emailName},</span>
		    <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">This is an important message to let you know that you have exceeded your <a href="https://info.orcid.org/ufaqs/what-are-the-api-limits/" target="_blank">daily Public API usage limit</a> with your integration:</p>
		    <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">Client Name: ${clientName}</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">Client ID: ${clientId}</p>
			<br/>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">Please remember that the ORCID Public API is free for non-commercial use by individuals as stated in the <a href="https://info.orcid.org/public-client-terms-of-service/" target="_blank">Public APIs Terms of Service</a>. By "non-commercial" we mean that you may not charge any re-use fees for the Public API, and you may not make use of the Public API in connection with any revenue-generating product or service.</p>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">Based on your API usage, we highly recommend you consider becoming an ORCID member for access to our <a href="https://info.orcid.org/what-is-orcid/services/member-api/" target="_blank">Member API</a>. Not only will it allow you to access a higher Rate Limits and an unrestricted Usage Quota, but you will be able to access Trusted Party data in ORCID records and contribute data to ORCID records from your institutional systems.</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">To minimize any disruption to your ORCID integration in the future, we would recommend that you reach out to our Engagement Team by replying to this email to discuss our ORCID membership options.
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>
 </#escape>
