package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.orcid.core.adapter.mapstruct.Iso3166CountryMapperV2;
import org.orcid.jaxb.model.common_v2.Iso3166Country;

public class Iso3166CountryMapperV2Test {

    private final Iso3166CountryMapperV2 converter = Iso3166CountryMapperV2.INSTANCE;

    @Test
    public void convertTo_returnsName() {
        assertEquals("US", converter.convertTo(Iso3166Country.US));
        assertEquals("GB", converter.convertTo(Iso3166Country.GB));
    }

    @Test
    public void convertTo_null_returnsNull() {
        assertNull(converter.convertTo(null));
    }

    @Test
    public void convertFrom_validCode_returnsEnum() {
        assertEquals(Iso3166Country.US, converter.convertFrom("US"));
        assertEquals(Iso3166Country.GB, converter.convertFrom("GB"));
    }

    @Test
    public void convertFrom_null_returnsNull() {
        assertNull(converter.convertFrom(null));
    }

    @Test
    public void convertFrom_emptyString_returnsNull() {
        assertNull(converter.convertFrom(""));
    }
}
