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
<@emailMacros.msg "email.common.dear" /> ${emailName}<@emailMacros.msg "email.common.dear.comma" />

Thank you for registering for an ORCID identifier. Please click the link below to confirm your registration and verify your email address.


${verificationUrl}?lang=${locale}


If you can't click the link, copy and paste it into your browser's address bar.

Your ORCID iD is ${orcidId}, and the link to your public record is ${baseUri}/${orcidId}

Next steps:

1. Add more information to your ORCID Record

Access your ORCID record at ${baseUri}/my-orcid and add additional information to your record.
Funders, publishers, universities and others use the information contained in an ORCID Record to help decrease the record keeping they ask from you. Increase the amount of information you can share in this way by adding other names you are known by, professional information, funding items you have received and works you have created to your Record.

For tips on adding information to your ORCID record see: 
http://support.orcid.org/knowledgebase/articles/460004

2. Continue to use your ORCID iD

May systems ask for your ORCID iD to create a link between you and your research outputs. Continue to use your ORCID iD whenever it is asked for to get credit for your work and decrease future record keeping.

Need Help?

If you have any questions or need help, contact the ORCID support team visit http://support.orcid.org.

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}

<@emailMacros.msg "email.api_record_creation.you_have_received.1" />${baseUri}/home?lang=${locale}<@emailMacros.msg "email.api_record_creation.you_have_received.2" />
<#include "email_footer.ftl"/>