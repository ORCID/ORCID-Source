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
<#escape x as x?html>
<!DOCTYPE html>
<html>
    <head>  
        <title>${subject}</title>
    </head>
    <body>
        <div style="padding: 20px; padding-top: 10px; width: 700px; margin: auto;">
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr />
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />
            </span>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.welcome.thank_you.1" />
                <br />
                <div align="center">
                    <a href="${verificationUrl}?lang=${locale}" target="orcid.blank" 
                    style="text-decoration: none; background: #31789B; border-color: #357ebd; color: #fff; margin-bottom: 0; font-weight: 400; text-align: center; vertical-align: middle; cursor: pointer; border: 1px solid transparent; white-space: nowrap; padding: 6px 12px; font-size: 14px; line-height: 1.428571429; border-radius: 4px;"><@emailMacros.msg "email.button" /></a>
                </div>
                <br />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.welcome.your_id.id" /><@emailMacros.space />${orcidId}<@emailMacros.msg "email.welcome.your_id.link" /><@emailMacros.space /><a href="${baseUri}/${orcidId}" target="orcid.blank">${baseUri}/${orcidId}</a>                
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 18px; color: #666666; font-weight: bold;">
                <@emailMacros.msg "email.welcome.next_steps" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <strong><@emailMacros.msg "email.welcome.next_steps.1" /></strong>
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.welcome.next_steps.1.description.1.1" /><@emailMacros.space />${baseUri}/my-orcid<@emailMacros.space /><@emailMacros.msg "email.welcome.next_steps.1.description.1.2" /><@emailMacros.space />
                <br /><br />
                <@emailMacros.msg "email.welcome.next_steps.1.description.2" />
                <br /><br />
                <@emailMacros.msg "email.welcome.next_steps.1.description.tips.1" /><@emailMacros.space /><a href='<@emailMacros.msg "email.welcome.next_steps.1.description.tips.1.link.href" />' /><@emailMacros.msg "email.welcome.next_steps.1.description.tips.1.link.text" /></a><@emailMacros.msg "email.welcome.next_steps.1.description.tips.2" /><a href='<@emailMacros.msg "email.welcome.next_steps.1.description.link.href" />' target="orcid.blank"><@emailMacros.msg "email.welcome.next_steps.1.description.link.text" /></a>
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <strong><@emailMacros.msg "email.welcome.next_steps.2" /></strong>
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.welcome.next_steps.2.description" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <strong><@emailMacros.msg "email.welcome.need_help" /></strong>
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.welcome.need_help.description.1" /><@emailMacros.space /><a href='<@emailMacros.msg "email.welcome.need_help.description.1.href" />'><@emailMacros.msg "email.welcome.need_help.description.1.text" /></a><@emailMacros.space /><@emailMacros.msg "email.welcome.need_help.description.2" /><@emailMacros.space /><a href='<@emailMacros.msg "email.welcome.need_help.description.2.href" />'><@emailMacros.msg "email.welcome.need_help.description.2.text" /></a>
            </p>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666; white-space: pre;">
<@emailMacros.msg "email.common.kind_regards.simple" />
<a href="${baseUri}/home?lang=${locale}">${baseUri}/</a>
            </p>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
                <@emailMacros.msg "email.common.you_have_received_this_email" />
            </p>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
               <#include "email_footer_html.ftl"/>
            </p>
         </div>
     </body>
 </html>
 </#escape>
