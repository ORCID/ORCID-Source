package org.orcid.jaxb.model.common_rc3;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.orcid.model.utils.DateUtils;

public class LastModifiedDateTest {

    @Test
    public void testWithLongs() {
        LastModifiedDate empty = new LastModifiedDate();
        LastModifiedDate _1000 = new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(1000));
        LastModifiedDate _1001 = new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(1001));
        assertTrue(_1000.after(null));
        assertTrue(_1000.after(empty));
        assertFalse(empty.after(_1000));
        assertFalse(empty.after(empty));
        assertFalse(_1000.after(_1000));
        assertTrue(_1001.after(_1000));
        assertFalse(_1000.after(_1001));
    }

    @Test
    public void testWithDates() {
        LastModifiedDate empty = new LastModifiedDate();
        LastModifiedDate _1000 = new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(new Date(1000)));
        LastModifiedDate _1001 = new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(new Date(1001)));
        assertTrue(_1000.after(null));
        assertTrue(_1000.after(empty));
        assertFalse(empty.after(_1000));
        assertFalse(empty.after(empty));
        assertFalse(_1000.after(_1000));
        assertTrue(_1001.after(_1000));
        assertFalse(_1000.after(_1001));
    }
}
