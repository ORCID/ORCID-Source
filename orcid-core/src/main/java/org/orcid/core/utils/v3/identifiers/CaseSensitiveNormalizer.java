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

import java.util.Locale;

import javax.annotation.Resource;

import org.orcid.core.manager.IdentifierTypeManager;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class CaseSensitiveNormalizer implements Normalizer{

    @Resource
    IdentifierTypeManager idman;

    @Override
    public String normalise(String type, String value) {
        if (!idman.fetchIdentifierTypeByDatabaseName(type, Locale.ENGLISH).getCaseSensitive()){
            return value.toLowerCase();
        }
        return value;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
