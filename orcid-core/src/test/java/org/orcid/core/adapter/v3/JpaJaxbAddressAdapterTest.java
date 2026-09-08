package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Date;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.mapstruct.v3.impl.JpaJaxbAddressAdapterImpl;
import org.orcid.core.adapter.MockedMapStructAdapters;

/**
 *
 * @author Angel Montenegro
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaJaxbAddressAdapterTest {

    private static final String CLIENT_SOURCE_ID = MockedMapStructAdapters.CLIENT_SOURCE_ID;

    private final MockedMapStructAdapters adapters = new MockedMapStructAdapters();

    private JpaJaxbAddressAdapter adapter;

    @Before
    public void setUpAdapter() throws Exception {
        // REAL MapStruct mapper: the whole behaviour under test is the mapping configuration,
        // so a mocked mapper would make every assertion below an assertion about a mock.
        adapter = adapters.get(JpaJaxbAddressAdapterImpl.class);
    }

    @Test
    public void fromAddressToAddressEntityTest() throws JAXBException {
        Address address = getAddress();
        AddressEntity addressEntity = adapter.toAddressEntity(address);
        assertNotNull(addressEntity);
        assertNull(addressEntity.getDateCreated());
        assertNull(addressEntity.getLastModified());
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name(), addressEntity.getIso2Country());
        assertNull(addressEntity.getSourceId());
        assertNull(addressEntity.getClientSourceId());
        assertNull(addressEntity.getElementSourceId());
    }

    @Test
    public void fromAddressEntityToAddressTest() throws IllegalAccessException {
        AddressEntity entity = getAddressEntity();
        Address address = adapter.toAddress(entity);
        assertNotNull(address);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertNotNull(address.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(address.getCreatedDate().getValue()));
        assertNotNull(address.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(address.getLastModifiedDate().getValue()));
        assertEquals(Long.valueOf(1), address.getPutCode());
        assertNotNull(address.getSource());
        assertEquals(CLIENT_SOURCE_ID, address.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        // not a user obo work
        assertNull(address.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromAddressEntityToUserOBOAddressTest() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(adapters.clientDetailsEntityCacheManager.retrieve(CLIENT_SOURCE_ID)).thenReturn(userOBOClient);

        AddressEntity entity = getAddressEntity();
        Address address = adapter.toAddress(entity);
        assertNotNull(address);
        assertNotNull(address.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(address.getCreatedDate().getValue()));
        assertNotNull(address.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(address.getLastModifiedDate().getValue()));
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
        String name = "/record_3.0/samples/read_samples/address-3.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Address) unmarshaller.unmarshal(inputStream);
    }

    private AddressEntity getAddressEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        AddressEntity result = new AddressEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(result, date);
        result.setId(Long.valueOf(1));
        result.setIso2Country(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name());
        result.setOrcid("0000-0000-0000-0000");
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name());
        result.setClientSourceId(CLIENT_SOURCE_ID);
        return result;
    }
}
