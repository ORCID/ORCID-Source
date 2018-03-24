package org.orcid.core.manager;

import java.util.Collection;

import javax.persistence.NoResultException;

import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.common_v2.VisibilityType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonalDetails;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface OrcidSecurityManager {

    boolean isAdmin();

    String getClientIdFromAPIRequest();

    void checkProfile(String orcid) throws NoResultException, OrcidDeprecatedException, OrcidNotClaimedException, LockedException, DeactivatedException;

    void checkSource(SourceAwareEntity<?> existingEntity);

    void checkSource(IdentifierTypeEntity existingEntity);

    void checkScopes(ScopePathType... requiredScopes);

    void checkClientAccessAndScopes(String orcid, ScopePathType... requiredScopes);

    void checkAndFilter(String orcid, VisibilityType element, ScopePathType requiredScope);

    void checkAndFilter(String orcid, Email email, ScopePathType requiredScope);
            
    void checkAndFilter(String orcid, Collection<? extends VisibilityType> elements, ScopePathType requiredScope);    
    
    void checkAndFilter(String orcid, WorkBulk workBulk, ScopePathType requiredScope);    
    
    void checkAndFilter(String orcid, ActivitiesSummary activities);

    void checkAndFilter(String orcid, PersonalDetails personalDetails);

    void checkAndFilter(String orcid, Person person);

    void checkAndFilter(String orcid, Record record);

    String getOrcidFromToken();
}
