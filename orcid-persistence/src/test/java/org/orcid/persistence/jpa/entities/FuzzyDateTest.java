package org.orcid.persistence.jpa.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class FuzzyDateTest {

    @Test
    public void testFuzzyDateComparison() {
        PublicationDateEntity one = new PublicationDateEntity();
        PublicationDateEntity two = null;
        //Test compare with null
        try {
            one.compareTo(two);
            fail();
        } catch (NullPointerException npe) {

        }

        //Compare with himself empty 
        assertEquals(one.compareTo(one), 0);

        //Compare empty objects
        two = new PublicationDateEntity();
        assertEquals(one.compareTo(two), 0);
        assertEquals(two.compareTo(one), 0);

        //Compare with empty object
        one.setYear(2013);
        assertEquals(one.compareTo(two), 1);
        assertEquals(two.compareTo(one), -1);

        one.setMonth(2);
        assertEquals(one.compareTo(two), 1);
        assertEquals(two.compareTo(one), -1);

        one.setDay(2);
        assertEquals(one.compareTo(two), 1);
        assertEquals(two.compareTo(one), -1);

        //Compare with himself
        assertEquals(one.compareTo(one), 0);

        //Compare with object
        two.setYear(2012);
        assertEquals(one.compareTo(two), 1);
        assertEquals(two.compareTo(one), -1);

        two.setMonth(1);
        assertEquals(one.compareTo(two), 1);
        assertEquals(two.compareTo(one), -1);

        two.setDay(1);
        assertEquals(one.compareTo(two), 1);
        assertEquals(two.compareTo(one), -1);

    }
}
