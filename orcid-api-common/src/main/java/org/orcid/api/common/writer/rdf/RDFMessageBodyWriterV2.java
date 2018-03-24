package org.orcid.api.common.writer.rdf;

import static org.orcid.core.api.OrcidApiConstants.APPLICATION_RDFXML;
import static org.orcid.core.api.OrcidApiConstants.JSON_LD;
import static org.orcid.core.api.OrcidApiConstants.N_TRIPLES;
import static org.orcid.core.api.OrcidApiConstants.TEXT_N3;
import static org.orcid.core.api.OrcidApiConstants.TEXT_TURTLE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.orcid.api.common.writer.rdf.vocabs.Geonames;
import org.orcid.api.common.writer.rdf.vocabs.PAV;
import org.orcid.api.common.writer.rdf.vocabs.PROV;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.History;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.springframework.beans.factory.annotation.Value;

//Record
@Provider
@Produces({ APPLICATION_RDFXML, TEXT_TURTLE, TEXT_N3, JSON_LD, N_TRIPLES })
public class RDFMessageBodyWriterV2 implements MessageBodyWriter<Record>{
    
    /**
     * Extension of Jena's outdated FOAF vocabulary
     *
     */
    public static class FOAF extends org.apache.jena.sparql.vocabulary.FOAF {

        /** The RDF model that holds the vocabulary terms */
        private static Model m_model = ModelFactory.createDefaultModel();

        /** The namespace of the vocabulary as a string< */
        public static final String NS = "http://xmlns.com/foaf/0.1/";

        // The properties below are from:
        // FOAF Vocabulary Specification 0.99
        // http://xmlns.com/foaf/spec/20140114.html
        // .. which seems to be missing from Jena's FOAF

        /** Indicates an account held by this agent.< */
        public static final Property account = m_model.createProperty(NS + "account");
        /** The given name of some person. */
        public static final Property givenName = m_model.createProperty("http://xmlns.com/foaf/0.1/givenName");
        /** The family_name of some person. */
        public static final Property familyName = m_model.createProperty("http://xmlns.com/foaf/0.1/familyName");

    }

    public static class LDP {

        /** The RDF model that holds the vocabulary terms */
        private static Model m_model = ModelFactory.createDefaultModel();

        /** The namespace of the vocabulary as a string */
        public static final String NS = "http://www.w3.org/ns/ldp#";

        public static final Property inbox = m_model.createProperty(NS + "inbox");
    }

    public static class AS {

        /** The RDF model that holds the vocabulary terms */
        private static Model m_model = ModelFactory.createDefaultModel();

        /** The namespace of the vocabulary as a string */
        public static final String NS = "https://www.w3.org/ns/activitystreams#";

        public static final Property outbox = m_model.createProperty(NS + "outbox");
    }

    public static class PIM {

        /** The RDF model that holds the vocabulary terms */
        private static Model m_model = ModelFactory.createDefaultModel();

        /** The namespace of the vocabulary as a string */
        public static final String NS = "http://www.w3.org/ns/pim/space#";

        public static final Property storage = m_model.createProperty(NS + "storage");
    }

    private static final String COUNTRIES_TTL = "countries.ttl";
    private static final String MEMBER_API = "https://api.orcid.org/";
    private static final String EN = "en";

    private static final List<String> URL_NAME_HOMEPAGE = Arrays.asList("homepage", "home", "home page", "personal", "personal homepage", "personal home page");
    private static final String URL_NAME_FOAF = "foaf";
    private static final String URL_NAME_WEBID = "webid";
    private static final String URL_NAME_INBOX = "inbox";
    private static final String URL_NAME_OUTBOX = "outbox";
    private static final String URL_NAME_STORAGE = "storage";

    private static OntModel countries;

    @Value("${org.orcid.core.baseUri:http://orcid.org}")
    private String baseUri = "http://orcid.org";

