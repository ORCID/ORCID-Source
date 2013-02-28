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
package org.orcid.frontend.web.forms.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.orcid.frontend.web.forms.CurrentWork;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.utils.BibtexUtils;

public class CurrentWorkBibtexValidator implements ConstraintValidator<ValidCurrentWorkBibtex, CurrentWork> {

    @Override
    public void initialize(ValidCurrentWorkBibtex constraintAnnotation) {
    }

    @Override
    public boolean isValid(CurrentWork currentWork, ConstraintValidatorContext context) {
        if (CitationType.BIBTEX.value().equals(currentWork.getCitationType())) {
            String citation = currentWork.getCitation();
            boolean isValid = BibtexUtils.isValid(citation);
            if(!isValid){
                context.buildConstraintViolationWithTemplate("BibTeX is not valid").addNode("citation").addConstraintViolation();
            }
            return isValid;
        }
        return true;
    }

}
