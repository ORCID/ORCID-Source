<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
		<title>${subject}</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="http://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
			    <@emailMacros.msg "email.common.dear" /><@emailMacros.space />${name}<@emailMacros.msg "email.common.dear.comma" />
		    </span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.auto_deprecate.1" />
			</p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.auto_deprecate.2" /><@emailMacros.space />${clientName}<@emailMacros.space /><@emailMacros.msg "email.auto_deprecate.3" /><@emailMacros.space />${deprecatedAccountCreationDate}<@emailMacros.space /><@emailMacros.msg "email.auto_deprecate.4" />
			</p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;"> 
				<@emailMacros.msg "email.auto_deprecate.5" />${deprecatedId}<@emailMacros.msg "email.auto_deprecate.6" />${primaryId}<@emailMacros.msg "email.auto_deprecate.7" />
			</p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;"> 
				<@emailMacros.msg "email.auto_deprecate.8" />             
		    </p>
			<hr />			
		 </div>
	 </body>
 </html>
 </#escape>
