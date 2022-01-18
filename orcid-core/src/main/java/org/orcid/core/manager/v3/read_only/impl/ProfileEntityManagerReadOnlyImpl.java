package org.orcid.core.manager.v3.read_only.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;

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
        List<Object[]> result = profileDao.getLockedReason(orcid);
        String reason = (String) result.get(0)[0];
        String description = (String) result.get(0)[1];
        String adminUser = (String) result.get(0)[2];
        String strDate = "";
        Date date = new Date();

        // format results
        if (adminUser == null || adminUser.isEmpty()) {
            adminUser = "";
        } else {
            adminUser = " by " + adminUser;
        }

        // If record_locked_date is missing, try to use last_indexed_date
        if ((Date) result.get(0)[3] == null) {
            date = (Date) result.get(0)[4];
        } else if ((Date) result.get(0)[4] != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            strDate = " on " + sdf.format(date);
        } else
            strDate = "";

        if (description == null || description.isEmpty()) {
            description = "";
        } else {
            description = " - " + description;
        }

        return (reason == null || reason.isEmpty()) ? "" : adminUser + strDate + " for: " + reason + description;
    }

    @Override
    public Boolean isOrcidValidAsDelegate(String orcid) {
        return profileDao.isOrcidValidAsDelegate(orcid);
    }   
}