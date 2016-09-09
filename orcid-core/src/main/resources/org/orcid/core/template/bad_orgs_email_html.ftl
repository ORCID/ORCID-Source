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
<!DOCTYPE html>
<html>
	<head>	
		<title>${subject}</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 10px; width: 700px; margin: auto;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		    <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666; white-space: pre;">We are writing to let you know about a bug that we identified recently, which may have affected the display of one or more of the organization names in your ORCID record.  As a result, your record may currently include incorrect information about the following organization(s):</p>
		    <ul style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666">
		    <#list orgDescriptions as orgDescription>
                <li>${orgDescription}</li>
            </#list>
            </ul>
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666; white-space: pre;">We have fixed the bug and have tried to fix the affiliation data that was affected in your record, though some correction may still be needed. We have changed the visibility setting on affected information to private - visible only to you - to allow you to review changes. We encourage you to sign into your record at <a href="https://orcid.org/my-orcid">https://orcid.org/my-orcid</a> to review and, if necessary, correct the affected affiliation information. You can then decide whether to <a href="http://support.orcid.org/knowledgebase/articles/124518-orcid-visibility-settings">keep this information visible only to you, make it publicly available, or share it only with those you trust</a>.</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666; white-space: pre;">Regards,

Laure Haak
Executive Director, ORCID
laure@orcid.org
			    
<a href="${baseUri}/home?lang=${locale}">${baseUri}</a>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>