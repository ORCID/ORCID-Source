/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.crossref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

/**
 * 
 * @author Will Simpson
 * 
 */
public class CrossRefMetadataTest {

    @Test
    public void testDeserialize() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        CrossRefMetadata metadata = mapper.readValue(getClass().getResourceAsStream("example_crossref_metadata.json"), CrossRefMetadata.class);
        assertNotNull(metadata);
        assertEquals("10.1017/CBO9780511523816.003", metadata.getDoi());
        assertEquals(3.9350538f, metadata.getScore(), 0.00000005f);
        assertEquals(100f, metadata.getNormalizedScore(), 0f);
        assertEquals("Spanish agriculture: the long view", metadata.getTitle());
        assertEquals("Simpson, J &amp; Simpson, J, 2009, , Cambridge University Press, Cambridge.", metadata.getFullCitation());
    }

    @Test
    public void testDeserializeList() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<CrossRefMetadata> metadata = mapper.readValue(getClass().getResourceAsStream("example_crossref_metadata_list.json"),
                new TypeReference<List<CrossRefMetadata>>() {
                });
        assertNotNull(metadata);
        assertEquals("10.1017/CBO9780511523816.001", metadata.get(0).getDoi());
        assertEquals("10.1017/CBO9780511523816.003", metadata.get(1).getDoi());
    }

}