    @Context
    private UriInfo uriInfo;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Record.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Record t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Record record, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {

        OntModel m = getOntModel();

        // System.out.println(httpHeaders);
        Individual profileDoc = null;
        if (record != null) {
            Individual person = describePerson(record, m);
            if (person != null) {
                profileDoc = describeAccount(record, m, person);
            }
        }
        MediaType jsonLd = new MediaType("application", "ld+json");
        MediaType nTriples = new MediaType("application", "n-triples");
        MediaType rdfXml = new MediaType("application", "rdf+xml");
        String base = null;
        if (getUriInfo() != null) {
                getUriInfo().getAbsolutePath().toASCIIString();
        }
        if (mediaType.isCompatible(nTriples)) { 
                // NOTE: N-Triples requires absolute URIs
                m.write(entityStream, "N-TRIPLES");
        }
        else if (mediaType.isCompatible(jsonLd)) {
                m.write(entityStream, "JSON-LD", base);
        }
        else if (mediaType.isCompatible(rdfXml)) {
            m.write(entityStream, "RDF/XML", base);        
        } else {
                // Turtle is the safest default         
            m.write(entityStream, "TURTLE", base);            
        }
    }

    private Individual describeAccount(Record record, OntModel m, Individual person) {
        String orcidURI = record.getOrcidIdentifier().getUri();
                String orcidPublicationsUri = orcidURI + "#workspace-works";
        Individual publications = m.createIndividual(orcidPublicationsUri, FOAF.Document);

        // list of publications
        // (anchor in the HTML rendering - foaf:publications goes to a foaf:Document - not to an
        // RDF list of publications - although we should probably also have that)
        person.addProperty(FOAF.publications, publications);

        
        String orcidAccountUri = orcidURI + "#orcid-id";               
        Individual account = m.createIndividual(orcidAccountUri, FOAF.OnlineAccount);        
        person.addProperty(FOAF.account, account);
        Individual webSite = null;
        if (baseUri != null) {
            webSite = m.createIndividual(baseUri, null);
            account.addProperty(FOAF.accountServiceHomepage, webSite);
        }
        String orcId = record.getOrcidIdentifier().getPath();
        account.addProperty(FOAF.accountName, orcId);
        account.addLabel(orcId, null);

        
        // The current page is the foaf:PersonalProfileDocument - this assumes
        // we have done a 303 See Other redirect to the RDF resource, so that it 
        // differs from the ORCID uri. 
        // for example:
        // 
        //     GET http://orcid.org/0000-0003-4654-1403
        //     Accept: text/turtle
        //  
        //     HTTP/1.1 303 See Other
        //     Location: https://pub.orcid.org/experimental_rdf_v1/0000-0001-9842-9718
        
        String profileUri;
        if (getUriInfo() != null) {
                profileUri = getUriInfo().getAbsolutePath().toASCIIString();
        } else { 
                // Some kind of fallback, although the PersonalProfiledocument should be an 
                // information resource without #anchor
                profileUri = orcidURI + "#personalProfileDocument";
        }
        Individual profileDoc = m.createIndividual(profileUri, 
                        FOAF.PersonalProfileDocument);
        profileDoc.addProperty(FOAF.primaryTopic, person);
        History history = record.getHistory();
        if (history != null) {
            if (history.getClaimed()) {
                // Set account as PersonalProfileDocument
                profileDoc.addProperty(FOAF.maker, person);

            }
            // Who made the profile?
            switch (history.getCreationMethod()) {
            case DIRECT:
            case MEMBER_REFERRED:
            case WEBSITE:
                profileDoc.addProperty(PAV.createdBy, person);
                profileDoc.addProperty(PROV.wasAttributedTo, person);
                if (webSite != null && 
                                (history.getCreationMethod() == CreationMethod.WEBSITE || history.getCreationMethod() == CreationMethod.DIRECT)) {
                        profileDoc.addProperty(PAV.createdWith, webSite);
                }
                break;
            case API:
                Individual api = m.createIndividual(MEMBER_API, PROV.SoftwareAgent);
                profileDoc.addProperty(PAV.importedBy, api);

                if (history.getClaimed()) {
                        profileDoc.addProperty(PAV.curatedBy, person);
                }

                break;
            default:
                // Some unknown agent!
                profileDoc.addProperty(PAV.createdWith, m.createIndividual(null, PROV.Agent));
            }

            if (history.getLastModifiedDate() != null) {
                Literal when = calendarAsLiteral(history.getLastModifiedDate().getValue(), m);
                profileDoc.addLiteral(PAV.lastUpdateOn, when);
                profileDoc.addLiteral(PROV.generatedAtTime, when);
            }
            if (history.getSubmissionDate() != null) {
                profileDoc.addLiteral(PAV.createdOn, calendarAsLiteral(history.getSubmissionDate().getValue(), m));
            }
            if (history.getCompletionDate() != null) {
                profileDoc.addLiteral(PAV.contributedOn, calendarAsLiteral(history.getCompletionDate().getValue(), m));
            }
            if (history.getDeactivationDate() != null) {
                profileDoc.addLiteral(PROV.invalidatedAtTime, calendarAsLiteral(history.getDeactivationDate().getValue(), m));
            }

        }

        return profileDoc;
    }

