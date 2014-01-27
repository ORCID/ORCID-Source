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

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.springframework.util.AutoPopulatingList;

/**
 * 
 * @author Will Simpson
 * 
 */
public class CrossRefPublicationsForm {

    private AutoPopulatingList<Publication> publications;

    private String orcid;

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = new AutoPopulatingList<Publication>(publications, Publication.class);
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcidIdentifier(String orcid) {
        this.orcid = orcid;
    }

    public OrcidProfile getOrcidProfile() {
        OrcidProfile profile = new OrcidProfile();
        profile.setOrcidIdentifier(orcid);
        OrcidWorks orcidWorks = new OrcidWorks();
        profile.setOrcidWorks(orcidWorks);
        orcidWorks.getOrcidWork().addAll(getOrcidWorks());
        return profile;
    }

    private List<OrcidWork> getOrcidWorks() {
        List<Publication> selectedPublications = getSelectedPublications();
        List<OrcidWork> orcidWorks = new ArrayList<OrcidWork>(selectedPublications.size());
        for (Publication publication : selectedPublications) {
            orcidWorks.add(publication.getOrcidWork());
        }
        return orcidWorks;
    }

    public List<Publication> getSelectedPublications() {
        List<Publication> selectedPublications = new ArrayList<Publication>(publications.size());
        for (Publication publication : publications) {
            if (publication.isSelected()) {
                selectedPublications.add(publication);
            }
        }
        return selectedPublications;
    }

}
