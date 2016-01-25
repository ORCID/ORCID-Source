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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.orcid.core.adapter.JpaJaxbAddressAdapter;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.springframework.cache.annotation.Cacheable;

public class AddressManagerImpl implements AddressManager {

    @Resource
    private AddressDao addressDao;
    
    @Resource
    private JpaJaxbAddressAdapter adapter;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Resource
    private SourceManager sourceManager;
    
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    private long getLastModified(String orcid) {
        Date lastModified = profileEntityManager.getLastModified(orcid);
        return (lastModified == null) ? 0 : lastModified.getTime();
    }
    
    @Override
    public Address getPrimaryAddress(String orcid) {        
        List<AddressEntity> addresses = addressDao.findByOrcid(orcid, getLastModified(orcid));
        Address address = null;
        if(addresses != null) {
            for(AddressEntity entity : addresses) {
                if(entity.getPrimary()) {
                    address = adapter.toAddress(entity);
                    break;
                }
            }
        }                    
        return address;
    }

    @Override
    public Address getAddress(String orcid, Long putCode) {
        AddressEntity entity = addressDao.find(orcid, putCode);
        return adapter.toAddress(entity);
    }
    
    @Override
    @Transactional
    public Address updateAddress(String orcid, Long putCode, Address address, boolean isApiCall) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        // Validate the address
        PersonValidator.validateAddress(address, sourceEntity, false);
        // Validate it is not duplicated
        List<AddressEntity> existingAddresses = addressDao.findByOrcid(orcid, getLastModified(orcid));
        for (AddressEntity existing : existingAddresses) {
            //If it is not the same element
            if(!existing.getId().equals(address.getPutCode())) {
                if (isDuplicated(existing, address, sourceEntity)) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("type", "address");
                    params.put("value", address.getCountry().getValue().value());
                    throw new OrcidDuplicatedElementException(params);
                }
            }
        }

        AddressEntity updatedEntity = addressDao.find(putCode);
        if (updatedEntity == null) {
            throw new ApplicationException();
        }

        
        SourceEntity existingSource = updatedEntity.getSource();
        //If it is an update from the API, check the source and preserve the original visibility
        if(isApiCall) {
            orcidSecurityManager.checkSource(existingSource);
            Visibility originalVisibility = Visibility.fromValue(updatedEntity.getVisibility().value());
            updatedEntity.setVisibility(originalVisibility);
        }        
        adapter.toAddressEntity(address, updatedEntity);
        updatedEntity.setLastModified(new Date());        
        updatedEntity.setSource(existingSource);
        addressDao.merge(updatedEntity);
        return adapter.toAddress(updatedEntity);
    }
    
    @Override
    @Transactional
    public Address createAddress(String orcid, Address address) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate the address
        PersonValidator.validateAddress(address, sourceEntity, true);
        // Validate it is not duplicated
        List<AddressEntity> existingAddresses = addressDao.findByOrcid(orcid, getLastModified(orcid));
        for (AddressEntity existing : existingAddresses) {
            if (isDuplicated(existing, address, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "address");
                params.put("value", address.getCountry().getValue().value());
                throw new OrcidDuplicatedElementException(params);
            }
        }

        AddressEntity newEntity = adapter.toAddressEntity(address);
        ProfileEntity profile = new ProfileEntity(orcid);
        newEntity.setUser(profile);
        newEntity.setDateCreated(new Date());
        newEntity.setSource(sourceEntity);        
        setIncomingPrivacy(newEntity, profile);
        addressDao.persist(newEntity);
        return adapter.toAddress(newEntity);
    }

    @Override
    @Transactional
    public boolean deleteAddress(String orcid, Long putCode) {
        AddressEntity entity = addressDao.find(orcid, putCode);
        SourceEntity existingSource = entity.getSource();
        orcidSecurityManager.checkSource(existingSource);

        try {
            addressDao.remove(entity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean isDuplicated(AddressEntity existing, Address address, SourceEntity source) {
        //If they have the same source
        if(existing.getSource() != null && existing.getSource().getSourceId().equals(source.getSourceId())) {
            if(existing.getIso2Country().equals(address.getCountry().getValue())) {
                return true;
            }
        }
        return false;
    }    
    
    private void setIncomingPrivacy(AddressEntity entity, ProfileEntity profile) {
        Visibility incomingCountryVisibility = entity.getVisibility();
        Visibility defaultCountryVisibility = profile.getProfileAddressVisibility() == null
                ? Visibility.fromValue(OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility().value())
                : org.orcid.jaxb.model.common_rc2.Visibility.fromValue(profile.getProfileAddressVisibility().value());
        if (profile.getClaimed() != null && profile.getClaimed()) {
            if (defaultCountryVisibility.isMoreRestrictiveThan(incomingCountryVisibility)) {
                entity.setVisibility(defaultCountryVisibility);
            }
        } else if (incomingCountryVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE);
        }
    }

    @Override
    @Cacheable(value = "address", key = "#orcid.concat('-').concat(#lastModified)")
    public Addresses getAddresses(String orcid, long lastModified) {
        List<AddressEntity> addressList = getAddresses(orcid, null);        
        Addresses result = adapter.toAddressList(addressList);
        result.updateIndexingStatusOnChilds();
        return result;
    }

    @Override
    @Cacheable(value = "public-address", key = "#orcid.concat('-').concat(#lastModified)")
    public Addresses getPublicAddresses(String orcid, long lastModified) {
        List<AddressEntity> addressList = getAddresses(orcid, null);
        Addresses result = adapter.toAddressList(addressList);
        result.updateIndexingStatusOnChilds();
        return result;
    }
    
    private List<AddressEntity> getAddresses(String orcid, Visibility visibility) {
        List<AddressEntity> addresses = addressDao.findByOrcid(orcid, getLastModified(orcid));
        if(visibility != null) {
            Iterator<AddressEntity> it = addresses.iterator();
            while(it.hasNext()) {
                AddressEntity entity = it.next();
                if(!entity.getVisibility().equals(visibility)) {
                    it.remove();
                }
            }
        }
        return addresses;
    }
    
    @Override
    public Addresses updateAddresses(String orcid, Addresses addresses, Visibility defaultVisibility) {
        List<AddressEntity> existingAddressList = addressDao.findByOrcid(orcid, getLastModified(orcid));
        //Delete the deleted ones
        for(AddressEntity existingAddress : existingAddressList) {
            boolean deleteMe = true;            
            for(Address updatedOrNew : addresses.getAddress()) {
                if(existingAddress.getId().equals(updatedOrNew.getPutCode())) {
                    deleteMe = false;
                    break;
                }
            }                                   
            if(deleteMe) {
                try {
                    addressDao.deleteAddress(orcid, existingAddress.getId());
                } catch (Exception e) {
                    throw new ApplicationException("Unable to delete address " + existingAddress.getId(), e);
                }
            }
        }
        
        if(addresses != null && addresses.getAddress() != null) {
            for(Address updatedOrNew : addresses.getAddress()) {
                if(updatedOrNew.getPutCode() != null) {
                    //Update the existing ones
                   for(AddressEntity existingAddress : existingAddressList) {
                       if(existingAddress.getId().equals(updatedOrNew.getPutCode())) {
                           existingAddress.setLastModified(new Date());
                           existingAddress.setVisibility(updatedOrNew.getVisibility());
                           existingAddress.setIso2Country(updatedOrNew.getCountry().getValue());
                           existingAddress.setPrimary(updatedOrNew.getPrimary());
                           existingAddress.setDisplayIndex(updatedOrNew.getDisplayIndex());
                           addressDao.merge(existingAddress);
                       }
                   }
                } else {
                    //Add the new ones
                    AddressEntity newAddress = adapter.toAddressEntity(updatedOrNew);
                    SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
                    ProfileEntity profile = new ProfileEntity(orcid);
                    newAddress.setUser(profile);
                    newAddress.setDateCreated(new Date());
                    newAddress.setSource(sourceEntity);
                    newAddress.setVisibility(updatedOrNew.getVisibility());
                    newAddress.setDisplayIndex(updatedOrNew.getDisplayIndex());
                    addressDao.persist(newAddress);
                    
                }
            }
        }
        
        if (defaultVisibility != null)
            addressDao.updateAddressVisibility(orcid, defaultVisibility);
        
        return addresses;
    }
}
