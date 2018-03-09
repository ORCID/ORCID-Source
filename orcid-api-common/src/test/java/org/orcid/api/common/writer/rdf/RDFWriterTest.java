package org.orcid.api.common.writer.rdf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jena.riot.RIOT;
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
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;
import org.orcid.jaxb.model.message.Visibility;

//@RunWith(OrcidJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:orcid-t1-web-context.xml" })
public class RDFWriterTest {

	private static final String EXAMPLE_RDF_URI = "http://pub.orcid.example.com/experimental_rdf_v1/000-1337";


	static { 
		// Ensure RIOT is initialized so we get consistent RDF writers
		RIOT.init();
	}
	
    private static DatatypeFactory dataTypeFactory;
    private RDFMessageBodyWriter rdfWriter = new RDFMessageBodyWriter();

    @Before
    public void makeDataTypeFactory() throws DatatypeConfigurationException {
        dataTypeFactory = DatatypeFactory.newInstance();
    }
    
    @Before
    public void injectFakeUriInfo() {  
    	UriInfo uriInfo = mock(UriInfo.class);
    	when(uriInfo.getAbsolutePath()).thenReturn(URI.create(EXAMPLE_RDF_URI));
		rdfWriter.setUriInfo(uriInfo);
    }
    

    private OrcidMessage fakeBio() throws DatatypeConfigurationException {
        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidProfile orcidProfile1 = new OrcidProfile();
        OrcidIdentifier orcidIdentifier = new OrcidIdentifier();
        orcidProfile1.setOrcidIdentifier(orcidIdentifier);
        orcidIdentifier.setUri("http://orcid.example.com/000-1337");
        orcidIdentifier.setPath("000-1337");
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
        personal.getOtherNames().addOtherName("Johnny",Visibility.PUBLIC);
        personal.getOtherNames().addOtherName("Mr Doe",Visibility.PUBLIC);

        ResearcherUrls urls = new ResearcherUrls();
        bio.setResearcherUrls(urls);

        ResearcherUrl anonymous = new ResearcherUrl(new Url("http://example.com/anon"),Visibility.PUBLIC);
        urls.getResearcherUrl().add(anonymous);

        // "home page" - with strange casing
        ResearcherUrl homePage = new ResearcherUrl(new Url("http://example.com/myPage"), new UrlName("homePage"),Visibility.PUBLIC);
        urls.getResearcherUrl().add(homePage);

        ResearcherUrl foaf = new ResearcherUrl(new Url("http://example.com/foaf#me"), new UrlName("FOAF"),Visibility.PUBLIC);
        urls.getResearcherUrl().add(foaf);

        ResearcherUrl webId = new ResearcherUrl(new Url("http://example.com/webId"), new UrlName("webID"),Visibility.PUBLIC);
        urls.getResearcherUrl().add(webId);

        ResearcherUrl other = new ResearcherUrl(new Url("http://example.com/other"), new UrlName("other"),Visibility.PUBLIC);
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
        assertTrue(str.contains("http://orcid.example.com/000-1337#orcid-id"));
        assertTrue(str.contains(">000-1337<"));
        // relative URI reference
        assertTrue(str.contains(EXAMPLE_RDF_URI));
        assertFalse(str.contains("subClassOf"));
        assertTrue(str.contains("foaf:publications"));
        assertTrue(str.contains("http://orcid.example.com/000-1337#workspace-works"));
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
        assertTrue(str.contains("<http://orcid.example.com/000-1337#orcid-id>"));
        assertTrue(str.contains("\"000-1337\""));
        assertTrue(str.contains(EXAMPLE_RDF_URI));
        assertTrue(str.contains("foaf:primaryTopic"));
        assertTrue(str.contains("foaf:Person"));
        assertTrue(str.contains("foaf:familyName"));
        assertTrue(str.contains("\"Doe\""));
        assertTrue(str.contains("foaf:givenName"));
        assertTrue(str.contains("\"John\""));
        // and the credit name, which here includes initial F
        assertTrue(str.contains("foaf:name"));
        assertTrue(str.contains("\"John F Doe\""));
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
    public void writeNtriples() throws Exception {

        ByteArrayOutputStream entityStream = new ByteArrayOutputStream(1024);
        rdfWriter.writeTo(fakeBio(), OrcidMessage.class, null, null, new MediaType("application", "n-triples"), null, entityStream);

        String str = entityStream.toString("utf-8");
        System.out.println(str);
        assertTrue(str.contains("<http://orcid.example.com/000-1337>"));
        assertTrue(str.contains("<http://xmlns.com/foaf/0.1/account>"));
        assertTrue(str.contains(EXAMPLE_RDF_URI));
        assertTrue(str.contains("<http://xmlns.com/foaf/0.1/Person>"));
        assertTrue(str.contains("<http://xmlns.com/foaf/0.1/familyName>"));
        assertTrue(str.contains("\"Doe\""));
        assertTrue(str.contains("<http://xmlns.com/foaf/0.1/givenName>"));
        assertTrue(str.contains("\"John\""));
        // and the credit name, which here includes initial F
        assertTrue(str.contains("<http://xmlns.com/foaf/0.1/name>"));
        assertTrue(str.contains("\"John F Doe\""));
        // ontology details should NOT be included
        assertFalse(str.contains("subClassOf"));
        // provenance
        assertTrue(str.contains("<http://purl.org/pav/lastUpdateOn>"));
        assertTrue(str.contains("1980-12-31T23:29:29.999Z"));
        // location
        assertTrue(str.contains("<http://www.geonames.org/ontology#countryCode>"));
        assertTrue(str.contains("GB"));

    }

    @Test
    public void writeJsonLD() throws Exception {

        ByteArrayOutputStream entityStream = new ByteArrayOutputStream(1024);
        rdfWriter.writeTo(fakeBio(), OrcidMessage.class, null, null, new MediaType("application", "ld+json"), null, entityStream);

        String str = entityStream.toString("utf-8");
        System.out.println(str);
        assertTrue(str.contains("\"http://orcid.example.com/000-1337\""));
        assertTrue(str.contains("account"));
        assertTrue(str.contains("\"http://orcid.example.com/000-1337#orcid-id\""));
        assertTrue(str.contains("\"http://orcid.example.com/000-1337#workspace-works\""));
        assertTrue(str.contains(EXAMPLE_RDF_URI));
        assertTrue(str.contains("Person"));
        assertTrue(str.contains("familyName"));
        assertTrue(str.contains("\"Doe\""));
        assertTrue(str.contains("givenName"));
        assertTrue(str.contains("\"John\""));
        // and the credit name, which here includes initial F
        assertTrue(str.contains("name"));
        assertTrue(str.contains("\"John F Doe\""));
        // ontology details should NOT be included
        assertFalse(str.contains("subClassOf"));
        // provenance
        assertTrue(str.contains("lastUpdateOn"));
        assertTrue(str.contains("1980-12-31T23:29:29.999Z"));
        // location
        assertTrue(str.contains("countryCode"));
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
        assertTrue(str.contains("rdfs:label"));
        assertTrue(str.contains("\"John Doe\""));
        // And family/given
        assertTrue(str.contains("foaf:familyName"));
        assertTrue(str.contains("\"Doe\""));
        assertTrue(str.contains("foaf:givenName"));
        assertTrue(str.contains("\"John\""));
    }

}
