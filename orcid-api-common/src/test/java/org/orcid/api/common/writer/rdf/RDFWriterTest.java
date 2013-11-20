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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.LastModifiedDate;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:orcid-t1-web-context.xml" })
public class RDFWriterTest {

    private static DatatypeFactory dataTypeFactory;
    private RDFMessageBodyWriter rdfWriter = new RDFMessageBodyWriter();

    @Before
    public void makeDataTypeFactory() throws DatatypeConfigurationException {
        dataTypeFactory = DatatypeFactory.newInstance();
    }
    
    private OrcidMessage fakeBio() throws DatatypeConfigurationException {
        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidProfile orcidProfile1 = new OrcidProfile();
        orcidProfile1.setOrcidId("http://orcid.example.com/000-1337");
        orcidProfile1.setOrcid("000-1337");
        OrcidBio bio = new OrcidBio();
        orcidProfile1.setOrcidBio(bio);
        OrcidHistory history = new OrcidHistory();
        XMLGregorianCalendar value = dataTypeFactory.newXMLGregorianCalendar(1980,12,31,23,29,29,999,0);
        history.setCreationMethod(CreationMethod.WEBSITE);
        history.setLastModifiedDate(new LastModifiedDate(value));
        orcidProfile1.setOrcidHistory(history);
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
        bio.getContactDetails().setAddress(new Address());
        bio.getContactDetails().getAddress().setCountry(new Country(Iso3166Country.GB));
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
        assertTrue(str.contains("pav:lastUpdateOn"));
        assertTrue(str.contains("1980-12-31T23:29:29.999Z"));
        assertTrue(str.contains("gn:countryCode"));
        assertTrue(str.contains("GB"));

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
        assertTrue(str.contains("pav:lastUpdateOn"));
        assertTrue(str.contains("1980-12-31T23:29:29.999Z"));
        assertTrue(str.contains("gn:countryCode"));
        assertTrue(str.contains("GB"));

    }

}
