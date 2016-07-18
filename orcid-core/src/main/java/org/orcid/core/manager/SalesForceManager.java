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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.orcid.pojo.SalesForceConsortium;
import org.orcid.pojo.SalesForceContact;
import org.orcid.pojo.SalesForceDetails;
import org.orcid.pojo.SalesForceMember;

/**
 * 
 * @author Will Simpson
 *
 */
public interface SalesForceManager {

    List<SalesForceMember> retrieveMembers();

    List<SalesForceMember> retrieveFreshMembers();

    List<SalesForceMember> retrieveConsortia();

    List<SalesForceMember> retrieveFreshConsortia();

    SalesForceConsortium retrieveConsortium(String consortiumId);

    SalesForceConsortium retrieveFreshConsortium(String consortiumId);

    SalesForceDetails retrieveDetails(String memberId, String consortiumLeadId);

    SalesForceDetails retrieveFreshDetails(String memberId, String consortiumLeadId);

    SalesForceDetails retrieveDetailsBySlug(String memberSlug);
    
    List<SalesForceContact> retrieveContactsByOpportunityId(String opportunityId);

    Map<String, List<SalesForceContact>> retrieveContactsByOpportunityId(Collection<String> opportunityIds);

    Map<String, List<SalesForceContact>> retrieveFreshContactsByOpportunityId(Collection<String> opportunityIds);

    /**
     * @return The sales force object id, if valid.
     * @throws IllegalArgumentException
     *             if the sales force object id is not the correct format, or
     *             could contain something malicious.
     */
    String validateSalesForceId(String memberId);

    /**
     * Clear caches
     * 
     */
    void evictAll();

}
