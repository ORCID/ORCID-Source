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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class ProfileKeywordManagerImpl implements ProfileKeywordManager {

    @Resource
    ProfileKeywordDao profileKeywordDao;

    @Resource
    private SourceManager sourceManager;
    
    /**
     * Return the list of keywords associated to a specific profile
     * @param orcid
     * @return 
     *          the list of keywords associated with the orcid profile
     * */
    @Override
    public List<ProfileKeywordEntity> getProfileKeywors(String orcid) {
        return profileKeywordDao.getProfileKeywors(orcid);
    }

    /**
     * Deleted a keyword from database
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully deleted
     * */
    @Override
    public boolean deleteProfileKeyword(String orcid, String keyword) {
        return profileKeywordDao.deleteProfileKeyword(orcid, keyword);
    }

    /**
     * Adds a keyword to a specific profile
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully created on database
     * */
    @Override
    public void addProfileKeyword(String orcid, String keyword) {
        SourceEntity source = sourceManager.retrieveSourceEntity();
        String sourceId = null;
        String clientSourceId = null;
        if(source.getSourceProfile() != null) {
            sourceId = source.getSourceProfile().getId();
        }
        
        if(source.getSourceClient() != null) {
            clientSourceId = source.getSourceClient().getId();
        }
        profileKeywordDao.addProfileKeyword(orcid, keyword, sourceId, clientSourceId);
    }

    /**
     * Update the list of keywords associated with a specific account
     * @param orcid
     * @param keywords    
     * */
    @Override
    public void updateProfileKeyword(String orcid, Keywords keywords) {
        List<ProfileKeywordEntity> currentKeywords = this.getProfileKeywors(orcid);
        Iterator<ProfileKeywordEntity> currentIt = currentKeywords.iterator();
        ArrayList<String> newKeywords = new ArrayList<String>(keywords.getKeywordsAsStrings());

        while (currentIt.hasNext()) {
            ProfileKeywordEntity existingKeyword = currentIt.next();
            //Delete non modified other names from the parameter list
            if (newKeywords.contains(existingKeyword.getKeywordName())) {
                newKeywords.remove(existingKeyword.getKeywordName());
            } else {
                //Delete other names deleted by user
                profileKeywordDao.deleteProfileKeyword(orcid, existingKeyword.getKeywordName());
            }
        }

        //At this point, only new other names are in the parameter list otherNames
        //Insert all these other names on database        
        for (String newKeyword : newKeywords) {
            profileKeywordDao.addProfileKeyword(orcid, newKeyword, orcid, null);
        }
        if (keywords.getVisibility() != null)
            profileKeywordDao.updateKeywordsVisibility(orcid, keywords.getVisibility());
    }

    @Override
    public Keywords getKeywords(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Keywords getPublicKeywords(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    private List<Keyword> getKeywords(String orcid, Visibility visibility) {
        List<ProfileKeywordEntity> keywords = profileKeywordDao.getProfileKeywors(orcid);
        if(visibility != null) {
            Iterator<ProfileKeywordEntity> it = keywords.iterator();
            while(it.hasNext()) {
                ProfileKeywordEntity keywordEntity = it.next();
                if(!visibility.equals(keywordEntity.getVisibility())) {
                    it.remove();
                }
            }
        } 
        
        return null;
    }
    
}
