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
package org.orcid.persistence.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.orcid.persistence.dao.OrcidPropsDao;
import org.orcid.persistence.jpa.entities.OrcidPropsEntity;
import org.springframework.util.Assert;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class OrcidPropsDaoImpl extends GenericDaoImpl<OrcidPropsEntity, String> implements OrcidPropsDao {

    public OrcidPropsDaoImpl() {
        super(OrcidPropsEntity.class);
    }

    /**
     * Creates a new key/value pair in the OrcidPropsEntity table
     * @param key
     * @param value
     * @return true if the new key/value row was successfully created
     * */
    @Override
    public boolean create(String key, String value) {
        Assert.hasText(key, "Cannot create an empty key");
        Assert.hasText(value, "Cannot assign an empty value");
        Query query = entityManager
                .createNativeQuery("INSERT INTO OrcidPropsEntity(id, value, date_created, last_modified) values(:key,:value,now(),now())");
        query.setParameter("key", key);
        query.setParameter("value", value);   
       
        return query.executeUpdate() > 0;
    }

    /**
     * Update a key/value pair in the OrcidPropsEntity table
     * @param key
     * @param value
     * @return true if the key/value row was successfully updated
     * */
    @Override
    public boolean update(String key, String value) {
        Assert.hasText(key, "Cannot create an empty key");
        Assert.hasText(value, "Cannot assign an empty value");
        Query query = entityManager
                .createNativeQuery("UPDATE OrcidPropsEntity SET value=:value, last_modified:now() WHERE id=:key");
        query.setParameter("key", key);
        query.setParameter("value", value);   
       
        return query.executeUpdate() > 0;
    }
    
    /**
     * Checks if the given key exists in the OrcidPropsEntity table.
     * @param key
     * @return true if the key exists on the OrcidPropsEntity table
     * @throws NonUniqueResultException if there are more than one row with the same key name
     * */
    @Override
    public boolean exists(String key) throws NonUniqueResultException {
        Assert.hasText(key, "Cannot look for empty keys");
        Query query = entityManager.createQuery("SELECT * FROM OrcidPropsEntity WHERE id=:key");
        query.setParameter("key", key);
        try {
            query.getSingleResult();
        } catch (NoResultException nre) {
            return false;
        } catch (NonUniqueResultException nure) {
            throw nure;
        }
        return  true;
    }

    @Override
    public String getValue(String key) {
        Assert.hasText(key, "Cannot look for empty keys");
        Query query = entityManager.createQuery("SELECT * FROM OrcidPropsEntity WHERE id=:key");
        query.setParameter("key", key);
        try {
            return (String) query.getSingleResult();
        } catch (NonUniqueResultException nure) {
            throw nure;
        } catch (NoResultException nre) {
            return null;
        }
    }

}
