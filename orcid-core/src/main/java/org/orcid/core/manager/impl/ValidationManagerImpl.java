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
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.pojo.ajaxForm.PojoUtil;
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

    private boolean validateTitle = false;

    private boolean validateOnlyOnePrimaryEmail = false;

    private boolean validateWorksHaveExternalIds = false;

    private boolean validateFundingHaveExternalIds = false;

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

    public void setValidateTitle(boolean validateTitle) {
        this.validateTitle = validateTitle;
    }

    public boolean isValidateOnlyOnePrimaryEmail() {
        return validateOnlyOnePrimaryEmail;
    }

    public void setValidateOnlyOnePrimaryEmail(boolean validateOnlyOnePrimaryEmail) {
        this.validateOnlyOnePrimaryEmail = validateOnlyOnePrimaryEmail;
    }

    public boolean isValidateWorksHaveExternalIds() {
        return validateWorksHaveExternalIds;
    }

    public void setValidateWorksHaveExternalIds(boolean validateWorksHaveExternalIds) {
        this.validateWorksHaveExternalIds = validateWorksHaveExternalIds;
    }

    public boolean isValidateFundingHaveExternalIds() {
        return validateFundingHaveExternalIds;
    }

    public void setValidateFundingHaveExternalIds(boolean validateFundingHaveExternalIds) {
        this.validateFundingHaveExternalIds = validateFundingHaveExternalIds;
    }

    @Override
    public void validateMessage(OrcidMessage orcidMessage) {
        if (ValidationBehaviour.IGNORE.equals(validationBehaviour)) {
            return;
        }
        doMessageVersionValidation(orcidMessage);
        doWorkTypeValidation(orcidMessage);
        doSchemaValidation(orcidMessage);
        doCustomValidation(orcidMessage);
    }
    
    @Override
    public void validateBioMessage(OrcidMessage orcidMessage) {
        if (ValidationBehaviour.IGNORE.equals(validationBehaviour)) {
            return;
        }
        doMessageVersionValidation(orcidMessage);
        doSchemaValidation(orcidMessage);
    }
    

    private void doMessageVersionValidation(OrcidMessage orcidMessage) {
        if (orcidMessage != null) {
            if (PojoUtil.isEmpty(orcidMessage.getMessageVersion())) {
                handleError("Message version is required");
            }
        }
    }

    private void doWorkTypeValidation(OrcidMessage orcidMessage) {
        if (orcidMessage == null || orcidMessage.getOrcidProfile() == null || orcidMessage.getOrcidProfile().getOrcidActivities() == null
                || orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks() == null
                || orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork() == null
                || orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().isEmpty())
            return;
        List<OrcidWork> works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        for (OrcidWork work : works) {
            if (work.getWorkType() == null)
                if (work.getWorkTitle() != null && work.getWorkTitle().getTitle() != null && !PojoUtil.isEmpty(work.getWorkTitle().getTitle().getContent()))
                    handleError("work-type is missing or invalid for work: '" + work.getWorkTitle().getTitle().getContent() + "'");
                else
                    handleError("work-type is missing or invalid");
        }
    }

    protected void doSchemaValidation(OrcidMessage orcidMessage) {
        Validator validator = createValidator();
        if (validator != null) {
            try {
                validator.validate(orcidMessage.toSource());
            } catch (SAXException e) {
                //Lets not log SAX exceptions since they are consuming a lot of room in our logs
                //handleError("ORCID message is invalid", e, orcidMessage);
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

            if (validateOnlyOnePrimaryEmail) {
                if (primaryCount == 0)
                    throw new OrcidValidationException("There must be at least one primary email");
            }
        }
    }

    private void checkActivities(OrcidActivities orcidActivities) {
        if (orcidActivities != null) {
            OrcidWorks works = orcidActivities.getOrcidWorks();
            if (works != null && works.getOrcidWork() != null && !works.getOrcidWork().isEmpty()) {
                checkWorks(works.getOrcidWork());
            }

            FundingList funding = orcidActivities.getFundings();
            if (funding != null && funding.getFundings() != null && !funding.getFundings().isEmpty()) {
                checkFunding(funding.getFundings());
            }
        }
    }

    private void checkWorks(List<OrcidWork> orcidWork) {
        for (OrcidWork work : orcidWork) {
            checkWork(work);
        }
    }

    public void checkWork(OrcidWork orcidWork) {

        if (validateTitle) {
            WorkTitle title = orcidWork.getWorkTitle();
            if (title == null || title.getTitle() == null || StringUtils.isEmpty(title.getTitle().getContent())) {
                throw new OrcidValidationException("Invalid Title: title cannot be null nor emtpy");
            }
        }

        if (validateWorksHaveExternalIds) {
            if (orcidWork.getWorkExternalIdentifiers() == null || orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier() == null
                    || orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier().isEmpty()) {
                throw new OrcidValidationException("Invalid work: Works added using message version 1.2_rc5 or greater must contain at least one external identifier");
            }
        }
    }

    private void checkFunding(List<Funding> fundings) {
        for (Funding funding : fundings) {
            checkFunding(funding);
        }
    }

    private void checkFunding(Funding funding) {
        if (validateFundingHaveExternalIds) {
            if (funding.getFundingExternalIdentifiers() == null || funding.getFundingExternalIdentifiers().getFundingExternalIdentifier() == null
                    || funding.getFundingExternalIdentifiers().getFundingExternalIdentifier().isEmpty()) {
                throw new OrcidValidationException(
                        "Invalid funding: Funding added using message version 1.2_rc5 or greater must contain at least one external identifier");
            }
        }

        if (funding.getStartDate() != null) {
            if (!PojoUtil.isEmpty(funding.getStartDate().getMonth()) && PojoUtil.isEmpty(funding.getStartDate().getYear())) {
                throw new OrcidValidationException("Invalid funding: Invalid start date");
            }
        }

        if (funding.getEndDate() != null) {
            if (!PojoUtil.isEmpty(funding.getEndDate().getMonth()) && PojoUtil.isEmpty(funding.getEndDate().getYear())) {
                throw new OrcidValidationException("Invalid funding: Invalid end date");
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
            break;
        case LOG_WARNING:
            LOG.warn(message, t);
            break;
        case LOG_ERROR:
            LOG.error(message, t);
            break;
        case LOG_INFO_WITH_XML:
            LOG.info(message, t);
            if (orcidMessage != null) {
                LOG.info("ORCID message is: {}", orcidMessage);
            }
            break;
        case LOG_WARNING_WITH_XML:
            LOG.warn(message, t);
            if (orcidMessage != null) {
                LOG.warn("ORCID message is: {}", orcidMessage);
            }
            break;
        case LOG_ERROR_WITH_XML:
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
