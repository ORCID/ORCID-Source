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
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.cache.annotation.Cacheable;

public class ProfileKeywordManagerImpl implements ProfileKeywordManager {

    @Resource
    private ProfileKeywordDao profileKeywordDao;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private JpaJaxbKeywordAdapter adapter;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Override
    @Cacheable(value = "keywords", key = "#orcid.concat('-').concat(#lastModified)")
    public Keywords getKeywords(String orcid, long lastModified) {
        List<ProfileKeywordEntity> entities = getProfileKeywordEntitys(orcid, null);
        Keywords result = adapter.toKeywords(entities);
        result.updateIndexingStatusOnChilds();
        return result;
    }

    @Override
    @Cacheable(value = "public-keywords", key = "#orcid.concat('-').concat(#lastModified)")
    public Keywords getPublicKeywords(String orcid, long lastModified) {
        List<ProfileKeywordEntity> entities = getProfileKeywordEntitys(orcid, Visibility.PUBLIC);
        Keywords result = adapter.toKeywords(entities);
        result.updateIndexingStatusOnChilds();
        return result;
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
    public boolean updateKeywordsVisibility(String orcid, Visibility defaultVisiblity) {
        Visibility v = (defaultVisiblity == null)
                ? Visibility.fromValue(OrcidVisibilityDefaults.KEYWORD_DEFAULT.getVisibility().value())
                : defaultVisiblity;
        return profileKeywordDao.updateKeywordsVisibility(orcid, v);
    }

    @Override
    public Keyword getKeyword(String orcid, Long putCode) {
        ProfileKeywordEntity entity = profileKeywordDao.getProfileKeyword(orcid, putCode);
        return adapter.toKeyword(entity);
    }

    @Override
    public boolean deleteKeyword(String orcid, Long putCode, boolean checkSource) {
        ProfileKeywordEntity entity = profileKeywordDao.getProfileKeyword(orcid, putCode);

        if (checkSource) {
            SourceEntity existingSource = entity.getSource();
            orcidSecurityManager.checkSource(existingSource);
        }

        try {
            profileKeywordDao.deleteProfileKeyword(entity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public Keyword createKeyword(String orcid, Keyword keyword) {
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
    public Keyword updateKeyword(String orcid, Long putCode, Keyword keyword) {
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
            throw new ApplicationException();
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
    public Keywords updateKeywords(String orcid, Keywords keywords, Visibility defaultVisibility) {
        List<ProfileKeywordEntity> existingKeywordsList = profileKeywordDao.getProfileKeywors(orcid);        
        // Delete the deleted ones
        for (ProfileKeywordEntity existing : existingKeywordsList) {
            boolean deleteMe = true;
            if(keywords.getKeywords() != null) {
                for (Keyword updatedOrNew : keywords.getKeywords()) {
                    if (existing.getId().equals(updatedOrNew.getPutCode())) {
                        deleteMe = false;
                        break;
                    }
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
                    // Update the existing ones
                    for (ProfileKeywordEntity existingKeyword : existingKeywordsList) {
                        if (existingKeyword.getId().equals(updatedOrNew.getPutCode())) {
                            existingKeyword.setLastModified(new Date());
                            existingKeyword.setVisibility(updatedOrNew.getVisibility());
                            existingKeyword.setKeywordName(updatedOrNew.getContent());
                            existingKeyword.setDisplayIndex(updatedOrNew.getDisplayIndex());
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
                    newKeyword.setVisibility(updatedOrNew.getVisibility());
                    newKeyword.setDisplayIndex(updatedOrNew.getDisplayIndex());
                    profileKeywordDao.persist(newKeyword);

                }
            }
        }

        if (defaultVisibility != null) {
            updateKeywordsVisibility(orcid, defaultVisibility);
        }

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
        org.orcid.jaxb.model.common_rc2.Visibility incomingKeywordVisibility = entity.getVisibility();
        org.orcid.jaxb.model.common_rc2.Visibility defaultKeywordVisibility = profile.getKeywordsVisibility() == null
                ? org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.KEYWORD_DEFAULT.getVisibility().value())
                : org.orcid.jaxb.model.common_rc2.Visibility.fromValue(profile.getKeywordsVisibility().value());
        if (profile.getClaimed() != null && profile.getClaimed()) {
            if (defaultKeywordVisibility.isMoreRestrictiveThan(incomingKeywordVisibility)) {
                entity.setVisibility(defaultKeywordVisibility);
            }
        } else if (incomingKeywordVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE);
        }
    }
}
