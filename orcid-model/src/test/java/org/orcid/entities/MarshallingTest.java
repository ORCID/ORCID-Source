package org.orcid.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;

/**
 * orcid-core - Oct 28, 2011 - PlaceholderTest
 * 
 * @author Declan Newman (declan)
 */

public class MarshallingTest {

    @Test
    public void testMarshallingFullMessage() throws JAXBException {
        OrcidMessage orcidMessage = getOrcidMessage();
        assertNotNull(orcidMessage);
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        assertNotNull(orcidProfile);
        assertEquals("4444-4444-4444-4446", orcidProfile.getOrcidIdentifier().getPath());
        OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
        assertNotNull(orcidActivities);
        assertEquals(4, orcidActivities.getAffiliations().getAffiliation().size());

        OrcidWorks orcidWorks = orcidProfile.retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork().size() == 1);
        OrcidWork orcidWork = orcidWorks.getOrcidWork().get(0);
        assertEquals("journal-article", orcidWork.getWorkType().value());

    }

    private OrcidMessage getOrcidMessage() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance("org.orcid.jaxb.model.message");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = MarshallingTest.class.getResourceAsStream("/orcid-internal-full-message-latest.xml");
        return (OrcidMessage) unmarshaller.unmarshal(inputStream);
    }

}
