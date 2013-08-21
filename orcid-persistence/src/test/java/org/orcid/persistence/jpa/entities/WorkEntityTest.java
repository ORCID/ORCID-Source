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
import static org.junit.Assert.fail;
import org.junit.Test;

public class WorkEntityTest {

    @Test
    public void testWorkEntityComparison() {
        WorkEntity one = new WorkEntity();
        WorkEntity two = null;

        //Compare with null
        try {
            one.compareTo(two);
            fail();
        } catch (NullPointerException npe) {

        }

        //Compare with himself empty
        assertEquals(one.compareTo(one), 0);

        //Compare empty objects
        two = new WorkEntity();
        assertEquals(one.compareTo(two), -1);
        assertEquals(two.compareTo(one), -1);

        //Compare with empty object
        one.setTitle("Title 1");
        assertEquals(one.compareTo(two), 1);
        assertEquals(two.compareTo(one), -1);

        one.setPublicationDate(new PublicationDateEntity(2013, 2, 1));
        assertEquals(one.compareTo(two), 1);
        assertEquals(two.compareTo(one), -1);

        //Compare with himself
        assertEquals(one.compareTo(one), 0);

        //Compare with object
        two.setPublicationDate(new PublicationDateEntity(2013, 1, 1));
        assertEquals(one.compareTo(two), 1);
        assertEquals(two.compareTo(one), -1);

        two.setPublicationDate(new PublicationDateEntity(2013, 2, 1));
        two.setTitle("Title 0");
        assertEquals(one.compareTo(two), 1);
        assertEquals(two.compareTo(one), -1);

        two.setTitle("Title 2");
        assertEquals(one.compareTo(two), -1);
        assertEquals(two.compareTo(one), 1);
    }

    @Test
    public void testChronologicallyOrderWorkEntityTest() {
        WorkEntity.ChronologicallyOrderedWorkEntityComparator comparator = new WorkEntity.ChronologicallyOrderedWorkEntityComparator();
        WorkEntity one = new WorkEntity();
        WorkEntity two = null;

        //Compare with null
        try {
            comparator.compare(one, two);
            fail();
        } catch (NullPointerException npe) {

        }

        //Compare with himself empty
        assertEquals(comparator.compare(one, one), 0);

        //Compare empty objects
        two = new WorkEntity();
        assertEquals(comparator.compare(one, two), -1);
        assertEquals(comparator.compare(two, one), -1);

        //Compare with empty object
        one.setTitle("Title 1");
        assertEquals(comparator.compare(one, two), 1);
        assertEquals(comparator.compare(two, one), -1);

        one.setPublicationDate(new PublicationDateEntity(2013, 2, 1));
        assertEquals(comparator.compare(one, two), -1);
        assertEquals(comparator.compare(two, one), 1);

        //Compare with himself
        assertEquals(comparator.compare(one, one), 0);

        //Compare with object
        two.setPublicationDate(new PublicationDateEntity(2013, 1, 1));
        assertEquals(comparator.compare(one, two), -1);
        assertEquals(comparator.compare(two, one), 1);

        two.setPublicationDate(new PublicationDateEntity(2013, 2, 1));
        two.setTitle("Title 0");
        assertEquals(comparator.compare(one, two), 1);
        assertEquals(comparator.compare(two, one), -1);

        two.setTitle("Title 2");
        assertEquals(comparator.compare(one, two), -1);
        assertEquals(comparator.compare(two, one), 1);
    }
}
