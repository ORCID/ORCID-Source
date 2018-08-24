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
		    <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">We are writing to let you know about a bug that we identified recently, which may have affected the display of one or more of the organization names in your ORCID record (<a href="${baseUriHttp}/${orcidId}" target="orcid.blank">${baseUriHttp}/${orcidId}</a>).  As a result, your record may currently include incorrect information about the following organization(s):</p>
		    <ul style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C">
		    <#list orgDescriptions as orgDescription>
                <li>${orgDescription}</li>
            </#list>
            </ul>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">We have fixed the bug and have tried to fix the affiliation data that was affected in your record, though some correction may still be needed. We have changed the visibility setting on affected information to private - visible only to you - to allow you to review changes. We encourage you to sign into your record at <a href="https://orcid.org/my-orcid">https://orcid.org/my-orcid</a> to review and, if necessary, correct the affected affiliation information. You can then decide whether to <a href="<@emailMacros.knowledgeBaseUri />/knowledgebase/articles/124518-orcid-visibility-settings">keep this information visible only to you, make it publicly available, or share it only with those you trust</a>.</p>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">We apologize for any inconvenience this may cause you. If you have any questions or concerns, or need information about how to update your record and/or the visibility settings, our <a href="mailto:support@orcid.org">global support team</a> are available to help you, at support@orcid.org.</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">Regards,

Laure Haak
Executive Director, ORCID
laure@orcid.org
			    
<a href="${baseUri}/home?lang=${locale}">${baseUri}</a>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
                <@emailMacros.msg "email.common.you_have_received_this_email" />
            </p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>
 </#escape>
