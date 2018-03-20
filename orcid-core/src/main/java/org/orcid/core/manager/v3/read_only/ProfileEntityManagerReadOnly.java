package org.orcid.core.manager.v3.read_only;

import org.orcid.persistence.jpa.entities.ProfileEntity;

public interface ProfileEntityManagerReadOnly extends ManagerReadOnlyBase {

    ProfileEntity findByOrcid(String orcid);
}