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

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.springframework.core.Ordered;

public interface Normalizer extends Ordered{

    public static final List<String> CAN_HANDLE_EVERYTHING = Lists.newArrayList();
    
    /** A list of identifier types (using their API names) that this Normalizer can handle
     * An empty list implies all identifier types.
     * 
     */
    public List<String> canHandle();

    
    public String normalise(String apiTypeName, String value);
}
