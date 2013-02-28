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

import org.junit.Test;
import org.orcid.frontend.web.forms.CurrentAffiliationsForm;
import org.orcid.frontend.web.forms.PastInstitutionsForm;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class CurrentAffiliationsFormValidationTest extends AbstractConstraintValidator<CurrentAffiliationsForm> {

    /*
     * Test to ensure that delegation from current affiliation form --> past
     * institution form remains in place
     */
    @Test
    public void validatePastInstFields() throws Exception {
        PastInstitutionsForm form = new PastInstitutionsForm();

        form.setDateFormat("dd/MM/yyyy");
        form.setStartDate("15/07/2001");
        form.setEndDate("15/07/1999");
        Set<ConstraintViolation<PastInstitutionsForm>> errors = validator.validate(form);
        // There'll be an additional field level error, hence this will be 5 and
        // not 4
        assertEquals(5, errors.size());
    }

}
