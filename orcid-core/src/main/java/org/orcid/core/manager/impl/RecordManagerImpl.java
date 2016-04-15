/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import javax.annotation.Resource;

import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RecordManager;
import org.orcid.jaxb.model.common_rc2.OrcidIdentifier;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.record_rc2.CompletionDate;
import org.orcid.jaxb.model.record_rc2.DeactivationDate;
import org.orcid.jaxb.model.record_rc2.History;
import org.orcid.jaxb.model.record_rc2.Preferences;
import org.orcid.jaxb.model.record_rc2.Record;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class RecordManagerImpl implements RecordManager {

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Override
    public Record getPublicRecord(String orcid) {
        Record record = new Record();
        record.setOrcidIdentifier(getOrcidIdentifier(orcid));
        record.setPreferences(getPreferences(orcid));
        record.setActivitiesSummary(profileEntityManager.getPublicActivitiesSummary(orcid));
        record.setPerson(profileEntityManager.getPublicPersonDetails(orcid));
        return record;
    }

    @Override
    public Record getRecord(String orcid) {
        Record record = new Record();
        record.setOrcidIdentifier(getOrcidIdentifier(orcid));
        record.setPreferences(getPreferences(orcid));
        record.setActivitiesSummary(profileEntityManager.getActivitiesSummary(orcid));
        record.setPerson(profileEntityManager.getPersonDetails(orcid));
        return record;
    }

    private OrcidIdentifier getOrcidIdentifier(String orcid) {
        OrcidIdentifier orcidIdentifier = new OrcidIdentifier();
        orcidIdentifier.setPath(orcid);
        orcidIdentifier.setHost(orcidUrlManager.getBaseHost());
        orcidIdentifier.setUri(orcidUrlManager.getBaseUriHttp() + "/" + orcid);
        return orcidIdentifier;
    }

    private Preferences getPreferences(String orcid) {
        Preferences preferences = new Preferences();
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        org.orcid.jaxb.model.message.Locale profileEntityLocale = profile.getLocale();
        if (profileEntityLocale != null) {
            preferences.setLocale(profileEntityLocale);
        }
        return preferences;
    }
    
    private History getHistory(String orcid) {
        History history = new History();
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        history.setClaimed(profile.getClaimed());
        if(profile.getCompletedDate() != null) {
            CompletionDate completionDate = new CompletionDate();
            completionDate.setValue(DateUtils.convertToXMLGregorianCalendar(profile.getCompletedDate()));
            history.setCompletionDate(completionDate);           
        }
        
        if(!PojoUtil.isEmpty(profile.getCreationMethod())) {
            history.setCreationMethod(CreationMethod.fromValue(profile.getCreationMethod()));
        }
        
        if(profile.getDeactivationDate() != null) {
            DeactivationDate deactivationDate = new DeactivationDate();
            deactivationDate.setValue(DateUtils.convertToXMLGregorianCalendar(profile.getDeactivationDate()));
            history.setDeactivationDate(deactivationDate);
        }
        history.setLastModifiedDate(null);
        history.setSource(null);
        history.setSubmissionDate(null);
        history.setVerifiedEmail(null);
        history.setVerifiedPrimaryEmail(null);
        
        return history;        
    }

}
