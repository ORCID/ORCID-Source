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
				<a href="${baseUri}/reset-password"><@emailMacros.msg "email.forgotten_id.try_anohter_email_link"/></a><@emailMacros.space /><@emailMacros.msg "email.forgotten_id.many_users_have"/><@emailMacros.msg "email.common.period"/>
				</li>
				<li>
					<@emailMacros.msg "email.forgotten_id.id_associated_with_email" /><@emailMacros.space /><a href="https://support.orcid.org/?ticket_form_id=360003481994"> <@emailMacros.msg "email.forgotten_id._please_contact_us"/></a>
				</li>

				<li><@emailMacros.msg "email.forgotten_id.certain_not_regiser" />
					<a href="${baseUri}/register"><@emailMacros.msg "email.forgotten_id.certain_not_regiser_url" /></a><@emailMacros.msg "email.common.period"/>
				</li>
			</ul>
			<#include "email_footer_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
