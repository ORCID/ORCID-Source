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
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.jpa.entities.OtherNameEntity;

public class OtherNameManagerImpl implements OtherNameManager {

    @Resource
    private OtherNameDao otherNameDao;

    /**
     * TODO
     * */
    @Override
    public List<OtherNameEntity> getOtherName(String orcid) {        
        return otherNameDao.getOtherName(orcid);
    }

    /**
     * TODO
     * */
    @Override
    public boolean updateOtherName(OtherNameEntity otherName) {        
        return otherNameDao.updateOtherName(otherName);
    }

    /**
     * TODO
     * */
    @Override
    public boolean addOtherName(String orcid, String displayName) {        
        return otherNameDao.addOtherName(orcid, displayName);
    }

    /**
     * TODO
     * */
    @Override
    public boolean deleteOtherName(OtherNameEntity otherName){
        return otherNameDao.deleteOtherName(otherName);
    }
    
    /**
     * TODO
     * */
    @Override
    public void updateOtherNames(String orcid, List<String> otherNames){
        List<OtherNameEntity> currentOtherNames = this.getOtherName(orcid);        
        Iterator<OtherNameEntity> currentIt = currentOtherNames.iterator();
        ArrayList<String> newOtherNames = new ArrayList<String>(otherNames);
        
        while(currentIt.hasNext()){            
            OtherNameEntity existingOtherName = currentIt.next(); 
            //Delete non modified other names from the parameter list
            if(otherNames.contains(existingOtherName.getDisplayName())) {
                newOtherNames.remove(existingOtherName.getDisplayName());
            } else {
                //Delete other names deleted by user
                otherNameDao.deleteOtherName(existingOtherName);
            }
        }
        
        //At this point, only new other names are in the parameter list otherNames
        //Insert all these other names on database
        for(String newOtherName : newOtherNames){
            otherNameDao.addOtherName(orcid, newOtherName);            
        }
    }
}
