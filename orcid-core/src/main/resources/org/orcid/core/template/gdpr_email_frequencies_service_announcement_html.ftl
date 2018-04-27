<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
    <head>
        <title><@emailMacros.msg "email.email_frequencies.subject" /></title>
    </head>
    <body>
        <div style="padding: 20px; padding-top: 0px;">
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr />
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
            <@emailMacros.msg "email.email_frequencies.dear" />&nbsp;${emailName},
        </span>
        <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
            <@emailMacros.msg "email.email_frequencies.1" />
        </p>
        <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
            <@emailMacros.msg "email.email_frequencies.2" />
        </p>
        <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
            <@emailMacros.msg "email.email_frequencies.3" />
        </p>
        <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
            <@emailMacros.msg "email.email_frequencies.4" />
        </p>
        <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
            <@emailMacros.msg "email.email_frequencies.5" />
        </p>
        <p>
            <@emailMacros.msg "email.email_frequencies.footer.1" /><br >
            <@emailMacros.msg "email.email_frequencies.footer.2" /><br >
            <@emailMacros.msg "email.email_frequencies.footer.3" /><br >
        </p>
        <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
            <#include "email_footer_html.ftl"/>
        </p>                
        </div>
    </body>
</html>
</#escape>