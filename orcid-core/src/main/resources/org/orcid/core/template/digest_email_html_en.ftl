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
                Hi ${emailName},
            </span>
            <p>
                You have <${totalMessageCount}> new <#if orcidMessageCount == 1>notification<#else>notifications</#if> in your ORCID inbox - see summary below. Please visit your <a href="${baseUri}/inbox?lang=${locale}" style="color: #338caf;">ORCID Inbox</a> to take action or see more details.
            </p>
            <#if digestEmail.notificationsBySourceId['ORCID']??><p>
                ORCID would like to let you know
                <ul>
                <#list digestEmail.notificationsBySourceId['ORCID'].allNotifications as notification>    
                    <li>${notification.subject}</li>
                </#list>
                </ul>
            </p></#if>
            <#list digestEmail.notificationsBySourceId?keys?sort as sourceId>
            <#if sourceId != 'ORCID'>
            <#list digestEmail.notificationsBySourceId[sourceId].notificationsByType?keys?sort as notificationType>
            <#list digestEmail.notificationsBySourceId[sourceId].notificationsByType[notificationType] as notification>
            <#if notificationType == 'PERMISSION'>
            <p>
                <div><img src="${baseUri}/static/img/request.png">${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId} offers to add/update items to your ORCID record.</div>
                <#assign itemsByType=notification.items.itemsByType>
                <#list itemsByType?keys?sort as itemType>
                <div>${itemType?capitalize}s (${itemsByType[itemType]?size})</div>
                <ul>
                <#list itemsByType[itemType] as item>
                    <li>${item.itemName} <#if item.externalIdentifier??>(${item.externalIdentifier.externalIdentifierType?lower_case}: ${item.externalIdentifier.externalIdentifierId})</#if></li>
                </#list>
                </ul>
                </#list>
                <div><a href="${baseUri}/inbox#${notification.putCode}">more info...</a> <a class="button" href="${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action">Add now</a></div>
            </p>
            <#elseif notificationType == 'AMENDED'>
            <p>
                <div><img src="${baseUri}/static/img/update.png">${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId} has updated recent ${notification.amendedSection?lower_case}s on your ORCID record.</div>
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
            <p>
                <a href="${baseUri}/inbox?lang=${locale}" style="text-decoration: none; text-align: center;">
                    <span style="padding-top: 10px; padding-bottom: 10px; padding-left: 15px; padding-right: 15px; background: #338caf; color: #FFF; display: block; width: 300px;">
                        View details in your ORCID inbox
                    </span>
                </a>
            </p>
            <p>
                <#assign frequency>
                <#switch orcidProfile.orcidInternal.preferences.sendEmailFrequencyDays>
                    <#case "0.0">immediate<#break>
                    <#case "7.0">weekly<#break>
                    <#case "91.3105">quarterly<#break>
                </#switch>
                </#assign>
                You have received this message because you opted in to receive ${frequency} inbox notifications about your ORCID record. <a href="${baseUri}/inbox?lang=${locale}" style="color: #338caf;">Learn more about how the Inbox works.</a>
            </p>
            <p>
                You may adjust your email frequency and subscription preferences in your <a href="${baseUri}/account?lang=${locale}" style="color: #338caf;">account settings</a>.
            </p>
            <p>
               <#include "email_footer_html.ftl"/>
            </p>            
        </div>      
    </body>
</html>