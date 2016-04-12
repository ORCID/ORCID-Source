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

import org.orcid.core.manager.BiographyManager;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.persistence.dao.BiographyDao;
import org.orcid.persistence.jpa.entities.BiographyEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class BiographyManagerImpl implements BiographyManager {

    @Resource
    private BiographyDao biographyDao;

    @Override
    public BiographyEntity getBiography(String orcid) {
        try {
            return biographyDao.getBiography(orcid);
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public boolean updateBiography(String orcid, Biography bio) {
        if (bio == null || PojoUtil.isEmpty(bio.getContent()) || bio.getVisibility() == null) {
            return false;
        }
        return biographyDao.updateBiography(orcid, bio.getContent(), bio.getVisibility());
    }

    @Override
    public void createBiography(String orcid, Biography bio) {
        if (bio == null || PojoUtil.isEmpty(bio.getContent()) || bio.getVisibility() == null) {
            return;
        }

        biographyDao.createBiography(orcid, bio.getContent(), bio.getVisibility());
    }

}
