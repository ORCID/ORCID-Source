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
package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.jpa.entities.AddressEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface AddressDao extends GenericDao<AddressEntity, Long> {
    AddressEntity getAddress(String orcid, Long putCode);

    List<AddressEntity> getAddresses(String orcid, long lastModified);
    
    List<AddressEntity> getAddresses(String orcid, Visibility visibility);

    List<Object[]> findAddressesToMigrate();
    
    boolean deleteAddress(String orcid, Long putCode);
    
    /**
     * Removes all address that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all address will be
     *            removed.
     */
    void removeAllAddress(String orcid);

    List<AddressEntity> getPublicAddresses(String orcid, long lastModified);
    
}
