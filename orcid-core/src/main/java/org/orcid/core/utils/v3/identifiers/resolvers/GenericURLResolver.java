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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.hsqldb.lib.StringUtil;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.NormalizationService;
import org.orcid.core.utils.v3.identifiers.ResolverCache;
import org.springframework.stereotype.Component;

@Component
public class GenericURLResolver implements Resolver {

    @Resource
    IdentifierTypeManager idman;

    @Resource
    NormalizationService normalizationService;

    @Resource
    ResolverCache cache;

    List<String> types;

    @PostConstruct
    public void init() {
        types = new ArrayList<String>(idman.fetchIdentifierTypesByAPITypeName(Locale.ENGLISH).keySet());
        types.remove("isbn");
    }

    @Override
    public List<String> canHandle() {
        return types;
    }

    /**
     * Checks for a http 200 1. if value is a URL, try that 2. If the value is
     * in the providedURL, try using that 3. Try normalizing the value and
     * creating a URL using the resolution prefix
     */
    @Override
    public boolean canResolve(String apiTypeName, String value, String providedURL) {
        if (StringUtil.isEmpty(value))
            return false;

        // if value is a URL, try that
        if (value.startsWith("http")) {
            if (cache.isHttp200(value))
                return true;
        }

        // If the value is in the providedURL, try using that
        if (!StringUtil.isEmpty(providedURL) && providedURL.toLowerCase().contains(value.toLowerCase()) && !providedURL.equals(value)) {
            if (cache.isHttp200(providedURL))
                return true;
        }

        // Try normalizing the value and creating a URL using the resolution
        // prefix
        String normUrl = normalizationService.generateNormalisedURL(apiTypeName, value);
        if (!StringUtils.isEmpty(normUrl)) {
            if (!normUrl.equals(providedURL) && cache.isHttp200(normUrl))
                return true;
        }

        return false;
    }

}
