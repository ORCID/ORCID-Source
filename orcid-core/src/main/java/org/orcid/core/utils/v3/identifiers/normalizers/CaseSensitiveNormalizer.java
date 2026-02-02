package org.orcid.core.utils.v3.identifiers.normalizers;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.orcid.core.adapter.v3.converter.ContributorsRolesAndSequencesConverter;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.pojo.IdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class CaseSensitiveNormalizer implements Normalizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseSensitiveNormalizer.class);

    @Resource
    IdentifierTypeManager idman;

    @Override
    public List<String> canHandle() {
        return CAN_HANDLE_EVERYTHING;
    }

    private Map<String, IdentifierType> idTypeMap;

    @PostConstruct
    public void init() {
        this.idTypeMap = idman.fetchIdentifierTypesByAPITypeName(Locale.ENGLISH);
        LOGGER.info("Initialised idTypeMap on CaseSensitiveNormalizer");
    }

    @Override
    public String normalise(String apiTypeName, String value) {
        IdentifierType t = this.idTypeMap.get(apiTypeName);
        if (t != null && !t.getCaseSensitive()){
            return value.toLowerCase();
        }
        return value;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


}
