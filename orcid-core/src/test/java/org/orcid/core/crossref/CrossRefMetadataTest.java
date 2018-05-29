package org.orcid.core.crossref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
