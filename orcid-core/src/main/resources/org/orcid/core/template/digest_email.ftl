<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.hi" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.digest.youhave" /><@emailMacros.space />${totalMessageCount}<@emailMacros.space /><@emailMacros.msg "email.digest.new" /><@emailMacros.space /><#if ((totalMessageCount?number) == 1)><@emailMacros.msg "email.digest.notification" /><#else><@emailMacros.msg "email.digest.notifications" /></#if><@emailMacros.space /><@emailMacros.msg "email.digest.inyourinbox" /><@emailMacros.space /><@emailMacros.msg "email.digest.pleasevisit_1" /><@emailMacros.space /><@emailMacros.msg "email.digest.orcidinbox" /><@emailMacros.space /><@emailMacros.msg "email.digest.pleasevisit_2" />${baseUri}/inbox<@emailMacros.msg "email.digest.pleasevisit_3" /><@emailMacros.space /><@emailMacros.msg "email.digest.pleasevisit_4" />

<#if digestEmail.notificationsBySourceId['ORCID']??>
<@emailMacros.msg "email.digest.orcidwouldlikeyoutoknow" />


<#list digestEmail.notificationsBySourceId['ORCID'].allNotifications as notification>    
    ${notification.subject}
</#list>

</#if>
<#list digestEmail.notificationsBySourceId?keys?sort as sourceId>
<#if sourceId != 'ORCID'>
<#list digestEmail.notificationsBySourceId[sourceId].notificationsByType?keys?sort as notificationType>
<#list digestEmail.notificationsBySourceId[sourceId].notificationsByType[notificationType] as notification>

<#if notificationType == 'PERMISSION'>
${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}: <#if notification.notificationSubject??>${notification.notificationSubject} <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if><#else><@emailMacros.msg "email.digest.requesttoadd" /> <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if></#if>
<#assign itemsByType=notification.items.itemsByType>
<#list itemsByType?keys?sort as itemType>
<@emailMacros.msg "email.common.recordsection." + itemType /> (${itemsByType[itemType]?size})
<#list itemsByType[itemType] as item>
    ${item.itemName?trim} <#if item.externalIdentifier??>(${item.externalIdentifier.type?lower_case}: ${item.externalIdentifier.value})</#if>
</#list>

<@emailMacros.msg "email.digest.plaintext.addnow" /><@emailMacros.space />${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action
<@emailMacros.msg "email.digest.plaintext.moreinfo" /><@emailMacros.space />${baseUri}/inbox#${notification.putCode}
</#list>
<#elseif notificationType == 'AMENDED'>
<#assign amendedSection><@emailMacros.msg "email.common.recordsection." + notification.amendedSection /></#assign>
<@emailMacros.msg "email.digest.hasupdated_1" />${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_2" /><@emailMacros.space />${amendedSection?lower_case}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_3" /> <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if>
<#if notification.items??>

<#list notification.items.items as item>
     ${item.itemName} <#if item.externalIdentifier??>(${item.externalIdentifier.type?lower_case}: ${item.externalIdentifier.value})</#if>
</#list>
</#if>
<#elseif notificationType == 'INSTITUTIONAL_CONNECTION'>
<@emailMacros.msg 'email.institutional_connection.1' /><@emailMacros.space />${(notification.idpName)!}<@emailMacros.space /><@emailMacros.msg 'email.institutional_connection.2' /><@emailMacros.msg 'email.institutional_connection.here' /><@emailMacros.msg 'email.institutional_connection.3' /><@emailMacros.space />${(notification.source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg 'email.institutional_connection.4' /><@emailMacros.space />${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if>
<#else>
${(notification.subject)}           
</#if>

</#list>
</#list>
</#if>
</#list>

<#if ((totalMessageCount?number) > 1)>
</#if>
<@emailMacros.msg "email.digest.plaintext.viewyourinbox" /><@emailMacros.space />${baseUri}/inbox
<@emailMacros.msg "email.digest.youhavereceived_1" /><@emailMacros.space /><@emailMacros.msg "email.digest.youhavereceived_2" /><@emailMacros.space /><@emailMacros.msg "email.digest.plaintext.learnmore_1" /><@emailMacros.msg "email.digest.learnmorelink" /><@emailMacros.msg "email.digest.plaintext.learnmore_2" />
<@emailMacros.msg "email.digest.youmayadjust_1" /><@emailMacros.space /><@emailMacros.msg "email.digest.accountsettings" /><@emailMacros.space /><@emailMacros.msg "email.digest.youmayadjust_2" />${baseUri}/account<@emailMacros.msg "email.digest.youmayadjust_3" />
<#include "email_footer.ftl"/>
