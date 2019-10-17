package org.orcid.core.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.orcid.jaxb.model.common_v2.Day;
import org.orcid.jaxb.model.common_v2.FuzzyDate;
import org.orcid.jaxb.model.common_v2.Month;
import org.orcid.jaxb.model.common_v2.Year;

public class FuzzyDateUtilsTest {

    @Test
    public void compareToTest() {
        FuzzyDate a = new FuzzyDate();
        FuzzyDate b = new FuzzyDate();

        // Equals
        a.setYear(new Year(2010));
        b.setYear(new Year(2010));

        assertTrue(FuzzyDateUtils.compareTo(a, b) == 0);

        a.setMonth(new Month(1));
        b.setMonth(new Month(1));

        assertTrue(FuzzyDateUtils.compareTo(a, b) == 0);

        a.setDay(new Day(1));
        b.setDay(new Day(1));

        assertTrue(FuzzyDateUtils.compareTo(a, b) == 0);

        // Before and After test
        a = new FuzzyDate();
        b = new FuzzyDate();

        a.setYear(new Year(2009));
        b.setYear(new Year(2010));

        assertTrue(FuzzyDateUtils.compareTo(a, b) < 0);
        assertTrue(FuzzyDateUtils.compareTo(b, a) > 0);

        a.setYear(new Year(2010));
        b.setYear(new Year(2010));
        b.setMonth(new Month(12));
        b.setDay(new Day(30));

        assertTrue(FuzzyDateUtils.compareTo(a, b) < 0);
        assertTrue(FuzzyDateUtils.compareTo(b, a) > 0);

        a.setYear(new Year(2010));
        a.setMonth(new Month(12));
        b.setYear(new Year(2010));
        b.setMonth(new Month(12));
        b.setDay(new Day(30));

        assertTrue(FuzzyDateUtils.compareTo(a, b) < 0);
        assertTrue(FuzzyDateUtils.compareTo(b, a) > 0);

        a.setYear(new Year(2010));
        a.setMonth(new Month(12));
        a.setDay(new Day(29));
        b.setYear(new Year(2010));
        b.setMonth(new Month(12));
        b.setDay(new Day(30));

        assertTrue(FuzzyDateUtils.compareTo(a, b) < 0);
        assertTrue(FuzzyDateUtils.compareTo(b, a) > 0);
    }

}
