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

import java.util.List;

import org.orcid.pojo.SalesForceDetails;
import org.orcid.pojo.SalesForceMember;

/**
 * 
 * @author Will Simpson
 *
 */
public interface SalesForceManager {

    List<SalesForceMember> retrieveMembers();

    SalesForceDetails retrieveDetails(String memberId);

    /**
     * @return The memberId, if valid.
     * @throws IllegalArgumentException
     *             if the memberId is not the correct format, or could contain
     *             something malicious.
     */
    String validateMemberId(String memberId);

}
