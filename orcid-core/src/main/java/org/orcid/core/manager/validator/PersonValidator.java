package org.orcid.core.manager.validator;

import java.util.HashMap;
import java.util.Map;

import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.jaxb.model.record.ResearcherUrl;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class PersonValidator {

    public static void validateResearcherUrl(ResearcherUrl researcherUrl, SourceEntity sourceEntity, boolean createFlag) {
        if(!createFlag) {
            if(researcherUrl.getPutCode() == null) {
                Map<String, String> params = new HashMap<String, String>();
                if (sourceEntity != null) {
                    params.put("clientName", sourceEntity.getSourceName());
                }
                throw new InvalidPutCodeException(params);
            }                        
        }
    }

}
