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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private SourceNameCacheManager sourceNameCacheManager;

    @Mock
    private ClientDetailsManager mockClientDetailsManager;

    @Mock
    private RecordNameDao mockRecordNameDao;

    @Mock
    private RecordNameManagerReadOnly mockRecordNameManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // by default return client details entity with user obo disabled
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.eq(CLIENT_SOURCE_ID))).thenReturn(new ClientDetailsEntity());
        ReflectionTestUtils.setField(clientDetailsEntityCacheManager, "clientDetailsManager", mockClientDetailsManager);

        Mockito.when(mockRecordNameDao.exists(Mockito.eq("0000-0000-0000-0000"))).thenReturn(true);
        Mockito.when(mockRecordNameManager.fetchDisplayablePublicName(Mockito.eq("0000-0000-0000-0000"))).thenReturn("test");
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameDao", mockRecordNameDao);
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameManagerReadOnlyV3", mockRecordNameManager);
    }

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
    public void fromAddressEntityToAddressTest() {
        AddressEntity entity = getAddressEntity();
        Address address = adapter.toAddress(entity);
        assertNotNull(address);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertNotNull(address.getCreatedDate());
        assertNotNull(address.getLastModifiedDate());
        assertEquals(Long.valueOf(1), address.getPutCode());
        assertNotNull(address.getSource());
        assertEquals(CLIENT_SOURCE_ID, address.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        // not a user obo work
        assertNull(address.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromAddressEntityToUserOBOAddressTest() {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.eq(CLIENT_SOURCE_ID))).thenReturn(userOBOClient);

        AddressEntity entity = getAddressEntity();
        Address address = adapter.toAddress(entity);
        assertNotNull(address);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertNotNull(address.getCreatedDate());
        assertNotNull(address.getLastModifiedDate());
        assertEquals(Long.valueOf(1), address.getPutCode());
        assertNotNull(address.getSource());
        assertEquals(CLIENT_SOURCE_ID, address.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        // user obo work
        assertNotNull(address.getSource().getAssertionOriginOrcid());
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
        result.setClientSourceId(CLIENT_SOURCE_ID);
        return result;
    }
}
