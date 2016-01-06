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
package org.orcid.api.common.delegator;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.tuple.Pair;

public interface CerifApiServiceDelgator {

    Response getPerson(String id);

    Response getPublication(String orcid, Long id);

    Response getProduct(String orcid, Long id);

    Response getEntities();

    Response getSemantics();
    
    Pair<String, Long> parseActivityID(String id) throws IllegalArgumentException;

}
