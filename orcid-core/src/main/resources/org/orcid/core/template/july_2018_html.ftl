<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
    <head>
        <title>ORCID and Your Data Privacy</title>
    </head>
    <body>
        <div style="padding: 20px; padding-top: 0px;">
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr />
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
                <@emailMacros.msg "email.common.dear" /> ${emailName}<@emailMacros.msg "email.common.dear.comma" />
            </span>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.july_2018.paragraph.1.1" /> <a href="<@emailMacros.msg "email.july_2018.paragraph.1.1.orcid" />" target="_blank"><@emailMacros.msg "email.july_2018.paragraph.1.1.name" /></a><@emailMacros.msg "email.july_2018.paragraph.1.2" /> <a href="<@emailMacros.msg "email.july_2018.paragraph.1.2.orcid" />" target="_blank"><@emailMacros.msg "email.july_2018.paragraph.1.2.name" /></a><@emailMacros.msg "email.july_2018.paragraph.1.3" /> <a href="<@emailMacros.msg "email.july_2018.paragraph.1.3.orcid" />" target="_blank"><@emailMacros.msg "email.july_2018.paragraph.1.3.name" /></a><@emailMacros.msg "email.july_2018.paragraph.1.4" />
            </p>            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.july_2018.paragraph.2.1" /> <a href="<@emailMacros.msg "email.july_2018.paragraph.2.url.link" />" target="_blank"><@emailMacros.msg "email.july_2018.paragraph.2.url.text" /></a> <@emailMacros.msg "email.july_2018.paragraph.2.2" />
            </p>























            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
            </p>
            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
            </p>

            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
            </p>
            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
            </p>


            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
                <small>
                <a href="${baseUri}/account" style="color: #338caf;">email preferences</a> 
                | <a href="${baseUri}/privacy-policy" style="color: #338caf;">privacy policy</a> 
                | ORCID, Inc. | 10411 Motor City Drive, Suite 750, Bethesda, MD 20817, USA 
                | <a href="${baseUri}" style="color: #338caf;">ORCID.org</a>
                </small>            
            </p> 
        </div>
        </body>
</html>
</#escape>
        
        
        




<@emailMacros.msg "email.july_2018.paragraph.3.1" /> <@emailMacros.msg "email.july_2018.paragraph.3.1.url.text" /> (<@emailMacros.msg "email.july_2018.paragraph.3.1.url.link" />)<@emailMacros.msg "email.july_2018.paragraph.3.2" /> <@emailMacros.msg "email.july_2018.paragraph.3.2.url.text" /> (<@emailMacros.msg "email.july_2018.paragraph.3.2.url.link" />)

<@emailMacros.msg "email.july_2018.paragraph.4" />

<@emailMacros.msg "email.july_2018.paragraph.5.1" /> <@emailMacros.msg "email.july_2018.paragraph.5.1.url.text" /> (<@emailMacros.msg "email.july_2018.paragraph.5.1.url.link" />)<@emailMacros.msg "email.july_2018.paragraph.5.2" /> <@emailMacros.msg "email.july_2018.paragraph.5.2.url.text" /> (<@emailMacros.msg "email.july_2018.paragraph.5.2.url.link" />)

<@emailMacros.msg "email.july_2018.paragraph.6" />

Laure Haak 
Executive Director, ORCID

<@emailMacros.msg "email.july_2018.note" />

<#include "tips_disclaimer_html.ftl"/>

----
<#include "email_footer.ftl"/>