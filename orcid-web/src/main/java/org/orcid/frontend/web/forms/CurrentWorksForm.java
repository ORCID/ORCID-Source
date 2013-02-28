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
package org.orcid.frontend.web.forms;

import java.util.ArrayList;
import java.util.List;

import org.orcid.core.crossref.CrossRefMetadata;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.springframework.util.AutoPopulatingList;

public class CurrentWorksForm {

    private AutoPopulatingList<CurrentWork> currentWorks;

    public CurrentWorksForm() {
    }

    public CurrentWorksForm(OrcidProfile orcidProfile) {
        OrcidWorks orcidWorks = orcidProfile.retrieveOrcidWorks();
        if (orcidWorks != null) {
            List<CurrentWork> currentWorks = new ArrayList<CurrentWork>();
            for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
                currentWorks.add(new CurrentWork(orcidWork));
            }
            setCurrentWorks(currentWorks);
        }
    }

    public CurrentWorksForm(List<CrossRefMetadata> metadatas) {
        List<CurrentWork> currentWorks = new ArrayList<CurrentWork>();
        for (CrossRefMetadata metadata : metadatas) {
            currentWorks.add(new CurrentWork(metadata));
        }
        setCurrentWorks(currentWorks);
    }

    public List<CurrentWork> getCurrentWorks() {
        return currentWorks;
    }

    public void setCurrentWorks(List<CurrentWork> publications) {
        this.currentWorks = new AutoPopulatingList<CurrentWork>(publications, CurrentWork.class);
    }

    public List<CurrentWork> getSelectedCurrentWorks() {
        List<CurrentWork> selected = new ArrayList<CurrentWork>();
        for (CurrentWork currentWork : currentWorks) {
            if (currentWork.isSelected()) {
                selected.add(currentWork);
            }
        }
        return selected;
    }

    public OrcidProfile getOrcidProfileWithSelectedOnly(String orcid) {
        return getOrcidProfile(orcid, getSelectedCurrentWorks());
    }

    public OrcidProfile getOrcidProfile(String orcid) {
        return getOrcidProfile(orcid, currentWorks);
    }

    public OrcidProfile getOrcidProfile(String orcid, List<CurrentWork> works) {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcid(orcid);
        if (works != null && !works.isEmpty()) {
            OrcidActivities orcidActivities = new OrcidActivities();
            orcidProfile.setOrcidActivities(orcidActivities);
            OrcidWorks orcidWorks = new OrcidWorks();
            orcidActivities.setOrcidWorks(orcidWorks);
            for (CurrentWork currentWork : works) {
                orcidWorks.getOrcidWork().add(currentWork.getOrcidWork());
            }
        }
        return orcidProfile;
    }

}
