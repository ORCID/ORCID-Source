package org.orcid.core.manager.read_only;

import org.orcid.persistence.jpa.entities.ProfileEntity;

public interface ProfileEntityManagerReadOnly extends ManagerReadOnlyBase {

    ProfileEntity findByOrcid(String orcid);
}