    private Literal calendarAsLiteral(XMLGregorianCalendar cal, OntModel m) {
        return m.createTypedLiteral(cal.toXMLFormat(), XSDDatatype.XSDdateTime);
    }

    private Individual describePerson(Record record, OntModel m) {
        String orcidUri = record.getOrcidIdentifier().getUri();
        Individual person = m.createIndividual(orcidUri, FOAF.Person);
        person.addRDFType(PROV.Person);

        if (record.getPerson() == null) {
            return person;
        }

        describePersonalDetails(record.getPerson().getName(), person, m);
        describeContactDetails(record.getPerson(), person, m);
        describeBiography(record.getPerson().getBiography(), person, m);
        describeResearcherUrls(record.getPerson().getResearcherUrls(), person, m);
        return person;
    }

    private void describeResearcherUrls(org.orcid.jaxb.model.record_v2.ResearcherUrls researcherUrls, Individual person, OntModel m) {
        if (researcherUrls == null || researcherUrls.getResearcherUrls() == null) {
            return;
        }
        for (ResearcherUrl url : researcherUrls.getResearcherUrls()) {
            Individual page = m.createIndividual(url.getUrl().getValue(), null);
            String urlName = url.getUrlName();
            if (isHomePage(urlName)) {
                person.addProperty(FOAF.homepage, page);
            } else if (isFoaf(urlName)) {
                // TODO: What if we want to link to the URL of the other FOAF
                // *Profile*?

                // Note: We don't dear here to do owl:sameAs or
                // prov:specializationOf as we don't know the extent of the
                // other FOAF profile - we'll
                // suffice to say it's an alternate view of the same person
                person.addProperty(PROV.alternateOf, page);
                page.addRDFType(FOAF.Person);
                page.addRDFType(PROV.Person);
                person.addSeeAlso(page);
            } else if (isWebID(urlName)) {
                person.addSameAs(page);
            } else if (isInbox(urlName)) {
                person.addProperty(LDP.inbox, page);
            } else if (isOutbox(urlName)) {
                person.addProperty(AS.outbox, page);
            } else if (isStorage(urlName)) {
                person.addProperty(PIM.storage, page);
            } else {
                // It's some other foaf:page which might not be about
                // this person
                person.addProperty(FOAF.page, page);
            }
        }
    }

    private boolean isFoaf(String urlName) {
        if (urlName == null) {
            return false;
        }
        return urlName.equalsIgnoreCase(URL_NAME_FOAF);
    }

    private boolean isWebID(String urlName) {
        if (urlName == null) {
            return false;
        }
        return urlName.toLowerCase().equals(URL_NAME_WEBID);
    }

    private boolean isInbox(String urlName) {
        if (urlName == null) {
            return false;
        }
        return urlName.equals(URL_NAME_INBOX);
    }

    private boolean isOutbox(String urlName) {
        if (urlName == null) {
            return false;
        }
        return urlName.equals(URL_NAME_OUTBOX);
    }

    private boolean isStorage(String urlName) {
        if (urlName == null) {
            return false;
        }
        return urlName.equals(URL_NAME_STORAGE);
    }

    /**
     * There's no indication in ORCID if the URL is a homepage or some other
     * page, so we'll guess based on it's name, it be something similar to
     * "home page".
     */
    private boolean isHomePage(String urlName) {
        if (urlName == null) {
            return false;
        }
        return URL_NAME_HOMEPAGE.contains(urlName);
    }

