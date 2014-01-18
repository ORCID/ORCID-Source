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

import java.util.HashMap;

import javax.annotation.Resource;

import org.orcid.core.manager.ActivityCacheManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.pojo.ajaxForm.Work;
import org.springframework.cache.annotation.Cacheable;

public class ActivityCacheManagerImpl extends Object implements ActivityCacheManager {

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    public String createKey(OrcidProfile profile) {
        if (profile == null)
            return null;
        return profile.getOrcid().getValue() + "-" + profile.getOrcidHistory().getLastModifiedDate().getValue().toXMLFormat();
    }

    @Cacheable(value = "pub-min-works-map", key = "#root.args[1]")
    public HashMap<String, Work> pubMinWorksMap(OrcidProfile profile, String key) {
        HashMap<String, Work> workMap = new HashMap<String, Work>();
        if (profile.getOrcidActivities() != null) {
            if (profile.getOrcidActivities().getOrcidWorks() != null) {
                for (OrcidWork orcidWork : profile.getOrcidActivities().getOrcidWorks().getOrcidWork()) {
                    if (Visibility.PUBLIC.equals(orcidWork.getVisibility())) {
                        workMap.put(orcidWork.getPutCode(), Work.minimizedValueOf(orcidWork));
                    }
                }
            }
        }
        return workMap;
    }
    

}
