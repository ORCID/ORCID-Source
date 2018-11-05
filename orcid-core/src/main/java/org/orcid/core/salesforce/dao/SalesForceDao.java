package org.orcid.core.salesforce.dao;

import java.util.List;

import org.orcid.core.salesforce.model.Badge;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactRole;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.OpportunityContactRole;
import org.orcid.core.salesforce.model.OrgId;

/**
 * 
 * @author Will Simpson
 *
 */
public interface SalesForceDao {

    static final String MAIN_CONTACT_ROLE = "Main Contact";

    static final String TECH_LEAD_ROLE = "Tech Lead";

    List<Member> retrieveMembers();

    Member retrieveMember(String accountId);

    List<Member> retrieveMembersByWebsite(String websiteUrl);

    List<Member> retrieveConsortia();

    Consortium retrieveConsortium(String consortiumId);

    MemberDetails retrieveDetails(String memberId, String consortiumLeadId);

    List<Contact> retrieveAllContactsByAccountId(String accountId);

    List<Contact> retrieveContactsWithRolesByAccountId(String accountId);

    List<Contact> retrieveContactsWithRolesByAccountId(String accountId, boolean includeNonCurrent);

    List<ContactRole> retrieveContactRolesByContactIdAndAccountId(String contactId, String accountId);

    String retrievePremiumConsortiumMemberTypeId();

    String retrieveConsortiumMemberRecordTypeId();
    
    List<OrgId> retrieveOrgIdsByAccountId(String accountId);
    
    String createOrgId(OrgId orgId);
    
    void removeOrgId(String salesForceObjectId);

    /**
     * @return The sales force object id, if valid.
     * @throws IllegalArgumentException
     *             if the sales force object id is not the correct format, or
     *             could contain something malicious.
     */
    String validateSalesForceId(String memberId);

    /**
     * 
     * @return the accountId of the member
     */
    String createMember(Member member);

    void updateMember(Member member);

    Opportunity retrieveOpportunity(String opportunityId);

    /**
     * 
     * @return the opportunity id
     */
    String createOpportunity(Opportunity member);

    void updateOpportunity(Opportunity opportunity);

    void removeOpportunity(String opportunityId);

    /**
     * 
     * @return the contact id
     */
    String createContact(Contact contact);

    void updateContact(Contact contact);

    /**
     * 
     * @return the contact role id
     */
    String createContactRole(ContactRole contact);

    void updateContactRole(ContactRole contactRole);

    void removeContactRole(String contactRoleId);

    String createOpportunityContactRole(OpportunityContactRole contactRole);
    
    List<Badge> retrieveBadges();

    String getAccessToken();

}