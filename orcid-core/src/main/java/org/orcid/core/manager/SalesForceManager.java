package org.orcid.core.manager;

import java.util.List;

import org.orcid.pojo.SalesForceIntegration;
import org.orcid.pojo.SalesForceMember;

/**
 * 
 * @author Will Simpson
 *
 */
public interface SalesForceManager {

    List<SalesForceMember> retrieveMembers();

    List<SalesForceIntegration> retrieveIntegrations(String memberId);

    /**
     * 
     * @throws IllegalArgumentException
     *             if the memberId is not the correct format, or could contain
     *             something malicious.
     */
    void validateMemberId(String memberId);

}
