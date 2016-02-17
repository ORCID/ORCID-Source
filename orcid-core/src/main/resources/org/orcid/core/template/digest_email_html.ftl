<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#import "email_macros.ftl" as emailMacros />
<!DOCTYPE html>
<html>
    <head></head>
    <body>
        <div style="padding: 20px; padding-top: 0px; font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; width: 800px;">
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr />
            <span style="font-weight: bold;">
                <@emailMacros.msg "email.common.hi" />${emailName}<@emailMacros.msg "email.common.dear.comma" />
            </span>
            <p>
                <@emailMacros.msg "email.digest.youhave" />${totalMessageCount}<@emailMacros.msg "email.digest.new" /><#if ((totalMessageCount?number) == 1)><@emailMacros.msg "email.digest.notification" /><#else><@emailMacros.msg "email.digest.notifications" /></#if><@emailMacros.msg "email.digest.inyourinbox" /><@emailMacros.msg "email.digest.pleasevisit_1" /><a href="${baseUri}/inbox?lang=${locale}" style="color: #338caf; text-decoration: none;"><@emailMacros.msg "email.digest.orcidinbox" /></a><@emailMacros.msg "email.digest.pleasevisit_4" />
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
                <div><img src="${baseUri}/static/img/request.png">&nbsp;${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}: <#if notification.notificationSubject??>${notification.notificationSubject}<#else><@emailMacros.msg "email.digest.requesttoadd" /></#if></div>
                <#assign itemsByType=notification.items.itemsByType>
                <#list itemsByType?keys?sort as itemType>
                <table width="100%" style="font-family: arial, helvetica, sans-serif; padding-left: 20px; padding-top: 15px;">
					<thead>
						<tr>
							<td width="100%" style="padding: 5px 0 5px 10px; color: #FFF;background: #939598; font-weight: bold; font-size: 18px;">					                
               					<img src="${baseUri}/static/img/chevron-down.png">&nbsp;<@emailMacros.msg "email.common.recordsection." + itemType /> (${itemsByType[itemType]?size})
               				</td>
               			</tr>
               		</thead>
               		<tbody>
               			<#list itemsByType[itemType] as item>
               			<tr>
               				<td width="100%" style="padding-top: 15px; padding-bottom: 10px; font-weight: bold; color: #494A4C">
               					${item.itemName?trim} <#if item.externalIdentifier??>(<span style="color: #338caf; font-weight: normal;">${item.externalIdentifier.externalIdentifierType?lower_case}:</span> <#if item.externalIdentifier.externalIdentifierId?starts_with("http")><a href="${item.externalIdentifier.externalIdentifierId}" style="color: #338caf; font-weight: normal; text-decoration: none;">${item.externalIdentifier.externalIdentifierId}</a><#else><span style="color: #338caf; font-weight: normal;">${item.externalIdentifier.externalIdentifierId}</span></#if>)</#if>			
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
                <div><img src="${baseUri}/static/img/update.png">&nbsp;<@emailMacros.msg "email.digest.hasupdated_1" />${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}<@emailMacros.msg "email.digest.hasupdated_2" />${amendedSection?lower_case}<@emailMacros.msg "email.digest.hasupdated_3" /></div>
                <#if notification.items??>
                <ul>
                <#list notification.items.items as item>
                    <li>${item.itemName} <#if item.externalIdentifier??>(${item.externalIdentifier.externalIdentifierType?lower_case}: ${item.externalIdentifier.externalIdentifierId})</#if></li>
                </#list>
                </ul>
                </#if>
            </p>
            <#else>
            ${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}
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
                <#assign frequency>
                <#switch orcidProfile.orcidInternal.preferences.sendEmailFrequencyDays>
                     <#case "0.0"><@emailMacros.msg "email.digest.frequency.immediate" /><#break>
                     <#case "7.0"><@emailMacros.msg "email.digest.frequency.weekly" /><#break>
                     <#case "91.3105"><@emailMacros.msg "email.digest.frequency.quarterly" /><#break>
                </#switch>
                </#assign>
                <@emailMacros.msg "email.digest.youhavereceived_1" />${frequency}<@emailMacros.msg "email.digest.youhavereceived_2" /><a href="<@emailMacros.msg "email.digest.learnmorelink" />" style="color: #338caf; text-decoration: none;"><@emailMacros.msg "email.digest.learnmore" /></a>
            </p>
            <p>
                <@emailMacros.msg "email.digest.youmayadjust_1" /><a href="${baseUri}/account?lang=${locale}" style="color: #338caf; text-decoration: none;"><@emailMacros.msg "email.digest.accountsettings" /></a>.
            </p>
            <hr />
            <p>
               <#include "email_footer_html.ftl"/>
            </p>
        </div>      
    </body>
</html>