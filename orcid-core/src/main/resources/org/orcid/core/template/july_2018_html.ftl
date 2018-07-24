<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
    <head>
        <title><@emailMacros.msg "email.july_2018.subject" /></title>
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
                <@emailMacros.msg "email.july_2018.paragraph.3.1" /> <a href="<@emailMacros.msg "email.july_2018.paragraph.3.1.url.link" />" target="_blank"><@emailMacros.msg "email.july_2018.paragraph.3.1.url.text" /></a><@emailMacros.msg "email.july_2018.paragraph.3.2" /> <a href="<@emailMacros.msg "email.july_2018.paragraph.3.2.url.link" />" target="_blank"><@emailMacros.msg "email.july_2018.paragraph.3.2.url.text" /></a>.
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.july_2018.paragraph.4" />
            </p>            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.july_2018.paragraph.5.1" /> <a href="<@emailMacros.msg "email.july_2018.paragraph.5.1.url.link" />" target="_blank"><@emailMacros.msg "email.july_2018.paragraph.5.1.url.text" /></a><@emailMacros.msg "email.july_2018.paragraph.5.2" /> <a href="<@emailMacros.msg "email.july_2018.paragraph.5.2.url.link" />" target="_blank"><@emailMacros.msg "email.july_2018.paragraph.5.2.url.text" /></a>
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; white-space: pre;"><@emailMacros.msg "email.service_announcement.regards" />
<@emailMacros.msg "email.service_announcement.director.name" />
		 
<@emailMacros.msg "email.service_announcement.director.title" />
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.july_2018.note.1" /> <a href="<@emailMacros.msg "email.july_2018.note.1.url.link" />" target="_blank"><@emailMacros.msg "email.july_2018.note.1.url.text" /></a><@emailMacros.msg "email.july_2018.note.2" /> <a href="<@emailMacros.msg "email.july_2018.note.2.url.link" />" target="_blank"><@emailMacros.msg "email.july_2018.note.2.url.text" /></a>
            </p>
            <#include "tips_disclaimer_html.ftl"/>
            <hr />
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <#include "email_footer_html.ftl"/>
            </p> 
        </div>
    </body>
</html>
</#escape>