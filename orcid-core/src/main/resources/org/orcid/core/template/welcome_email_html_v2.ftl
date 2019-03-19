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
        <div style="padding: 20px; padding-top: 10px; margin: auto;">
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr />
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />
            </span>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.welcome.thank_you.1" />
            </p>

             <table
                        cellpadding="0"
                        cellspacing="0"
                        style="font-family: Helvetica, Arial, sans-serif;  border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; margin: 0 auto; margin-top:20px"
                    >
                        <tbody>
                        <tr>
                            <td
                            style="border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-radius: 4px; margin: 0;"
                            >
                            <a
                                href="${verificationUrl}?lang=${locale}"
                                style="font-size: 20px; font-family: Helvetica, Arial, sans-serif; text-decoration: none; border-radius: 4.8px; line-height: 25px; display: inline-block; font-weight: normal; white-space: nowrap; background-color: #31789B; color: #ffffff; padding: 8px 16px; border: 1px solid #31789B;"
                                > <@emailMacros.msg "email.verify.button" /></a
                            >
                            </td>
                        </tr>
                        </tbody>
                    </table>

                    <table
                        cellpadding="0"
                        cellspacing="0"
                        style="font-family: arial, helvetica, sans-serif; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; margin: 0 auto; "
                    >
                        <tbody>
                        <tr>
                            <td>
                            <p
                                align="center"
                                class="text-center"
                                style="line-height: 24px; font-size: 16px; margin: 0; padding-bottom: 30px; padding-top: 20px; word-break: break-word;"
                            >
                                <a
                                href="${verificationUrl}?lang=${locale}"
                                target="orcid.blank"
                                >${verificationUrl}?lang=${locale}</a
                                >
                            </p>
                            </td>
                        </tr>
                        </tbody>
                    </table>
    

            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.welcome.your_id.id" /><@emailMacros.space />${orcidId}<@emailMacros.msg "email.welcome.your_id.link" /><@emailMacros.space /><a href="${baseUri}/${orcidId}" target="orcid.blank">${baseUri}/${orcidId}</a>                
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 18px; color: #494A4C; font-weight: bold;">
                <@emailMacros.msg "email.welcome.next_steps" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <strong><@emailMacros.msg "email.welcome.next_steps.1" /></strong>
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.welcome.next_steps.1.description.1.1" /><@emailMacros.space />${baseUri}/my-orcid<@emailMacros.space /><@emailMacros.msg "email.welcome.next_steps.1.description.1.2" /><@emailMacros.space />
                <br /><br />
                <@emailMacros.msg "email.welcome.next_steps.1.description.2" />
                <br /><br />
                <@emailMacros.msg "email.welcome.next_steps.1.description.tips.1" /><@emailMacros.space /><a href='<@emailMacros.msg "email.welcome.next_steps.1.description.tips.1.link.href" />' /><@emailMacros.msg "email.welcome.next_steps.1.description.tips.1.link.text" /></a><@emailMacros.msg "email.welcome.next_steps.1.description.tips.2" /><a href='<@emailMacros.msg "email.welcome.next_steps.1.description.link.href" />' target="orcid.blank"><@emailMacros.msg "email.welcome.next_steps.1.description.link.text" /></a>
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <strong><@emailMacros.msg "email.welcome.next_steps.2" /></strong>
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.welcome.next_steps.2.description" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <strong><@emailMacros.msg "email.welcome.need_help" /></strong>
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.common.need_help.description.1" /><@emailMacros.space /><a href='<@emailMacros.msg "email.common.need_help.description.1.href" />'><@emailMacros.msg "email.common.need_help.description.1.text" /></a><@emailMacros.space /><@emailMacros.msg "email.common.need_help.description.2" /><@emailMacros.space /><a href='<@emailMacros.msg "email.common.need_help.description.2.href" />'><@emailMacros.msg "email.common.need_help.description.2.text" /></a>
            </p>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">
<@emailMacros.msg "email.common.warm_regards.simple" />
<a href="${baseUri}/home?lang=${locale}">${baseUri}/</a>
            </p>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
                <@emailMacros.msg "email.common.you_have_received_this_email" />
            </p>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
               <#include "email_footer_html.ftl"/>
            </p>
         </div>
     </body>
 </html>
 </#escape>
