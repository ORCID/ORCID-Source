/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.common.writer.rdf;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:orcid-t1-web-context.xml" })
public class RDFWriterTest {

    private RDFMessageBodyWriter rdfWriter = new RDFMessageBodyWriter();

    private OrcidMessage fakeBio() {
        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidProfile orcidProfile1 = new OrcidProfile();
        orcidProfile1.setOrcidId("http://orcid.example.com/000-1337");
        orcidProfile1.setOrcid("000-1337");
        OrcidBio bio = new OrcidBio();
        orcidProfile1.setOrcidBio(bio);
        PersonalDetails personal = new PersonalDetails();
        bio.setPersonalDetails(personal);
        personal.setFamilyName(new FamilyName("Doe"));
        personal.setCreditName(new CreditName("John F Doe"));
        personal.setGivenNames(new GivenNames("John"));
        personal.setOtherNames(new OtherNames());
        personal.getOtherNames().addOtherName("Johnny");
        personal.getOtherNames().addOtherName("Mr Doe");

        bio.setContactDetails(new ContactDetails());
        bio.getContactDetails().setEmail(Arrays.asList(new Email("john@example.org"), new Email("doe@example.com")));

        orcidMessage.setOrcidProfile(orcidProfile1);
        return orcidMessage;

    }

    @Test
    public void writeRdfXML() throws Exception {

        ByteArrayOutputStream entityStream = new ByteArrayOutputStream(1024);

        rdfWriter.writeTo(fakeBio(), OrcidMessage.class, null, null, new MediaType("application", "rdf+xml"), null, entityStream);

        String str = entityStream.toString("utf-8");
        System.out.println(str);
        assertTrue(str.contains("http://orcid.example.com/000-1337"));
        assertTrue(str.contains("foaf:name>John F"));
        assertTrue(str.contains("rdf:about"));
        assertFalse(str.contains("subClassOf"));
    }

    @Test
    public void writeTurte() throws Exception {

        ByteArrayOutputStream entityStream = new ByteArrayOutputStream(1024);
        rdfWriter.writeTo(fakeBio(), OrcidMessage.class, null, null, new MediaType("text", "turtle"), null, entityStream);

        String str = entityStream.toString("utf-8");
        System.out.println(str);
        assertTrue(str.contains("<http://orcid.example.com/000-1337>"));
        assertTrue(str.contains("foaf:Person"));
        assertTrue(str.contains("foaf:name \"John F"));
        assertFalse(str.contains("subClassOf"));
    }

}
