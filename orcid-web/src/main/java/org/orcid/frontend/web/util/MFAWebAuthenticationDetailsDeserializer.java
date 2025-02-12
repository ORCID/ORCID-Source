package org.orcid.frontend.web.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orcid.authorization.authentication.MFAWebAuthenticationDetails;

import java.io.IOException;

public class MFAWebAuthenticationDetailsDeserializer extends JsonDeserializer<MFAWebAuthenticationDetails> {
    @Override
    public MFAWebAuthenticationDetails deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = mapper.readTree(jsonParser);
        JsonNode verificationCodeNode = jsonNode.get("verificationCode");
        JsonNode recoveryCodeNode = jsonNode.get("recoveryCode");
        JsonNode remoteAddressNode = jsonNode.get("remoteAddress");
        JsonNode sessionIdNode = jsonNode.get("sessionId");

        String verificationCode = (verificationCodeNode != null && verificationCodeNode.isTextual()) ? verificationCodeNode.asText() : null;
        String recoveryCode = (recoveryCodeNode != null && recoveryCodeNode.isTextual()) ? recoveryCodeNode.asText() : null;
        String remoteAddress = (remoteAddressNode != null && remoteAddressNode.isTextual()) ? remoteAddressNode.asText() : null;
        String sessionId = (sessionIdNode != null && sessionIdNode.isTextual()) ? sessionIdNode.asText() : null;

        return new MFAWebAuthenticationDetails(remoteAddress, sessionId, verificationCode, recoveryCode);
    }
}
