package org.orcid.core.manager.v3.validator;

import java.util.List;

import javax.annotation.Resource;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.Items;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;

public class ExternalIDValidator {

    @Resource
    IdentifierTypeManager identifierTypeManager;

    @Value("${org.orcid.core.validations.requireRelationship:false}")
    private boolean requireRelationshipOnExternalIdentifier;
    
    public ExternalIDValidator() {
    }

    public void setRequireRelationshipOnExternalIdentifier(boolean requireRelationshipOnExternalIdentifier) {
        this.requireRelationshipOnExternalIdentifier = requireRelationshipOnExternalIdentifier;
    }

    public void validateWorkOrPeerReview(ExternalID id) {
        if (id == null)
            return;
        
        List<String> errors = Lists.newArrayList();
        
        if (id.getType() == null || !identifierTypeManager.fetchIdentifierTypesByAPITypeName(null).containsKey(id.getType())) {
            errors.add("type");
        }
        
        if(PojoUtil.isEmpty(id.getValue())) {
            errors.add("value");
        }
        
        if(requireRelationshipOnExternalIdentifier) {
            if(id.getRelationship() == null) {
                errors.add("relationship");
            }
        }
        
        checkAndThrow(errors);
    }

    public void validatePeerReview(ExternalIDs ids) {
        if (ids == null) // yeuch
            return;
        List<String> errors = Lists.newArrayList();
        boolean hasVersionOfIdentifier = false;
        boolean hasSelfIdentifier = false;
        for (ExternalID id : ids.getExternalIdentifier()) {
            if (id.getType() == null || !identifierTypeManager.fetchIdentifierTypesByAPITypeName(null).containsKey(id.getType())) {
                errors.add(id.getType());
            }
            
            if(PojoUtil.isEmpty(id.getValue())) {
                errors.add("value");
            }
            
            if(requireRelationshipOnExternalIdentifier) {
                if(id.getRelationship() == null) {
                    errors.add("relationship");
                }
            }
            
            if(Relationship.VERSION_OF.equals(id.getRelationship())) {
                hasVersionOfIdentifier = true;
            }
            
            if(Relationship.SELF.equals(id.getRelationship())) {
                hasSelfIdentifier = true;
            }
        }
        
        if(hasVersionOfIdentifier && !hasSelfIdentifier) {
            errors.add("version-of requires at least one self identifier");
        } 
        
        checkAndThrow(errors);
    }
    
    public void validateWork(ExternalIDs ids, boolean apiRequest) {
        if (ids == null) // yeuch
            return;
        List<String> errors = Lists.newArrayList();
        boolean hasSelfIdentifier = false;
        for (ExternalID id : ids.getExternalIdentifier()) {
            if (id.getType() == null || !identifierTypeManager.fetchIdentifierTypesByAPITypeName(null).containsKey(id.getType())) {
                errors.add(id.getType());
            }
            
            if(PojoUtil.isEmpty(id.getValue())) {
                errors.add("value");
            }
            
            if(requireRelationshipOnExternalIdentifier) {
                if(id.getRelationship() == null) {
                    errors.add("relationship");
                }
            }
            
            if(Relationship.SELF.equals(id.getRelationship())) {
                hasSelfIdentifier = true;
            }
        }
        
        if(apiRequest && !hasSelfIdentifier) {
            errors.add("At least one identifier with 'self' relationship is required");
        } 
        
        checkAndThrow(errors);
    }

    public void validateFunding(ExternalIDs ids) {
        if (ids == null) // urgh
            return;
        List<String> errors = Lists.newArrayList();
        for (ExternalID id : ids.getExternalIdentifier()) {
            if (id.getType() == null || !identifierTypeManager.fetchIdentifierTypesByAPITypeName(null).containsKey(id.getType())) {
                errors.add(id.getType());
            }
            
            if(PojoUtil.isEmpty(id.getValue())) {
                errors.add("value");
            }
            
            if(requireRelationshipOnExternalIdentifier) {
                if(id.getRelationship() == null) {
                    errors.add("relationship");
                }
            }
        }                
        
        checkAndThrow(errors);
    }

    public void validateNotificationItems(Items items) {
        if (items == null)
            return;
        List<String> errors = Lists.newArrayList();
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
                
                if(requireRelationshipOnExternalIdentifier) {
                    if(extId.getRelationship() == null) {
                        errors.add("relationship");
                    }
                }
            }
        }
        checkAndThrow(errors);
    }

    private void checkAndThrow(List<String> errors) {
        if (!errors.isEmpty()) {
            StringBuffer errorString = new StringBuffer();
            errors.forEach(n -> errorString.append(" " + n));
            throw new ActivityIdentifierValidationException("Invalid external-id " + errorString.toString());
        }
    }

}
