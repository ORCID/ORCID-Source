package org.orcid.scheduler.validation;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.orcid.jaxb.model.v3.rc2.record.Record;
import org.orcid.persistence.dao.ValidatedPublicProfileDao;
import org.orcid.persistence.jpa.entities.ValidatedPublicProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;

public class PublicProfileValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicProfileValidator.class);

    protected Client jerseyClient;

    @Resource
    protected ValidatedPublicProfileDao validatedPublicProfileDao;

    private URI baseUri;

    private Schema schema;

    private boolean developmentMode;
    
    @Value("org.orcid.scheduler.api.profile.validation.batchSize:100")
    private int batchSize;

    public PublicProfileValidator(String baseUri, boolean developmentMode) throws URISyntaxException {
        this.baseUri = new URI(baseUri);
        this.developmentMode = developmentMode;

        Source source = new StreamSource(getClass().getResourceAsStream("/record_3.0_rc2/record-3.0_rc2.xsd"));
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new PublicProfileValidatorResourceResolver());
        try {
            schema = schemaFactory.newSchema(source);
        } catch (SAXException e) {
            throw new RuntimeException("Error creating ValidatedPublicProfileManagerImpl: " + e);
        }
    }

    public void validatePublicRecords() {
        jerseyClient = PublicProfileValidationClient.create(developmentMode);
        List<String> orcidIds = validatedPublicProfileDao.getNextRecordsToValidate(batchSize);
        for (String orcid : orcidIds) {
            try {
                ValidatedPublicProfileEntity validation = validatePublicProfile(orcid);
                if (validation != null) {
                    validatedPublicProfileDao.merge(validation);
                }
            } catch (Exception e) {
                LOGGER.error("Error validating public records", e);
            }
        }
    }

    private ValidatedPublicProfileEntity validatePublicProfile(String orcid) throws JAXBException, IOException {
        ValidatedPublicProfileEntity validation = getValidation(orcid);
        ClientResponse response = getApiResponse(orcid);
        if (response.getStatus() == 200) {
            // validate profile
            JAXBContext context = JAXBContext.newInstance(Record.class);
            Source source = new JAXBSource(context, response.getEntity(Record.class));
            Validator validator = schema.newValidator();
            try {
                validator.validate(source);
                validation.setValid(true);
            } catch (SAXException e) {
                validation.setValid(false);
                validation.setError(e.getCause().getCause().getMessage());
            }
        } else {
            LOGGER.warn("Unexpected response code {} from public API, aborting validation for public record {}", response.getStatus(), orcid);
            return null;
        }
        return validation;
    }

    private ValidatedPublicProfileEntity getValidation(String orcid) {
        ValidatedPublicProfileEntity validation = new ValidatedPublicProfileEntity();
        validation.setId(orcid);
        validation.setDateCreated(new Date());
        validation.setLastModified(new Date());
        return validation;
    }

    private ClientResponse getApiResponse(String orcid) {
        WebResource webResource = jerseyClient.resource(baseUri).path(orcid + "/record");
        webResource.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
        Builder builder = webResource.accept(MediaType.APPLICATION_XML);
        ClientResponse response = builder.get(ClientResponse.class);
        return response;
    }

    public void setDevelopmentMode(boolean developmentMode) {
        this.developmentMode = developmentMode;
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-scheduler-beans-context.xml");
        PublicProfileValidator validator = (PublicProfileValidator) context.getBean("publicProfileValidator");
        validator.setDevelopmentMode(true);
        validator.validatePublicRecords();
    }

}
