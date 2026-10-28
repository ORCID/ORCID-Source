package org.orcid.core.manager.v3.read_only;

import org.orcid.persistence.jpa.entities.ProfileEntity;

public interface ProfileEntityManagerReadOnly extends ManagerReadOnlyBase {

    ProfileEntity findByOrcid(String orcid);
    
    Boolean isLocked(String orcid);

    String getLockedReason(String orcid);
    
    Boolean isOrcidValidAsDelegate(String orcid); 
    
    Boolean haveMemberPushedWorksOrAffiliationsToRecord(String orcid, String clientId);

    Boolean hasToken(String userName, long lastModified);

    /**
     * Whether the record has to reset its password before it can sign in with
     * one. Never cached: which database answers depends on the bean, the
     * writable manager reads the primary and the read-only one the replica.
     */
    boolean isPasswordResetRequired(String orcid);
}