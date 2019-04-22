<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
    <head>
        <title><@emailMacros.msg "email.march_2019.subject" /></title>
    </head>
    <body>
        <div style="padding: 20px; padding-top: 0px;">
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr />
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">
                <@emailMacros.msg "email.common.dear" /> ${emailName}<@emailMacros.msg "email.common.dear.comma" />
            </span>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.march_2019.paragraph_1" />
            </p>            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.march_2019.paragraph_2" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.march_2019.paragraph_3.part_1" /><a href="https://orcid.org/signin" target="_blank"><@emailMacros.msg "email.march_2019.paragraph_3.part_2" /></a> <@emailMacros.msg "email.march_2019.paragraph_3.part_3" /><a href="https://support.orcid.org/hc/en-us/articles/360006971213-Account-email-addresses" target="_blank"><@emailMacros.msg "email.march_2019.paragraph_3.part_4" /></a> <@emailMacros.msg "email.march_2019.paragraph_3.part_5" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.march_2019.paragraph_4.part_1" /><a href="https://support.orcid.org/hc/en-us/articles/360006894494-Visibility-preferences" target="_blank"><@emailMacros.msg "email.march_2019.paragraph_4.part_2" /></a> <@emailMacros.msg "email.march_2019.paragraph_4.part_3" />
            </p>            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.march_2019.paragraph_5.part_1" /><a href="https://orcid.org/blog/2019/03/20/don%E2%80%99t-lose-access-your-orcid-record" target="_blank"><@emailMacros.msg "email.march_2019.paragraph_5.part_2" /></a>
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; white-space: pre;">
                <@emailMacros.msg "email.march_2019.thanks" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; white-space: pre;">
                <@emailMacros.msg "email.march_2019.director_name" />
                
                <@emailMacros.msg "email.march_2019.director_title" />
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