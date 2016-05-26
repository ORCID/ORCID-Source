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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.orcid.core.adapter.JpaJaxbAddressAdapter;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.core.version.impl.LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
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

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;    
    
    private long getLastModified(String orcid) {
        Date lastModified = profileEntityManager.getLastModified(orcid);
        return (lastModified == null) ? 0 : lastModified.getTime();
    }
    
    @Override
    public void setSourceManager(SourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }
    
    @Override
    @Cacheable(value = "primary-address", key = "#orcid.concat('-').concat(#lastModified)")
    public Address getPrimaryAddress(String orcid, long lastModified) {        
        List<AddressEntity> addresses = addressDao.getAddresses(orcid, getLastModified(orcid));
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
        AddressEntity entity = addressDao.getAddress(orcid, putCode);
        return adapter.toAddress(entity);
    }
    
    @Override
    @Transactional
    public Address updateAddress(String orcid, Long putCode, Address address, boolean isApiRequest) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        AddressEntity updatedEntity = addressDao.getAddress(orcid, putCode);
        Visibility originalVisibility = Visibility.fromValue(updatedEntity.getVisibility().value());
        
        //Save the original source
        String existingSourceId = updatedEntity.getSourceId();
        String existingClientSourceId = updatedEntity.getClientSourceId();
        
        //If it is an update from the API, check the source and preserve the original visibility
        if(isApiRequest) {
            orcidSecurityManager.checkSource(updatedEntity);            
        }
        
        // Validate the address
        PersonValidator.validateAddress(address, sourceEntity, false, isApiRequest, originalVisibility);
        // Validate it is not duplicated
        List<AddressEntity> existingAddresses = addressDao.getAddresses(orcid, getLastModified(orcid));
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
                        
        adapter.toAddressEntity(address, updatedEntity);
        updatedEntity.setLastModified(new Date());        

        //Be sure it doesn't overwrite the source
        updatedEntity.setSourceId(existingSourceId);
        updatedEntity.setClientSourceId(existingClientSourceId);                
        
        addressDao.merge(updatedEntity);
        return adapter.toAddress(updatedEntity);
    }
    
    @Override
    @Transactional
    public Address createAddress(String orcid, Address address, boolean isApiRequest) { 
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate the address
        PersonValidator.validateAddress(address, sourceEntity, true, isApiRequest, null);
        // Validate it is not duplicated
        List<AddressEntity> existingAddresses = addressDao.getAddresses(orcid, getLastModified(orcid));
        for (AddressEntity existing : existingAddresses) {
            if (isDuplicated(existing, address, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "address");
                params.put("value", address.getCountry().getValue().value());
                throw new OrcidDuplicatedElementException(params);
            }
        }

        AddressEntity newEntity = adapter.toAddressEntity(address);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        newEntity.setUser(profile);
        newEntity.setDateCreated(new Date());
        
        //Set the source
        if(sourceEntity.getSourceProfile() != null) {
            newEntity.setSourceId(sourceEntity.getSourceProfile().getId());
        }
        if(sourceEntity.getSourceClient() != null) {
            newEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
        }        
        
        setIncomingPrivacy(newEntity, profile);
        addressDao.persist(newEntity);
        return adapter.toAddress(newEntity);
    }

    @Override
    @Transactional
    public boolean deleteAddress(String orcid, Long putCode) {
        AddressEntity entity = addressDao.getAddress(orcid, putCode);
        orcidSecurityManager.checkSource(entity);

        try {
            addressDao.remove(entity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean isDuplicated(AddressEntity existing, Address address, SourceEntity source) {
        if (!existing.getId().equals(address.getPutCode())) {
            //If they have the same source 
            String existingSourceId = existing.getElementSourceId(); 
            if (!PojoUtil.isEmpty(existingSourceId) && existingSourceId.equals(source.getSourceId())) {
                if(existing.getIso2Country().equals(address.getCountry().getValue())) {
                    return true;
                }
            }
        }
        return false;
    }    
    
    private void setIncomingPrivacy(AddressEntity entity, ProfileEntity profile) {
        org.orcid.jaxb.model.common_rc2.Visibility incomingCountryVisibility = entity.getVisibility();
        org.orcid.jaxb.model.common_rc2.Visibility defaultCountryVisibility = (profile.getActivitiesVisibilityDefault() == null) ? org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE : org.orcid.jaxb.model.common_rc2.Visibility.fromValue(profile.getActivitiesVisibilityDefault().value());        
        if (profile.getClaimed() != null && profile.getClaimed()) {
            entity.setVisibility(defaultCountryVisibility);            
        } else if (incomingCountryVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE);
        }
    }

    @Override
    @Cacheable(value = "address", key = "#orcid.concat('-').concat(#lastModified)")
    public Addresses getAddresses(String orcid, long lastModified) {
        return getAddresses(orcid, null);        
    }

    @Override
    @Cacheable(value = "public-address", key = "#orcid.concat('-').concat(#lastModified)")
    public Addresses getPublicAddresses(String orcid, long lastModified) {
        return getAddresses(orcid, Visibility.PUBLIC);
    }
    
    private Addresses getAddresses(String orcid, Visibility visibility) {
        List<AddressEntity> addresses = new ArrayList<AddressEntity>();
        
        if (visibility == null) {
            addresses = addressDao.getAddresses(orcid, getLastModified(orcid));
        } else {
            addresses = addressDao.getAddresses(orcid, visibility);
        }           
        
        Addresses result = adapter.toAddressList(addresses);
        result.updateIndexingStatusOnChilds();
        LastModifiedDatesHelper.calculateLatest(result);
        
        return result;
    }
    
    @Override
    public Addresses updateAddresses(String orcid, Addresses addresses) {
        List<AddressEntity> existingAddressList = addressDao.getAddresses(orcid, getLastModified(orcid));
        //Delete the deleted ones
        for(AddressEntity existingAddress : existingAddressList) {
            boolean deleteMe = true;            
            if(addresses.getAddress() != null) {
                for(Address updatedOrNew : addresses.getAddress()) {
                    if(existingAddress.getId().equals(updatedOrNew.getPutCode())) {
                        deleteMe = false;
                        break;
                    }
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
                    
                    //Set the source id
                    if(sourceEntity.getSourceProfile() != null) {
                        newAddress.setSourceId(sourceEntity.getSourceProfile().getId());
                    }
                    if(sourceEntity.getSourceClient() != null) {
                        newAddress.setClientSourceId(sourceEntity.getSourceClient().getId());
                    }
                                                            
                    newAddress.setVisibility(updatedOrNew.getVisibility());
                    newAddress.setDisplayIndex(updatedOrNew.getDisplayIndex());
                    addressDao.persist(newAddress);
                    
                }
            }
        }
        
        return addresses;
    }
}
