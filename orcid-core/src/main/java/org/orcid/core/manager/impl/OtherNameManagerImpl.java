/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.OtherNameManager;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.jpa.entities.OtherNameEntity;

public class OtherNameManagerImpl implements OtherNameManager {

    @Resource
    private OtherNameDao otherNameDao;

    /**
     * Get other names for an specific orcid account
     * @param orcid          
     * @return
     *           The list of other names related with the specified orcid profile
     * */
    @Override
    public List<OtherNameEntity> getOtherName(String orcid) {
        return otherNameDao.getOtherName(orcid);
    }

    /**
     * Update other name entity with new values
     * @param otherName
     * @return
     *          true if the other name was sucessfully updated, false otherwise
     * */
    @Override
    public boolean updateOtherName(OtherNameEntity otherName) {
        return otherNameDao.updateOtherName(otherName);
    }

    /**
     * Create other name for the specified account
     * @param orcid
     * @param displayName
     * @return
     *          true if the other name was successfully created, false otherwise 
     * */
    @Override
    public boolean addOtherName(String orcid, String displayName) {
        return otherNameDao.addOtherName(orcid, displayName);
    }

    /**
     * Delete other name from database
     * @param otherName
     * @return 
     *          true if the other name was successfully deleted, false otherwise
     * */
    @Override
    public boolean deleteOtherName(OtherNameEntity otherName) {
        return otherNameDao.deleteOtherName(otherName);
    }

    /**
     * Get a list of other names and decide which other names might be deleted,
     * and which ones should be created
     * @param orcid
     * @param List<String> otherNames
     * */
    @Override
    public void updateOtherNames(String orcid, OtherNames otherNames) {
        List<OtherNameEntity> currentOtherNames = this.getOtherName(orcid);
        Iterator<OtherNameEntity> currentIt = currentOtherNames.iterator();
        ArrayList<String> newOtherNames = new ArrayList<String>(otherNames.getOtherNamesAsStrings());

        while (currentIt.hasNext()) {
            OtherNameEntity existingOtherName = currentIt.next();
            //Delete non modified other names from the parameter list
            if (newOtherNames.contains(existingOtherName.getDisplayName())) {
                newOtherNames.remove(existingOtherName.getDisplayName());
            } else {
                //Delete other names deleted by user
                otherNameDao.deleteOtherName(existingOtherName);
            }
        }

        //At this point, only new other names are in the parameter list otherNames
        //Insert all these other names on database
        for (String newOtherName : newOtherNames) {
            otherNameDao.addOtherName(orcid, newOtherName);
        }
    }
}
