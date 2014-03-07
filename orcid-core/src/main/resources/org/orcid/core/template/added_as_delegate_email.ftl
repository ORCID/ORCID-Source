<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#-- 
    NOTE from LADP
    In the original email there was what was supposed to be placeholder text for 
    the Granting ORCID User's email address but in NotificationManagerImpl.java 
    there was no parameter. Code was added (by Laura, commented out, needs to be
    reviewed) to add this parameter (and suggest that this particular email come 
    from the ORCID user instead of support). If all is okay in the the manager, 
    usersEmail@domain.com in the last paragraph below should be changed to 
    something like ${grantingOrcidEmail}
-->
<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /> ${emailNameForDelegate}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.added_as_delegate.you_have.1" />${grantingOrcidName}<@emailMacros.msg "email.added_as_delegate.you_have.2" />${baseUri}/${grantingOrcidValue}?lang=${locale}<@emailMacros.msg "email.added_as_delegate.you_have.3" />${grantingOrcidName}<@emailMacros.msg "email.added_as_delegate.you_have.4" />

<@emailMacros.msg "email.added_as_delegate.for_a_tutorial" />

<@emailMacros.msg "email.added_as_delegate.if_you_have.1" />${grantingOrcidName}<@emailMacros.msg "email.added_as_delegate.if_you_have.2" />


<@emailMacros.msg "email.common.kind_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>