package org.orcid.core.manager;

import java.util.List;

import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;

/**
 * 
 * @author Will Simpson
 *
 */
public interface UserConnectionManager {

    List<UserconnectionEntity> findByOrcid(String orcid);

    void remove(String orcid, UserconnectionPK userConnectionPK);

}
