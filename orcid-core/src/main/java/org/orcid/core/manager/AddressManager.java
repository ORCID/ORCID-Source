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
package org.orcid.core.manager;

import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.record_rc4.Addresses;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface AddressManager {
    void setSourceManager(SourceManager sourceManager); 
    
    Address getPrimaryAddress(String orcid, long lastModified);
    
    Addresses getAddresses(String orcid, long lastModified);
    
    Addresses getPublicAddresses(String orcid, long lastModified);
    
    Address getAddress(String orcid, Long putCode);        

    Address updateAddress(String orcid, Long putCode, Address address, boolean isApiRequest);

    Address createAddress(String orcid, Address address, boolean isApiRequest);

    boolean deleteAddress(String orcid, Long putCode);
    
    Addresses updateAddresses(String orcid, Addresses addresses);
}
