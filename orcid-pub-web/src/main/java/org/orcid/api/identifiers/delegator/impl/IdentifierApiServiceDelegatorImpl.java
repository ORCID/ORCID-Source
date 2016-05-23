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
package org.orcid.api.identifiers.delegator.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.LocaleUtils;
import org.orcid.api.identifiers.delegator.IdentifierApiServiceDelegator;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.IdentifierType;

public class IdentifierApiServiceDelegatorImpl implements IdentifierApiServiceDelegator {
    
    @Resource 
    IdentifierTypeManager identifierTypeManager;
    
    @Resource
    private LocaleManager localeManager;
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response getIdentifierTypes(String locale) {
        Collection<IdentifierType> types = identifierTypeManager.fetchIdentifierTypesByAPITypeName().values();
        List<IdentifierTypeWithDescription> typesWithDescriptions = new ArrayList<IdentifierTypeWithDescription>();        
        Locale loc = LocaleUtils.toLocale(locale);
        for (IdentifierType type : types){
            IdentifierTypeWithDescription td = new IdentifierTypeWithDescription();
            td.setDateCreated(type.getDateCreated());
            td.setDeprecated(type.getDeprecated());
            td.setLastModified(type.getLastModified());
            td.setDeprecated(type.getDeprecated());
            td.setName(type.getName());
            td.setPutCode(type.getPutCode());
            td.setSourceClient(type.getSourceClient());
            td.setValidationRegex(type.getValidationRegex());
            td.setResolutionPrefix(type.getResolutionPrefix());
            td.setDescription(getMessage(type.getName(), loc));
            typesWithDescriptions.add(td);
        }
        return Response.ok(typesWithDescriptions).build();
    }
    
    private String getMessage(String type, Locale locale) {
        try {
            String key = new StringBuffer("org.orcid.jaxb.model.record.WorkExternalIdentifierType.").append(type).toString();
            return localeManager.resolveMessage(key, locale, type);
        }catch(Exception e){
            return type.replace('_', ' ');
        }
    }
    
    public static class IdentifierTypeWithDescription extends IdentifierType{
        private static final long serialVersionUID = 1L;
        public String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }        
    }
    
}
