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

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.orcid.core.manager.SalesForceManager;
import org.orcid.pojo.SalesForceDetails;
import org.orcid.pojo.SalesForceMember;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceMemberDetailsCacheEntryFactory implements CacheEntryFactory {

    @Resource
    private SalesForceManager salesForceManager;

    @Override
    public Object createEntry(Object key) throws Exception {
        SalesForceMemberDetailsCacheKey detailsCacheKey = (SalesForceMemberDetailsCacheKey) key;
        SalesForceDetails details = salesForceManager.retrieveFreshDetails(detailsCacheKey.getMemberId(), detailsCacheKey.getConsotiumLeadId());
        List<SalesForceMember> members = salesForceManager.retrieveMembers();
        Optional<SalesForceMember> match = members.stream().filter(e -> detailsCacheKey.getMemberId().equals(e.getId())).findFirst();
        if (match.isPresent()) {
            SalesForceMember salesForceMember = match.get();
            details.setMember(salesForceMember);
        }
        return details;
    }

}
