package org.orcid.core.manager.v3.read_only.impl;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbSpamAdapter;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.SpamManagerReadOnly;
import org.orcid.jaxb.model.v3.release.record.Spam;
import org.orcid.persistence.dao.SpamDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpamManagerReadOnlyImpl implements SpamManagerReadOnly {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpamManagerReadOnlyImpl.class);
    
    @Resource
    protected JpaJaxbSpamAdapter jpaJaxbSpamAdapter;
    
    @Resource
    protected SpamDao spamDao;        

    @Override
    public Spam getSpam(String orcid) {
        try {
            return jpaJaxbSpamAdapter.toSpam(spamDao.getSpam(orcid));             
        } catch(Exception e) {
            LOGGER.error("Exception getting record name", e);
        }
        return null;
    }

    @Override
    public boolean exists(String orcid) {
        return spamDao.exists(orcid);
    }

}
