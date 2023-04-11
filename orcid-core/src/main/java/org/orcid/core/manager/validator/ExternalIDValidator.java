package org.orcid.core.manager.validator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.Items;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class ExternalIDValidator {

    @Resource
    IdentifierTypeManager identifierTypeManager;

    public ExternalIDValidator() {
    }

    public void validateWorkOrPeerReview(ExternalID id) {
        if (id == null)
            return;
        
        List<String> errors = new ArrayList<String>();
        
        if (id.getType() == null || !identifierTypeManager.fetchIdentifierTypesByAPITypeName(null).containsKey(id.getType())) {
            errors.add("type");
        }
        
        if(PojoUtil.isEmpty(id.getValue())) {
            errors.add("value");
        }
                
        if(id.getRelationship() == null) {
            errors.add("relationship");
        }        
        
        checkAndThrow(errors);
    }

    public void validateWorkOrPeerReview(ExternalIDs ids) {
        if (ids == null) // yeuch
            return;
        List<String> errors = new ArrayList<String>();
        for (ExternalID id : ids.getExternalIdentifier()) {
            if (id.getType() == null || !identifierTypeManager.fetchIdentifierTypesByAPITypeName(null).containsKey(id.getType())) {
                errors.add(id.getType());
            }
            
            if(PojoUtil.isEmpty(id.getValue())) {
                errors.add("value");
            }
                        
            if(id.getRelationship() == null) {
                errors.add("relationship");
            }            
        }
        checkAndThrow(errors);
    }

    public void validateFunding(ExternalIDs ids) {
        if (ids == null) // urgh
            return;
        List<String> errors = new ArrayList<String>();
        for (ExternalID id : ids.getExternalIdentifier()) {
            if (id.getType() == null || !identifierTypeManager.fetchIdentifierTypesByAPITypeName(null).containsKey(id.getType())) {
                errors.add(id.getType());
            }
            
            if(PojoUtil.isEmpty(id.getValue())) {
                errors.add("value");
            }
                        
            if(id.getRelationship() == null) {
                errors.add("relationship");
            }            
        }                
        
        checkAndThrow(errors);
    }

    public void validateNotificationItems(Items items) {
        if (items == null)
            return;
        List<String> errors = new ArrayList<String>();
        for (Item i : items.getItems()) {
            if (i.getExternalIdentifier() != null && i.getExternalIdentifier().getType() != null) {
                ExternalID extId = i.getExternalIdentifier();
                if (extId.getType() == null
                        || !identifierTypeManager.fetchIdentifierTypesByAPITypeName(null).containsKey(extId.getType())) {
                    errors.add(i.getExternalIdentifier().getType());
                }
                
                if(PojoUtil.isEmpty(extId.getValue())) {
                    errors.add("value");
                }
                                
                if(extId.getRelationship() == null) {
                    errors.add("relationship");
                }                
            }
        }
        checkAndThrow(errors);
    }

    private void checkAndThrow(List<String> errors) {
        if (!errors.isEmpty()) {
            StringBuffer errorString = new StringBuffer();
            errors.forEach(n -> errorString.append(" " + n));
            throw new ActivityIdentifierValidationException("Invalid external-id" + errorString.toString());
        }
    }

}
