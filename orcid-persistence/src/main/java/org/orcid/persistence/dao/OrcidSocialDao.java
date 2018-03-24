package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.OrcidSocialEntity;
import org.orcid.persistence.jpa.entities.OrcidSocialType;

public interface OrcidSocialDao {

    void save(String orcid, OrcidSocialType type, String encryptedCredentials);

    void delete(String orcid, OrcidSocialType type);

    boolean isEnabled(String orcid, OrcidSocialType type);

    boolean updateLatestRunDate(String orcid, OrcidSocialType type);

    List<OrcidSocialEntity> getRecordsToTweet();
}
