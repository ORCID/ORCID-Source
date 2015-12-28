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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.orcid.core.adapter.JpaJaxbKeywordAdapter;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.exception.OtherNameNotFoundException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class ProfileKeywordManagerImpl implements ProfileKeywordManager {

    @Resource
    private ProfileKeywordDao profileKeywordDao;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private JpaJaxbKeywordAdapter adapter;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    /**
     * Return the list of keywords associated to a specific profile
     * 
     * @param orcid
     * @return the list of keywords associated with the orcid profile
     */
    @Override
    public List<ProfileKeywordEntity> getProfileKeywors(String orcid) {
        return profileKeywordDao.getProfileKeywors(orcid);
    }

    /**
     * Deleted a keyword from database
     * 
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully deleted
     */
    @Override
    public boolean deleteProfileKeyword(String orcid, String keyword) {
        return profileKeywordDao.deleteProfileKeyword(orcid, keyword);
    }

    /**
     * Adds a keyword to a specific profile
     * 
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully created on database
     */
    @Override
    public void addProfileKeyword(String orcid, String keyword) {
        SourceEntity source = sourceManager.retrieveSourceEntity();
        String sourceId = null;
        String clientSourceId = null;
        if (source.getSourceProfile() != null) {
            sourceId = source.getSourceProfile().getId();
        }

        if (source.getSourceClient() != null) {
            clientSourceId = source.getSourceClient().getId();
        }
        profileKeywordDao.addProfileKeyword(orcid, keyword, sourceId, clientSourceId);
    }

    /**
     * Update the list of keywords associated with a specific account
     * 
     * @param orcid
     * @param keywords
     */
    @Override
    public void updateProfileKeyword(String orcid, org.orcid.jaxb.model.message.Keywords keywords) {
        List<ProfileKeywordEntity> currentKeywords = this.getProfileKeywors(orcid);
        Iterator<ProfileKeywordEntity> currentIt = currentKeywords.iterator();
        ArrayList<String> newKeywords = new ArrayList<String>(keywords.getKeywordsAsStrings());

        while (currentIt.hasNext()) {
            ProfileKeywordEntity existingKeyword = currentIt.next();
            // Delete non modified other names from the parameter list
            if (newKeywords.contains(existingKeyword.getKeywordName())) {
                newKeywords.remove(existingKeyword.getKeywordName());
            } else {
                // Delete other names deleted by user
                profileKeywordDao.deleteProfileKeyword(orcid, existingKeyword.getKeywordName());
            }
        }

        // At this point, only new other names are in the parameter list
        // otherNames
        // Insert all these other names on database
        for (String newKeyword : newKeywords) {
            profileKeywordDao.addProfileKeyword(orcid, newKeyword, orcid, null);
        }
        if (keywords.getVisibility() != null)
            profileKeywordDao.updateKeywordsVisibility(orcid, keywords.getVisibility());
    }

    @Override
    public Keywords getKeywordsV2(String orcid) {
        List<ProfileKeywordEntity> entities = getProfileKeywordEntitys(orcid, null);
        return adapter.toKeywords(entities);
    }

    @Override
    public Keywords getPublicKeywordsV2(String orcid) {
        List<ProfileKeywordEntity> entities = getProfileKeywordEntitys(orcid, Visibility.PUBLIC);
        return adapter.toKeywords(entities);
    }

    private List<ProfileKeywordEntity> getProfileKeywordEntitys(String orcid, Visibility visibility) {
        List<ProfileKeywordEntity> keywords = profileKeywordDao.getProfileKeywors(orcid);
        if (visibility != null) {
            Iterator<ProfileKeywordEntity> it = keywords.iterator();
            while (it.hasNext()) {
                ProfileKeywordEntity keywordEntity = it.next();
                if (!visibility.equals(keywordEntity.getVisibility())) {
                    it.remove();
                }
            }
        }

        return keywords;
    }

    @Override
    public Keyword getKeywordV2(String orcid, Long putCode) {
        ProfileKeywordEntity entity = profileKeywordDao.getProfileKeyword(orcid, putCode);
        return adapter.toKeyword(entity);
    }

    @Override
    public boolean deleteKeywordV2(String orcid, Long putCode) {
        ProfileKeywordEntity entity = profileKeywordDao.getProfileKeyword(orcid, putCode);
        SourceEntity existingSource = entity.getSource();
        orcidSecurityManager.checkSource(existingSource);

        try {
            profileKeywordDao.deleteProfileKeyword(entity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public Keyword createKeywordV2(String orcid, Keyword keyword) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate the keyword
        PersonValidator.validateKeyword(keyword, sourceEntity, true);
        // Validate it is not duplicated
        List<ProfileKeywordEntity> existingKeywords = profileKeywordDao.getProfileKeywors(orcid);
        for (ProfileKeywordEntity existing : existingKeywords) {
            if (isDuplicated(existing, keyword, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "keyword");
                params.put("value", keyword.getContent());
                throw new OrcidDuplicatedElementException(params);
            }
        }

        ProfileKeywordEntity newEntity = adapter.toProfileKeywordEntity(keyword);
        ProfileEntity profile = new ProfileEntity(orcid);
        newEntity.setProfile(profile);
        newEntity.setDateCreated(new Date());
        newEntity.setSource(sourceEntity);
        setIncomingPrivacy(newEntity, profile);
        profileKeywordDao.persist(newEntity);
        return adapter.toKeyword(newEntity);
    }

    @Override
    @Transactional
    public Keyword updateKeywordV2(String orcid, Long putCode, Keyword keyword) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        // Validate the keyword
        PersonValidator.validateKeyword(keyword, sourceEntity, false);
        // Validate it is not duplicated
        List<ProfileKeywordEntity> existingKeywords = profileKeywordDao.getProfileKeywors(orcid);
        for (ProfileKeywordEntity existing : existingKeywords) {
            if (isDuplicated(existing, keyword, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "keyword");
                params.put("value", keyword.getContent());
                throw new OrcidDuplicatedElementException(params);
            }
        }

        ProfileKeywordEntity updatedEntity = profileKeywordDao.find(putCode);
        if (updatedEntity == null) {
            throw new OtherNameNotFoundException();
        }

        Visibility originalVisibility = Visibility.fromValue(updatedEntity.getVisibility().value());
        SourceEntity existingSource = updatedEntity.getSource();
        orcidSecurityManager.checkSource(existingSource);
        adapter.toProfileKeywordEntity(keyword, updatedEntity);
        updatedEntity.setLastModified(new Date());
        updatedEntity.setVisibility(originalVisibility);
        updatedEntity.setSource(existingSource);
        profileKeywordDao.merge(updatedEntity);
        return adapter.toKeyword(updatedEntity);
    }

    @Override
    @Transactional
    public Keywords updateKeywordsV2(String orcid, Keywords keywords, Visibility defaultVisiblity) {
        List<ProfileKeywordEntity> existingKeywordsList = profileKeywordDao.getProfileKeywors(orcid);
        // Delete the deleted ones
        for (ProfileKeywordEntity existing : existingKeywordsList) {
            boolean deleteMe = true;
            for (Keyword updatedOrNew : keywords.getKeywords()) {
                if (existing.getId().equals(updatedOrNew.getPutCode())) {
                    deleteMe = false;
                    break;
                }
            }

            if (deleteMe) {
                try {
                    profileKeywordDao.deleteProfileKeyword(existing);
                } catch (Exception e) {
                    throw new ApplicationException("Unable to delete keyword " + existing.getId(), e);
                }
            }
        }

        if (keywords != null && keywords.getKeywords() != null) {
            for (Keyword updatedOrNew : keywords.getKeywords()) {
                if (updatedOrNew.getPutCode() != null) {
                    // Update the existing ones or create new ones
                    for (ProfileKeywordEntity existingKeyword : existingKeywordsList) {
                        if (existingKeyword.getId().equals(updatedOrNew.getPutCode())) {
                            existingKeyword.setLastModified(new Date());
                            existingKeyword.setVisibility(Visibility.fromValue(defaultVisiblity.value()));
                            existingKeyword.setKeywordName(updatedOrNew.getContent());
                            profileKeywordDao.merge(existingKeyword);
                        }
                    }
                } else {
                    // Add the new ones
                    ProfileKeywordEntity newKeyword = adapter.toProfileKeywordEntity(updatedOrNew);
                    SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
                    ProfileEntity profile = new ProfileEntity(orcid);
                    newKeyword.setProfile(profile);
                    newKeyword.setDateCreated(new Date());
                    newKeyword.setSource(sourceEntity);
                    newKeyword.setVisibility(Visibility.fromValue(defaultVisiblity.value()));
                    profileKeywordDao.persist(newKeyword);

                }
            }
        }

        if (defaultVisiblity != null)
            profileKeywordDao.updateKeywordsVisibility(orcid, org.orcid.jaxb.model.message.Visibility.fromValue(defaultVisiblity.value()));

        return keywords;
    }

    private boolean isDuplicated(ProfileKeywordEntity existing, org.orcid.jaxb.model.record_rc2.Keyword keyword, SourceEntity source) {
        if (!existing.getId().equals(keyword.getPutCode())) {
            if (existing.getSource() != null) {
                if (!PojoUtil.isEmpty(existing.getSource().getSourceId()) && existing.getSource().getSourceId().equals(source.getSourceId())) {
                    if (existing.getKeywordName() != null && existing.getKeywordName().equals(keyword.getContent())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setIncomingPrivacy(ProfileKeywordEntity entity, ProfileEntity profile) {
        org.orcid.jaxb.model.common.Visibility incomingKeywordVisibility = entity.getVisibility();
        org.orcid.jaxb.model.common.Visibility defaultKeywordVisibility = profile.getKeywordsVisibility() == null
                ? org.orcid.jaxb.model.common.Visibility.fromValue(OrcidVisibilityDefaults.KEYWORD_DEFAULT.getVisibility().value())
                : org.orcid.jaxb.model.common.Visibility.fromValue(profile.getResearcherUrlsVisibility().value());
        if (profile.getClaimed() != null && profile.getClaimed()) {
            if (defaultKeywordVisibility.isMoreRestrictiveThan(incomingKeywordVisibility)) {
                entity.setVisibility(defaultKeywordVisibility);
            }
        } else if (incomingKeywordVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common.Visibility.PRIVATE);
        }
    }
}
