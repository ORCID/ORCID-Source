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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.read_only.impl.ManagerReadOnlyBaseImpl;
import org.orcid.core.salesforce.cache.MemberDetailsCacheKey;
import org.orcid.core.salesforce.dao.SalesForceDao;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.SlugUtils;
import org.orcid.core.salesforce.model.SubMember;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.persistence.dao.SalesForceConnectionDao;
import org.orcid.persistence.jpa.entities.SalesForceConnectionEntity;
import org.orcid.utils.ReleaseNameUtils;

import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceManagerImpl extends ManagerReadOnlyBaseImpl implements SalesForceManager {

    @Resource(name = "salesForceMembersListCache")
    private SelfPopulatingCache salesForceMembersListCache;

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

    @SuppressWarnings("unchecked")
    @Override
    public List<Member> retrieveMembers() {
        return (List<Member>) salesForceMembersListCache.get(releaseName).getObjectValue();
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
        List<Member> members = retrieveMembers();
        Optional<Member> match = members.stream().filter(e -> {
            String id = e.getId();
            String legacyId = id.substring(0, 15);
            return memberId.equalsIgnoreCase(id) || memberId.equals(legacyId);
        }).findFirst();
        if (match.isPresent()) {
            Member salesForceMember = match.get();
            MemberDetails details = (MemberDetails) salesForceMemberDetailsCache
                    .get(new MemberDetailsCacheKey(memberId, salesForceMember.getConsortiumLeadId(), releaseName)).getObjectValue();
            details.setMember(salesForceMember);
            details.setSubMembers(findSubMembers(memberId));
            return details;
        }
        throw new IllegalArgumentException("No member details found for " + memberId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Contact> retrieveContactsByAccountId(String accountId) {
        return (List<Contact>) salesForceContactsCache.get(accountId).getObjectValue();
    }

    @Override
    public void addOrcidsToContacts(List<Contact> contacts) {
        List<String> emails = contacts.stream().map(c -> c.getEmail()).collect(Collectors.toList());
        Map<String, String> emailsToOrcids = emailManager.findIdsByEmails(emails);
        contacts.stream().forEach(c -> {
            c.setOrcid(emailsToOrcids.get(c.getEmail()));
        });
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
        });
    }

    @Override
    public String retriveAccountIdByOrcid(String orcid) {
        SalesForceConnectionEntity connection = salesForceConnectionDao.findByOrcid(orcid);
        return connection != null ? connection.getSalesForceAccountId() : null;
    }

    @Override
    public void updateMember(Member member) {
        salesForceDao.updateMember(member);
        salesForceMembersListCache.removeAll();
    }

    @Override
    public void createContact(Contact contact) {
        contact.setAccountId(retriveAccountIdByOrcid(sourceManager.retrieveRealUserOrcid()));
        if (StringUtils.isBlank(contact.getEmail())) {
            String contactOrcid = contact.getOrcid();
            Email primaryEmail = emailManager.getEmails(contactOrcid, getLastModified(contactOrcid)).getEmails().stream().filter(e -> e.isPrimary()).findFirst().get();
            contact.setEmail(primaryEmail.getEmail());
        }
        salesForceDao.createContact(contact);
        // Need to make more granular!
        evictAll();
    }

    @Override
    public void evictAll() {
        salesForceMembersListCache.removeAll();
        salesForceMemberDetailsCache.removeAll();
        salesForceConsortiaListCache.removeAll();
        salesForceConsortiumCache.removeAll();
        salesForceContactsCache.removeAll();
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

}
