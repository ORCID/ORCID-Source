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
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;

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
        XMLGregorianCalendar value = dataTypeFactory.newXMLGregorianCalendar(1980, 12, 31, 23, 29, 29, 999, 0);
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

        ResearcherUrls urls = new ResearcherUrls();
        bio.setResearcherUrls(urls);

        ResearcherUrl anonymous = new ResearcherUrl(new Url("http://example.com/anon"));
        urls.getResearcherUrl().add(anonymous);

        // "home page" - with strange casing
        ResearcherUrl homePage = new ResearcherUrl(new Url("http://example.com/myPage"), new UrlName("homePage"));
        urls.getResearcherUrl().add(homePage);

        ResearcherUrl foaf = new ResearcherUrl(new Url("http://example.com/foaf#me"), new UrlName("FOAF"));
        urls.getResearcherUrl().add(foaf);

        ResearcherUrl webId = new ResearcherUrl(new Url("http://example.com/webId"), new UrlName("webID"));
        urls.getResearcherUrl().add(webId);

        ResearcherUrl other = new ResearcherUrl(new Url("http://example.com/other"), new UrlName("other"));
        urls.getResearcherUrl().add(other);

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
        assertTrue(str.contains("rdf:about"));
        assertTrue(str.contains("http://orcid.example.com/000-1337"));
        assertTrue(str.contains("foaf:name>John F Doe<"));
        assertTrue(str.contains("foaf:givenName>John<"));
        assertTrue(str.contains("foaf:familyName>Doe<"));
        assertTrue(str.contains("foaf:account"));
        assertTrue(str.contains("http://orcid.example.com/000-1337/"));
        assertFalse(str.contains("subClassOf"));
        assertTrue(str.contains("foaf:mbox"));
        assertTrue(str.contains("mailto:john@example.org"));
        assertTrue(str.contains("mailto:doe@example.com"));
        assertTrue(str.contains("pav:lastUpdateOn"));
        assertTrue(str.contains("1980-12-31T23:29:29.999Z"));
        assertTrue(str.contains("foaf:based_near"));
        assertTrue(str.contains("gn:countryCode"));
        assertTrue(str.contains("GB"));
        assertTrue(str.contains("owl:sameAs"));
        assertTrue(str.contains("http://example.com/webId"));
        assertTrue(str.contains("rdfs:seeAlso"));
        assertTrue(str.contains("prov:alternateOf"));
        assertTrue(str.contains("http://example.com/foaf#me"));

        assertTrue(str.contains("foaf:page"));
        assertTrue(str.contains("http://example.com/anon"));
        assertTrue(str.contains("http://example.com/other"));

    }

    @Test
    public void writeTurte() throws Exception {

        ByteArrayOutputStream entityStream = new ByteArrayOutputStream(1024);
        rdfWriter.writeTo(fakeBio(), OrcidMessage.class, null, null, new MediaType("text", "turtle"), null, entityStream);

        String str = entityStream.toString("utf-8");
        System.out.println(str);
        assertTrue(str.contains("<http://orcid.example.com/000-1337>"));
        assertTrue(str.contains("foaf:account"));
        assertTrue(str.contains("<http://orcid.example.com/000-1337/>"));
        assertTrue(str.contains("foaf:Person"));
        assertTrue(str.contains("foaf:familyName \"Doe"));
        assertTrue(str.contains("foaf:givenName \"John"));
        // and the credit name, which here includes initial F
        assertTrue(str.contains("foaf:name \"John F Doe"));
        // ontology details should NOT be included
        assertFalse(str.contains("subClassOf"));
        // provenance
        assertTrue(str.contains("pav:lastUpdateOn"));
        assertTrue(str.contains("1980-12-31T23:29:29.999Z"));
        // location
        assertTrue(str.contains("gn:countryCode"));
        assertTrue(str.contains("GB"));

    }

    @Test
    public void missingCreditName() throws Exception {

        ByteArrayOutputStream entityStream = new ByteArrayOutputStream(1024);
        OrcidMessage fakeBio = fakeBio();
        // empty creditName
        fakeBio.getOrcidProfile().getOrcidBio().getPersonalDetails().setCreditName(null);
        rdfWriter.writeTo(fakeBio, OrcidMessage.class, null, null, new MediaType("text", "turtle"), null, entityStream);

        String str = entityStream.toString("utf-8");
        System.out.println(str);
        // Should NOT include a foaf:name
        assertFalse(str.contains("foaf:name"));
        // but do include a concatenation as a label
        assertTrue(str.contains("rdfs:label \"John Doe"));
        // And family/given
        assertTrue(str.contains("foaf:familyName \"Doe"));
        assertTrue(str.contains("foaf:givenName \"John"));
    }

}
