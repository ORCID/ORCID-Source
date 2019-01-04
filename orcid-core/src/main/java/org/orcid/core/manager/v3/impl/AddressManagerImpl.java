package org.orcid.core.manager.v3.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.v3.AddressManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.AddressManagerReadOnlyImpl;
import org.orcid.core.manager.v3.validator.PersonValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.utils.v3.SourceEntityUtils;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.Addresses;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class AddressManagerImpl extends AddressManagerReadOnlyImpl implements AddressManager {

    @Resource(name = "orcidSecurityManagerV3")
    protected OrcidSecurityManager orcidSecurityManager;
    
    @Resource(name = "sourceManagerV3")
    protected SourceManager sourceManager;    
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager; 
    
    @Override
    @Transactional
    public Address updateAddress(String orcid, Long putCode, Address address, boolean isApiRequest) {
        Source activeSource = sourceManager.retrieveActiveSource();
        AddressEntity updatedEntity = addressDao.getAddress(orcid, putCode);
        Visibility originalVisibility = Visibility.fromValue(updatedEntity.getVisibility());
        
        //Save the original source
        Source originalSource = SourceEntityUtils.extractSourceFromEntity(updatedEntity);
        
        //If it is an update from the API, check the source and preserve the original visibility
        if(isApiRequest) {
            orcidSecurityManager.checkSourceAndThrow(updatedEntity);            
        }
        
        // Validate the address
        PersonValidator.validateAddress(address, activeSource, false, isApiRequest, originalVisibility);
        // Validate it is not duplicated
        List<AddressEntity> existingAddresses = addressDao.getAddresses(orcid, getLastModified(orcid));
        for (AddressEntity existing : existingAddresses) {
            //If it is not the same element
            if(!existing.getId().equals(address.getPutCode())) {
                if (isDuplicated(existing, address, activeSource)) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("type", "address");
                    params.put("value", address.getCountry().getValue().name());
                    throw new OrcidDuplicatedElementException(params);
                }
            }
        }
                        
        adapter.toAddressEntity(address, updatedEntity);
        updatedEntity.setLastModified(new Date());        

        //Be sure it doesn't overwrite the source
        SourceEntityUtils.populateSourceAwareEntityFromSource(originalSource, updatedEntity);
        
        addressDao.merge(updatedEntity);
        return adapter.toAddress(updatedEntity);
    }
    
    @Override    
    public Address createAddress(String orcid, Address address, boolean isApiRequest) { 
        Source activeSource = sourceManager.retrieveActiveSource();
        // Validate the address
        PersonValidator.validateAddress(address, activeSource, true, isApiRequest, null);
        // Validate it is not duplicated
        List<AddressEntity> existingAddresses = addressDao.getAddresses(orcid, getLastModified(orcid));
        for (AddressEntity existing : existingAddresses) {
            if (isDuplicated(existing, address, activeSource)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "address");
                params.put("value", address.getCountry().getValue().name());
                throw new OrcidDuplicatedElementException(params);
            }            
        }

        AddressEntity newEntity = adapter.toAddressEntity(address);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        newEntity.setUser(profile);
        newEntity.setDateCreated(new Date());
        
        SourceEntityUtils.populateSourceAwareEntityFromSource(activeSource, newEntity);
        
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(newEntity, isApiRequest);
        setIncomingPrivacy(newEntity, profile);
        addressDao.persist(newEntity);
        return adapter.toAddress(newEntity);
    }

    @Override
    @Transactional
    public boolean deleteAddress(String orcid, Long putCode) {
        AddressEntity entity = addressDao.getAddress(orcid, putCode);
        orcidSecurityManager.checkSourceAndThrow(entity);

        try {
            addressDao.remove(entity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean isDuplicated(AddressEntity existing, Address address, Source activeSource) {
        if (!existing.getId().equals(address.getPutCode())) {
            //If they have the same source 
            String existingSourceId = existing.getElementSourceId(); 
            if (!PojoUtil.isEmpty(existingSourceId) && SourceEntityUtils.isTheSameForDuplicateChecking(activeSource,existing)) {
                //TODO: Not sure this works!  String vs Iso3166Country enum 
                if(existing.getIso2Country().equals(address.getCountry().getValue())) {
                    return true;
                }
            }
        }
        return false;
    }    
    
    private void setIncomingPrivacy(AddressEntity entity, ProfileEntity profile) {
        String incomingCountryVisibility = entity.getVisibility();
        String defaultCountryVisibility = (profile.getActivitiesVisibilityDefault() == null) ? org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name() : profile.getActivitiesVisibilityDefault();        
        if (profile.getClaimed() != null && profile.getClaimed()) {
            entity.setVisibility(defaultCountryVisibility);            
        } else if (incomingCountryVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        }
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
                           existingAddress.setVisibility(updatedOrNew.getVisibility().name());
                           existingAddress.setIso2Country(updatedOrNew.getCountry().getValue().name());
                           existingAddress.setDisplayIndex(updatedOrNew.getDisplayIndex());
                           addressDao.merge(existingAddress);
                       }
                   }
                } else {
                    //Add the new ones
                    AddressEntity newAddress = adapter.toAddressEntity(updatedOrNew);
                    Source activeSource = sourceManager.retrieveActiveSource();
                    ProfileEntity profile = new ProfileEntity(orcid);
                    newAddress.setUser(profile);
                    newAddress.setDateCreated(new Date());
                    
                    SourceEntityUtils.populateSourceAwareEntityFromSource(activeSource, newAddress);
                                                            
                    newAddress.setVisibility(updatedOrNew.getVisibility().name());
                    newAddress.setDisplayIndex(updatedOrNew.getDisplayIndex());
                    addressDao.persist(newAddress);
                    
                }
            }
        }        
        return addresses;
    }

    @Override
    public void removeAllAddress(String orcid) {
        addressDao.removeAllAddress(orcid);
    }
}
