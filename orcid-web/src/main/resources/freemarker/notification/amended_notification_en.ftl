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
<html>
<#assign verDateTime = startupDate?datetime>
<#assign ver="${verDateTime?iso_utc}">
<#include "/common/html-head.ftl" />
    <body data-baseurl="<@spring.url '/'/>">
        <div>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
                Dear ${emailName},
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                Thought you'd like to know... ${notification.source.sourceName.content} has updated your ORCID record.
            </p>            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; white-space: pre;">
Kind Regards,
The ORCID Team
support@orcid.org
            </p>
         </div>
     </body>
 </html>