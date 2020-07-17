<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
	<title>${subject}</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px; font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <p>
		    	<@emailMacros.msg "email.forgotten_id.could_not_find" />
		    	${submittedEmail}
		    </p>
			<ul>
				<li><@emailMacros.msg "email.forgotten_id.try_anohter_email" />
				<a href="${baseUri}/reset-password"> <@emailMacros.msg "email.forgotten_id.try_anohter_email_link"/></a><@emailMacros.msg "email.service_announcement.dot_bottom"/>
				</li>
				<li><@emailMacros.msg "email.forgotten_id.contact_us" />
				<a href="https://support.orcid.org/hc/en-us/requests/new"> <@emailMacros.msg "email.common.if_you_have_any.contact_us"/></a><@emailMacros.msg "email.service_announcement.dot_bottom"/>
				</li>

				<li><@emailMacros.msg "email.forgotten_id.certain_not_regiser" />
					<a href="${baseUri}/register"><@emailMacros.msg "email.forgotten_id.certain_not_regiser_url" /></a><@emailMacros.msg "email.service_announcement.dot_bottom"/>
				</li>
			</ul>
		  	<p style="white-space: pre;">
<@emailMacros.msg "email.common.warm_regards" />
<a href='${baseUri}/home?lang=${locale}" />' target="orcid.contact_us"><@emailMacros.msg "email.common.need_help.description.2.href" /></a>
			</p>
			<p>
				<#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>
 </#escape>
