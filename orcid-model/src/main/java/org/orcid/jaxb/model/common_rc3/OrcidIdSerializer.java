package org.orcid.jaxb.model.common_rc3;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidIdSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) value;
        if (list.isEmpty()) {
            return;
        }
        String string = list.get(0);
        jgen.writeString(string);
    }

}
