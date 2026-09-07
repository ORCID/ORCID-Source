package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.orcid.core.adapter.mapstruct.AdditionalInfoJsonMapper;

public class AdditionalInfoJsonMapperTest {

    @Test
    public void fromJsonShouldReturnNullForNullValue() {
        Map<String, Object> result = AdditionalInfoJsonMapper.INSTANCE.fromJson(null);
        assertNull(result);
    }

    @Test
    public void fromJsonShouldParseMap() {
        Map<String, Object> result = AdditionalInfoJsonMapper.INSTANCE.fromJson("{\"foo\":\"bar\",\"count\":2}");
        assertNotNull(result);
        assertEquals("bar", result.get("foo"));
        assertEquals(Integer.valueOf(2), result.get("count"));
    }

    @Test
    public void toJsonShouldSerializeMap() {
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("foo", "bar");
        input.put("count", Integer.valueOf(2));

        String json = AdditionalInfoJsonMapper.INSTANCE.toJson(input);
        Map<String, Object> output = AdditionalInfoJsonMapper.INSTANCE.fromJson(json);

        assertNotNull(output);
        assertEquals("bar", output.get("foo"));
        assertEquals(Integer.valueOf(2), output.get("count"));
    }
}
