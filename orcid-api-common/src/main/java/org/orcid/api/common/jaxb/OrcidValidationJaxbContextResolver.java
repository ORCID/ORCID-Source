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
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.orcid.api.common.OrcidApiConstants;
import org.orcid.api.common.exception.OrcidBadRequestException;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.xml.sax.SAXException;

/**
 * orcid-api - Nov 10, 2011 - OrcidValidationJaxbContextResolver
 * 
 * @author Declan Newman (declan)
 **/
@Provider
@Produces(value = { OrcidApiConstants.VND_ORCID_XML, OrcidApiConstants.ORCID_XML, MediaType.APPLICATION_XML })
@Consumes(value = { OrcidApiConstants.VND_ORCID_XML, OrcidApiConstants.ORCID_XML, MediaType.APPLICATION_XML })
public class OrcidValidationJaxbContextResolver implements ContextResolver<Unmarshaller> {

    private static final Logger logger = Logger.getLogger(OrcidValidationJaxbContextResolver.class);

    private JAXBContext jaxbContext;

    @Override
    public Unmarshaller getContext(Class<?> type) {
        try {
            jaxbContext = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Source source = new StreamSource(OrcidValidationJaxbContextResolver.class.getResourceAsStream("/orcid-message-" + OrcidMessage.DEFAULT_VERSION + ".xsd"));
            Schema schema = schemaFactory.newSchema(source);
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(new OrcidValidationHandler());
            return unmarshaller;
        } catch (JAXBException e) {
            throw new WebApplicationException(getResponse(e));
        } catch (SAXException e) {
            throw new WebApplicationException(getResponse(e));
        }

    }

    private Response getResponse(Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setErrorDesc(new ErrorDesc(e.getMessage()));
        return Response.serverError().entity(entity).build();
    }

    public class OrcidValidationHandler implements ValidationEventHandler {

        @Override
        public boolean handleEvent(ValidationEvent event) {
            if (event.getSeverity() == ValidationEvent.FATAL_ERROR || event.getSeverity() == ValidationEvent.ERROR) {
                logger.error(event.getMessage());
                throw new OrcidBadRequestException(event.getMessage());
            } else if (event.getSeverity() == ValidationEvent.WARNING) {
                logger.warn(event.getMessage());
            }
            return true;
        }

    }

}
