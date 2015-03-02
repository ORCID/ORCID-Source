<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#import "email_macros.ftl" as emailMacros />
<!DOCTYPE html>
<html>
	<head>
	<title>${subject}</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		    <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
				<@emailMacros.msg "email.common.dear" /> ${emailName}<@emailMacros.msg "email.common.dear.comma" />
			</span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				Thank you for registering for an ORCID identifier. Please click the link below to confirm your registration and verify your email address.<br /><br />
				<a href="${verificationUrl}?lang=${locale}" target="_blank">${verificationUrl}?lang=${locale}</a>
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				If you can't click the link, copy and paste it into your browser's address bar.<br /><br />
				<strong>Your ORCID iD is ${orcidId}, and the link to your public record is <a href="${baseUri}/${orcidId}" target="_blank">${baseUri}/${orcidId}</a></strong>
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<strong>Next steps:</strong>
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<strong>1. Add more information to your ORCID Record</strong>
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				Access your ORCID record at ${baseUri}/my-orcid and add additional information to your record.
				Funders, publishers, universities and others use the information contained in an ORCID Record to help decrease the record keeping they ask from you. Increase the amount of information you can share in this way by adding other names you are known by, professional information, funding items you have received and works you have created to your Record.
				<br /><br />
				For tips on adding information to your ORCID record see:
				<br /> 
				<a href="http://support.orcid.org/knowledgebase/articles/460004" target="_blank">http://support.orcid.org/knowledgebase/articles/460004</a>
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<strong>2. Continue to use your ORCID iD</strong>
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				Many systems ask for your ORCID iD to create a link between you and your research outputs. Continue to use your ORCID iD whenever it is asked for to get credit for your work and decrease future record keeping.
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<strong>Need Help?</strong>
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				If you have any questions or need help, contact the ORCID support team visit <a href="http://support.orcid.org" target="_blank">http://support.orcid.org</a>.
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666; white-space: pre;">
<@emailMacros.msg "email.common.kind_regards" />
<a href="${baseUri}/home?lang=${locale}">${baseUri}/</a>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
				<@emailMacros.msg "email.common.you_have_received_this_email" />
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>