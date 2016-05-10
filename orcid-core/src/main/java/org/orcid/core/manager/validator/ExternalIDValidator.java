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

import javax.annotation.Resource;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.jaxb.model.notification.permission_rc2.Item;
import org.orcid.jaxb.model.notification.permission_rc2.Items;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;

public class ExternalIDValidator {

    @Resource
    IdentifierTypeManager identifierTypeManager;

    public ExternalIDValidator() {
    }

    public void validateWorkOrPeerReview(ExternalID id) {
        if (id == null)
            return;
        if (id.getType() == null || !identifierTypeManager.fetchIdentifierTypesByAPITypeName().containsKey(id.getType())) {
            checkAndThrow(Lists.newArrayList(id.getType()));
        }
    }

    public void validateWorkOrPeerReview(ExternalIDs ids) {
        if (ids == null) // yeuch
            return;
        List<String> errors = Lists.newArrayList();
        for (ExternalID id : ids.getExternalIdentifier()) {
            if (id.getType() == null || !identifierTypeManager.fetchIdentifierTypesByAPITypeName().containsKey(id.getType())) {
                errors.add(id.getType());
            }
        }
        checkAndThrow(errors);
    }

    public void validateFunding(ExternalIDs ids) {
        if (ids == null) // urgh
            return;
        List<String> errors = Lists.newArrayList();
        for (ExternalID id : ids.getExternalIdentifier()) {
            if (id.getType() == null || !identifierTypeManager.fetchIdentifierTypesByAPITypeName().containsKey(id.getType())) {
                errors.add(id.getType());
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
                if (i.getExternalIdentifier().getType() == null
                        || !identifierTypeManager.fetchIdentifierTypesByAPITypeName().containsKey(i.getExternalIdentifier().getType())) {
                    errors.add(i.getExternalIdentifier().getType());
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
