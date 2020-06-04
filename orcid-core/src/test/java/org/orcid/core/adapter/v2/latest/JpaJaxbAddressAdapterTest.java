package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbAddressAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Address;
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
    private final Date now = new Date();
    
    @Resource
    private JpaJaxbAddressAdapter adapter;        
        
    @Test
    public void fromAddressToAddressEntityTest() throws JAXBException {                
        Address address = getAddress();
        AddressEntity addressEntity = adapter.toAddressEntity(address);
        assertNotNull(addressEntity);
        assertNotNull(addressEntity.getDateCreated());
        assertNotNull(addressEntity.getLastModified());
        assertEquals(Iso3166Country.US.name(), addressEntity.getIso2Country());  
        assertNull(addressEntity.getSourceId());
        assertNull(addressEntity.getClientSourceId());
        assertNull(addressEntity.getElementSourceId());
    }
    
    @Test
    public void fromOtherNameEntityToOtherNameTest() throws IllegalAccessException {                
        AddressEntity entity = getAddressEntity();
        Address address = adapter.toAddress(entity);
        assertNotNull(address);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(now, address.getCreatedDate());
        assertEquals(now, address.getLastModifiedDate());
        assertEquals(Long.valueOf(1), address.getPutCode());
        assertNotNull(address.getSource());
        assertEquals(CLIENT_SOURCE_ID, address.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
    }
    
    private Address getAddress() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Address.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/address-2.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Address) unmarshaller.unmarshal(inputStream);
    }
    
    private AddressEntity getAddressEntity() throws IllegalAccessException {
        AddressEntity result = new AddressEntity();
        FieldUtils.writeField(result, "dateCreated", now, true);
        FieldUtils.writeField(result, "lastModified", now, true);
        result.setId(Long.valueOf(1));
        result.setIso2Country(Iso3166Country.US.name());
        result.setUser(new ProfileEntity("0000-0000-0000-0000"));
        result.setVisibility(Visibility.PUBLIC.name());
        result.setClientSourceId(CLIENT_SOURCE_ID);
        return result;
    }
}
