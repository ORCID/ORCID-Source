package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Date;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbEmailAdapter;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.junit.Before;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.mapstruct.impl.JpaJaxbEmailAdapterImpl;
import org.orcid.core.adapter.MockedMapStructAdapters;

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
    public void testEmailEntityToEmail() throws JAXBException {
        Email email = getEmail();
        assertNotNull(email);
        EmailEntity entity = jpaJaxbEmailAdapter.toEmailEntity(email);
        assertNotNull(entity);
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());
        assertEquals("user1@email.com", entity.getEmail());
        assertEquals(Visibility.PUBLIC.name(), entity.getVisibility());

        // Source
        assertNull(entity.getSourceId());
        assertNull(entity.getClientSourceId());
        assertNull(entity.getElementSourceId());
    }

    @Test
    public void fromEmailEntityToEmail() throws IllegalAccessException {
        EmailEntity entity = getEmailEntity();
        Email email = jpaJaxbEmailAdapter.toEmail(entity);
        assertNotNull(email);
        assertNotNull(email.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(email.getCreatedDate().getValue()));
        assertNotNull(email.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(email.getLastModifiedDate().getValue()));
        assertNotNull(email.getLastModifiedDate().getValue());
        assertNotNull(email.getCreatedDate().getValue());
        assertEquals("email@test.orcid.org", email.getEmail());
        assertEquals(Visibility.PRIVATE, email.getVisibility());
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
