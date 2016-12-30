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
import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface OrcidSecurityManager {

    boolean isAdmin();

    boolean isPasswordConfirmationRequired();

    String getClientIdFromAPIRequest();

    void checkProfile(String orcid) throws NoResultException, OrcidDeprecatedException, OrcidNotClaimedException, LockedException;

    void checkSource(SourceAwareEntity<?> existingEntity);

    void checkSource(IdentifierTypeEntity existingEntity);

    void checkScopes(ScopePathType requiredScope);

    void checkClientAccessAndScopes(String orcid, ScopePathType requiredScope);

    void checkAndFilter(String orcid, VisibilityType element, ScopePathType requiredScope);

    void checkAndFilter(String orcid, Collection<? extends VisibilityType> elements, ScopePathType requiredScope);

    void checkAndFilter(String orcid, ActivitiesSummary activities, ScopePathType requiredScope);

    void checkAndFilter(String orcid, PersonalDetails personalDetails, ScopePathType requiredScope);

    void checkAndFilter(String orcid, Person person, ScopePathType requiredScope);

    void checkAndFilter(String orcid, Record record, ScopePathType requiredScope);
}
