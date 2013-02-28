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

import java.io.Serializable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.orcid.frontend.web.forms.SearchOrcidBioForm;

public class OrcidBioSearchValidator implements ConstraintValidator<ValidOrcidBioSearchAnnotation, SearchOrcidBioForm>, Serializable {

    private static final int MIN_FIELD_LENGTH = 2;

    /**
     * 
     */
    private static final long serialVersionUID = -5135598443972938680L;

    @Override
    public void initialize(ValidOrcidBioSearchAnnotation constraintAnnotation) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isValid(SearchOrcidBioForm searchOrcidBioForm, ConstraintValidatorContext context) {

        // if the orcid is populated, consider the input valid
        if (!StringUtils.isBlank(searchOrcidBioForm.getOrcid())) {
            return true;
        }

        // if any field other than the orcid is populated, it's eligible for
        // validation
        return isAtLeastOneField2Chars(searchOrcidBioForm.getFamilyName(), searchOrcidBioForm.getGivenName(), searchOrcidBioForm.getInstitutionName(),
                searchOrcidBioForm.getKeyword());

    }

    private boolean isAtLeastOneField2Chars(String... fields) {
        for (String field : fields) {
            if (!StringUtils.isBlank(field) && field.length() >= MIN_FIELD_LENGTH) {
                return true;
            }
        }
        return false;
    }
}
