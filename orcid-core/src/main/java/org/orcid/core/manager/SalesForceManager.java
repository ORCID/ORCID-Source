package org.orcid.core.manager;

import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.manager.read_only.ManagerReadOnlyBase;
import org.orcid.core.salesforce.model.Badge;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactPermission;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.OrgId;

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

    MemberDetails retrieveDetailsBySlug(String memberSlug, boolean publicOnly);

    MemberDetails retrieveDetails(String memberId);

    MemberDetails retrieveDetails(String memberId, boolean publicOnly);

    MemberDetails retrieveFreshDetails(String memberId);

    List<Contact> retrieveContactsByAccountId(String accountId);

    List<Contact> retrieveFreshContactsByAccountId(String accountId);
    
    List<Contact> retrieveSubMemberContactsByConsortiumId(String consortiumId);
    
    void writeContactsCsv(Writer writer, List<Contact> contacts);
    
    void addOrcidsToContacts(List<Contact> contacts);

    void addAccessInfoToContacts(List<Contact> contacts, String accountId);
    
    List<OrgId> retrieveOrgIdsByAccountId(String accountId);

    List<OrgId> retrieveFreshOrgIdsByAccountId(String accountId);

    void enableAccess(String accountId, List<Contact> contactsList);

    List<String> retrieveAccountIdsByOrcid(String orcid);

    String retrievePrimaryAccountIdByOrcid(String orcid);

    /**
     * 
     * @return the accountId of the member
     */
    String createMember(Member member, Contact initialContact);

    void updateMember(Member member);

    Optional<Member> findBestWebsiteMatch(URL webSiteUrl, Collection<Member> possibleMatches);

    Optional<Member> checkExistingMember(Member member);

    /**
     * 
     * @return the boolean indicating whether submember exists in member
     */
    boolean checkExistingSubMember(Member member, String parentAccountId);
    
    void createOrgId(OrgId orgId);
    
    void removeOrgId(OrgId orgId);

    /**
     * 
     * @return the id of the opportunity
     */
    String createOpportunity(Opportunity opportunity);

    void flagOpportunityAsRemovalRequested(Opportunity opportunity);

    void flagOpportunityAsRemovalNotRequested(Opportunity opportunity);

    void removeOpportunity(Opportunity opportunity);

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

    Map<String, Badge> retrieveBadgesMap();

    List<OrgId> retrieveAllOrgIds();

}
