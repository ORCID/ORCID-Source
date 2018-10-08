package org.orcid.pojo.ajaxForm;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.orcid.jaxb.model.v3.rc2.common.CreatedDate;
import org.orcid.jaxb.model.v3.rc2.common.Day;
import org.orcid.jaxb.model.v3.rc2.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc2.common.Month;
import org.orcid.jaxb.model.v3.rc2.common.Year;
import org.orcid.jaxb.model.v3.rc2.record.Affiliation;
import org.orcid.jaxb.model.v3.rc2.record.Employment;
import org.orcid.utils.DateUtils;

public class PojoUtilTest {

    @Test
    public void affiliationsCreateDateSortString_StartAndEndDateExistsTest() {
        Affiliation aff = new Employment();
        FuzzyDate start = new FuzzyDate();
        FuzzyDate end = new FuzzyDate();
        start.setDay(new Day(1));
        start.setMonth(new Month(2));
        start.setYear(new Year(3));
        end.setDay(new Day(4));
        end.setMonth(new Month(5));
        end.setYear(new Year(6));
        aff.setStartDate(start);
        aff.setEndDate(end);
        String dateSortString = PojoUtil.createDateSortString(aff);
        assertNotNull(dateSortString);
        assertEquals("X-6-05-04-3-02-01", dateSortString);
    }

    @Test
    public void affiliationsCreateDateSortString_StartAndEndDateNullTest() {
        Affiliation aff = new Employment();
        aff.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(0)));
        String dateSortString = PojoUtil.createDateSortString(aff);
        assertNotNull(dateSortString);
        assertThat(dateSortString, anyOf(is("Z-1969-12-31"), is("Z-1970-1-1")));
    }

    @Test
    public void affiliationsCreateDateSortString_StartDateOnlyTest() {
        Affiliation aff = new Employment();
        FuzzyDate start = new FuzzyDate();
        start.setDay(new Day(1));
        start.setMonth(new Month(2));
        start.setYear(new Year(3));
        aff.setStartDate(start);
        String dateSortString = PojoUtil.createDateSortString(aff);
        assertEquals("Y-3-02-01", dateSortString);
    }

    @Test
    public void affiliationsCreateDateSortString_EndDateOnlyTest() {
        Affiliation aff = new Employment();
        FuzzyDate end = new FuzzyDate();
        end.setDay(new Day(1));
        end.setMonth(new Month(2));
        end.setYear(new Year(3));
        aff.setEndDate(end);
        String dateSortString = PojoUtil.createDateSortString(aff);
        assertEquals("X-3-02-01", dateSortString);
    }

    @Test
    public void affiliationsCreateDateSortString_NullStartYearTest() {
        Affiliation aff = new Employment();
        FuzzyDate start = new FuzzyDate();
        start.setDay(new Day(1));
        start.setMonth(new Month(2));
        aff.setStartDate(start);
        String dateSortString = PojoUtil.createDateSortString(aff);
        assertEquals("Y-NaN-02-01", dateSortString);
    }

    @Test
    public void affiliationsCreateDateSortString_NullEndYearTest() {
        Affiliation aff = new Employment();
        FuzzyDate end = new FuzzyDate();
        end.setDay(new Day(1));
        end.setMonth(new Month(2));
        aff.setEndDate(end);
        String dateSortString = PojoUtil.createDateSortString(aff);
        assertEquals("X-NaN-02-01", dateSortString);
    }

    @Test
    public void affiliationsCreateDateSortString_NullStartYearNullEndYearTest() {
        Affiliation aff = new Employment();
        FuzzyDate start = new FuzzyDate();
        FuzzyDate end = new FuzzyDate();
        start.setDay(new Day(1));
        start.setMonth(new Month(2));
        end.setDay(new Day(3));
        end.setMonth(new Month(4));
        aff.setStartDate(start);
        aff.setEndDate(end);
        String dateSortString = PojoUtil.createDateSortString(aff);
        assertEquals("X-NaN-04-03-NaN-02-01", dateSortString);
    }
    
    @Test
    public void affiliationsCreateDateSortString_StartDate_NullMonthNullDayTest() {
        Affiliation aff = new Employment();
        FuzzyDate start = new FuzzyDate();
        start.setYear(new Year(2017));
        aff.setStartDate(start);
        String dateSortString = PojoUtil.createDateSortString(aff);
        assertEquals("Y-2017-00-00", dateSortString);
    }
    
    @Test
    public void affiliationsCreateDateSortString_EndDate_NullMonthNullDayTest() {
        Affiliation aff = new Employment();
        FuzzyDate end = new FuzzyDate();
        end.setYear(new Year(2017));
        aff.setEndDate(end);
        String dateSortString = PojoUtil.createDateSortString(aff);
        assertEquals("X-2017-00-00", dateSortString);
    }
    
    @Test
    public void affiliationsCreateDateSortString_StartDateAndEndDate_NullMonthNullDayTest() {
        Affiliation aff = new Employment();
        FuzzyDate start = new FuzzyDate();
        FuzzyDate end = new FuzzyDate();
        start.setYear(new Year(1970));
        end.setYear(new Year(2017));
        aff.setStartDate(start);
        aff.setEndDate(end);
        String dateSortString = PojoUtil.createDateSortString(aff);
        assertEquals("X-2017-00-00-1970-00-00", dateSortString);
    }
}
