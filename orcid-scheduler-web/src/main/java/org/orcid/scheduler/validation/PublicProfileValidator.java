package org.orcid.scheduler.validation;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.persistence.dao.ValidatedPublicProfileDao;
import org.orcid.persistence.jpa.entities.ValidatedPublicProfileEntity;
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;

public class PublicProfileValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicProfileValidator.class);

    @Resource
    private JerseyClientHelper jerseyClientHelper;
    
    @Resource(name = "jerseyClientHelperDevelopmentMode")
    private JerseyClientHelper jerseyClientHelperDevelopmentMode;

    @Resource
    protected ValidatedPublicProfileDao validatedPublicProfileDao;

    @Value("${org.orcid.scheduler.api.profile.validation.baseUrl:https://localhost:8443/orcid-pub-web/v3.0/}")
    private String baseUri;

    private Schema schema;

    @Value("${org.orcid.scheduler.api.profile.validation.developmentMode:false}")
    private boolean developmentMode;

    @Value("${org.orcid.scheduler.api.profile.validation.maxAgeInDays:90}")
    private int validationMaxAgeInDays;

    @Value("${org.orcid.scheduler.api.profile.validation.batchSize:100}")
    private int batchSize;        

    public void processValidationCycle() {
        init();
        removeOldRecords();
        validateRecords();
    }
    
    private void init() {
        Source source = new StreamSource(getClass().getResourceAsStream("/record_3.0/record-3.0.xsd"));
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new PublicProfileValidatorResourceResolver());
        try {
            schema = schemaFactory.newSchema(source);
        } catch (SAXException e) {
            throw new RuntimeException("Error creating PublicProfileValidator: " + e);
        }
    }
    
    private void removeOldRecords() {
        Calendar maxAge = Calendar.getInstance();
        maxAge.add(Calendar.DAY_OF_YEAR, -validationMaxAgeInDays);
        validatedPublicProfileDao.removeOldRecords(maxAge.getTime());
    }

    public void validateRecords() {
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
        JerseyClientResponse<Record, String> response = getApiResponse(orcid);
        if (response.getStatus() == 200) {
            // validate profile
            JAXBContext context = JAXBContext.newInstance(Record.class);
            Source source = new JAXBSource(context, response.getEntity());
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
        return validation;
    }

    private JerseyClientResponse<Record, String> getApiResponse(String orcid) {
        String url = (baseUri.endsWith("/") ? baseUri : baseUri + '/') + orcid + "/record";
        JerseyClientResponse<Record, String> response;
        if(developmentMode) {
            LOGGER.warn("You are running the public profile validation in development mode!!!!");
            response = jerseyClientHelperDevelopmentMode.executeGetRequest(url, MediaType.APPLICATION_XML_TYPE, Record.class, String.class);
        } else {
            response = jerseyClientHelper.executeGetRequest(url, MediaType.APPLICATION_XML_TYPE, Record.class, String.class); 
        }
        return response;
    }

    public void setDevelopmentMode(boolean developmentMode) {
        this.developmentMode = developmentMode;
    }

}
