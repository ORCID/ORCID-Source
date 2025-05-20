package org.orcid.frontend.web.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

import java.io.IOException;

public class SwitchUserGrantedAuthorityDeserializer extends JsonDeserializer<SwitchUserGrantedAuthority>  {
    @Override
    public SwitchUserGrantedAuthority deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode jsonNode = mapper.readTree(p);
        JsonNode authorityNode = jsonNode.get("authority");
        JsonNode sourceNode = jsonNode.get("source");
        JsonNode sourceNodeClassNode = sourceNode.get("@class");

        String role = authorityNode.asText();

        AbstractAuthenticationToken authentication;

        if(sourceNodeClassNode != null) {
            switch (sourceNodeClassNode.textValue()) {
                case "org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken":
                    authentication = mapper.convertValue(sourceNode, PreAuthenticatedAuthenticationToken.class);
                    break;
                case "org.springframework.security.authentication.UsernamePasswordAuthenticationToken":
                    authentication = mapper.convertValue(sourceNode, UsernamePasswordAuthenticationToken.class);
                    break;
                default:
                    throw new IllegalArgumentException("Cannot deserialize SwitchUserGrantedAuthority, authority node of class " + sourceNodeClassNode.textValue() + " cannot be handled");
            }
        } else {
            throw new IllegalArgumentException("Cannot deserialize SwitchUserGrantedAuthority, source node of class doesnt have the @class attribute. " + sourceNode.asText());
        }

        return new SwitchUserGrantedAuthority(role, authentication);
    }
}
