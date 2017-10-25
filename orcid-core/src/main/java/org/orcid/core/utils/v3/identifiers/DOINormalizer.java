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

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class DOINormalizer implements Normalizer {

    @Override
    public String normalise(String type, String value) {
        if (!type.equals("doi"))
            return value;
        String returnValue = value;
        returnValue = returnValue.replace("https://", "");
        returnValue = returnValue.replace("http://", "");
        returnValue = returnValue.replace("doi:", "");
        returnValue = returnValue.replace("doi.org/", "");
        returnValue = returnValue.replace("dx.doi.org/", "");
        return returnValue;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
