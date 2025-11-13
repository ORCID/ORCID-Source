package org.orcid.core.manager.v3.read_only.impl;

import java.text.SimpleDateFormat;

import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class ProfileEntityManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ProfileEntityManagerReadOnly { 

    protected ProfileDao profileDao;       
    
    public void setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }    

    /**
     * Fetch a ProfileEntity from the database Instead of calling this function,
     * use the cache profileEntityCacheManager whenever is possible
     */
    @Override
    public ProfileEntity findByOrcid(String orcid) {
        return profileDao.find(orcid);
    }

    @Override
    public Boolean isLocked(String orcid) {
        return profileDao.isLocked(orcid);
    }   
    
    @Override
    public String getLockedReason(String orcid) {
        ProfileEntity profileEntity = profileDao.getLockedReason(orcid);
        String reason = profileEntity.getReasonLocked();
        String description = "";
        String adminUser = "";
        String strDate = "";

        // format results
        if (profileEntity.getRecordLockingAdmin() != null) {
            adminUser = " by " + profileEntity.getRecordLockingAdmin();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        if (profileEntity.getRecordLockedDate() != null) {
            strDate = " on " + sdf.format(profileEntity.getRecordLockedDate());
        } else if (profileEntity.getLastModified() != null) {
            strDate = " on " + sdf.format(profileEntity.getLastModified());
        }

        if (profileEntity.getReasonLockedDescription() != null) {
            description = " - " + profileEntity.getReasonLockedDescription();
        }

        return (reason == null || reason.isEmpty()) ? "" : adminUser + strDate + " for: " + reason + description;
    }

    @Override
    public Boolean isOrcidValidAsDelegate(String orcid) {
        return profileDao.isOrcidValidAsDelegate(orcid);
    }
    
    @Override
    public Boolean haveMemberPushedWorksOrAffiliationsToRecord(String orcid, String clientId) {
        if(PojoUtil.isEmpty(orcid) || PojoUtil.isEmpty(clientId)) {
            return false;
        }
        return profileDao.haveMemberPushedWorksOrAffiliationsToRecord(orcid, clientId);
    }
}
