/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.utils.v3.identifiers;

import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class DOINormalizer implements Normalizer {

    private static final List<String> canHandle = Lists.newArrayList("doi");

    @Override
    public List<String> canHandle() {
        return canHandle;
    }

    @Override
    public String normalise(String type, String value) {
        if (!type.equals("doi"))
            return value;
        String returnValue = value;
        returnValue = returnValue.replace("https://", "");
        returnValue = returnValue.replace("http://", "");
        returnValue = returnValue.replace("doi:", "");
        returnValue = returnValue.replace("dx.doi.org/", "");
        returnValue = returnValue.replace("doi.org/", "");
        return returnValue;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
