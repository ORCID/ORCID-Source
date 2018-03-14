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

import org.apache.jena.ext.com.google.common.collect.Lists;

public interface Resolver {

    public static final List<String> CAN_HANDLE_EVERYTHING = Lists.newArrayList();

    public List<String> canHandle();

    //note these could easily be extend to turn into works:
    //public Optional<Work> resolve(String apiTypeName, String value);

    /**
     * If this resolver can handle the apiTypeName provided then attempt to
     * resolve the value.
     * 
     * @param apiTypeName
     * @param value
     * @param providedURL
     * @return
     */
    public ResolutionResult resolve(String apiTypeName, String value);

}
