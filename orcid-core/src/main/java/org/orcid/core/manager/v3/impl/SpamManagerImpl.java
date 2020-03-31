package org.orcid.core.manager.v3.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.SpamManager;
import org.orcid.core.manager.v3.read_only.impl.SpamManagerReadOnlyImpl;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SpamEntity;

/**
 *
 * @author Daniel Palafox
 *
 */
public class SpamManagerImpl extends SpamManagerReadOnlyImpl implements SpamManager {
            
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    @Override
    public boolean removeSpam(String orcid) {        
        return spamDao.removeSpam(orcid);
    }

    @Override
    public boolean createOrUpdateSpam(String orcid) {                
        SpamEntity spamEntity = null;        
        
        if (profileEntityManager.orcidExists(orcid)) {
            ProfileEntity profileEntity = profileEntityManager.findByOrcid(orcid);            
            
            if (!profileEntity.isReviewed() && !profileEntity.getRecordLocked() 
                    && "USER".equals(profileEntity.getOrcidType()) && profileEntity.getDeactivationDate() == null) {
                if (spamDao.exists(orcid)) {
                    spamEntity = spamDao.getSpam(orcid);
                    spamDao.updateSpamCount(spamEntity, spamEntity.getSpamCounter() + 1);
                } else {
                    spamEntity = new SpamEntity();
                    spamEntity.setOrcid(orcid);
                    spamEntity.setSpamCounter(1);
                    spamEntity.setSourceType(org.orcid.persistence.jpa.entities.SourceType.USER);
                    spamDao.createSpam(spamEntity);
                }          
                return true;
            }                                       
        }
        return false;
    }
}
