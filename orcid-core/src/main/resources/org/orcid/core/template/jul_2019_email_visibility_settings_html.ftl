<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
    <head>
        <title><@emailMacros.msg "email.2019.vis_settings.subject" /></title>
    </head>
    <body>
        <div style="padding: 20px; padding-top: 0px;">
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr />
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">
                <@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />
            </span>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.2019.vis_settings.we_are_contacting_you_about" /><@emailMacros.space /><a href="${baseUri}/${orcidId}" target="orcid.blank">${baseUri}/${orcidId}</a>
            </p>            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.2019.vis_settings.as_an_orcid_user" /><@emailMacros.space /><a href="https://support.orcid.org/hc/en-us/articles/360006897614-Visibility-settings" target="email.2019.vis_settings.visibility_settings"><@emailMacros.msg "email.2019.vis_settings.visibility_settings" /></a><@emailMacros.space /><@emailMacros.msg "email.2019.vis_settings.for_more_information" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.2019.vis_settings.when_we_first_launched" /><@emailMacros.space /><a href="https://orcid.org/blog/2013/01/18/orcid-tech-update-default-privacy-settings" target="email.2019.vis_settings.we_later_changed"><@emailMacros.msg "email.2019.vis_settings.we_later_changed" /></a><@emailMacros.space /><@emailMacros.msg "email.2019.vis_settings.except_your_email" /><@emailMacros.space /><a href="${baseUri}/account" target="email.2019.vis_settings.account_settings"><@emailMacros.msg "email.2019.vis_settings.account_settings" /></a><@emailMacros.msg "email.common.period" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.2019.vis_settings.we_are_contacting_you_to_thank" />
            </p>            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.2019.vis_settings.changing_who_you_share" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.2019.vis_settings.for_more_information_please" /><@emailMacros.space /><a href="https://support.orcid.org/hc/en-us/articles/360006897614-Visibility-settings" target="email.2019.vis_settings.visibility_settings"><@emailMacros.msg "email.2019.vis_settings.visibility_settings" /></a><@emailMacros.space /><@emailMacros.msg "email.2019.vis_settings.if_you_have" /> 
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.2019.vis_settings.thanks_for_your" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.2019.vis_settings.cheers" /><@emailMacros.msg "email.common.dear.comma" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.march_2019.director_name" /><@emailMacros.msg "email.common.dear.comma" /><@emailMacros.space /><@emailMacros.msg "email.march_2019.director_title" />
            </p>
            <#include "tips_disclaimer_html.ftl"/>
            <hr />
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <#include "email_footer_html.ftl"/>
            </p> 
        </div>
    </body>
</html>
</#escape>