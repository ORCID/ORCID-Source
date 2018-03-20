package org.orcid.core.utils.v3.identifiers.normalizers;

import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class CurieNormalizer implements Normalizer {

    private static final List<String> canHandle = Lists.newArrayList("rrid");

    @Override
    public List<String> canHandle() {
        return canHandle;
    }

    @Override
    public String normalise(String apiTypeName, String value) {
        if (!canHandle.contains(apiTypeName))
            return value;
        if (!value.startsWith(apiTypeName.toUpperCase() + ":"))
            return apiTypeName.toUpperCase() + ":" + value;
        return value;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
