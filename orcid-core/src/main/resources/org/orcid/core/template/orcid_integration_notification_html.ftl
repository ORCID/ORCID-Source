<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
    <head>
        <title>${subject}</title>
    </head>
    <body>
		<p>
           <@emailMacros.msg "notification.mvp.youCanBenefit" />
		</p>         
		<p>
		   <@emailMacros.msg "notification.mvp.basedOnYourVerifiedEmail" /><@emailMacros.space />${memberName}.<@emailMacros.space /><@emailMacros.msg "notification.mvp.connectingWithThisIntegration" />@emailMacros.space />${memberName}<@emailMacros.space /><@emailMacros.msg "notification.mvp.toAutomaticallyAdd" />
		</p>
		<p>
			<button
			  style="color: #fff; background: #085c77; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer;"
			  onclick="window.open('${memberWebpageUrl}', '_blank');"
			>
			  <@emailMacros.msg "notification.mvp.connectWith" /><@emailMacros.space />${memberName}
			</button
		</p>            
    </body>
 </html>
 </#escape>
