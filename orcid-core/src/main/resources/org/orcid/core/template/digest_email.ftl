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
<@emailMacros.msg "email.common.hi" />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.digest.youhave" />${totalMessageCount}<@emailMacros.msg "email.digest.new" /><#if ((totalMessageCount?number) == 1)><@emailMacros.msg "email.digest.notification" /><#else><@emailMacros.msg "email.digest.notifications" /></#if><@emailMacros.msg "email.digest.inyourinbox" /><@emailMacros.msg "email.digest.pleasevisit_1" /><@emailMacros.msg "email.digest.orcidinbox" /><@emailMacros.msg "email.digest.pleasevisit_2" />${baseUri}/inbox<@emailMacros.msg "email.digest.pleasevisit_3" /><@emailMacros.msg "email.digest.pleasevisit_4" />

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
${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}: <#if notification.notificationSubject??>${notification.notificationSubject}<#else><@emailMacros.msg "email.digest.requesttoadd" /></#if>
<#assign itemsByType=notification.items.itemsByType>
<#list itemsByType?keys?sort as itemType>
<@emailMacros.msg "email.common.recordsection." + itemType /> (${itemsByType[itemType]?size})
<#list itemsByType[itemType] as item>
    ${item.itemName?trim} <#if item.externalIdentifier??>(${item.externalIdentifier.externalIdentifierType?lower_case}: ${item.externalIdentifier.externalIdentifierId})</#if>
</#list>

<@emailMacros.msg "email.digest.plaintext.addnow" />${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action
<@emailMacros.msg "email.digest.plaintext.moreinfo" />${baseUri}/inbox#${notification.putCode}
</#list>
<#elseif notificationType == 'AMENDED'>
<#assign amendedSection><@emailMacros.msg "email.common.recordsection." + notification.amendedSection /></#assign>
<@emailMacros.msg "email.digest.hasupdated_1" />${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}<@emailMacros.msg "email.digest.hasupdated_2" />${amendedSection?lower_case}<@emailMacros.msg "email.digest.hasupdated_3" />
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

<#if ((totalMessageCount?number) > 1)>
</#if>
<@emailMacros.msg "email.digest.plaintext.viewyourinbox" />${baseUri}/inbox

<#assign frequency>
    <#switch orcidProfile.orcidInternal.preferences.sendEmailFrequencyDays>
        <#case "0.0"><@emailMacros.msg "email.digest.frequency.immediate" /><#break>
        <#case "7.0"><@emailMacros.msg "email.digest.frequency.weekly" /><#break>
        <#case "91.3105"><@emailMacros.msg "email.digest.frequency.quarterly" /><#break>
    </#switch>
</#assign>
<@emailMacros.msg "email.digest.youhavereceived_1" />${frequency}<@emailMacros.msg "email.digest.youhavereceived_2" /><@emailMacros.msg "email.digest.plaintext.learnmore_1" /><@emailMacros.msg "email.digest.learnmorelink" /><@emailMacros.msg "email.digest.plaintext.learnmore_2" />
<@emailMacros.msg "email.digest.youmayadjust_1" /><@emailMacros.msg "email.digest.accountsettings" /><@emailMacros.msg "email.digest.youmayadjust_2" />${baseUri}/account<@emailMacros.msg "email.digest.youmayadjust_3" />
<#include "email_footer.ftl"/>