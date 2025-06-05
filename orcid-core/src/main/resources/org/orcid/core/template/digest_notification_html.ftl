<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
    <!DOCTYPE html>
    <html>
    <head>
        <title>${subject}</title>
    </head>
    <body>
    <div style="
                max-width: 736px;
                padding: 32px;
                margin: auto;
                font-family: Arial, helvetica, sans-serif;
                color: #494A4C;
                font-size: 15px;
                line-height: 1.5;
            ">
        <#include "notification_header_html.ftl"/>
        <#list digestEmail.notificationsBySourceId?keys?sort as sourceId>
            <#if sourceId != 'ORCID'>
                <#list digestEmail.notificationsBySourceId[sourceId].notificationsByType?keys?sort as notificationType>
                    <#if notificationType == 'PERMISSION' || notificationType == 'INSTITUTIONAL_CONNECTION'>
                        <hr style="color: #ff9c00;background-color: #ff9c00;border-style: solid;border-width: 2px;"/>
                        <div style="font-weight: bold;display: flex;align-items: center;text-align: start;letter-spacing: 0.5px;">
                            <span style="background-color:#ff9c00;height: 8px;width: 8px;border-radius: 50%;display: inline-block;margin-right: 8px;"></span>
                            <p style="color: #ff9c00;margin: 6px 0;font-size: 12px;font-weight: bold;"><@emailMacros.msg "notification.digest.permissions" /></p>
                        </div>
                        <p style="margin: 15px 0;font-weight: bold;">
                            ${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}
                            <@emailMacros.space /><@emailMacros.msg "notification.digest.askedPermission" /></p>
                        <hr style="color: #ff9c00;background-color: #ff9c00;border-style: solid;border-width: 2px;"/>
                    <#elseif notificationType == 'ADMINISTRATIVE'>
                            <#if subjectDelegate??>
                                <hr style="color: #447405;background-color: #447405;border-style: solid;border-width: 2px;"/>
                                <div style="font-weight: bold;display: flex;align-items: center;text-align: start;letter-spacing: 0.5px;">
                                    <span style="background-color:#447405;height: 8px;width: 8px;border-radius: 50%;display: inline-block;margin-right: 8px;margin-top: 10px;"></span>
                                    <p style="color: #447405;margin: 6px 0;font-size: 12px;font-weight: bold;"><@emailMacros.msg "notification.share.record" /></p>
                                </div>
                                <p style="margin: 15px 0;font-weight: bold;">
                                <#if subjectDelegate?ends_with("has made you an Account Delegate for their ORCID record")>
                                    ${(subjectDelegate)}
                                <#elseif subjectDelegate?ends_with("has been added as a Trusted Individual")>
                                    ${(subjectDelegate)}
                                <#elseif subjectDelegate?ends_with("has revoked their Account Delegate access to your record")>
                                    ${(subjectDelegate)}
                                <#elseif subjectDelegate?starts_with("[ORCID] Trusting")>
                                    ${(subjectDelegate)}
                                </#if>
                                </p>
                                <hr style="color: #447405;background-color: #447405;border-style: solid;border-width: 2px;"/>
                            </#if>
                    <#elseif notificationType == 'CUSTOM'>
                        <hr style="color: #447405;background-color: #447405;border-style: solid;border-width: 2px;"/>
                        <div style="font-weight: bold;display: flex;align-items: center;text-align: start;letter-spacing: 0.5px;">
                            <span style="background-color:#447405;height: 8px;width: 8px;border-radius: 50%;display: inline-block;margin-right: 8px;margin-top: 10px;"></span>
                            <p style="color: #447405;margin: 6px 0;font-size: 12px;font-weight: bold;"><@emailMacros.msg "notification.digest.data" /></p>
                        </div>
                        <hr style="color: #447405;background-color: #447405;border-style: solid;border-width: 2px;"/>
                    </#if>
                    <#list digestEmail.notificationsBySourceId[sourceId].notificationsByType[notificationType] as notification>
                        <#if notificationType == 'PERMISSION'>
                            <#if notification.notificationIntro??>
                            <#if notification.notificationIntro?contains("::")>
                            <#assign splitValues = notification.notificationIntro?split("::") />
    						<#assign memberName = splitValues[0] />
    						<#assign memberWebUrl = splitValues[1] />
    						<p><b>${notification.notificationSubject}</b></p>
                            <p><@emailMacros.msg "notification.mvp.youCanBenefit" /></p>
                            <p><@emailMacros.msg "notification.mvp.basedOnYourVerifiedEmail" /><@emailMacros.space /><b>${memberName}</b>.<@emailMacros.space /><@emailMacros.msg "notification.mvp.connectingWithThisIntegration" /><@emailMacros.space />${memberName}<@emailMacros.space /><@emailMacros.msg "notification.mvp.toAutomaticallyAdd" />
							</p> 
							<p>
							               <a href="${memberWebUrl}"
                               rel="noopener noreferrer"
                               target="_blank"
                            >
                                <button type="button"
                                        style="width: 200px;
                                        background-color: #2e7f9f;
                                        color: white;
                                        position: relative;
                                        display: inline-block;
                                        padding: .8em;
                                        border: 1px solid transparent;
                                        border-radius: 3px;
                                        outline: none;
                                        font-family: inherit;
                                        font-size: 13px;
                                        font-weight: normal;
                                        line-height: 1.15384615;
                                        text-align: center;
                                        text-decoration: none;
                                        cursor: pointer;
                                        user-select: none;
                                        border: transparent;">
                                    <span style="text-transform: uppercase;">
                                        <@emailMacros.msg "notification.mvp.connectWith" /><@emailMacros.space />${memberName}
                                    </span>
                                </button>
                            </a>
                            <p/>
                            <br>
                            <p style="word-break: break-word">
                                <@emailMacros.msg "notification.digest.cantClick" />
                                <a href="${memberWebUrl}"
                                   target="orcid.blank"
                                   style="text-decoration: underline;color: #085c77;display: inline-block;"
                                >${memberWebUrl}</a>
                            </p>
							<#else>
							<p>${notification.notificationIntro}</p>
                            <p><#if notification.notificationSubject??>${notification.notificationSubject} <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if><#else><@emailMacros.msg "email.digest.requesttoadd" /> <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if></#if></p>
                            <#assign itemsByType=notification.items.itemsByType>
                            <#list itemsByType?keys?sort as itemType>
                                <div>
                                    <p style="margin-bottom: 2px;">
                                        <strong><@emailMacros.msg "email.common.recordsection." + itemType /></strong><@emailMacros.space />(${itemsByType[itemType]?size})
                                    </p>
                                </div>
                                <div>
                                    <ul style="padding-left: 0;margin-top: 2px;">
                                        <#list itemsByType[itemType] as item>
                                            <li><@emailMacros.space />${item.itemName?trim} <#if item.externalIdentifier??>(${item.externalIdentifier.type?lower_case}: ${item.externalIdentifier.value})</#if></li>
                                        </#list>
                                    </ul>
                                </div>
                            </#list>
                            <a href="${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action"
                               rel="noopener noreferrer"
                               target="_blank"
                            >
                                <button type="button"
                                        style="width: 165px;
                                        background-color: #2e7f9f;
                                        color: white;
                                        position: relative;
                                        display: inline-block;
                                        padding: .8em;
                                        border: 1px solid transparent;
                                        border-radius: 3px;
                                        outline: none;
                                        font-family: inherit;
                                        font-size: 13px;
                                        font-weight: normal;
                                        line-height: 1.15384615;
                                        text-align: center;
                                        text-decoration: none;
                                        cursor: pointer;
                                        user-select: none;
                                        border: transparent;">
                                    <span style="text-transform: uppercase;">
                                        <@emailMacros.msg "notification.digest.grantPermission" />
                                    </span>
                                </button>
                            </a>
                            <br>
                            <p style="word-break: break-word">
                                <@emailMacros.msg "notification.digest.cantClick" />
                                <a href="${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action"
                                   target="orcid.blank"
                                   style="text-decoration: underline;color: #085c77;display: inline-block;"
                                >${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action</a>
                            </p>
                            </#if>
                            </#if>
                        <#elseif notificationType == 'AMENDED' && !verboseNotifications>
                            <p>
                                <@emailMacros.msg "notification.digest.showing" />
                                <@emailMacros.space /><b>${digestEmail.sources?size}</b><@emailMacros.space />
                                <@emailMacros.msg "notification.digest.outOf" /><@emailMacros.space />
                                <b>${digestEmail.sources?size}</b><@emailMacros.space />
                                <@emailMacros.msg "notification.digest.changes" />
                            </p>
                            <p><b>${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}</b></p>
                            <#assign amendedSection><@emailMacros.msg "email.common.recordsection." + notification.amendedSection /></#assign>
                            <br>
                            <@emailMacros.msg "email.digest.hasupdated_1" />
                            <br>
                            ${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_2" /><@emailMacros.space />${amendedSection?lower_case}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_3" /><@emailMacros.space /><#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if>
                        <#elseif notificationType == 'INSTITUTIONAL_CONNECTION'>
                            <@emailMacros.msg 'email.institutional_connection.1' /><@emailMacros.space />${(notification.idpName)!}<@emailMacros.space /><@emailMacros.msg 'email.institutional_connection.2' /><@emailMacros.msg 'email.institutional_connection.here' /><@emailMacros.msg 'email.institutional_connection.3' /><@emailMacros.space />${(notification.source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg 'email.institutional_connection.4' /><@emailMacros.space />${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if>
                            <br>
                        <#elseif notificationType != 'AMENDED'>
                            <#if notificationType == 'ADMINISTRATIVE'>
                                <#if subjectDelegate??>
                                    <#if subjectDelegate?ends_with("has made you an Account Delegate for their ORCID record")>
                                        <@bodyHtmlDelegateRecipient?interpret />
                                    <#elseif subjectDelegate?ends_with("has been added as a Trusted Individual")>
                                        <@bodyHtmlDelegate?interpret />
                                    <#elseif subjectDelegate?ends_with("has revoked their Account Delegate access to your record")>
                                    	<@bodyHtmlDelegate?interpret />
                                    <#elseif subjectDelegate?starts_with("[ORCID] Trusting")>
                                        <@bodyHtmlAdminDelegate?interpret />
                                    <#else>
                                        ${(notification.subject)}
                                    </#if>
                                </#if>
                            <#else>
                                ${(notification.subject)}
                            </#if>
                        </#if>
                    </#list>
                </#list>
            </#if>
        </#list>
        <#if verboseNotifications>
            <#include "digest_notification_amend_section_html.ftl"/>
        </#if>
        <#include "notification_footer_html.ftl"/>
    </div>
    </body>
    </html>
</#escape>
