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

import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;

public class ExternalIDValidator {

    private static ExternalIDValidator instance = new ExternalIDValidator();
    
    private ExternalIDValidator(){}
    
    public static ExternalIDValidator getInstance(){
        return instance;
    }
    
    public void validateWorkOrPeerReview(ExternalID id){
        try{
            WorkExternalIdentifierType t = WorkExternalIdentifierType.fromValue(id.getType());
        }catch (IllegalArgumentException e){
            checkAndThrow(Lists.newArrayList(id.getType()));
        }
    }

    public void validateWorkOrPeerReview(ExternalIDs ids){     
        if (ids==null) //yeuch
            return;
        List<String> errors = Lists.newArrayList();        
        for (ExternalID id : ids.getExternalIdentifier()){
            try{
                WorkExternalIdentifierType t = WorkExternalIdentifierType.fromValue(id.getType());
            }catch (IllegalArgumentException e){
                errors.add(id.getType());
            }
        }            
        checkAndThrow(errors);
    }

    public void validateFunding(ExternalIDs ids){
        if (ids==null) //urgh
            return;
        List<String> errors = Lists.newArrayList();        
        for (ExternalID id : ids.getExternalIdentifier()){
            try{
                FundingExternalIdentifierType t = FundingExternalIdentifierType.fromValue(id.getType());
            }catch (IllegalArgumentException e){
                errors.add(id.getType());
            }
        }            
        checkAndThrow(errors);
    }

    private void checkAndThrow(List<String> errors){
        if (!errors.isEmpty()){
            StringBuffer errorString = new StringBuffer();
            errors.forEach(n -> errorString.append(" "+n));
            throw new ActivityIdentifierValidationException("Invalid external-id "+errorString.toString());
        }
    }

}
