<#import "email_macros.ftl" as emailMacros />
Dear ${emailName},

This is an important message to let you know that you have exceeded your daily Public API usage limit with your integration (https://info.orcid.org/ufaqs/what-are-the-api-limits/):

Client Name: ${clientName}
Client ID: ${clientId}

Please remember that the ORCID Public API is free for non-commercial use by individuals as stated in the Public APIs Terms of Service (https://info.orcid.org/public-client-terms-of-service). By “non-commercial” we mean that you may not charge any re-use fees for the Public API, and you may not make use of the Public API in connection with any revenue-generating product or service.

Based on your API usage, we highly recommend you consider becoming an ORCID member for access to our Member API (https://info.orcid.org/documentation/features/member-api/). Not only will it allow you to access a higher Rate Limits and an unrestricted Usage Quota, but you will be able to access Trusted Party data in ORCID records and contribute data to ORCID records from your institutional systems.

To minimize any disruption to your ORCID integration in the future, we would recommend that you reach out to our Engagement Team by replying to this email to discuss our ORCID membership options.


Warm Regards,
ORCID Support Team 
https://support.orcid.org

<#include "email_footer.ftl"/>
