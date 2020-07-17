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
		    <hr />
		    <p>
		    	<@emailMacros.msg "email.forgotten_id.the_orcid_associated_email" />
				${submittedEmail}
				<@emailMacros.msg "email.reset_password.is_colon" />
		    <br>
		    	<a href="${baseUri}/${orcid}?lang=${locale}">${baseUri}/${orcid}</a>
		    </p>
		    <p>
                <@emailMacros.msg "email.common.if_you_have_any" /><@emailMacros.space /><a href='https://support.orcid.org/hc/en-us/requests/new' target="orcid.contact_us"><@emailMacros.msg "email.common.if_you_have_any.contact_us" /></a><@emailMacros.msg "email.common.if_you_have_any2" />
		    </p>	  
		  	<p style="white-space: pre;">
<@emailMacros.msg "email.common.warm_regards" />
<a href='${baseUri}/home?lang=${locale}' target="orcid.contact_us"><@emailMacros.msg "email.common.need_help.description.2.href" /></a>
			</p>   
			<p>	
				<@emailMacros.msg "email.common.you_have_received_this_email" />	
			</p>
			<p>
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>
 </#escape>
