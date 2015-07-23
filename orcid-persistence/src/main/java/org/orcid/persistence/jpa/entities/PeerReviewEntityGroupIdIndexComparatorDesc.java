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
package org.orcid.persistence.jpa.entities;

import java.io.Serializable;
import java.util.Comparator;

import org.orcid.utils.OrcidStringUtils;

/**
 * 
 * Comparator that compares OrcidEntity objects based on their group id.
 * 
 * Spring Sort annotation didn't seem to have descending function.
 * Sort by index and then compare value
 * 
 * @author rcpeters
 * 
 */
public class PeerReviewEntityGroupIdIndexComparatorDesc<T> implements Comparator<PeerReviewEntity>, Serializable {

    private static final long serialVersionUID = 1L;
    @Override
    public int compare(PeerReviewEntity o1, PeerReviewEntity o2) {
        String groupId = o1.getGroupId();
        String otherGroupId = o2.getGroupId();
        return OrcidStringUtils.compareStrings(groupId, otherGroupId);        
    }
}