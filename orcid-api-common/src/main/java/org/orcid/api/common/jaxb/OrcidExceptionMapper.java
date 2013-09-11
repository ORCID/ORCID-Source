/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.common.jaxb;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;
import org.orcid.api.common.OrcidApiConstants;
import org.orcid.api.common.exception.OrcidApiException;
import org.orcid.core.security.DeprecatedException;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

/**
 * orcid-api - Nov 8, 2011 - OrcidExceptionMapper
 * 
 * @author Declan Newman (declan)
 */
@Provider
@Consumes(value = { OrcidApiConstants.VND_ORCID_JSON, OrcidApiConstants.VND_ORCID_XML, OrcidApiConstants.ORCID_JSON, OrcidApiConstants.ORCID_XML,
        MediaType.APPLICATION_XML, MediaType.WILDCARD, MediaType.APPLICATION_JSON })
@Produces(value = { OrcidApiConstants.VND_ORCID_JSON, OrcidApiConstants.VND_ORCID_XML, OrcidApiConstants.ORCID_JSON, OrcidApiConstants.ORCID_XML,
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class OrcidExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidExceptionMapper.class);

    @Override
    public Response toResponse(Throwable e) {
        // Whatever exception has been caught, make sure we log it.
        LOGGER.error("An exception has occured", e);
        if (OrcidApiException.class.isAssignableFrom(e.getClass())) {
            return ((OrcidApiException) e).getResponse();
        } else if (WebApplicationException.class.isAssignableFrom(e.getClass())) {
            OrcidMessage entity = get500OrcidEntity(e);
            WebApplicationException webException = (WebApplicationException) e;
            return Response.status(webException.getResponse().getStatus()).entity(entity).build();
        } else if (AuthenticationException.class.isAssignableFrom(e.getClass())) {
            OrcidMessage entity = getOrcidEntity("Authentication problem", e);
            return Response.status(Response.Status.UNAUTHORIZED).entity(entity).build();
        } else if (SecurityException.class.isAssignableFrom(e.getClass())) {
            OrcidMessage entity = getOrcidEntity("Security problem", e);
            return Response.status(Response.Status.FORBIDDEN).entity(entity).build();
        } else if (IllegalStateException.class.isAssignableFrom(e.getClass())) {
            OrcidMessage entity = getOrcidEntity("Illegal state", e);
            return Response.status(Response.Status.FORBIDDEN).entity(entity).build();
        } else if (IllegalArgumentException.class.isAssignableFrom(e.getClass())) {
            OrcidMessage entity = getOrcidEntity("Bad Request", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        } else if (DeprecatedException.class.isAssignableFrom(e.getClass())) {
            OrcidMessage entity = getOrcidEntity("Account Deprecated", e);
            return Response.status(Response.Status.MOVED_PERMANENTLY).entity(entity).build();
        } else {
            OrcidMessage entity = get500OrcidEntity(e);
            return Response.serverError().entity(entity).build();
        }
    }

    /**
     * @param e
     * @return
     */
    private OrcidMessage get500OrcidEntity(Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        entity.setErrorDesc(new ErrorDesc(StringUtils.isNotBlank(e.getMessage()) ? e.getMessage()
                : "It is possible that this is a bug. If you could raise an issue in Github it would be much " + "appreciated. Thanks, the ORCID team"));
        return entity;
    }

    private OrcidMessage getOrcidEntity(String prefix, Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        entity.setErrorDesc(new ErrorDesc(prefix + " : " + e.getMessage()));
        return entity;
    }

}
