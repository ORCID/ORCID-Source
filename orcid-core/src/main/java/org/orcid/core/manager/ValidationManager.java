package org.orcid.core.manager;

import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface ValidationManager {

    void setValidationBehaviour(ValidationBehaviour validationBehaviour);

    void validateMessage(OrcidMessage orcidMessage);

    void validateBioMessage(OrcidMessage orcidMessage);
}
