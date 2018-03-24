package org.orcid.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

/**
 * 
 * @author Will Simpson
 * 
 */
public class DateUtilsTest {

    @Test
    public void testConvertStringToXMLGregorianCalendar() {
        assertEquals("2011-03-14T02:34:16", DateUtils.convertToXMLGregorianCalendar("2011-03-14T02:34:16").toXMLFormat());
        assertEquals("2002-03-14", DateUtils.convertToXMLGregorianCalendar("2002-03-14").toXMLFormat());
        assertEquals("2002-03-07", DateUtils.convertToXMLGregorianCalendar("2002-03-07").toXMLFormat());
        assertEquals("2002-03-07", DateUtils.convertToXMLGregorianCalendar("2002-03-7").toXMLFormat());
        assertEquals("2000-09", DateUtils.convertToXMLGregorianCalendar("2000-09").toXMLFormat());
        assertEquals("2000-09", DateUtils.convertToXMLGregorianCalendar("2000-9").toXMLFormat());
        assertEquals("1984-03", DateUtils.convertToXMLGregorianCalendar("1984-21").toXMLFormat());
        assertEquals("1984-10", DateUtils.convertToXMLGregorianCalendar("1984-34").toXMLFormat());
        assertEquals("2002", DateUtils.convertToXMLGregorianCalendar("2002").toXMLFormat());
        assertNull(DateUtils.convertToXMLGregorianCalendar((String) null));
        assertNull(DateUtils.convertToXMLGregorianCalendar("Select a date"));
    }

    @Test
    public void testConvertImpossibleDateStringToXMLGregorianCalendar() {
        String impossibleDateString = "1987-02-29";
        XMLGregorianCalendar xmlGregorianCalendar = DateUtils.convertToXMLGregorianCalendar(impossibleDateString);
        assertNull(xmlGregorianCalendar);
    }

}
