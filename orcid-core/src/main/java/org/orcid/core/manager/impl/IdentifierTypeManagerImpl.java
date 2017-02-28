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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.orcid.core.adapter.impl.IdentifierTypePOJOConverter;
import org.orcid.core.adapter.impl.jsonidentifiers.ExternalIdentifierTypeConverter;
import org.orcid.core.locale.LocaleManager;
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

import com.google.common.collect.Lists;

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
    
    @Resource
    private LocaleManager localeManager;

    private IdentifierTypePOJOConverter adapter = new IdentifierTypePOJOConverter();
    private ExternalIdentifierTypeConverter externalIdentifierTypeConverter = new ExternalIdentifierTypeConverter();    
    
    /**
     * Null locale will result in Locale.ENGLISH
     */
    @Override
    @Cacheable("identifier-types")
    public IdentifierType fetchIdentifierTypeByDatabaseName(String name, Locale loc) {
        loc = (loc == null )? Locale.ENGLISH : loc;
        IdentifierTypeEntity entity = idTypeDao.getEntityByName(name);
        IdentifierType type = adapter.fromEntity(entity);
        type.setDescription(getMessage(type.getName(), loc));
        return type;
    }

    /**
     * Returns an immutable map of API Type Name->identifierType objects.
     * Null locale will result in Locale.ENGLISH
     * 
     */
    @Override
    @Cacheable("identifier-types-map")
    public Map<String, IdentifierType> fetchIdentifierTypesByAPITypeName(Locale loc) {
        loc = (loc == null )? Locale.ENGLISH : loc;
        List<IdentifierTypeEntity> entities = idTypeDao.getEntities();
        Map<String, IdentifierType> ids = new HashMap<String, IdentifierType>();
        for (IdentifierTypeEntity e : entities) {
            IdentifierType id = adapter.fromEntity(e);
            id.setDescription(getMessage(id.getName(), loc));
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
        securityManager.checkSource(entity);
        entity.setIsDeprecated(id.getDeprecated());
        entity.setResolutionPrefix(id.getResolutionPrefix());
        entity.setValidationRegex(id.getValidationRegex());
        entity.setLastModified(new Date());
        entity.setIsCaseSensitive(id.getCaseSensitive());
        entity.setPrimaryUse(id.getPrimaryUse());
        entity = idTypeDao.updateIdentifierType(entity);
        return adapter.fromEntity(entity);
    }
    
    private String getMessage(String type, Locale locale) {
        try {
            String key = new StringBuffer("org.orcid.jaxb.model.record.WorkExternalIdentifierType.").append(type).toString();
            return localeManager.resolveMessage(key, locale, type);
        }catch(Exception e){
            return type.replace('_', ' ');
        }
    }

    
    /** Seems pointless to base on live data - based on 2016 datadump
     *  DOI,414627,9.49E+06,8.56E+06
        EID,176888,5.52E+06,5.42E+06
        PMID,65623,1.17E+06,1.16E+06
        ISSN,64859,944926,493274
        WOSUID,45497,1.37E+06,1.35E+06
        PMC,41232,272073,270988
        ISBN,39629,217805,172146
        OTHER_ID,15486,203683,200963
        SOURCE_WORK_ID,14091,279023,277629
        ARXIV,5199,134103,130695
        HANDLE,1535,26142,26069
        BIBCODE,1347,83041,82412
     */
    
    private static List<String> topTypes = Lists.newArrayList("doi","eid","pmid","issn","wosuid","pmc","isbn","other-id","arxiv","handle","bibcode");
    
    /**
     * Returns an immutable map of the top X API Type Name->identifierType objects.
     * Null locale will result in Locale.ENGLISH
     * 
     */
    @Override
    /*@Cacheable("identifier-types-map-top")*/
    public Collection<IdentifierType> fetchMostPopularIdentifierTypesByAPITypeName(Locale loc) {
        Map<String, IdentifierType> all = this.fetchIdentifierTypesByAPITypeName(loc);
        Map<String, IdentifierType> topX = new TreeMap<String,IdentifierType>();
        for (String s: topTypes)
            if (all.containsKey(s))
                topX.put(all.get(s).getDescription().toLowerCase(), all.get(s));  
        return topX.values();
    }

    @Override
    /*@Cacheable("identifier-types-map-prefix")*/
    public Collection<IdentifierType> queryByPrefix(String query, Locale loc) {
        SortedMap<String,IdentifierType> results = new TreeMap<String,IdentifierType>();
        Map<String, IdentifierType>types = fetchIdentifierTypesByAPITypeName(loc);
        
        //stick them in a trie so we can do a deep prefix search
        PatriciaTrie<Set<IdentifierType>> trie = new PatriciaTrie<Set<IdentifierType>>();
        for (String type : types.keySet()) {
            IdentifierType t = types.get(type);
            if (!trie.containsKey(t.getName()))
                trie.put(t.getName(), new HashSet<IdentifierType>());
            trie.get(t.getName()).add(t);
            for (String s: t.getDescription().split(" ")){
                if (!trie.containsKey(s))
                    trie.put(s, new HashSet<IdentifierType>());
                trie.get(s).add(t);
            }
        }
        
        //dedupe and sort
        SortedMap<String,Set<IdentifierType>> sorted = trie.prefixMap(query);
        for (Set<IdentifierType> set : sorted.values()){
            for (IdentifierType t : set){
                results.put(t.getDescription().toLowerCase(),t);                
            }
        }
        return results.values();
    }


}
