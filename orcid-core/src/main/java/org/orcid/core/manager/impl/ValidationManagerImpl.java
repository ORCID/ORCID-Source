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
package org.orcid.core.manager.impl;

import java.io.IOException;
import java.util.List;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.cli.ValidateOrcidMessage;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.manager.ValidationBehaviour;
import org.orcid.core.manager.ValidationManager;
import org.orcid.jaxb.model.message.Citation;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.utils.BibtexException;
import org.orcid.utils.BibtexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * 
 * @author Will Simpson
 * 
 */
public class ValidationManagerImpl implements ValidationManager {

    private ValidationBehaviour validationBehaviour = ValidationBehaviour.LOG_WARNING;

    private String version = OrcidMessage.DEFAULT_VERSION;
    
    private boolean requireOrcidProfile;

    private boolean validateBibtex = true;
    
    private boolean validateTitle = false;
    
    private boolean validateWorkType = false;
    
    private Schema schema;

    private static final Logger LOG = LoggerFactory.getLogger(ValidationManagerImpl.class);
    
    @Override
    public void setValidationBehaviour(ValidationBehaviour validationBehaviour) {
        this.validationBehaviour = validationBehaviour;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setRequireOrcidProfile(boolean requireOrcidProfile) {
        this.requireOrcidProfile = requireOrcidProfile;
    }

    public void setValidateBibtex(boolean validateBibtex) {
        this.validateBibtex = validateBibtex;
    }
    
    public void setValidateTitle(boolean validateTitle) {
        this.validateTitle = validateTitle;
    }

    public boolean isValidateWorkType() {
        return validateWorkType;
    }

    public void setValidateWorkType(boolean validateWorkType) {
        this.validateWorkType = validateWorkType;
    }

    @Override
    public void validateMessage(OrcidMessage orcidMessage) {
        if (ValidationBehaviour.IGNORE.equals(validationBehaviour)) {
            return;
        }               
        doSchemaValidation(orcidMessage);
        doCustomValidation(orcidMessage);
    }
    
    protected void doSchemaValidation(OrcidMessage orcidMessage) {
        Validator validator = createValidator();
        if (validator != null) {
            try {
                validator.validate(orcidMessage.toSource());
            } catch (SAXException e) {
                handleError("ORCID message is invalid", e, orcidMessage);
            } catch (IOException e) {
                handleError("Unable to read ORCID message", e, orcidMessage);
            }
        }
    }

    public void doCustomValidation(OrcidMessage orcidMessage) {
        try {
            checkMessage(orcidMessage);
        } catch (OrcidValidationException e) {
            handleError("Custom validation found a problem", e, orcidMessage);
        }
    }

    private void checkMessage(OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage != null ? orcidMessage.getOrcidProfile() : null;
        if (orcidProfile == null) {
            if (requireOrcidProfile) {
                throw new OrcidValidationException("There must be an orcid-profile element");
            }
        } else {            
            checkBio(orcidProfile.getOrcidBio());
            checkActivities(orcidProfile.getOrcidActivities());
        }
    }

    private void checkBio(OrcidBio orcidBio) {
        if (orcidBio != null) {
            checkContactDetails(orcidBio.getContactDetails());
        }
    }

    private void checkContactDetails(ContactDetails contactDetails) {
        if (contactDetails != null) {
            List<Email> emailList = contactDetails.getEmail();
            int primaryCount = 0;
            for (Email email : emailList) {
                if (email.isPrimary()) {
                    primaryCount++;
                }
            }
            if (primaryCount > 1) {
                throw new OrcidValidationException("There must not be more than one primary email");
            }
        }
    }

    private void checkActivities(OrcidActivities orcidActivities) {
        if (orcidActivities != null) {
            OrcidWorks works = orcidActivities.getOrcidWorks();
            if (works != null && works.getOrcidWork() != null && !works.getOrcidWork().isEmpty()) {
                checkWorks(works.getOrcidWork());
            }
        }
    }

    private void checkWorks(List<OrcidWork> orcidWork) {
        for (OrcidWork work : orcidWork) {
            checkWork(work);
        }
    }

    public void checkWork(OrcidWork orcidWork) {
        if (validateBibtex && orcidWork.getWorkCitation() != null) {
            Citation workCitation = orcidWork.getWorkCitation();
            if (CitationType.BIBTEX.equals(workCitation.getWorkCitationType())) {
                try {
                    BibtexUtils.validate(workCitation.getCitation());
                } catch (BibtexException e) {
                    throw new OrcidValidationException("Invalid BibTeX citation: " + workCitation.getCitation() + "\n", e);
                }
            }
        }
        
        if(validateTitle){
            WorkTitle title = orcidWork.getWorkTitle();
            if(title == null || title.getTitle() == null || StringUtils.isEmpty(title.getTitle().getContent())){
                throw new OrcidValidationException("Invalid Title: title cannot be null nor emtpy");
            }
        }  
        
        //Do this only if the message version is greather than version 23
        if(validateWorkType){            
            if(orcidWork.getWorkType() != null && orcidWork.getWorkType().isDeprecated()){
                throw new OrcidValidationException("Invalid work type: Type " + orcidWork.getWorkType().value() + " is deprecated");
            }
        }
    }

    private void initSchema() {
        if (schema == null) {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            try {                
            	schema = factory.newSchema(ValidateOrcidMessage.class.getResource("/orcid-message-" + version + ".xsd"));
            } catch (SAXException e) {
                handleError("Error initializing schema", e);
            }
        }
    }

    private Validator createValidator() {
        initSchema();
        if (schema == null) {
            handleError("Unable to create validator because schema is null");
        }
        return schema.newValidator();
    }

    private void handleError(String message) {
        handleError(message, null, null);
    }

    private void handleError(String message, Throwable t) {
        handleError(message, t, null);
    }

    private void handleError(String message, Throwable t, OrcidMessage orcidMessage) {
        switch (validationBehaviour) {
        case IGNORE:
            break;
        case LOG_INFO:
            LOG.info(message, t);
            if (orcidMessage != null) {
                LOG.info("ORCID message is: {}", orcidMessage);
            }
            break;
        case LOG_WARNING:
            LOG.warn(message, t);
            if (orcidMessage != null) {
                LOG.warn("ORCID message is: {}", orcidMessage);
            }
            break;
        case LOG_ERROR:
            LOG.error(message, t);
            if (orcidMessage != null) {
                LOG.error("ORCID message is: {}", orcidMessage);
            }
            break;
        case THROW_VALIDATION_EXCEPTION:
            if (t instanceof OrcidValidationException) {
                throw (OrcidValidationException) t;
            } else {
                throw new OrcidValidationException(message, t);
            }
        default:
            throw new RuntimeException("Unknown validation behaviour: " + validationBehaviour);
        }
    }

}
