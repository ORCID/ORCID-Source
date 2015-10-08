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
Hi ${emailName},

You have ${totalMessageCount} new <#if ((totalMessageCount?number) == 1)>notification<#else>notifications</#if> in your ORCID inbox - see summary below. Please visit your ORCID Inbox (${baseUri}/inbox) to take action or see more details.

<#if digestEmail.notificationsBySourceId['ORCID']??>
ORCID would like to let you know

<#list digestEmail.notificationsBySourceId['ORCID'].allNotifications as notification>    
    ${notification.subject}
</#list>

</#if>
<#list digestEmail.notificationsBySourceId?keys?sort as sourceId>
<#if sourceId != 'ORCID'>
<#list digestEmail.notificationsBySourceId[sourceId].notificationsByType?keys?sort as notificationType>
<#list digestEmail.notificationsBySourceId[sourceId].notificationsByType[notificationType] as notification>
<#if notificationType == 'PERMISSION'>
${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}: ${notification.notificationSubject!'Request to add items'}
<#assign itemsByType=notification.items.itemsByType>
<#list itemsByType?keys?sort as itemType>
${itemType?capitalize}<#if itemType == 'WORK'>s</#if> (${itemsByType[itemType]?size})
Visit ${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action to add now.

<#list itemsByType[itemType] as item>
    ${item.itemName} <#if item.externalIdentifier??>(${item.externalIdentifier.externalIdentifierType?lower_case}: ${item.externalIdentifier.externalIdentifierId})</#if>
</#list>
</#list>
<#elseif notificationType == 'AMENDED'>
${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId} has updated recent ${notification.amendedSection?lower_case}s on your ORCID record.
<#if notification.items??>

<#list notification.items.items as item>
     ${item.itemName} <#if item.externalIdentifier??>(${item.externalIdentifier.externalIdentifierType?lower_case}: ${item.externalIdentifier.externalIdentifierId})</#if>
</#list>
</#if>
<#else>
${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}
</#if>

</#list>
</#list>
</#if>
</#list>

VIEW YOUR ORCID INBOX: ${baseUri}/inbox

<#assign frequency>
    <#switch orcidProfile.orcidInternal.preferences.sendEmailFrequencyDays>
        <#case "0.0">immediate<#break>
        <#case "7.0">weekly<#break>
        <#case "91.3105">quarterly<#break>
    </#switch>
</#assign>
You have received this message because you opted in to receive ${frequency} inbox notifications about your ORCID record. Learn more about how the Inbox works (http://support.orcid.org/knowledgebase/articles/665437).

You may adjust your email frequency and subscription preferences in your account settings (${baseUri}/account).

<#include "email_footer.ftl"/>