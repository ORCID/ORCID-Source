package org.orcid.api.common.writer.rdf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jena.riot.RIOT;
import org.junit.Before;
import org.junit.Test;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.common_v2.Country;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.FamilyName;
import org.orcid.jaxb.model.record_v2.GivenNames;
import org.orcid.jaxb.model.record_v2.History;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;

//@RunWith(OrcidJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:orcid-t1-web-context.xml" })
public class RDFWriterTest {

	private static final String EXAMPLE_RDF_URI = "http://pub.orcid.example.com/experimental_rdf_v1/000-1337";


	static { 
		// Ensure RIOT is initialized so we get consistent RDF writers
		RIOT.init();
	}
	
    private static DatatypeFactory dataTypeFactory;
    private RDFMessageBodyWriterV2 rdfWriter = new RDFMessageBodyWriterV2();

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
    

    private Record fakeBio() throws DatatypeConfigurationException {
        Record r = new Record();
        
        r.setOrcidIdentifier(new OrcidIdentifier());
        r.getOrcidIdentifier().setPath("000-1337");
        r.getOrcidIdentifier().setUri("http://orcid.example.com/000-1337");
        
        r.setHistory(new History());
        r.getHistory().setCreationMethod(CreationMethod.WEBSITE);
        XMLGregorianCalendar value = dataTypeFactory.newXMLGregorianCalendar(1980, 12, 31, 23, 29, 29, 999, 0);
        r.getHistory().setLastModifiedDate(new LastModifiedDate(value));
        r.getHistory().setClaimed(true);
        
        r.setPerson(new Person());
        r.getPerson().setBiography(new Biography());
        
        r.getPerson().setName(new Name());
        r.getPerson().getName().setFamilyName(new FamilyName("Doe"));
        r.getPerson().getName().setCreditName(new CreditName("John F Doe"));
        r.getPerson().getName().setGivenNames(new GivenNames("John"));
        r.getPerson().setOtherNames(new OtherNames());
        r.getPerson().getOtherNames().setOtherNames(new ArrayList<OtherName>());
        OtherName n = new OtherName();
        n.setContent("Johnny");
        n.setVisibility(Visibility.PUBLIC);
        OtherName n1 = new OtherName();
        n1.setContent("Mr Doe");
        n1.setVisibility(Visibility.PUBLIC);
        r.getPerson().getOtherNames().getOtherNames().add(n);
        r.getPerson().getOtherNames().getOtherNames().add(n1);
        
        r.getPerson().setResearcherUrls(new ResearcherUrls());
        r.getPerson().getResearcherUrls().setResearcherUrls(new ArrayList<ResearcherUrl>());

        ResearcherUrl anonymous = new ResearcherUrl();
        anonymous.setUrl(new Url("http://example.com/anon"));
        anonymous.setVisibility(Visibility.PUBLIC);
        r.getPerson().getResearcherUrls().getResearcherUrls().add(anonymous);

        r.getPerson().getResearcherUrls().getResearcherUrls().add(buildRUrl("http://example.com/myPage","homePage"));
        r.getPerson().getResearcherUrls().getResearcherUrls().add(buildRUrl("http://example.com/foaf#me","FOAF"));
        r.getPerson().getResearcherUrls().getResearcherUrls().add(buildRUrl("http://example.com/webId","webID"));
        r.getPerson().getResearcherUrls().getResearcherUrls().add(buildRUrl("http://example.com/other","other"));

        r.getPerson().setAddresses(new Addresses());
        r.getPerson().getAddresses().setAddress(new ArrayList<Address>());
        Address a = new Address();
        a.setCountry(new Country());
        a.getCountry().setValue(Iso3166Country.GB);
        r.getPerson().getAddresses().getAddress().add(a);
        
        r.getPerson().setEmails(new Emails());
        r.getPerson().getEmails().setEmails(new ArrayList<Email>());
        Email e = new Email();
        e.setEmail("john@example.org");
        e.setCurrent(true);
        Email e1 = new Email();
        e1.setEmail("doe@example.com");
        e1.setCurrent(true);
        r.getPerson().getEmails().getEmails().add(e);
        r.getPerson().getEmails().getEmails().add(e1);
        return r;
    }

    @Test
    public void writeRdfXML() throws Exception {

        ByteArrayOutputStream entityStream = new ByteArrayOutputStream(1024);

        rdfWriter.writeTo(fakeBio(), Record.class, null, null, new MediaType("application", "rdf+xml"), null, entityStream);

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
        rdfWriter.writeTo(fakeBio(), Record.class, null, null, new MediaType("text", "turtle"), null, entityStream);

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
        rdfWriter.writeTo(fakeBio(), Record.class, null, null, new MediaType("application", "n-triples"), null, entityStream);

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
        rdfWriter.writeTo(fakeBio(), Record.class, null, null, new MediaType("application", "ld+json"), null, entityStream);

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
        Record fakeBio = fakeBio();
        // empty creditName
        fakeBio.getPerson().getName().setCreditName(null);
        //fakeBio.getOrcidProfile().getOrcidBio().getPersonalDetails().setCreditName(null);
        rdfWriter.writeTo(fakeBio, Record.class, null, null, new MediaType("text", "turtle"), null, entityStream);

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
    
    private ResearcherUrl buildRUrl(String url, String name){
        ResearcherUrl foaf = new ResearcherUrl();
        foaf.setUrl(new Url(url));
        foaf.setUrlName(name);
        foaf.setVisibility(Visibility.PUBLIC);
        return foaf;

    }

}
