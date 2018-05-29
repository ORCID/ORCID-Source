package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.AddressManagerReadOnly;
import org.orcid.jaxb.model.v3.rc1.record.Address;
import org.orcid.jaxb.model.v3.rc1.record.Addresses;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface AddressManager extends AddressManagerReadOnly {    
    Address updateAddress(String orcid, Long putCode, Address address, boolean isApiRequest);

    Address createAddress(String orcid, Address address, boolean isApiRequest);

    boolean deleteAddress(String orcid, Long putCode);
    
    Addresses updateAddresses(String orcid, Addresses addresses);
    
    /**
     * Removes all address that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all address will be
     *            removed.
     */
    void removeAllAddress(String orcid);
}
