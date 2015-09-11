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

Here's what has happened since the last time you visited your ORCID record.

Visit ${baseUri}/notifications?lang=${locale} to view all notifications.

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

<@emailMacros.msg "email.common.you_have_received_this_email_opt_out.1" />${baseUri}/account?lang=${locale}.

<#include "email_footer.ftl"/>
