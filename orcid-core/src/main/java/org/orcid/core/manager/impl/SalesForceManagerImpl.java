package org.orcid.core.manager.impl;

import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.orcid.core.cache.GenericCacheManager;
import org.orcid.core.cache.OrcidString;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.read_only.impl.ManagerReadOnlyBaseImpl;
import org.orcid.core.salesforce.cache.MemberDetailsCacheKey;
import org.orcid.core.salesforce.dao.SalesForceDao;
import org.orcid.core.salesforce.model.Badge;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactPermission;
import org.orcid.core.salesforce.model.ContactRole;
import org.orcid.core.salesforce.model.ContactRoleType;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.OpportunityContactRole;
import org.orcid.core.salesforce.model.OrgId;
import org.orcid.core.salesforce.model.SlugUtils;
import org.orcid.core.salesforce.model.SubMember;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.persistence.dao.SalesForceConnectionDao;
import org.orcid.persistence.jpa.entities.SalesForceConnectionEntity;
import org.orcid.utils.DateUtils;
import org.orcid.utils.ReleaseNameUtils;

import com.google.common.base.Functions;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceManagerImpl extends ManagerReadOnlyBaseImpl implements SalesForceManager {

    private static final String OPPORTUNITY_CLOSED_LOST = "Closed Lost";

    private static final String OPPORTUNITY_TYPE = "New";

    private static final String OPPORTUNITY_INITIAL_STAGE_NAME = "Negotiation/Review";

    private static final String OPPORTUNITY_PUBLIC_STAGE_NAME = "Invoice Paid";

    private static final String OPPORTUNITY_NAME = "Opportunity from registry";

    private static final Pattern SUBDOMAIN_PATTERN = Pattern.compile("^www\\.");

    @Resource(name = "salesForceMembersListCache")
    private Cache<String, List<Member>> salesForceMembersListCache;

    @Resource(name = "salesForceMemberCache")
    private Cache<String, Member> salesForceMemberCache;

    @Resource(name = "salesForceMemberDetailsCache")
    private Cache<MemberDetailsCacheKey, MemberDetails> salesForceMemberDetailsCache;

    @Resource(name = "salesForceConsortiaListCache")
    private Cache<String, List<Member>> salesForceConsortiaListCache;

    @Resource(name = "salesForceConsortiumCache")
    private Cache<String, Consortium> salesForceConsortiumCache;

    @Resource(name = "salesForceContactsCache")
    private Cache<String, List<Contact>> salesForceContactsCache;

    @Resource
    private SalesForceDao salesForceDao;

    @Resource(name = "salesForceConnectionEntityCacheManager")
    private GenericCacheManager<OrcidString, List<SalesForceConnectionEntity>> salesForceConnectionEntityCacheManager;

    @Resource
    private SalesForceConnectionDao salesForceConnectionDao;

    @Resource
    private EmailManager emailManager;

    @Resource
    private SourceManager sourceManager;
    
    @Resource
    private OrcidUrlManager orcidUrlManager;
    
    @Resource
    private LocaleManager localeManager;
    
    private String releaseName = ReleaseNameUtils.getReleaseName();

    private String premiumConsortiumMemberTypeId;

    private String consortiumMemberRecordTypeId;
    
    private Map<String, Badge> badgesMap;

    @SuppressWarnings("unchecked")
    @Override
    public List<Member> retrieveMembers() {
        return salesForceMembersListCache.get(releaseName);
    }

    @Override
    public Member retrieveMember(String accountId) {
        return salesForceMemberCache.get(accountId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Member> retrieveConsortia() {
        return salesForceConsortiaListCache.get(releaseName);
    }

    @Override
    public Consortium retrieveConsortium(String consortiumId) {
        return salesForceConsortiumCache.get(consortiumId);
    }

    @Override
    public MemberDetails retrieveDetailsBySlug(String memberSlug) {
        return retrieveDetailsBySlug(memberSlug, false);
    }

    @Override
    public MemberDetails retrieveDetailsBySlug(String memberSlug, boolean publicOnly) {
        String memberId = SlugUtils.extractIdFromSlug(memberSlug);
        return retrieveDetails(memberId, publicOnly);
    }

    @Override
    public MemberDetails retrieveDetails(String memberId) {
        return retrieveDetails(memberId, false);
    }

    @Override
    public MemberDetails retrieveDetails(String memberId, boolean publicOnly) {
        Member salesForceMember = retrieveMember(memberId);
        if (salesForceMember != null) {
            MemberDetails details = salesForceMemberDetailsCache
                    .get(new MemberDetailsCacheKey(memberId, salesForceMember.getConsortiumLeadId(), releaseName));
            details.setMember(salesForceMember);
            List<SubMember> allSubMembers = findSubMembers(memberId);
            if (publicOnly) {
                details.setSubMembers(
                        allSubMembers.stream().filter(m -> OPPORTUNITY_PUBLIC_STAGE_NAME.equals(m.getOpportunity().getStageName())).collect(Collectors.toList()));
            } else {
                details.setSubMembers(allSubMembers);
            }
            return details;
        }
        return null;
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
        return salesForceContactsCache.get(accountId);
    }

    @Override
    public List<Contact> retrieveFreshContactsByAccountId(String accountId) {
        salesForceContactsCache.remove(accountId);
        return retrieveContactsByAccountId(accountId);
    }

    @Override
    public List<Contact> retrieveSubMemberContactsByConsortiumId(String consortiumId) {
        return findSubMembers(consortiumId).stream().flatMap(s -> {
            String subMemberAccountid = s.getOpportunity().getTargetAccountId();
            Member member = retrieveMember(subMemberAccountid);
            List<Contact> contacts = retrieveContactsByAccountId(subMemberAccountid);
            return contacts.stream().map(c -> {
                c.setMember(member);
                return c;
            });
        }).collect(Collectors.toList());
    }
    
    @Override
    public void writeContactsCsv(Writer writer, List<Contact> contacts) {
        @SuppressWarnings("resource")
        CSVWriter csvWriter = new CSVWriter(writer);
        csvWriter.writeNext(buildHeader());
        for (Contact contact : contacts) {
            ContactRoleType roleType = contact.getRole().getRoleType();
            String orcid = contact.getOrcid() != null ? orcidUrlManager.getBaseUrl() + "/" + contact.getOrcid() : "";
            String[] line = new String[] { contact.getMember().getPublicDisplayName(), contact.getName(), contact.getEmail(), orcid,
                    String.valueOf(contact.getRole().isVotingContact()), localeManager.resolveMessage(roleType.getClass().getName() + "." + roleType.name()) };
            csvWriter.writeNext(line);
        }
    }

    private String[] buildHeader() {
        return new String[] { localeManager.resolveMessage("manage_consortium.contacts_member_name"),
                localeManager.resolveMessage("manage_consortium.contacts_contact_name"), localeManager.resolveMessage("manage_consortium.contacts_contact_email"),
                localeManager.resolveMessage("manage_consortium.contacts_contact_orcid"), localeManager.resolveMessage("manage_consortium.contacts_voting_contact"),
                localeManager.resolveMessage("manage_consortium.contacts_role") };
    }

    @Override
    public void addOrcidsToContacts(List<Contact> contacts) {
        List<String> emails = contacts.stream().map(c -> c.getEmail()).filter(Objects::nonNull).collect(Collectors.toList());
        if (!emails.isEmpty()) {
            Map<String, String> emailsToOrcids = emailManager.findIdsByEmails(emails);
            contacts.stream().forEach(c -> {
                String email = c.getEmail();
                if (email != null) {
                    c.setOrcid(emailsToOrcids.get(email));
                }
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

    @SuppressWarnings("unchecked")
    @Override
    public List<OrgId> retrieveOrgIdsByAccountId(String accountId) {
        // return (List<OrgId>)
        // salesForceOrgIdsCache.get(accountId).getObjectValue();
        return salesForceDao.retrieveOrgIdsByAccountId(accountId);
    }

    @Override
    public List<OrgId> retrieveFreshOrgIdsByAccountId(String accountId) {
        // salesForceOrgIdsCache.remove(accountId);
        return retrieveOrgIdsByAccountId(accountId);
    }

    @Override
    public void enableAccess(String accountId, List<Contact> contactsList) {
        contactsList.forEach(c -> {
            String orcid = c.getOrcid();
            if (orcid == null) {
                return;
            }
            addSalesForceConnection(accountId, c);
            c.setSelfServiceEnabled(true);
        });
    }

    @Override
    public List<String> retrieveAccountIdsByOrcid(String orcid) {
        List<SalesForceConnectionEntity> connections = salesForceConnectionEntityCacheManager.retrieve(new OrcidString(orcid));
        return connections.stream().map(c -> c.getSalesForceAccountId()).collect(Collectors.toList());
    }

    @Override
    public String retrievePrimaryAccountIdByOrcid(String orcid) {
        List<SalesForceConnectionEntity> connections = salesForceConnectionEntityCacheManager.retrieve(new OrcidString(orcid));
        return connections.stream().filter(c -> c.isPrimary()).findFirst().get().getSalesForceAccountId();
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

        if (firstExistingMember.isPresent()) {
            String subMemberAcccountId = firstExistingMember.get().getId();
            MemberDetails memberDetails = retrieveDetails(parentAccountId);
            subMemberExists = memberDetails.getSubMembers().stream().anyMatch(s -> subMemberAcccountId.equals(s.getOpportunity().getTargetAccountId()));
        }

        return subMemberExists;
    }

    @Override
    public String createMember(Member member, Contact initialContact) {
        String consortiumLeadId = member.getConsortiumLeadId();
        Member consortium = retrieveMember(consortiumLeadId);
        String consortiumOwnerId = consortium.getOwnerId();
        Opportunity opportunity = new Opportunity();
        URL websiteUrl = member.getWebsiteUrl();
        Optional<Member> firstExistingMember = findBestWebsiteMatch(websiteUrl);
        String accountId = null;

        if (firstExistingMember.isPresent()) {
            accountId = firstExistingMember.get().getId();
        } else {
            member.setParentId(consortiumLeadId);
            member.setOwnerId(consortiumOwnerId);
            member.setCountry(consortium.getCountry());
            accountId = salesForceDao.createMember(member);
        }
        opportunity.setOwnerId(consortiumOwnerId);
        opportunity.setTargetAccountId(accountId);
        opportunity.setConsortiumLeadId(consortiumLeadId);
        opportunity.setType(OPPORTUNITY_TYPE);
        opportunity.setMemberType(getPremiumConsortiumMemberTypeId());
        opportunity.setStageName(OPPORTUNITY_INITIAL_STAGE_NAME);
        opportunity.setCloseDate(calculateCloseDate());
        opportunity.setMembershipStartDate(consortium.getLastMembershipStartDate());
        opportunity.setMembershipEndDate(consortium.getLastMembershipEndDate());
        opportunity.setRecordTypeId(getConsortiumMemberRecordTypeId());
        opportunity.setName(OPPORTUNITY_NAME);
        String opportunityId = createOpportunity(opportunity);
        initialContact.setAccountId(accountId);
        createOpportunityContact(initialContact, opportunityId);
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
    public Map<String, Badge> retrieveBadgesMap() {
        if (badgesMap == null) {
            badgesMap = salesForceDao.retrieveBadges().stream().collect(Collectors.toMap(Badge::getId, Functions.identity()));
        }
        return badgesMap;
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
    public void createOrgId(OrgId orgId) {
        String accountId = orgId.getAccountId();
        List<OrgId> existingOrgIds = salesForceDao.retrieveOrgIdsByAccountId(accountId);
        Optional<OrgId> existingOrgId = existingOrgIds.stream().filter(o -> {
            if (orgId.getOrgIdType().equals(o.getOrgIdType()) && orgId.getOrgIdValue().equals(o.getOrgIdValue())) {
                return true;
            }
            return false;
        }).findFirst();
        if (!existingOrgId.isPresent()) {
            salesForceDao.createOrgId(orgId);
        }
        salesForceContactsCache.remove(accountId);
    }

    @Override
    public void removeOrgId(OrgId orgId) {
        salesForceDao.removeOrgId(orgId.getId());
        removeMemberDetailsFromCache(orgId.getAccountId());
    }

    @Override
    public String createOpportunity(Opportunity opportunity) {
        String accountId = salesForceDao.createOpportunity(opportunity);
        return accountId;
    }

    @Override
    public void flagOpportunityAsRemovalRequested(Opportunity opportunity) {
        String userOrcid = sourceManager.retrieveRealUserOrcid();
        String consortiumLeadId = opportunity.getConsortiumLeadId();
        checkOpportunityUpdatePermissions(opportunity);
        Opportunity updatedOpportunity = new Opportunity();
        updatedOpportunity.setId(opportunity.getId());
        updatedOpportunity.setRemovalRequested(true);
        updatedOpportunity.setNextStep("Removal requested by " + userOrcid);
        salesForceDao.updateOpportunity(updatedOpportunity);
        salesForceMembersListCache.clear();
        removeMemberDetailsFromCache(consortiumLeadId);
        salesForceConsortiumCache.remove(consortiumLeadId);
    }

    @Override
    public void flagOpportunityAsRemovalNotRequested(Opportunity opportunity) {
        String userOrcid = sourceManager.retrieveRealUserOrcid();
        checkOpportunityUpdatePermissions(opportunity);
        Opportunity updatedOpportunity = new Opportunity();
        updatedOpportunity.setId(opportunity.getId());
        updatedOpportunity.setRemovalRequested(false);
        updatedOpportunity.setNextStep("Removal request cancelled by " + userOrcid);
        salesForceDao.updateOpportunity(updatedOpportunity);
        salesForceMembersListCache.clear();
        String consortiumLeadId = opportunity.getConsortiumLeadId();
        removeMemberDetailsFromCache(consortiumLeadId);
        salesForceConsortiumCache.remove(consortiumLeadId);
    }

    @Override
    public void removeOpportunity(Opportunity opportunity) {
        checkOpportunityUpdatePermissions(opportunity);
        salesForceDao.removeOpportunity(opportunity.getId());
        String consortiumLeadId = opportunity.getConsortiumLeadId();
        removeMemberDetailsFromCache(consortiumLeadId);
        salesForceConsortiumCache.remove(consortiumLeadId);
    }

    private void checkOpportunityUpdatePermissions(Opportunity opportunity) {
        Opportunity retrievedOpportunity = salesForceDao.retrieveOpportunity(opportunity.getId());
        if (!opportunity.getConsortiumLeadId().equals(retrievedOpportunity.getConsortiumLeadId())) {
            throw new IllegalStateException("Opportunity consortium lead mismatch");
        }
        if (!opportunity.getTargetAccountId().equals(retrievedOpportunity.getTargetAccountId())) {
            throw new IllegalStateException("Opportunity target account mismatch");
        }
        List<String> accountIds = retrieveAccountIdsByOrcid(sourceManager.retrieveRealUserOrcid());
        boolean authorized = accountIds.contains(retrievedOpportunity.getConsortiumLeadId());
        if (!authorized) {
            throw new OrcidUnauthorizedException("Insufficient permissions to update opportunity");
        }
    }

    @Override
    public void createContact(Contact contact) {
        String accountId = contact.getAccountId();
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
        List<Contact> existingContactsWithRoles = salesForceDao.retrieveContactsWithRolesByAccountId(accountId, true);
        Optional<ContactRole> existingContactRole = existingContactsWithRoles.stream()
                .filter(c -> contactId.equals(c.getId()) && ContactRoleType.OTHER_CONTACT.equals(c.getRole().getRoleType())).map(c -> c.getRole()).findFirst();
        if (existingContactRole.isPresent()) {
            ContactRole contactRole = existingContactRole.get();
            if (!contactRole.isCurrent()) {
                contactRole.setCurrent(true);
                salesForceDao.updateContactRole(contactRole);
            }
        } else {
            ContactRole contactRole = new ContactRole();
            contactRole.setContactId(contactId);
            contactRole.setRoleType(ContactRoleType.OTHER_CONTACT);
            contactRole.setAccountId(contact.getAccountId());
            salesForceDao.createContactRole(contactRole);
        }
        addSalesForceConnection(accountId, contact);
        salesForceContactsCache.remove(accountId);
    }

    private void createOpportunityContact(Contact contact, String opportunityId) {
        String accountId = contact.getAccountId();
        String contactOrcid = contact.getOrcid();
        if (StringUtils.isBlank(contact.getEmail())) {
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
        OpportunityContactRole contactRole = new OpportunityContactRole();
        contactRole.setContactId(contactId);
        contactRole.setRoleType(ContactRoleType.MAIN_CONTACT);
        contactRole.setOpportunityId(opportunityId);
        salesForceDao.createOpportunityContactRole(contactRole);
        addSalesForceConnection(accountId, contact);
    }

    @Override
    public void removeContact(Contact contact) {
        String accountId = contact.getAccountId();
        List<Contact> existingContacts = retrieveContactsByAccountId(accountId);
        List<Contact> updatedList = new ArrayList<>(1);
        updatedList.add(contact);
        checkContactUpdatePermissions(existingContacts, updatedList);
        removeContactRole(contact);
        removeContactAccess(contact);
        salesForceContactsCache.remove(accountId);
    }

    private void removeContactAccess(Contact contact) {
        String accountId = contact.getAccountId();
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
        String accountId = contact.getAccountId();
        List<ContactRole> contactRoles = salesForceDao.retrieveContactRolesByContactIdAndAccountId(contact.getId(), accountId);
        contactRoles.stream().filter(r -> r.getId().equals(contact.getRole().getId())).findFirst().ifPresent(r -> {
            r.setCurrent(false);
            salesForceDao.updateContactRole(r);
        });
    }

    /**
     * 
     * This is a package private method because no user permissions are checked.
     * 
     * @see SalesForceManagerImpl#updateContacts(Collection), which does check
     *      user permissions.
     */
    void updateContact(Contact contact, List<Contact> existingContacts) {
        String accountId = contact.getAccountId();
        ContactRole updatedRole = null;
        Optional<Contact> existingContactOfSameType = existingContacts.stream()
                .filter(existing -> contact.getId().equals(existing.getId()) && contact.getRole().getRoleType().equals(existing.getRole().getRoleType())).findFirst();
        if (existingContactOfSameType.isPresent()) {
            updatedRole = existingContactOfSameType.get().getRole();
            if (!updatedRole.getId().equals(contact.getRole().getId())) {
                removeContactRole(contact);
            }
        } else {
            removeContactRole(contact);
            updatedRole = new ContactRole();
            updatedRole.setAccountId(accountId);
            updatedRole.setContactId(contact.getId());
            updatedRole.setRoleType(contact.getRole().getRoleType());
        }
        updatedRole.setVotingContact(contact.getRole().isVotingContact());
        updatedRole.setCurrent(true);
        if (updatedRole.getId() == null) {
            updatedRole.setId(salesForceDao.createContactRole(updatedRole));
        } else {
            salesForceDao.updateContactRole(updatedRole);
        }
        contact.getRole().setId(updatedRole.getId());
        salesForceContactsCache.remove(accountId);
    }

    @Override
    public void updateContacts(Collection<Contact> contacts) {
        String accountId = contacts.iterator().next().getAccountId();
        if (contacts.stream().allMatch(c -> !accountId.equals(c.getAccountId()))) {
            throw new IllegalStateException("Contacts account id inconsistent");
        }
        List<Contact> existingContacts = salesForceDao.retrieveContactsWithRolesByAccountId(accountId, true);
        List<Contact> currentContacts = existingContacts.stream().filter(c -> c.getRole().isCurrent()).collect(Collectors.toList());
        // Ensure contact ORCID iDs are correct by getting from registry DB.
        addOrcidsToContacts(currentContacts);
        checkContactUpdatePermissions(currentContacts, contacts);
        // Need to remove roles with validation rules in SF first
        currentContacts.stream().filter(c -> {
            return ContactRoleType.MAIN_CONTACT.equals(c.getRole().getRoleType()) || ContactRoleType.AGREEMENT_SIGNATORY.equals(c.getRole().getRoleType())
                    || c.getRole().isVotingContact();
        }).forEach(c -> removeContactRole(c));
        contacts.stream().forEach(c -> updateContact(c, existingContacts));
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
        contacts.stream().filter(c -> c.getOrcid() != null && !existingConnectionsOrcids.contains(c.getOrcid())).forEach(c -> addSalesForceConnection(accountId, c));
    }

    private void addSalesForceConnection(String accountId, Contact contact) {
        String orcid = contact.getOrcid();
        if (orcid == null) {
            return;
        }
        List<SalesForceConnectionEntity> existingConnections = salesForceConnectionDao.findByOrcid(orcid);
        if (existingConnections.stream().anyMatch(c -> accountId.equals(c.getSalesForceAccountId()))) {
            return;
        }
        SalesForceConnectionEntity newConnection = new SalesForceConnectionEntity(orcid, contact.getEmail(), accountId);

        if (existingConnections.isEmpty()) {
            newConnection.setPrimary(true);
        } else {
            Member member = retrieveMember(accountId);
            if (member != null && member.getConsortiumLeadId() == null) {
                newConnection.setPrimary(true);
                existingConnections.stream().filter(c -> c.isPrimary()).forEach(c -> {
                    c.setPrimary(false);
                    salesForceConnectionDao.merge(c);
                });
            }
        }
        salesForceConnectionDao.persist(newConnection);
    }

    @Override
    public void evictAll() {
        evictLists();
        salesForceMemberCache.clear();
        salesForceMemberDetailsCache.clear();
        salesForceConsortiumCache.clear();
        salesForceContactsCache.clear();
        premiumConsortiumMemberTypeId = null;
        consortiumMemberRecordTypeId = null;
        badgesMap = null;
    }

    private void evictLists() {
        salesForceMembersListCache.clear();
        salesForceConsortiaListCache.clear();
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
