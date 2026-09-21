package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.v3.release.common.Url;

public class UrlMapperV3Test {

    private final UrlMapperV3 mapper = Mappers.getMapper(UrlMapperV3.class);

    @Test
    public void blankValuesMapToNull() {
        Url url = new Url();
        url.setValue(" ");

        assertNull(mapper.map(url));
        assertNull(mapper.map(" "));
    }

    @Test
    public void valuesAreTrimmedInBothDirections() {
        Url url = new Url();
        url.setValue(" https://orcid.org ");

        assertEquals("https://orcid.org", mapper.map(url));
        assertEquals("https://orcid.org", mapper.map(" https://orcid.org ").getValue());
    }
}