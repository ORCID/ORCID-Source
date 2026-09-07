package org.orcid.core.utils.v3.identifiers.normalizers;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.pojo.IdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class CaseSensitiveNormalizer implements Normalizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseSensitiveNormalizer.class);

    @Autowired
    @Lazy
    private IdentifierTypeManager idman;

    private Map<String, IdentifierType> idTypeMap;

    @Override
    public List<String> canHandle() {
        return CAN_HANDLE_EVERYTHING;
    }

    private Map<String, IdentifierType> getIdTypeMap() {
        if (idTypeMap == null && idman != null) {
            idTypeMap = idman.fetchIdentifierTypesByAPITypeName(Locale.ENGLISH);
        }
        return idTypeMap;
    }

    @Override
    public String normalise(String apiTypeName, String value) {
        if (apiTypeName == null || value == null) {
            return value;
        }
        Map<String, IdentifierType> map = getIdTypeMap();
        if (map != null) {
            IdentifierType t = map.get(apiTypeName);
            if (t != null && Boolean.FALSE.equals(t.getCaseSensitive())) {
                return value.toLowerCase();
            }
        }
        return value;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}