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

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.orcid.core.cli.ValidateOrcidMessage;
import org.orcid.core.manager.ValidationBehaviour;
import org.orcid.core.manager.ValidationManager;
import org.orcid.jaxb.model.message.OrcidMessage;
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

    private Schema schema;

    private static final Logger LOG = LoggerFactory.getLogger(ValidationManagerImpl.class);

    @Override
    public void setValidationBehaviour(ValidationBehaviour validationBehaviour) {
        this.validationBehaviour = validationBehaviour;
    }

    @Override
    public void validateMessage(OrcidMessage orcidMessage) {
        if (ValidationBehaviour.IGNORE.equals(validationBehaviour)) {
            return;
        }
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

    private void initSchema() {
        if (schema == null) {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            try {
                schema = factory.newSchema(ValidateOrcidMessage.class.getResource("/orcid-message-" + OrcidMessage.DEFAULT_VERSION + ".xsd"));
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
        case THROW_RUNTIME_EXCEPTION:
            throw new RuntimeException(message, t);
        default:
            throw new RuntimeException("Unknown validation behaviour: " + validationBehaviour);
        }
    }

}
