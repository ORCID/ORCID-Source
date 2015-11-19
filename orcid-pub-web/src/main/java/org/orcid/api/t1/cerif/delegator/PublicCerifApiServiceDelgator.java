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
package org.orcid.api.t1.cerif.delegator;

import javax.ws.rs.core.Response;

public interface PublicCerifApiServiceDelgator {

    Response getPerson(String id);

    Response getPublication(String id);

    Response getProduct(String id);

    Response getEntities();

    Response getSemantics();

}
