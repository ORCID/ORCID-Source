/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.validator;

import java.util.HashMap;
import java.util.Map;

import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.core.exception.PutCodeRequiredException;
import org.orcid.jaxb.model.record.ResearcherUrl;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class PersonValidator {

    public static void validateResearcherUrl(ResearcherUrl researcherUrl, SourceEntity sourceEntity, boolean createFlag) {
        if(createFlag) {
            if(researcherUrl.getPutCode() != null) {
                Map<String, String> params = new HashMap<String, String>();
                if (sourceEntity != null) {
                    params.put("clientName", sourceEntity.getSourceName());
                }
                throw new InvalidPutCodeException(params);
            }                        
        } else {
            if(researcherUrl.getPutCode() == null) {
                Map<String, String> params = new HashMap<String, String>();                
                throw new PutCodeRequiredException(params);
            }
        }
    }

}
