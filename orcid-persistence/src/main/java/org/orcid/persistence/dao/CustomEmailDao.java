package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;
import org.orcid.persistence.jpa.entities.keys.CustomEmailPk;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface CustomEmailDao extends GenericDao<CustomEmailEntity, CustomEmailPk> {

    CustomEmailEntity findByClientIdAndEmailType(String clientDetailsId, EmailType emailType);
    boolean createCustomEmail(String clientDetailsId, EmailType emailType, String content);
    boolean updateCustomEmail(String clientDetailsId, EmailType emailType, String content);
    boolean deleteCustomEmail(String clientDetailsId, EmailType emailType);
}
