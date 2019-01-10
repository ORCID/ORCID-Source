package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.v3.JpaJaxbAddressAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.v3.rc2.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Address;
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
    @Resource(name = "jpaJaxbAddressAdapterV3")
    private JpaJaxbAddressAdapter adapter;        
        
    @Test
    public void fromAddressToAddressEntityTest() throws JAXBException {                
        Address address = getAddress();
        AddressEntity addressEntity = adapter.toAddressEntity(address);
        assertNotNull(addressEntity);
        assertNotNull(addressEntity.getDateCreated());
        assertNotNull(addressEntity.getLastModified());
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name(), addressEntity.getIso2Country());  
        assertNull(addressEntity.getSourceId());
        assertNull(addressEntity.getClientSourceId());
        assertNull(addressEntity.getElementSourceId());
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
        String name = "/record_3.0_rc1/samples/read_samples/address-3.0_rc1.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Address) unmarshaller.unmarshal(inputStream);
    }
    
    private AddressEntity getAddressEntity() {
        AddressEntity result = new AddressEntity();
        result.setId(Long.valueOf(1));
        result.setDateCreated(new Date());
        result.setLastModified(new Date());
        result.setIso2Country(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name());
        result.setUser(new ProfileEntity("0000-0000-0000-0000"));
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name());
        result.setClientSourceId("APP-000000001");
        return result;
    }
}
