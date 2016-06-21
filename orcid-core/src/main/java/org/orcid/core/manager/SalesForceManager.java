package org.orcid.core.manager;

import java.util.List;

import org.orcid.pojo.SalesForceMember;

/**
 * 
 * @author Will Simpson
 *
 */
public interface SalesForceManager {

    List<SalesForceMember> retrieveMembers();

}
