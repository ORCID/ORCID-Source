package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RecordManager;
import org.orcid.jaxb.model.record_rc2.Record;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class RecordManagerImpl implements RecordManager {

    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Override
    public Record getPublicRecord(String orcid) {
        Record record = new Record();                
        record.setActivitiesSummary(profileEntityManager.getPublicActivitiesSummary(orcid));
        record.setPerson(profileEntityManager.getPublicPersonDetails(orcid));        
        return record;
    }

    @Override
    public Record getRecord(String orcid) {
        Record record = new Record();                
        record.setActivitiesSummary(profileEntityManager.getActivitiesSummary(orcid));
        record.setPerson(profileEntityManager.getPersonDetails(orcid));        
        return record;        
    }

}
