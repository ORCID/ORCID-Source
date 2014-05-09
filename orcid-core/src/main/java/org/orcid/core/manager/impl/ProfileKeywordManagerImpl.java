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

import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

public class ProfileKeywordManagerImpl implements ProfileKeywordManager {

    @Resource
    ProfileKeywordDao profileKeywordDao;

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
        profileKeywordDao.addProfileKeyword(orcid, keyword);
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
            if (newKeywords.contains(existingKeyword.getKeyword())) {
                newKeywords.remove(existingKeyword.getKeyword());
            } else {
                //Delete other names deleted by user
                profileKeywordDao.deleteProfileKeyword(orcid, existingKeyword.getKeyword());
            }
        }

        //At this point, only new other names are in the parameter list otherNames
        //Insert all these other names on database
        for (String newKeyword : newKeywords) {
            profileKeywordDao.addProfileKeyword(orcid, newKeyword);
        }
        if (keywords.getVisibility() != null)
            profileKeywordDao.updateKeywordsVisibility(orcid, keywords.getVisibility());
    }

}
