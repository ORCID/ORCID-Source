<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
	<title><@emailMacros.msg "email.service_announcement.2017.verify_email.subject" /></title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">
		    <@emailMacros.msg "email.service_announcement.2017.verify_email.dear" />&nbsp;${emailName},
		    </span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.msg "email.service_announcement.2017.body_1" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.msg "email.service_announcement.2017.body_2" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.msg "email.service_announcement.2017.body_3" />
		    </p>
		    <ol style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
		    	<li><@emailMacros.msg "email.service_announcement.2017.body_3_option_1" />&nbsp;<a href="${verificationUrl}">${verificationUrl}</a></li>
		    	<li><@emailMacros.msg "email.service_announcement.2017.body_3_option_2" /></li>
		   		<li><@emailMacros.msg "email.service_announcement.2017.body_3_option_3" /></li>
		    </ol>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.msg "email.service_announcement.2017.body_4_html" />
		    </p>
		    <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				<@emailMacros.msg "email.service_announcement.2017.regards" />
				<br><@emailMacros.msg "email.service_announcement.2017.orcid_team" />
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				<@emailMacros.msg "email.service_announcement.2017.footer_1" />
				<br>
				<br><@emailMacros.msg "email.service_announcement.2017.footer_2" />
				<br>
				<br><@emailMacros.msg "email.service_announcement.2017.footer_3" />&nbsp;<a href="${emailFrequencyUrl}" target="orcid.blank"><@emailMacros.msg "email.service_announcement.2017.footer_3_unsubscribe_link_text" /></a>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				<#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>
 </#escape>
