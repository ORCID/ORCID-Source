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
package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbAddressAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.common_rc4.Iso3166Country;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbAddressAdapterTest extends MockSourceNameCache {
    @Resource
    private JpaJaxbAddressAdapter adapter;        
        
    @Test
    public void fromAddressToAddressEntityTest() throws JAXBException {                
        Address address = getAddress();
        AddressEntity addressEntity = adapter.toAddressEntity(address);
        assertNotNull(addressEntity);
        assertNotNull(addressEntity.getDateCreated());
        assertNotNull(addressEntity.getLastModified());
        assertEquals(Iso3166Country.US, addressEntity.getIso2Country());        
        assertEquals("8888-8888-8888-8880", addressEntity.getElementSourceId());
    }
    
    @Test
    public void fromOtherNameEntityToOtherNameTest() {                
        AddressEntity entity = getAddressEntity();
        Address address = adapter.toAddress(entity);
        assertNotNull(address);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertNotNull(address.getCreatedDate());
        assertNotNull(address.getLastModifiedDate());
        assertEquals(Long.valueOf(1), address.getPutCode());
        assertNotNull(address.getSource());
        assertEquals("APP-000000001", address.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
    }
    
    private Address getAddress() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Address.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0_rc4/samples/address-2.0_rc4.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Address) unmarshaller.unmarshal(inputStream);
    }
    
    private AddressEntity getAddressEntity() {
        AddressEntity result = new AddressEntity();
        result.setId(Long.valueOf(1));
        result.setDateCreated(new Date());
        result.setLastModified(new Date());
        result.setIso2Country(Iso3166Country.US);
        result.setUser(new ProfileEntity("0000-0000-0000-0000"));
        result.setVisibility(Visibility.PUBLIC);
        result.setClientSourceId("APP-000000001");
        return result;
    }
}
