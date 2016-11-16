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

import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.Member;

/**
 * 
 * @author Will Simpson
 *
 */
public interface SalesForceManager {

    List<Member> retrieveMembers();

    List<Member> retrieveConsortia();

    Consortium retrieveConsortium(String consortiumId);

    MemberDetails retrieveDetailsBySlug(String memberSlug);

    MemberDetails retrieveDetails(String memberId);
    
    List<Contact> retrieveContactsByAccountId(String accountId);

    List<Contact> retrieveContactsByOpportunityId(String opportunityId);

    Map<String, List<Contact>> retrieveContactsByOpportunityId(Collection<String> opportunityIds);

    /**
     * Clear caches
     * 
     */
    void evictAll();

}
