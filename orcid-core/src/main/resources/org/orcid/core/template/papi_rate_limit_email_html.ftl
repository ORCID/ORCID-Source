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
		    <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">This is an important message to let you know that you have exceeded our daily Public API usage limit with your integration:</p>
		    <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">Client Name: ${clientName}</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">Client ID: ${clientId}</p>
			<br/>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">Please remember that the ORCID Public API is free for non-commercial use by individuals as stated in the Public APIs Terms of Service (https://info.orcid.org/public-client-terms-of-service/). By “non-commercial” we mean that you may not charge any re-use fees for the Public API, and you may not make use of the Public API in connection with any revenue-generating product or service.</p>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">If you need access to an ORCID API for commercial use, need a higher usage quota, organizational administration of your API credentials, or the ability to write data to or access Trusted Party data in ORCID records, our Member API (https://info.orcid.org/documentation/features/member-api/) is available to ORCID member organizations.</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">To minimize any disruption to your ORCID integration in the future, we would recommend that you reach out to our Engagement Team by replying to this email to discuss our ORCID membership options.
     		<br/>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">
            Warm Regards, 
			ORCID Support Team 
			https://support.orcid.org
			</p>
            
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>
 </#escape>
