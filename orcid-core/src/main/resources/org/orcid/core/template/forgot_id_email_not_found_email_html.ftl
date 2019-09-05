<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
	<title>${subject}</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${submittedEmail}<@emailMacros.msg "email.common.dear.comma" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.msg "email.forgotten_id.could_not_find" /><br />
		    	${submittedEmail}
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.forgotten_id.you_can" /><@emailMacros.space /><a href='${baseUri}/signin' target="orcid.sign_in"><@emailMacros.msg "email.forgotten_id.try_another_email" /></a><@emailMacros.msg "email.forgotten_id.or_register" />
		    </p>
            <table cellpadding="0" cellspacing="0" style="font-family: Helvetica, Arial, sans-serif;  border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; margin: 0 auto; margin-top:20px">
                <tbody>
                    <tr>
                        <td style="border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-radius: 4px; margin: 0;">
                            <a href="${baseUri}/signin?lang=${locale}" style="font-size: 20px; font-family: Helvetica, Arial, sans-serif; text-decoration: none; border-radius: 4.8px; line-height: 25px; display: inline-block; font-weight: normal; white-space: nowrap; background-color: #31789B; color: #ffffff; padding: 8px 16px; border: 1px solid #31789B;"> 
                                <@emailMacros.msg "email.forgotten_id.register" />
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.forgotten_id.no_access" /><@emailMacros.space /><a href='${baseUri}/help/contact-us' target="orcid.sign_in"><@emailMacros.msg "email.common.if_you_have_any.contact_us" /></a><@emailMacros.msg "email.common.if_you_have_any2" />
            </p>
 		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">		  
				<@emailMacros.msg "email.common.warm_regards" /><br />
				<a href="${baseUri}/home?lang=${locale}">${baseUri}/<a/>
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
