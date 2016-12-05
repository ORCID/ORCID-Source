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
import org.orcid.core.adapter.JpaJaxbEmailAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.Email;
import org.orcid.persistence.jpa.entities.EmailEntity;
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
public class JpaJaxbEmailAdapterTest extends MockSourceNameCache {

    @Resource
    private JpaJaxbEmailAdapter jpaJaxbEmailAdapter;

    @Test
    public void testEmailEntityToEmail() throws JAXBException {
        Email email = getEmail();
        assertNotNull(email);
        EmailEntity entity = jpaJaxbEmailAdapter.toEmailEntity(email);
        assertNotNull(entity);
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getLastModified());
        assertEquals("user1@email.com", entity.getId());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, entity.getVisibility());
        assertEquals("8888-8888-8888-8880", entity.getElementSourceId());
    }

    @Test
    public void fromEmailToEmailEntity() {
        EmailEntity entity = getEmailEntity();
        Email email = jpaJaxbEmailAdapter.toEmail(entity);
        assertNotNull(email);
        assertNotNull(email.getLastModifiedDate().getValue());
        assertNotNull(email.getCreatedDate().getValue());
        assertEquals("email@test.orcid.org", email.getEmail());
        assertEquals(Visibility.PRIVATE, email.getVisibility());
    }
    
    private Email getEmail() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Email.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0_rc4/samples/email-2.0_rc4.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Email) unmarshaller.unmarshal(inputStream);
    }
    
    private EmailEntity getEmailEntity() {
        EmailEntity result = new EmailEntity();
        result.setId("email@test.orcid.org");
        result.setCurrent(true);
        result.setPrimary(true);
        result.setProfile(new ProfileEntity("0000-0000-0000-0000"));
        result.setVerified(true);
        result.setDateCreated(new Date());
        result.setLastModified(new Date());       
        result.setVisibility(org.orcid.jaxb.model.message.Visibility.PRIVATE);   
        result.setClientSourceId("APP-000000001");
        
        return result;
    }
}
