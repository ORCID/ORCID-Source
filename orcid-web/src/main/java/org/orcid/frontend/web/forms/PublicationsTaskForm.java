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

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.AutoPopulatingList;

public class PublicationsTaskForm {

    private String orcid;

    private AutoPopulatingList<Publication> publications;

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = new AutoPopulatingList<Publication>(publications, Publication.class);
    }

    public int[] getPositions() {
        List<Integer> positionsToDelete = new ArrayList<Integer>(publications.size());
        int position = 0;
        for (Publication publication : publications) {
            if (publication.isSelected()) {
                positionsToDelete.add(position);
            }
            position++;
        }
        return ArrayUtils.toPrimitive(positionsToDelete.toArray(new Integer[0]));
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

}
