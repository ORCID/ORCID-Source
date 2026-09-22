package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.common_v2.Title;

public class TitleMapperV2Test {

    private final TitleMapperV2 mapper = Mappers.getMapper(TitleMapperV2.class);

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
