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

You have <${totalMessageCount}> new <#if orcidMessageCount == 1>notification<#else>notifications</#if> in your ORCID inbox - see summary below. Please visit your ORCID account inbox to take action. [1]

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
${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId} would like to add the following items to your record.
<#assign itemsByType=notification.items.itemsByType>
<#list itemsByType?keys?sort as itemType>
${itemType?capitalize}s (${itemsByType[itemType]?size})
Visit ${notification.authorizationUrl.uri} to add now.

<#list itemsByType[itemType] as item>
    ${item.itemName} <#if item.externalIdentifier??>(${item.externalIdentifier.externalIdentifierType?lower_case}: ${item.externalIdentifier.externalIdentifierId})</#if>
</#list>
</#list>
<#elseif notificationType == 'AMENDED'>
${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId} amended the ${notification.amendedSection?lower_case}s section of your record.
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

You have received this message because you opted in to receive Inbox notifications about your ORCID record. Learn more about how the Inbox works [2].
You may adjust your email frequency and subscription preferences in your account settings [3].

[1] ORCID account inbox: ${baseUri}/inbox
[2] About the ORCID Inbox: http://support.orcid.org/knowledgebase/articles/665437
[3] ORCID account settings: ${baseUri}/account 
[4] Email preferences: ${baseUri}/account 
[5] ORCID privacy policy: ${baseUri}/footer/privacy-policy

<#include "email_footer.ftl"/>