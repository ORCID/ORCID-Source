package org.orcid.frontend.web.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

import java.io.IOException;

public class SwitchUserGrantedAuthorityDeserializer extends JsonDeserializer<SwitchUserGrantedAuthority>  {
    @Override
    public SwitchUserGrantedAuthority deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode jsonNode = mapper.readTree(p);
        JsonNode sourceNode = jsonNode.get("source");
        JsonNode authorityNode = jsonNode.get("authority");

        String role = authorityNode.asText();
        UsernamePasswordAuthenticationToken authentication = mapper.convertValue(sourceNode, UsernamePasswordAuthenticationToken.class);

        return new SwitchUserGrantedAuthority(role, authentication);
    }
}
