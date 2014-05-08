package org.orcid.persistence.dao.impl;

import org.orcid.persistence.dao.CustomEmailDao;
import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;
import org.orcid.persistence.jpa.entities.keys.CustomEmailPk;

public class CustomEmailDaoImpl extends GenericDaoImpl<CustomEmailEntity, CustomEmailPk> implements CustomEmailDao {

    public CustomEmailDaoImpl() {
        super(CustomEmailEntity.class);
    }

    @Override
    public CustomEmailEntity findByClientIdAndEmailType(String clientDetailsId, EmailType emailType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean createCustomEmail(String clientDetailsId, EmailType emailType, String content) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean updateCustomEmail(String clientDetailsId, EmailType emailType, String content) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean deleteCustomEmail(String clientDetailsId, EmailType emailType) {
        // TODO Auto-generated method stub
        return false;
    }

    

}
