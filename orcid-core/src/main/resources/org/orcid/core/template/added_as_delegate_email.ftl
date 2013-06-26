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
    In the original email there was what was supposed to be placeholder text for the Granting ORCID User's email address
    but in NotificationManagerImpl.java there was no parameter. Code was added (by Laura, commented out, needs to be
    reviewed) to add this parameter (and suggest that this particular email come from the ORCID user instead of support).
    If all is okay in the the manager, usersEmail@domain.com in the last paragraph below should be changed to something
    like ${grantingOrcidEmail}
-->

Dear ${emailNameForDelegate},

You have been made an Account Delegate by ${grantingOrcidName} with the ORCID iD ${baseUri}/${grantingOrcidValue}. Being made an Account Delegate means that this user has included you in their trusted relationships. As a result you may update and make additions to ${grantingOrcidName}'s ORCID Record.

For a tutorial on the functions that you can perform as an Account Delegate please view http://support.orcid.org/knowledgebase/articles/217659.

If you have questions or concerns about being an Account Delegate, please contact ${grantingOrcidName} at usersEmail@domain.com, or the ORCID Help Desk at support@orcid.org.


Kind Regards,
The ORCID Team
support@orcid.org 
${baseUri}

You have received this e-mail as a service announcement related to your ORCID Account. 