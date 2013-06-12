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
package org.orcid.persistence.jpa.entities;

import static org.junit.Assert.assertEquals;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.SequenceType;

/**
 * 
 * @author Will Simpson
 *
 */
public class WorkContributorEntityTest {

    @Test
    public void testInSortedSet() {
        SortedSet<WorkContributorEntity> set = new TreeSet<>();

        WorkContributorEntity bce1 = new WorkContributorEntity();
        bce1.setCreditName("Gaius Julius Caesar");
        bce1.setContributorRole(ContributorRole.AUTHOR);
        bce1.setSequence(SequenceType.FIRST);
        set.add(bce1);

        WorkContributorEntity bce2 = new WorkContributorEntity();
        bce2.setCreditName("Marcus Licinius Crassus");
        bce2.setContributorRole(ContributorRole.AUTHOR);
        bce2.setSequence(SequenceType.ADDITIONAL);
        set.add(bce2);
        assertEquals(2, set.size());

        WorkContributorEntity bce3 = new WorkContributorEntity();
        bce3.setCreditName("Gnaeus Pompeius Magnus");
        bce3.setContributorRole(ContributorRole.AUTHOR);
        bce3.setSequence(SequenceType.ADDITIONAL);
        set.add(bce3);
        assertEquals(3, set.size());
    }

}
