<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html >
<html>
	<head>
	<title>${subject}</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.msg "email.reset_password_not_found.we_could_not_find" /><@emailMacros.space />${submittedEmail}
		    </p>
			<ul>
				<li>
					<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
						<@emailMacros.msg "email.reset_password_not_found.registered_using_another_email" /><@emailMacros.space /><a href="${baseUri}/reset-password"><@emailMacros.msg "email.reset_password_not_found.try_another_email" /></a><@emailMacros.space /><@emailMacros.msg "email.reset_password_not_found.many_users_have" />
					</p>
				</li>
				<li>
					<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
						<@emailMacros.msg "email.reset_password_not_found.id_associated_with_email_no_longer_access" /><@emailMacros.space /><a href="https://support.orcid.org/?ticket_form_id=360003481994"><@emailMacros.msg "email.reset_password_not_found.contact_us" /></a>
					</p>
				</li>
				<li>
					<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
						<@emailMacros.msg "email.reset_password_not_found.not_registered_using_other_email" /><@emailMacros.space /><a href="${baseUri}/register"><@emailMacros.msg "email.reset_password_not_found.register_for_an_orcid" /></a>
					</p>
				</li>
			</ul>

			<#include "email_footer_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