    private void describeBiography(org.orcid.jaxb.model.record_v2.Biography biography, Individual person, OntModel m) {
        if (biography != null && biography.getContent()!=null) {
            // FIXME: Which language is the biography written in? Can't assume
            // EN
            person.addProperty(FOAF.plan, biography.getContent());
        }
    }

    private void describeContactDetails(Person orcidPerson, Individual person, OntModel m) {
        if (orcidPerson == null) {
            return;
        }

        Emails emails = orcidPerson.getEmails();
        if (emails != null) {
            for (Email email : emails.getEmails()) {
                if (email.isCurrent()) {

                    Individual mbox = m.createIndividual("mailto:" + email.getEmail(), null);
                    person.addProperty(FOAF.mbox, mbox);
                }
            }
        }

        Addresses addresses = orcidPerson.getAddresses();
        Address addr = (addresses != null && addresses.getAddress().size()>0)?addresses.getAddress().get(0):null;
        if (addr != null) {
            if (addr.getCountry() != null) {
                String countryCode = addr.getCountry().getValue().name();

                Individual position = m.createIndividual(Geonames.Feature);
                position.addProperty(Geonames.countryCode, countryCode);
                person.addProperty(FOAF.based_near, position);

                Individual country = getCountry(countryCode);                
                if (country != null) {
                        country = addToModel(position.getOntModel(), country);
                    position.addProperty(Geonames.parentCountry, country);
                }

                // TODO: Include URI and (a) full name of country
                // Potential source: geonames.org
                // See https://gist.github.com/stain/7566375
            }
        }
    }

    private Individual addToModel(OntModel ontModel, Individual country) {
        // ontModel.addSubModel(country.getModel());
        ontModel.add(country.listProperties().toList());
        return country;
    }

    private Individual getCountry(String countryCode) {
        ResIterator hasCountryCode = getCountries().listSubjectsWithProperty(Geonames.countryCode, countryCode);
        if (hasCountryCode.hasNext()) {
            return getCountries().getIndividual(hasCountryCode.next().getURI());
        }
        return null;

    }

    private void describePersonalDetails(Name name, Individual person, OntModel m) {
        
        if (name.getCreditName() != null) {
            // User has provided full name
            String creditName = name.getCreditName().getContent();
            person.addProperty(FOAF.name, creditName);
            person.addLabel(creditName, null);
        } else if (name.getGivenNames() != null && name.getFamilyName() != null) {
            //@formatter:off
            // Naive fallback assuming givenNames ~= first name and familyName ~= lastName
            // See http://www.w3.org/International/questions/qa-personal-names for further
            // considerations -- we don't report this as foaf:name as we can't be sure of the ordering.
            //@formatter:on

            // NOTE: ORCID gui is westernized asking for "First name" and
            // "Last name" and assuming the above mapping
            String label = name.getGivenNames().getContent() + " " + name.getFamilyName().getContent();
            person.addLabel(label, null);
        }

        if (name.getGivenNames() != null) {
            person.addProperty(FOAF.givenName, name.getGivenNames().getContent());
        }
        if (name.getFamilyName() != null) {
            person.addProperty(FOAF.familyName, name.getFamilyName().getContent());
        }

    }

    protected OntModel getOntModel() {
        
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.setNsPrefix("foaf", FOAF.NS);
        ontModel.setNsPrefix("prov", PROV.NS);
        ontModel.setNsPrefix("pav", PAV.NS);
        ontModel.setNsPrefix("gn", Geonames.NS);
        // ontModel.getDocumentManager().loadImports(foaf.getOntModel());
        return ontModel;
    }

    protected OntModel getCountries() {
        if (countries != null) { 
                // Check for a static cache
                return countries;
        }
        
        // Load list of countries
        InputStream countriesStream = getClass().getResourceAsStream(COUNTRIES_TTL);
        if (countriesStream == null) { 
                throw new IllegalStateException("Can't find country resource on classpath: " + COUNTRIES_TTL);
        }
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.read(countriesStream, "http://example.com/", "TURTLE");

        // Note: We should not need to synchronize(this) to cache, 
        // as the odd concurrent duplicate load is not harmful
        // and can be thrown away
        countries = ontModel;
        return countries;
    }

    
    public UriInfo getUriInfo() {
        return uriInfo;
    }

    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }
}
