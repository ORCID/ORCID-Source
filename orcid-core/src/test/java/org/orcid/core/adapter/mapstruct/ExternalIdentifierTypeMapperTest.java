package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.orcid.core.adapter.mapstruct.ExternalIdentifierTypeMapper;

public final class ExternalIdentifierTypeMapperTest {
   
    ExternalIdentifierTypeMapper converter = Mappers.getMapper(ExternalIdentifierTypeMapper.class);

    @Test
    public void testConvertTo() {
        assertEquals("SOMETHING", converter.convertTo("something"));
        assertEquals("GRANT_NUMBER", converter.convertTo("grant_number"));
        assertEquals("ERM_WHAT_ELSE", converter.convertTo("erm-what-else"));
    }

    @Test
    public void testConvertFrom() {
        assertEquals("something", converter.convertFrom("SOMETHING"));
        assertEquals("grant_number", converter.convertFrom("GRANT_NUMBER"));
        assertEquals("erm-what-else", converter.convertFrom("ERM_WHAT_ELSE"));
    }

}