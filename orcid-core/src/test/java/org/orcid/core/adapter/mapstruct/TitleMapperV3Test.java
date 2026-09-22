package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.v3.release.common.Title;

public class TitleMapperV3Test {

    private final TitleMapperV3 mapper = Mappers.getMapper(TitleMapperV3.class);

    @Test
    public void blankValuesMapToNull() {
        Title title = new Title();
        title.setContent(" ");

        assertNull(mapper.map(title));
        assertNull(mapper.map(" "));
        assertNull(mapper.map((Title) null));
        assertNull(mapper.map((String) null));
    }

    @Test
    public void valuesAreTrimmedInBothDirections() {
        Title title = new Title();
        title.setContent(" A Great Title ");

        assertEquals("A Great Title", mapper.map(title));
        assertEquals("A Great Title", mapper.map(" A Great Title ").getContent());
    }
}
