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

import javax.persistence.NoResultException;

import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.common_rc4.VisibilityType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record_rc4.Person;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;
import org.orcid.jaxb.model.record_rc4.Record;

/**
 * 
 * @author Will Simpson
 *
 */
public interface OrcidSecurityManager {

	void checkScopes(ScopePathType requiredScope);
	
	void checkClientAccessAndScopes(String orcid, ScopePathType requiredScope);
	
	void checkAndFilter(VisibilityType element, ScopePathType requiredScope);
	
	void checkAndFilter(Collection<? extends VisibilityType> elements, ScopePathType requiredScope);
	
	void checkAndFilter(ActivitiesSummary activities, ScopePathType requiredScope);
	
	void checkAndFilter(PersonalDetails personalDetails, ScopePathType requiredScope);
	
	void checkAndFilter(Person person, ScopePathType requiredScope);
	
	void checkAndFilter(Record record, ScopePathType requiredScope);
	
	boolean isAdmin();

    boolean isPasswordConfirmationRequired();
    
    String getClientIdFromAPIRequest();
    
    void checkProfile(String orcid) throws NoResultException, OrcidDeprecatedException, OrcidNotClaimedException, LockedException;
}
