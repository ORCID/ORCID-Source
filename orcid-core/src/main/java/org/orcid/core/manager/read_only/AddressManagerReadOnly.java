package org.orcid.core.manager.read_only;

import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface AddressManagerReadOnly {
    Address getPrimaryAddress(String orcid);
    
    Addresses getAddresses(String orcid);
    
    Addresses getPublicAddresses(String orcid);
    
    Address getAddress(String orcid, Long putCode);
}
