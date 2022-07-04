<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
	<title>${subject}</title>
	</head>
	<body>
		<div style="
					max-width: 736px;
					padding: 32px;
					margin: auto;
					font-family: Arial, helvetica, sans-serif;
					color: #494A4C;
					font-size: 15px;
					line-height: 1.5;
				">
			<img style="width: 120px" src="http://i5.cmail20.com/ti/t/02/55D/4F6/033235/////csimport/orcid-logo_0.jpg" alt="ORCID.org"/>
			<hr style="color: #e0e0e0;background-color: #e0e0e0;border-style: solid;border-width: 2px;" />
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.reactivation.thank_you" />
            </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.reactivation.to_reactivate" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
			    <a style="text-decoration: underline;color: #085c77;" href="${reactivationUrl}">${reactivationUrl}</a>
 		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">		  
				<@emailMacros.msg "email.reactivation.after" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		  		<@emailMacros.msg "email.common.if_you_have_any1" /> <a style="text-decoration: underline;color: #085c77;" href="<@emailMacros.msg "email.common.need_help.description.2.href" />"><@emailMacros.msg "email.common.need_help.description.2.href" /></a><@emailMacros.msg "email.common.if_you_have_any2" />
		    </p>		    
		  	<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;"
				><@emailMacros.msg "email.common.warm_regards" /><a style="text-decoration: underline;color: #085c77;" href='<@emailMacros.msg "email.common.need_help.description.2.href" />' target="orcid.contact_us"><@emailMacros.msg "email.common.need_help.description.2.href" /></a>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				<a style="text-decoration: underline;color: #085c77;" href="${baseUri}/home?lang=${locale}">${baseUri}/</a>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				<@emailMacros.msg "email.common.you_have_received_this_email" />
			</p>
			<br>
			<footer>
				<p>
					<a style="text-decoration: underline;color: #085c77;display: inline-block;" href="${baseUri}/account" target="_blank">
						<@emailMacros.msg "notification.footer.preferences" />
					</a>
					<a style="text-decoration: underline;color: #085c77;padding-left: 16px;display: inline-block;" href="https://orcid.org/footer/privacy-policy" target="_blank">
						<@emailMacros.msg "notification.footer.privacy" />
					</a>
					<a style="text-decoration: underline;color: #085c77;padding-left: 16px;" href="https://orcid.org" target="_blank">
						<@emailMacros.msg "notification.footer.orcid" />
					</a>
				</p>
				<p>
					<b>
						<@emailMacros.msg "notification.footer.orcidInc" />
					</b> <@emailMacros.msg "notification.footer.address" />
				</p>
			</footer>
		</div>
	 </body>
 </html>
 </#escape>
