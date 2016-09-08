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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.salesforce.dao.SalesForceDao;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.SlugUtils;
import org.orcid.core.salesforce.model.SubMember;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceManagerImpl implements SalesForceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesForceManagerImpl.class);

    @Resource(name = "salesForceMembersListCache")
    private SelfPopulatingCache salesForceMembersListCache;

    @Resource(name = "salesForceMemberDetailsCache")
    private SelfPopulatingCache salesForceMemberDetailsCache;

    @Resource
    private SalesForceDao salesForceDao;

    private String releaseName = ReleaseNameUtils.getReleaseName();

    @SuppressWarnings("unchecked")
    @Override
    public List<Member> retrieveMembers() {
        return (List<Member>) salesForceMembersListCache.get(releaseName).getObjectValue();
    }

    @Override
    public List<Member> retrieveConsortia() {
        // XXX Implement cache
        return salesForceDao.retrieveFreshConsortia();
    }

    @Override
    public Consortium retrieveConsortium(String consortiumId) {
        // XXX Implement cache
        return salesForceDao.retrieveFreshConsortium(consortiumId);
    }

    @Override
    public MemberDetails retrieveDetailsBySlug(String memberSlug) {
        String memberId = SlugUtils.extractIdFromSlug(memberSlug);
        return retrieveDetails(memberId);
    }

    @Override
    public MemberDetails retrieveDetails(String memberId) {
        List<Member> members = retrieveMembers();
        Optional<Member> match = members.stream().filter(e -> memberId.equals(e.getId())).findFirst();
        if (match.isPresent()) {
            Member salesForceMember = match.get();
            MemberDetails details = (MemberDetails) salesForceMemberDetailsCache
                    .get(new SalesForceMemberDetailsCacheKey(memberId, salesForceMember.getConsortiumLeadId(), releaseName)).getObjectValue();
            details.setMember(salesForceMember);
            details.setContacts(findContacts(salesForceMember));
            details.setSubMembers(findSubMembers(memberId));
            return details;
        }
        throw new IllegalArgumentException("No member details found for " + memberId);
    }

    @Override
    public List<Contact> retrieveContactsByOpportunityId(String opportunityId) {
        List<String> opportunityIds = new ArrayList<>();
        opportunityIds.add(opportunityId);
        Map<String, List<Contact>> results = salesForceDao.retrieveFreshContactsByOpportunityId(opportunityIds);
        return results.get(opportunityId);
    }

    @Override
    public Map<String, List<Contact>> retrieveContactsByOpportunityId(Collection<String> opportunityIds) {
        // XXX Implement cache
        return salesForceDao.retrieveFreshContactsByOpportunityId(opportunityIds);
    }

    @Override
    public void evictAll() {
        salesForceMembersListCache.removeAll();
        salesForceMemberDetailsCache.removeAll();
    }

    private List<Contact> findContacts(Member member) {
        String memberId = member.getId();
        String consortiumLeadId = member.getConsortiumLeadId();
        if (consortiumLeadId != null) {
            Consortium consortium = retrieveConsortium(consortiumLeadId);
            Optional<Opportunity> opp = consortium.getOpportunities().stream().filter(e -> memberId.equals(e.getTargetAccountId())).findFirst();
            if (opp.isPresent()) {
                String oppId = opp.get().getId();
                return retrieveContactsByOpportunityId(oppId);
            }
        } else {
            // It might be a consortium
            Optional<Member> consortium = retrieveConsortia().stream().filter(e -> memberId.equals(e.getId())).findFirst();
            if (consortium.isPresent()) {
                String mainOpportunityId = consortium.get().getMainOpportunityId();
                if (mainOpportunityId != null) {
                    return retrieveContactsByOpportunityId(mainOpportunityId);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<SubMember> findSubMembers(String memberId) {
        Consortium consortium = retrieveConsortium(memberId);
        if (consortium != null) {
            List<String> opportunityIds = consortium.getOpportunities().stream().map(e -> e.getId()).collect(Collectors.toList());
            Map<String, List<Contact>> contactsMap = retrieveContactsByOpportunityId(opportunityIds);
            List<SubMember> subMembers = consortium.getOpportunities().stream().map(o -> {
                SubMember subMember = new SubMember();
                subMember.setOpportunity(o);
                subMember.setSlug(SlugUtils.createSlug(o.getTargetAccountId(), o.getAccountName()));
                List<Contact> contactsList = contactsMap.get(o.getId());
                Optional<Contact> mainContactOptional = contactsList.stream().filter(c -> SalesForceDao.MAIN_CONTACT_ROLE.equals(c.getRole())).findFirst();
                if (mainContactOptional.isPresent()) {
                    subMember.setMainContact(mainContactOptional.get());
                }
                return subMember;
            }).collect(Collectors.toList());
            return subMembers;
        }
        return Collections.emptyList();
    }

}
