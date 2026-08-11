package org.orcid.core.adapter.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.orcid.jaxb.model.common_v2.Iso3166Country;

public class Iso3166CountryConverterTest {

    private final Iso3166CountryConverter converter = new Iso3166CountryConverter();

    @Test
    public void convertTo_returnsName() {
        assertEquals("US", converter.convertTo(Iso3166Country.US, null));
        assertEquals("GB", converter.convertTo(Iso3166Country.GB, null));
    }

    @Test
    public void convertTo_null_returnsNull() {
        assertNull(converter.convertTo(null, null));
    }

    @Test
    public void convertFrom_validCode_returnsEnum() {
        assertEquals(Iso3166Country.US, converter.convertFrom("US", null));
        assertEquals(Iso3166Country.GB, converter.convertFrom("GB", null));
    }

    @Test
    public void convertFrom_null_returnsNull() {
        assertNull(converter.convertFrom(null, null));
    }

    @Test
    public void convertFrom_emptyString_returnsNull() {
        assertNull(converter.convertFrom("", null));
    }
}
