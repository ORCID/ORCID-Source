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
            <p style="font-family: arial, helvetica, sans-serif; font-size: 16px; color: #494A4C;">
                <@emailMacros.msg "email.welcome.your_id.id" /><@emailMacros.space />${orcidId}<br/>
                <@emailMacros.msg "email.welcome.your_id.link" /><@emailMacros.space /><a href="${baseUri}/${orcidId}" target="orcid.blank">${baseUri}/${orcidId}</a>
            </p>
            <hr />
            <p style="font-family: arial, helvetica, sans-serif; font-size: 16px; color: #494A4C;">
                <@emailMacros.msg "email.verify.hi" /><@emailMacros.space />${userName},<br/>
                <#if isPrimary?? && isPrimary><@emailMacros.msg "email.verify.primary.reminder" /><#else><@emailMacros.msg "email.verify.alternate.reminder" /></#if>
            </p>
            <#include "how_do_i_verify_my_email_address_html.ftl"/>
            <hr />
            <#if isPrimary?? && isPrimary>
                <p style="font-family: arial, helvetica, sans-serif; font-size: 16px; color: #494A4C;">
                    <@emailMacros.msg "email.welcome.please_visit_our" /><@emailMacros.space /><a href="https://info.orcid.org/researchers/" target="orcid.blank"><@emailMacros.msg "email.welcome.researcher_homepage" /></a><@emailMacros.space /><@emailMacros.msg "email.welcome.for_more_information" />
                </p>
            </#if>
            <#include "email_footer_html.ftl"/>
         </div>
     </body>
 </html>
 </#escape>
