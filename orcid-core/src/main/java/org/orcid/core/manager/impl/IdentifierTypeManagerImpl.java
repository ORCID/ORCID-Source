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
package org.orcid.core.manager.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.adapter.impl.IdentifierTypePOJOConverter;
import org.orcid.core.adapter.impl.jsonidentifiers.ExternalIdentifierTypeConverter;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.IdentifierTypeDao;
import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.IdentifierType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * Manages the map of external identifier types.
 * 
 * Identifier types cannot be deleted, but they can be marked as deprecated.
 * 
 * Identifier types are fun! In the API, they are (generally) lower case with
 * hyphens. In the DB they are (generally) upper case with underscores.
 * 
 * @author tom
 *
 */
public class IdentifierTypeManagerImpl implements IdentifierTypeManager {

    @Resource
    private IdentifierTypeDao idTypeDao;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private OrcidSecurityManager securityManager;

    private IdentifierTypePOJOConverter adapter = new IdentifierTypePOJOConverter();
    private ExternalIdentifierTypeConverter externalIdentifierTypeConverter = new ExternalIdentifierTypeConverter();

    public void setSourceManager(SourceManager manager) {
        this.sourceManager = manager;
    }

    @Override
    @Cacheable("identifier-types")
    public IdentifierType fetchIdentifierTypeByDatabaseName(String name) {
        IdentifierTypeEntity entity = idTypeDao.getEntityByName(name);
        return adapter.fromEntity(entity);
    }

    /**
     * Returns an immutable map of API Type Name->identifierType objects.
     * 
     */
    @Override
    @Cacheable("identifier-types-map")
    public Map<String, IdentifierType> fetchIdentifierTypesByAPITypeName() {
        List<IdentifierTypeEntity> entities = idTypeDao.getEntities();
        Map<String, IdentifierType> ids = new HashMap<String, IdentifierType>();
        for (IdentifierTypeEntity e : entities) {
            IdentifierType id = adapter.fromEntity(e);
            ids.put(id.getName(), id);
        }
        return Collections.unmodifiableMap(ids);
    }

    @Override
    @CacheEvict(value = { "identifier-types", "identifier-types-map" }, allEntries = true)
    public IdentifierType createIdentifierType(IdentifierType id) {
        IdentifierTypeEntity entity = adapter.fromPojo(id);
        SourceEntity source = sourceManager.retrieveSourceEntity();
        entity.setSourceClient(source.getSourceClient());
        Date now = new Date();
        entity.setDateCreated(now);
        entity.setLastModified(now);
        entity = idTypeDao.addIdentifierType(entity);
        return adapter.fromEntity(entity);
    }

    @Override
    @CacheEvict(value = { "identifier-types", "identifier-types-map" }, allEntries = true)
    public IdentifierType updateIdentifierType(IdentifierType id) {
        IdentifierTypeEntity entity = idTypeDao.getEntityByName(externalIdentifierTypeConverter.convertTo(id.getName(), null));
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceClient(entity.getSourceClient());
        securityManager.checkSource(sourceEntity);
        entity.setIsDeprecated(id.getDeprecated());
        entity.setResolutionPrefix(id.getResolutionPrefix());
        entity.setValidationRegex(id.getValidationRegex());
        entity.setLastModified(new Date());
        entity = idTypeDao.updateIdentifierType(entity);
        return adapter.fromEntity(entity);
    }

}
