<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
    <head></head>
    <body>
        <div style="padding: 20px; padding-top: 0px; font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; width: 800px;">
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr />
            <span style="font-weight: bold;">
                <@emailMacros.msg "email.common.hi" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />
            </span>
            <p>
                <@emailMacros.msg "email.digest.youhave" /><@emailMacros.space />${totalMessageCount}<@emailMacros.space /><@emailMacros.msg "email.digest.new" /><@emailMacros.space /><#if ((totalMessageCount?number) == 1)><@emailMacros.msg "email.digest.notification" /><#else><@emailMacros.msg "email.digest.notifications" /></#if><@emailMacros.space /><@emailMacros.msg "email.digest.inyourinbox" /><@emailMacros.space /><@emailMacros.msg "email.digest.pleasevisit_1" /><@emailMacros.space /><a href="${baseUri}/inbox?lang=${locale}" style="color: #338caf; text-decoration: none;"><@emailMacros.msg "email.digest.orcidinbox" /></a><@emailMacros.space /><@emailMacros.msg "email.digest.pleasevisit_4" />
            </p>
            <#if digestEmail.notificationsBySourceId['ORCID']??> 
            <p>
                <#-- Use assign to prevent strange whitespace formatting in output -->
                <#assign orcidwouldlikeyoutoknow><@emailMacros.msg "email.digest.orcidwouldlikeyoutoknow" /></#assign>
                ${orcidwouldlikeyoutoknow}
                <ul>
                <#list digestEmail.notificationsBySourceId['ORCID'].allNotifications as notification>
                    <li>${notification.subject}</li>
                </#list>
                </ul>
            </p>
            </#if>
            <#list digestEmail.notificationsBySourceId?keys?sort as sourceId>
	        <#if sourceId != 'ORCID'>
		    <#list digestEmail.notificationsBySourceId[sourceId].notificationsByType?keys?sort as notificationType>
			<#list digestEmail.notificationsBySourceId[sourceId].notificationsByType[notificationType] as notification>
			<#if notificationType == 'PERMISSION'>
            <p>
                <div><img src="https://orcid.org/sites/all/themes/orcid/img/request.png">&nbsp;${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}: <#if notification.notificationSubject??>${notification.notificationSubject} <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if> <#else><@emailMacros.msg "email.digest.requesttoadd" /> <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if> </#if></div>
                <#assign itemsByType=notification.items.itemsByType>
                <#list itemsByType?keys?sort as itemType>
                <table width="100%" style="font-family: arial, helvetica, sans-serif; padding-left: 20px; padding-top: 15px;">
					<thead>
						<tr>
							<td width="100%" style="padding: 5px 0 5px 10px; color: #FFF;background: #939598; font-weight: bold; font-size: 18px;">					                
               					<img src="https://orcid.org/sites/all/themes/orcid/img/chevron-down.png">&nbsp;<@emailMacros.msg "email.common.recordsection." + itemType /> (${itemsByType[itemType]?size})
               				</td>
               			</tr>
               		</thead>
               		<tbody>
               			<#list itemsByType[itemType] as item>
               			<tr>
               				<td width="100%" style="padding-top: 15px; padding-bottom: 10px; font-weight: bold; color: #494A4C">
               					${item.itemName?trim} <#if item.externalIdentifier??>(<span style="color: #338caf; font-weight: normal;">${item.externalIdentifier.type?lower_case}:</span> <#if item.externalIdentifier.value?starts_with("http")><a href="${item.externalIdentifier.value}" style="color: #338caf; font-weight: normal; text-decoration: none;">${item.externalIdentifier.value}</a><#else><span style="color: #338caf; font-weight: normal;">${item.externalIdentifier.value}</span></#if>)</#if>			
               				</td>
               			</tr>
                		</#list>
		                <tr>
							<td width="100%">
								<ul style="padding: 0; margin: 0; float: right;">
									<li style="display: inline;">						                
			                			<a href="${baseUri}/inbox#${notification.putCode}" style="padding: 10px 15px; float: left; color: #338caf; text-decoration: none;">more info...</a>
			                		</li>
			                		<li style="display: inline;">
			               				<a href="${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action" style="padding: 10px 15px; background: #338caf; color: #FFF; text-decoration: none; float: right;"><@emailMacros.msg "email.digest.addnow" /></a> 
		                			</li>
		                		</ul>
		                	</td>
		                </tr>
                    </tbody>					                      
	        	</table>
	        	</#list> 
            </p>
			<#elseif notificationType == 'AMENDED'>
            <p>
                <#assign amendedSection><@emailMacros.msg "email.common.recordsection." + notification.amendedSection /></#assign>
                <div>
                    <img src="https://orcid.org/sites/all/themes/orcid/img/update.png">&nbsp;<@emailMacros.msg "email.digest.hasupdated_1" />
                    ${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_2" /><@emailMacros.space />${amendedSection?lower_case}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_3" /><#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if>
                </div>
                <#if notification.items??>
                <ul>
                <#list notification.items.items as item>
                    <li>${item.itemName} <#if item.externalIdentifier??>(${item.externalIdentifier.type?lower_case}: ${item.externalIdentifier.value})</#if></li>
                </#list>
                </ul>
                </#if>
            </p>
            <#elseif notificationType == 'INSTITUTIONAL_CONNECTION'>
            <p> 
            	<div><img src="https://orcid.org/sites/all/themes/orcid/img/request.png">&nbsp;<@emailMacros.msg 'email.institutional_connection.1' /><@emailMacros.space />${(notification.idpName)!}<@emailMacros.space /><@emailMacros.msg 'email.institutional_connection.2' /><a href="${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action"><@emailMacros.msg "email.institutional_connection.here" /></a><@emailMacros.msg 'email.institutional_connection.3' /><@emailMacros.space />${(notification.source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg 'email.institutional_connection.4' /><@emailMacros.space /><#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if></div>
            </p>
            <#else>
            <p>
            	<div><img src="https://orcid.org/sites/all/themes/orcid/img/request.png">&nbsp;${(notification.subject)}</div>
            </p>
            </#if>
			</#list>
			</#list>
			</#if>
            </#list>
            <p style="text-align: center;">
                <a href="${baseUri}/inbox?lang=${locale}" style="text-decoration: none; color: #338caf;">                    
                     <!-- Use assign to prevent strange whitespace formatting in output -->
                     <#assign viewyourinbox><@emailMacros.msg "email.digest.viewyourinbox" /></#assign>
                     ${viewyourinbox}                    
                </a>
            </p>
            <p>                
                <@emailMacros.msg "email.digest.youhavereceived_1" /><@emailMacros.space /><@emailMacros.msg "email.digest.youhavereceived_2" /><@emailMacros.space /><a href="<@emailMacros.msg "email.digest.learnmorelink" />" style="color: #338caf; text-decoration: none;"><@emailMacros.msg "email.digest.learnmore" /></a>
            </p>
            <p>
                <@emailMacros.msg "email.digest.youmayadjust_1" /><@emailMacros.space /><a href="${baseUri}/account?lang=${locale}" style="color: #338caf; text-decoration: none;"><@emailMacros.msg "email.digest.accountsettings" /></a>.
            </p>
            <hr />
            <p>
               <#include "email_footer_html.ftl"/>
            </p>
        </div>      
    </body>
</html>
</#escape>
