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
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.MockedMapStructAdapters;
import org.orcid.core.adapter.mapstruct.v3.impl.JpaJaxbEmailAdapterImpl;

/**
 *
 * @author Angel Montenegro
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaJaxbEmailAdapterTest {

    private static final String CLIENT_SOURCE_ID = MockedMapStructAdapters.CLIENT_SOURCE_ID;

    private final MockedMapStructAdapters adapters = new MockedMapStructAdapters();

    private JpaJaxbEmailAdapter jpaJaxbEmailAdapter;

    @Before
    public void setUpAdapter() throws Exception {
        // REAL MapStruct mapper: the whole behaviour under test is the mapping configuration,
        // so a mocked mapper would make every assertion below an assertion about a mock.
        jpaJaxbEmailAdapter = adapters.get(JpaJaxbEmailAdapterImpl.class);
    }

    @Test
    public void testEmailToEmailEntity() throws JAXBException {
        Email email = getEmail();
        assertNotNull(email);
        EmailEntity entity = jpaJaxbEmailAdapter.toEmailEntity(email);
        assertNotNull(entity);
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());
        assertNull(entity.getDateVerified());
        assertEquals("user1@email.com", entity.getEmail());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name(), entity.getVisibility());

        // Source
        assertNull(entity.getSourceId());
        assertNull(entity.getClientSourceId());
        assertNull(entity.getElementSourceId());
    }

    @Test
    public void testEmailEntityToEmail() throws IllegalAccessException {
        EmailEntity entity = getEmailEntity();
        Email email = jpaJaxbEmailAdapter.toEmail(entity);
        assertNotNull(email);
        assertNotNull(email.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(email.getCreatedDate().getValue()));
        assertNotNull(email.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(email.getLastModifiedDate().getValue()));
        assertNotNull(email.getVerificationDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(email.getVerificationDate().getValue()));
        assertEquals("email@test.orcid.org", email.getEmail());
        assertEquals(Visibility.PRIVATE, email.getVisibility());

        // no user obo
        assertNull(email.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void testEmailEntityToUserOBOEmail() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(adapters.clientDetailsEntityCacheManager.retrieve(CLIENT_SOURCE_ID)).thenReturn(userOBOClient);

        EmailEntity entity = getEmailEntity();
        Email email = jpaJaxbEmailAdapter.toEmail(entity);
        assertNotNull(email);
        assertNotNull(email.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(email.getCreatedDate().getValue()));
        assertNotNull(email.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(email.getLastModifiedDate().getValue()));
        assertEquals("email@test.orcid.org", email.getEmail());
        assertEquals(Visibility.PRIVATE, email.getVisibility());

        // user obo
        assertNotNull(email.getSource().getAssertionOriginOrcid());
    }

    private Email getEmail() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Email.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/email-2.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Email) unmarshaller.unmarshal(inputStream);
    }

    private EmailEntity getEmailEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        EmailEntity result = new EmailEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(result, date);
        result.setDateVerified(date);
        result.setEmail("email@test.orcid.org");
        result.setCurrent(true);
        result.setPrimary(true);
        result.setOrcid("0000-0000-0000-0000");
        result.setVerified(true);
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        result.setClientSourceId(CLIENT_SOURCE_ID);

        return result;
    }
}
