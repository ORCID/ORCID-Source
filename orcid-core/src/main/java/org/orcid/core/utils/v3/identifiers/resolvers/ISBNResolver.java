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
package org.orcid.core.utils.v3.identifiers.resolvers;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.hsqldb.lib.StringUtil;
import org.orcid.core.utils.v3.identifiers.NormalizationService;
import org.orcid.core.utils.v3.identifiers.ResolverCache;
import org.orcid.core.utils.v3.identifiers.normalizers.ISBNNormalizer;
import org.springframework.stereotype.Component;

@Component
public class ISBNResolver implements Resolver {

    @Resource
    NormalizationService normalizationService;

    @Resource
    ISBNNormalizer norm;

    @Resource
    ResolverCache cache;

    public List<String> canHandle() {
        return norm.canHandle();
    }

    @Override
    public boolean canResolve(String apiTypeName, String value, String providedURL) {
        // If the value is in the providedURL, try using that
        if (!StringUtil.isEmpty(providedURL) && providedURL.toLowerCase().contains(value.toLowerCase()) && !providedURL.equals(value)) {
            if (cache.isHttp200(providedURL)) // assuming not worldcat!
                return true;
        }

        // Try normalizing value & creating a URL using the resolution prefix
        // this assumes we're using worldcat - 303 on success, 200 on fail
        String normUrl = normalizationService.generateNormalisedURL(apiTypeName, value);
        if (!StringUtils.isEmpty(normUrl)) {
            if (!normUrl.equals(providedURL) && cache.isHttp303(normUrl))
                return true;
        }

        return false;
    }

}
