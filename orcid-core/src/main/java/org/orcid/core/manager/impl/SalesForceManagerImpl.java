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
package org.orcid.core.manager.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.read_only.impl.ManagerReadOnlyBaseImpl;
import org.orcid.core.salesforce.cache.MemberDetailsCacheKey;
import org.orcid.core.salesforce.dao.SalesForceDao;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactPermission;
import org.orcid.core.salesforce.model.ContactRole;
import org.orcid.core.salesforce.model.ContactRoleType;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.SlugUtils;
import org.orcid.core.salesforce.model.SubMember;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.persistence.dao.SalesForceConnectionDao;
import org.orcid.persistence.jpa.entities.SalesForceConnectionEntity;
import org.orcid.utils.DateUtils;
import org.orcid.utils.ReleaseNameUtils;

import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceManagerImpl extends ManagerReadOnlyBaseImpl implements SalesForceManager {

    private static final String OPPORTUNITY_CLOSED_LOST = "Closed Lost";

    private static final String OPPORTUNITY_TYPE = "New";

    private static final String OPPORTUNITY_INITIAL_STAGE_NAME = "Invoice Paid";

    private static final String OPPORTUNITY_NAME = "Opportunity from registry";

    private static final Pattern SUBDOMAIN_PATTERN = Pattern.compile("^www\\.");

    @Resource(name = "salesForceMembersListCache")
    private SelfPopulatingCache salesForceMembersListCache;

    @Resource(name = "salesForceMemberCache")
    private SelfPopulatingCache salesForceMemberCache;

    @Resource(name = "salesForceMemberDetailsCache")
    private SelfPopulatingCache salesForceMemberDetailsCache;

    @Resource(name = "salesForceConsortiaListCache")
    private SelfPopulatingCache salesForceConsortiaListCache;

    @Resource(name = "salesForceConsortiumCache")
    private SelfPopulatingCache salesForceConsortiumCache;

    @Resource(name = "salesForceContactsCache")
    private SelfPopulatingCache salesForceContactsCache;

    @Resource
    private SalesForceDao salesForceDao;

    @Resource
    private SalesForceConnectionDao salesForceConnectionDao;

    @Resource
    private EmailManager emailManager;

    @Resource
    private SourceManager sourceManager;

    private String releaseName = ReleaseNameUtils.getReleaseName();

    private String premiumConsortiumMemberTypeId;

    private String consortiumMemberRecordTypeId;

    @SuppressWarnings("unchecked")
    @Override
    public List<Member> retrieveMembers() {
        return (List<Member>) salesForceMembersListCache.get(releaseName).getObjectValue();
    }

    @Override
    public Member retrieveMember(String accountId) {
        return (Member) salesForceMemberCache.get(accountId).getObjectValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Member> retrieveConsortia() {
        return (List<Member>) salesForceConsortiaListCache.get(releaseName).getObjectValue();
    }

    @Override
    public Consortium retrieveConsortium(String consortiumId) {
        return (Consortium) salesForceConsortiumCache.get(consortiumId).getObjectValue();
    }

    @Override
    public MemberDetails retrieveDetailsBySlug(String memberSlug) {
        String memberId = SlugUtils.extractIdFromSlug(memberSlug);
        return retrieveDetails(memberId);
    }

    @Override
    public MemberDetails retrieveDetails(String memberId) {
        Member salesForceMember = retrieveMember(memberId);
        if (salesForceMember != null) {
            MemberDetails details = (MemberDetails) salesForceMemberDetailsCache
                    .get(new MemberDetailsCacheKey(memberId, salesForceMember.getConsortiumLeadId(), releaseName)).getObjectValue();
            details.setMember(salesForceMember);
            details.setSubMembers(findSubMembers(memberId));
            return details;
        }
        throw new IllegalArgumentException("No member details found for " + memberId);
    }

    @Override
    public MemberDetails retrieveFreshDetails(String memberId) {
        salesForceMemberCache.remove(memberId);
        removeMemberDetailsFromCache(memberId);
        return retrieveDetails(memberId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Contact> retrieveContactsByAccountId(String accountId) {
        return (List<Contact>) salesForceContactsCache.get(accountId).getObjectValue();
    }

    @Override
    public List<Contact> retrieveFreshContactsByAccountId(String accountId) {
        salesForceContactsCache.remove(accountId);
        return retrieveContactsByAccountId(accountId);
    }

    @Override
    public void addOrcidsToContacts(List<Contact> contacts) {
        List<String> emails = contacts.stream().map(c -> c.getEmail()).collect(Collectors.toList());
        if (!emails.isEmpty()) {
            Map<String, String> emailsToOrcids = emailManager.findIdsByEmails(emails);
            contacts.stream().forEach(c -> {
                c.setOrcid(emailsToOrcids.get(c.getEmail()));
            });
        }
    }

    @Override
    public void addAccessInfoToContacts(List<Contact> contacts, String accountId) {
        for (Contact contact : contacts) {
            String orcid = contact.getOrcid();
            if (orcid != null && salesForceConnectionDao.findByOrcidAndAccountId(orcid, accountId) != null) {
                contact.setSelfServiceEnabled(true);
            } else {
                contact.setSelfServiceEnabled(false);
            }
        }
    }

    @Override
    public void enableAccess(String accountId, List<Contact> contactsList) {
        contactsList.forEach(c -> {
            String orcid = c.getOrcid();
            if (orcid == null) {
                return;
            }
            SalesForceConnectionEntity connection = salesForceConnectionDao.findByOrcidAndAccountId(orcid, accountId);
            if (connection == null) {
                connection = new SalesForceConnectionEntity();
                connection.setOrcid(orcid);
                connection.setSalesForceAccountId(accountId);
                connection.setEmail(c.getEmail());
                salesForceConnectionDao.persist(connection);
            }
            c.setSelfServiceEnabled(true);
        });
    }

    @Override
    public String retrieveAccountIdByOrcid(String orcid) {
        SalesForceConnectionEntity connection = salesForceConnectionDao.findByOrcid(orcid);
        return connection != null ? connection.getSalesForceAccountId() : null;
    }
    
    @Override
    public Optional<Member> checkExistingMember(Member member) {
        URL websiteUrl = member.getWebsiteUrl();
        Optional<Member> firstExistingMember = findBestWebsiteMatch(websiteUrl);
        return firstExistingMember;
    }
    
    @Override
    public boolean checkExistingSubMember(Member member, String parentAccountId) {
        boolean subMemberExists = false;
        URL websiteUrl = member.getWebsiteUrl();
        Optional<Member> firstExistingMember = findBestWebsiteMatch(websiteUrl);
        
        if(firstExistingMember.isPresent()){
            String subMemberAcccountId = firstExistingMember.get().getId();
            MemberDetails memberDetails = retrieveDetails(parentAccountId);
            subMemberExists = memberDetails.getSubMembers().stream().anyMatch(s -> subMemberAcccountId.equals(s.getOpportunity().getTargetAccountId()));  
        } 
        
        return subMemberExists;
    }


    @Override
    public String createMember(Member member) {
        Opportunity opportunity = new Opportunity();
        URL websiteUrl = member.getWebsiteUrl();
        Optional<Member> firstExistingMember = findBestWebsiteMatch(websiteUrl);
        String accountId = null;
        if (firstExistingMember.isPresent()) {
            accountId = firstExistingMember.get().getId();
        } else {
            accountId = salesForceDao.createMember(member);
        }
        opportunity.setTargetAccountId(accountId);
        String consortiumLeadId = retrieveAccountIdByOrcid(sourceManager.retrieveRealUserOrcid());
        opportunity.setConsortiumLeadId(consortiumLeadId);
        opportunity.setType(OPPORTUNITY_TYPE);
        opportunity.setMemberType(getPremiumConsortiumMemberTypeId());
        opportunity.setStageName(OPPORTUNITY_INITIAL_STAGE_NAME);
        opportunity.setCloseDate(calculateCloseDate());
        opportunity.setMembershipStartDate(calculateMembershipStartDate());
        opportunity.setMembershipEndDate(calculateMembershipEndDate());
        opportunity.setRecordTypeId(getConsortiumMemberRecordTypeId());
        opportunity.setName(OPPORTUNITY_NAME);
        createOpportunity(opportunity);
        removeMemberDetailsFromCache(consortiumLeadId);
        salesForceConsortiumCache.remove(consortiumLeadId);
        return accountId;
    }

    private Optional<Member> findBestWebsiteMatch(URL webSiteUrl) {
        String host = webSiteUrl.getHost();
        String hostWithoutSubdomain = SUBDOMAIN_PATTERN.matcher(host).replaceFirst("");
        return findBestWebsiteMatch(webSiteUrl, salesForceDao.retrieveMembersByWebsite(hostWithoutSubdomain));
    }

    @Override
    public Optional<Member> findBestWebsiteMatch(URL webSiteUrl, Collection<Member> possibleMatches) {
        // Check exact match
        Optional<Member> match = findExactMatch(webSiteUrl, possibleMatches);
        if (match.isPresent()) {
            return match;
        }
        // Check without protocol
        match = findMatchWithoutProtocol(webSiteUrl, possibleMatches);
        if (match.isPresent()) {
            return match;
        }
        // Check just the host
        match = findMatchWithJustHost(webSiteUrl, possibleMatches);
        if (match.isPresent()) {
            return match;
        }
        // Check same IP address, by using URL.equals(URL) method.
        match = findMatchUsingIp(webSiteUrl, possibleMatches);
        if (match.isPresent()) {
            return match;
        }
        return Optional.<Member> empty();
    }

    private Optional<Member> findExactMatch(URL webSiteUrl, Collection<Member> possibleMatches) {
        Optional<Member> match = possibleMatches.stream().filter(m -> {
            return webSiteUrl.toString().equals(m.getWebsiteUrl().toString());
        }).findFirst();
        return match;
    }

    private Optional<Member> findMatchWithoutProtocol(URL webSiteUrl, Collection<Member> possibleMatches) {
        Optional<Member> match;
        match = possibleMatches.stream().filter(m -> {
            String effectiveUrl = buildUrlWithoutProtocol(webSiteUrl);
            URL memberUrl = m.getWebsiteUrl();
            String effectiveMemberUrl = buildUrlWithoutProtocol(memberUrl);
            return effectiveUrl.toString().equals(effectiveMemberUrl.toString());
        }).findFirst();
        return match;
    }

    private String buildUrlWithoutProtocol(URL webSiteUrl) {
        return webSiteUrl.getHost() + '/' + StringUtils.defaultString(webSiteUrl.getPath()) + '?' + StringUtils.defaultString(webSiteUrl.getQuery());
    }

    private Optional<Member> findMatchWithJustHost(URL webSiteUrl, Collection<Member> possibleMatches) {
        Optional<Member> match;
        match = possibleMatches.stream().filter(m -> {
            return webSiteUrl.getHost().equals(m.getWebsiteUrl().getHost());
        }).findFirst();
        return match;
    }

    private Optional<Member> findMatchUsingIp(URL webSiteUrl, Collection<Member> possibleMatches) {
        Optional<Member> match;
        match = possibleMatches.stream().filter(m -> {
            return webSiteUrl.equals(m.getWebsiteUrl());
        }).findFirst();
        return match;
    }

    private String calculateCloseDate() {
        return DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(new Date()).toXMLFormat();
    }

    private String calculateMembershipStartDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return String.format("%s-01-01", year);
    }

    private String calculateMembershipEndDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return String.format("%s-12-31", year);
    }

    private String getPremiumConsortiumMemberTypeId() {
        if (premiumConsortiumMemberTypeId == null) {
            premiumConsortiumMemberTypeId = salesForceDao.retrievePremiumConsortiumMemberTypeId();
        }
        return premiumConsortiumMemberTypeId;
    }

    private String getConsortiumMemberRecordTypeId() {
        if (consortiumMemberRecordTypeId == null) {
            consortiumMemberRecordTypeId = salesForceDao.retrieveConsortiumMemberRecordTypeId();
        }
        return consortiumMemberRecordTypeId;
    }

    @Override
    public void updateMember(Member member) {
        salesForceDao.updateMember(member);
        String memberId = member.getId();
        salesForceMemberCache.remove(memberId);
        removeMemberDetailsFromCache(memberId);
        String consortiumLeadId = member.getConsortiumLeadId();
        if (consortiumLeadId != null) {
            removeMemberDetailsFromCache(consortiumLeadId);
            salesForceConsortiumCache.remove(consortiumLeadId);
        }
    }

    @Override
    public String createOpportunity(Opportunity opportunity) {
        String accountId = salesForceDao.createOpportunity(opportunity);
        return accountId;
    }

    @Override
    public void flagOpportunityAsClosed(String opportunityId) {
        String accountId = retrieveAccountIdByOrcid(sourceManager.retrieveRealUserOrcid());
        MemberDetails memberDetails = retrieveDetails(accountId);
        boolean authorized = memberDetails.getSubMembers().stream().anyMatch(s -> opportunityId.equals(s.getOpportunity().getId()));
        if (authorized) {
            Opportunity opportunity = new Opportunity();
            opportunity.setId(opportunityId);
            opportunity.setStageName(OPPORTUNITY_CLOSED_LOST);
            salesForceDao.updateOpportunity(opportunity);
        }
        salesForceMembersListCache.removeAll();
        removeMemberDetailsFromCache(accountId);
        salesForceConsortiumCache.remove(accountId);
    }

    @Override
    public void createContact(Contact contact) {
        String accountId = retrieveAccountIdByOrcid(sourceManager.retrieveRealUserOrcid());
        contact.setAccountId(accountId);
        if (StringUtils.isBlank(contact.getEmail())) {
            String contactOrcid = contact.getOrcid();
            Email primaryEmail = emailManager.getEmails(contactOrcid).getEmails().stream().filter(e -> e.isPrimary()).findFirst().get();
            contact.setEmail(primaryEmail.getEmail());
        }
        List<Contact> existingContacts = salesForceDao.retrieveAllContactsByAccountId(accountId);
        Optional<Contact> existingContact = existingContacts.stream().filter(c -> {
            if ((contact.getOrcid() != null && contact.getOrcid().equals(c.getOrcid())) || (contact.getEmail() != null && contact.getEmail().equals(c.getEmail()))) {
                return true;
            }
            return false;
        }).findFirst();
        String contactId = existingContact.isPresent() ? existingContact.get().getId() : salesForceDao.createContact(contact);
        ContactRole contactRole = new ContactRole();
        contactRole.setContactId(contactId);
        contactRole.setRoleType(ContactRoleType.OTHER_CONTACT);
        contactRole.setAccountId(contact.getAccountId());
        salesForceDao.createContactRole(contactRole);
        if (salesForceConnectionDao.findByOrcidAndAccountId(contact.getOrcid(), accountId) == null) {
            salesForceConnectionDao.persist(new SalesForceConnectionEntity(contact.getOrcid(), contact.getEmail(), accountId));
        }
        salesForceContactsCache.remove(accountId);
    }

    @Override
    public void removeContact(Contact contact) {
        String accountId = retrieveAccountIdByOrcid(sourceManager.retrieveRealUserOrcid());
        List<Contact> existingContacts = retrieveContactsByAccountId(accountId);
        List<Contact> updatedList = new ArrayList<>(1);
        updatedList.add(contact);
        checkContactUpdatePermissions(existingContacts, updatedList);
        removeContactRole(contact);
        removeContactAccess(contact);
        salesForceContactsCache.remove(accountId);
    }

    private void removeContactAccess(Contact contact) {
        String accountId = retrieveAccountIdByOrcid(sourceManager.retrieveRealUserOrcid());
        String contactOrcid = contact.getOrcid();
        if (contactOrcid != null) {
            SalesForceConnectionEntity connection = salesForceConnectionDao.findByOrcidAndAccountId(contactOrcid, accountId);
            if (connection != null) {
                salesForceConnectionDao.remove(connection);
            }
        }
    }

    @Override
    public void removeContactRole(Contact contact) {
        String accountId = retrieveAccountIdByOrcid(sourceManager.retrieveRealUserOrcid());
        List<ContactRole> contactRoles = salesForceDao.retrieveContactRolesByContactIdAndAccountId(contact.getId(), accountId);
        contactRoles.stream().filter(r -> r.getId().equals(contact.getRole().getId())).findFirst().ifPresent(r -> salesForceDao.removeContactRole(r.getId()));
    }

    /**
     * 
     * This is a package private method because no user permissions are checked.
     * 
     * @see SalesForceManagerImpl#updateContacts(Collection), which does check
     *      user permissions.
     */
    void updateContact(Contact contact) {
        String accountId = retrieveAccountIdByOrcid(sourceManager.retrieveRealUserOrcid());
        removeContactRole(contact);
        ContactRole contactRole = new ContactRole();
        contactRole.setAccountId(accountId);
        contactRole.setContactId(contact.getId());
        contactRole.setRoleType(contact.getRole().getRoleType());
        contactRole.setVotingContact(contact.getRole().isVotingContact());
        String contactRoleId = salesForceDao.createContactRole(contactRole);
        contact.getRole().setId(contactRoleId);
        salesForceContactsCache.remove(accountId);
    }

    @Override
    public void updateContacts(Collection<Contact> contacts) {
        String accountId = retrieveAccountIdByOrcid(sourceManager.retrieveRealUserOrcid());
        List<Contact> existingContacts = salesForceDao.retrieveContactsWithRolesByAccountId(accountId);
        // Ensure contact ORCID iDs are correct by getting from registry DB.
        addOrcidsToContacts(existingContacts);
        checkContactUpdatePermissions(existingContacts, contacts);
        // Need to remove roles with validation rules in SF first
        existingContacts.stream().filter(c -> {
            return ContactRoleType.MAIN_CONTACT.equals(c.getRole().getRoleType()) || ContactRoleType.AGREEMENT_SIGNATORY.equals(c.getRole().getRoleType())
                    || c.getRole().isVotingContact();
        }).forEach(c -> removeContactRole(c));
        contacts.stream().forEach(c -> updateContact(c));
        // Update access control list for self-service
        updateAccessControl(contacts, accountId);
        salesForceContactsCache.remove(accountId);
    }

    private void updateAccessControl(Collection<Contact> contacts, String accountId) {
        List<SalesForceConnectionEntity> existingConnections = salesForceConnectionDao.findByAccountId(accountId);
        List<String> contactOrcids = contacts.stream().map(c -> c.getOrcid()).collect(Collectors.toList());
        // Remove any connections no longer in the contacts list
        existingConnections.stream().filter(c -> !contactOrcids.contains(c.getOrcid())).forEach(c -> salesForceConnectionDao.remove(c));
        // Give access to any contacts that do not already have access
        List<String> existingConnectionsOrcids = existingConnections.stream().map(c -> c.getOrcid()).collect(Collectors.toList());
        contacts.stream().filter(c -> c.getOrcid() != null && !existingConnectionsOrcids.contains(c.getOrcid()))
                .forEach(c -> salesForceConnectionDao.persist(new SalesForceConnectionEntity(c.getOrcid(), c.getEmail(), accountId)));
    }

    @Override
    public void evictAll() {
        evictLists();
        salesForceMemberCache.removeAll();
        salesForceMemberDetailsCache.removeAll();
        salesForceConsortiumCache.removeAll();
        salesForceContactsCache.removeAll();
        premiumConsortiumMemberTypeId = null;
        consortiumMemberRecordTypeId = null;
    }

    private void evictLists() {
        salesForceMembersListCache.removeAll();
        salesForceConsortiaListCache.removeAll();
    }

    private void removeMemberDetailsFromCache(String memberId) {
        salesForceMemberDetailsCache.remove(new MemberDetailsCacheKey(memberId, null, ReleaseNameUtils.getReleaseName()));
    }

    private List<SubMember> findSubMembers(String memberId) {
        Consortium consortium = retrieveConsortium(memberId);
        if (consortium != null) {
            List<SubMember> subMembers = consortium.getOpportunities().stream().map(o -> {
                SubMember subMember = new SubMember();
                subMember.setOpportunity(o);
                subMember.setSlug(SlugUtils.createSlug(o.getTargetAccountId(), o.getAccountName()));
                return subMember;
            }).collect(Collectors.toList());
            return subMembers;
        }
        return Collections.emptyList();
    }

    @Override
    public List<ContactPermission> calculateContactPermissions(Collection<Contact> contacts) {
        String currentUser = sourceManager.retrieveRealUserOrcid();
        boolean isCurrentUserSuperContact = contacts.stream().anyMatch(c -> currentUser.equals(c.getOrcid()) && isSuperContact(c));
        List<ContactPermission> permissions = new ArrayList<>();
        for (Contact contact : contacts) {
            ContactPermission permission = new ContactPermission();
            permission.setContactRoleId(contact.getRole().getId());
            if (isSuperContact(contact) || isVotingContact(contact)) {
                permission.setAllowedEdit(isCurrentUserSuperContact);
            } else {
                permission.setAllowedEdit(true);
            }
            permissions.add(permission);
        }
        return permissions;
    }

    @Override
    public void checkContactUpdatePermissions(Collection<Contact> existingContacts, Collection<Contact> updatedContacts) {
        List<ContactPermission> permissions = calculateContactPermissions(existingContacts);
        Map<String, ContactPermission> permissionsMap = ContactPermission.mapByContactRoleId(permissions);
        Map<String, Contact> existingContactsMap = Contact.mapByContactRoleId(existingContacts);
        for (Contact updatedContact : updatedContacts) {
            String updatedContactRoleId = updatedContact.getRole().getId();
            Contact existingContact = existingContactsMap.get(updatedContactRoleId);
            if (existingContact == null) {
                throw new IllegalStateException("Should be able to update a non-existent contact");
            }
            if (contactChanged(existingContact, updatedContact)) {
                ContactPermission permission = permissionsMap.get(existingContact.getRole().getId());
                if (permission == null) {
                    throw new IllegalStateException("Can't find permissions for existing contact");
                }
                if (!permission.isAllowedEdit()) {
                    throw new OrcidUnauthorizedException("Insufficient permissions to update contact");
                }
            }
        }
    }

    private boolean contactChanged(Contact existingContact, Contact updatedContact) {
        ContactRole existingRole = existingContact.getRole();
        ContactRole updatedRole = updatedContact.getRole();
        return !existingRole.getRoleType().equals(updatedRole.getRoleType()) || ObjectUtils.notEqual(existingRole.isVotingContact(), updatedRole.isVotingContact());
    }

    private boolean isSuperContact(Contact c) {
        return ContactRoleType.MAIN_CONTACT.equals(c.getRole().getRoleType()) || ContactRoleType.AGREEMENT_SIGNATORY.equals(c.getRole().getRoleType());
    }

    private boolean isVotingContact(Contact contact) {
        return Boolean.TRUE.equals(contact.getRole().isVotingContact());
    }

}
