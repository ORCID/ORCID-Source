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
        <div style="padding: 20px; padding-top: 10px; margin: auto; line-height: 1.5;">
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr />
            <p style="font-family: arial, helvetica, sans-serif; font-size: 16px; color: #494A4C;">
                <@emailMacros.msg "email.welcome.your_id.id" /><@emailMacros.space />${orcidId}<br/>
                <@emailMacros.msg "email.welcome.your_id.link" /><@emailMacros.space /><a href="${baseUri}/${orcidId}" target="orcid.blank">${baseUri}/${orcidId}</a>
            </p>
            <hr />
            <p style="font-family: arial, helvetica, sans-serif; font-size: 16px; color: #494A4C;">
                <@emailMacros.msg "email.welcome" /><@emailMacros.space />${userName},<br/>
                <@emailMacros.msg "email.welcome.congrats" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 16px; color: #494A4C;">
                <@emailMacros.msg "email.welcome.verify.1" />
            </p>
            <#include "how_do_i_verify_my_email_address_html.ftl"/>
            <hr />
            <p style="font-family: arial, helvetica, sans-serif; font-size: 16px; color: #494A4C;">
                <@emailMacros.msg "email.welcome.please_visit_our" /><@emailMacros.space /><a href="https://info.orcid.org/researchers/" target="orcid.blank"><@emailMacros.msg "email.welcome.researcher_homepage" /></a><@emailMacros.space /><@emailMacros.msg "email.welcome.for_more_information" />
            </p>
            <#include "email_footer_html.ftl"/>
         </div>
     </body>
 </html>
 </#escape>
