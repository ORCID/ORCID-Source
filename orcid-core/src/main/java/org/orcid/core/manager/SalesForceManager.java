/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.manager.read_only.ManagerReadOnlyBase;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactPermission;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.Opportunity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface SalesForceManager extends ManagerReadOnlyBase {

    List<Member> retrieveMembers();

    Member retrieveMember(String accountId);

    List<Member> retrieveConsortia();

    Consortium retrieveConsortium(String consortiumId);

    MemberDetails retrieveDetailsBySlug(String memberSlug);

    MemberDetails retrieveDetails(String memberId);

    MemberDetails retrieveFreshDetails(String memberId);

    List<Contact> retrieveContactsByAccountId(String accountId);
    
    List<Contact> retrieveFreshContactsByAccountId(String accountId);

    void addOrcidsToContacts(List<Contact> contacts);

    void addAccessInfoToContacts(List<Contact> contacts, String accountId);

    void enableAccess(String accountId, List<Contact> contactsList);

    String retrieveAccountIdByOrcid(String orcid);

    /**
     * 
     * @return the accountId of the member
     */
    String createMember(Member member);

    void updateMember(Member member);

    Optional<Member> findBestWebsiteMatch(URL webSiteUrl, Collection<Member> possibleMatches);
    
    Optional<Member> checkExistingMember(Member member);
    
    /**
     * 
     * @return the boolean indicating whether submember exists in member
     */
    boolean checkExistingSubMember(Member member, String parentAccountId);
    
    /**
     * 
     * @return the id of the opportunity
     */
    String createOpportunity(Opportunity opportunity);

    void flagOpportunityAsClosed(String opportunityId);

    void createContact(Contact contact);

    void updateContacts(Collection<Contact> contacts);

    void removeContact(Contact contact);

    void removeContactRole(Contact contact);

    /**
     * Clear caches
     * 
     */
    void evictAll();

    List<ContactPermission> calculateContactPermissions(Collection<Contact> contacts);

    /**
     * @throws OrcidUnauthorizedException
     */
    void checkContactUpdatePermissions(Collection<Contact> existingContacts, Collection<Contact> updatedContacts);

}
