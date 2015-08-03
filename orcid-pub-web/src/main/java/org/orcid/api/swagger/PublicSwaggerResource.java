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
package org.orcid.api.swagger;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.orcid.api.common.swagger.SwaggerJSONResource;
import org.orcid.core.api.OrcidApiConstants;

import io.swagger.annotations.Api;

/**
 * Resource that serves swagger.json
 * 
 * @author tom
 *
 */
@Path(OrcidApiConstants.SWAGGER_PATH)
@Api(OrcidApiConstants.SWAGGER_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class PublicSwaggerResource extends SwaggerJSONResource {}