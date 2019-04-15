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
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">
            Dear ${emailName},
        </span>
        <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
            You may have heard about the <a href="https://www.eugdpr.org/" target="_blank">General Data Protection Regulation</a> (GDPR), which is taking effect in Europe later this month. The GDPR improves transparency and data privacy rights of individuals. We're writing to explain how our practices align with GDPR.
        </p>
        <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
            Individual control and transparency are core <a href="https://orcid.org/about/what-is-orcid/principles" target="_blank">ORCID principles</a>. As an ORCID user, you have always been in control of what information is added to your record, who can view that information, and which organizations can read, add, or update your record. 
        </p>
        <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
            We evaluate our practices every year during an external audit of our <a href="https://orcid.org/privacy-policy" target="_blank">privacy policy</a> and practices. In addition, a recently commissioned <a href="https://orcid.org/blog/2018/04/18/orcid-and-data-privacy-germany" target="_blank">expert legal review</a> of our data privacy practices considered both German data protection law and the GDPR framework. Both of these reports found ORCID to be in a strong position with regard to GDPR.
        </p>
        <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
            We continue to monitor the evolving interpretation of the regulation to ensure your rights are protected. Based on our evaluation, we have made the following changes to the ORCID Registry and services:
        </p>
        <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">Privacy Policy</span>
            <ul style="list-style:disc">
                <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;"><u>Clear language.</u> We <a href="https://orcid.org/blog/2017/03/21/valuing-privacy-and-transparency" target="_blank">updated our privacy policy</a> to make it more clear and concise.</li>
            </ul>
        </p>        
        <p>
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">Your rights as a user under GDPR</span>
            <ul style="list-style:disc">
                <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">We created a <a href="https://support.orcid.org/hc/articles/360006895834" target="_blank">new Knowledge Base article</a> explaining the rights you have under the GDPR and how you can adjust your account settings in the Registry interface.</li>
            </ul>
        </p>
        <p>
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">Security documentation</span>
            <ul style="list-style:disc">
                <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">We updated our <a href="https://support.orcid.org/hc/articles/360006897574" target="_blank">security documentation</a> to reflect our current practices.</li>
            </ul>
        </p>
        <p>
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">Registration</span>
            <ul style="list-style:disc">
                <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;"><u>Visibility settings.</u> The default visibility setting for new items added to your record is no longer preselected in the registration form. You can view and update your current setting by going to your <a href="https://orcid.org/account" target="_blank">Account Settings</a> and clicking on the "visibility preferences" tab.</li>
                <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;"><u>Email frequency.</u> The frequency setting for receiving emails from ORCID is no longer preselected in the registration form. You can view your current setting by going to your <a href="https://orcid.org/account" target="_blank">Account Settings</a> and clicking on the "Email and notification preferences" tab.</li>
            </ul>
        </p>
        <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
            As always, please <a href="https://orcid.org/help/contact-us" target="_blank">contact us</a> if you have questions about ORCID and your data privacy.
        </p>
        <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">
Cheers,

- Laure

Laurel L. Haak
Executive Director, ORCID
        </p>
        <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
            You have received this email as a service announcement related to your ORCID Account.
        </p>
        <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
            <small>
            <a href="${baseUri}/account" style="color: #2E7F9F;">email preferences</a> 
            | <a href="${baseUri}/privacy-policy" style="color: #2E7F9F;">privacy policy</a> 
            | ORCID, Inc. | 10411 Motor City Drive, Suite 750, Bethesda, MD 20817, USA 
            | <a href="${baseUri}" style="color: #2E7F9F;">ORCID.org</a>
            </small>            
        </p>                       
        </div>
    </body>
</html>
</#escape>