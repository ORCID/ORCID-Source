package org.orcid.core.manager.impl;

import java.util.HashMap;

import javax.annotation.Resource;

import org.orcid.core.manager.ActivityManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.pojo.ajaxForm.Work;
import org.springframework.cache.annotation.Cacheable;

public class ActivityManagerImpl extends Object implements ActivityManager {

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    public String createKey(OrcidProfile profile) {
        if (profile == null) return null;
        return profile.getOrcid().getValue() + "-" + profile.getOrcidHistory().getLastModifiedDate()
                .getValue().toXMLFormat();
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
