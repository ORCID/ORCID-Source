package org.orcid.api.common.writer.rdf;

import static org.orcid.core.api.OrcidApiConstants.APPLICATION_RDFXML;
import static org.orcid.core.api.OrcidApiConstants.TEXT_N3;
import static org.orcid.core.api.OrcidApiConstants.TEXT_TURTLE;
import static org.orcid.core.api.OrcidApiConstants.JSON_LD;
import static org.orcid.core.api.OrcidApiConstants.N_TRIPLES;

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

import org.orcid.api.common.writer.rdf.vocabs.Geonames;
import org.orcid.api.common.writer.rdf.vocabs.PAV;
import org.orcid.api.common.writer.rdf.vocabs.PROV;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.springframework.beans.factory.annotation.Value;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;

/**
 * @author Stian Soiland-Reyes
 * @author Sarven Capadisli
 */
@Provider
@Produces({ APPLICATION_RDFXML, TEXT_TURTLE, TEXT_N3, JSON_LD, N_TRIPLES })
public class RDFMessageBodyWriter implements MessageBodyWriter<OrcidMessage> {
	
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
	    public static final Property account = m_model.createProperty( NS + "account" );
	    /** The given name of some person. */
	    public static final Property givenName = m_model.createProperty( "http://xmlns.com/foaf/0.1/givenName" );
	    /** The family_name of some person. */
	    public static final Property familyName = m_model.createProperty( "http://xmlns.com/foaf/0.1/familyName" );

	    
		
	}
    public static class LDP {

        /** The RDF model that holds the vocabulary terms */
        private static Model m_model = ModelFactory.createDefaultModel();

        /** The namespace of the vocabulary as a string */
        public static final String NS = "http://www.w3.org/ns/ldp#";

        public static final Property inbox = m_model.createProperty( NS + "inbox" );
    }
    public static class AS {

        /** The RDF model that holds the vocabulary terms */
        private static Model m_model = ModelFactory.createDefaultModel();

        /** The namespace of the vocabulary as a string */
        public static final String NS = "https://www.w3.org/ns/activitystreams#";

        public static final Property outbox = m_model.createProperty( NS + "outbox" );
    }
    public static class PIM {

        /** The RDF model that holds the vocabulary terms */
        private static Model m_model = ModelFactory.createDefaultModel();

        /** The namespace of the vocabulary as a string */
        public static final String NS = "http://www.w3.org/ns/pim/space#";

        public static final Property storage = m_model.createProperty( NS + "storage" );
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

    /**
     * Ascertain if the MessageBodyWriter supports a particular type.
     * 
     * 
     * @param type
     *            the class of object that is to be written.
     * @param genericType
     *            the type of object to be written, obtained either by
     *            reflection of a resource method return type or via inspection
     *            of the returned instance.
     *            {@link javax.ws.rs.core.GenericEntity} provides a way to
     *            specify this information at runtime.
     * @param annotations
     *            an array of the annotations on the resource method that
     *            returns the object.
     * @param mediaType
     *            the media type of the HTTP entity.
     * @return true if the type is supported, otherwise false.
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return OrcidMessage.class.isAssignableFrom(type);
    }

    /**
     * Called before <code>writeTo</code> to ascertain the length in bytes of
     * the serialized form of <code>t</code>. A non-negative return value is
     * used in a HTTP <code>Content-Length</code> header.
     * 
     * @param message
     *            the instance to write
     * @param type
     *            the class of object that is to be written.
     * @param genericType
     *            the type of object to be written, obtained either by
     *            reflection of a resource method return type or by inspection
     *            of the returned instance.
     *            {@link javax.ws.rs.core.GenericEntity} provides a way to
     *            specify this information at runtime.
     * @param annotations
     *            an array of the annotations on the resource method that
     *            returns the object.
     * @param mediaType
     *            the media type of the HTTP entity.
     * @return length in bytes or -1 if the length cannot be determined in
     *         advance
     */
    @Override
    public long getSize(OrcidMessage message, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // TODO: Can we calculate the size in advance?
        // It would mean buffering up the actual RDF
        return -1;
    }

    /**
     * Write a type to an HTTP response. The response header map is mutable but
     * any changes must be made before writing to the output stream since the
     * headers will be flushed prior to writing the response body.
     * 
     * @param message
     *            the instance to write.
     * @param type
     *            the class of object that is to be written.
     * @param genericType
     *            the type of object to be written, obtained either by
     *            reflection of a resource method return type or by inspection
     *            of the returned instance.
     *            {@link javax.ws.rs.core.GenericEntity} provides a way to
     *            specify this information at runtime.
     * @param annotations
     *            an array of the annotations on the resource method that
     *            returns the object.
     * @param mediaType
     *            the media type of the HTTP entity.
     * @param httpHeaders
     *            a mutable map of the HTTP response headers.
     * @param entityStream
     *            the {@link java.io.OutputStream} for the HTTP entity. The
     *            implementation should not close the output stream.
     * @throws java.io.IOException
     *             if an IO error arises
     * @throws javax.ws.rs.WebApplicationException
     *             if a specific HTTP error response needs to be produced. Only
     *             effective if thrown prior to the response being committed.
     */
    @Override
    public void writeTo(OrcidMessage xml, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {

        OntModel m = getOntModel();

        if (xml.getErrorDesc() != null) {
            describeError(xml.getErrorDesc(), m);
        }

        OrcidProfile orcidProfile = xml.getOrcidProfile();
        // System.out.println(httpHeaders);
        Individual profileDoc = null;
        if (orcidProfile != null) {
            Individual person = describePerson(orcidProfile, m);
            if (person != null) {
                profileDoc = describeAccount(orcidProfile, m, person);
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

    protected void describeError(ErrorDesc errorDesc, OntModel m) {
        String error = errorDesc.getContent();
        Individual root = m.createIndividual(m.createResource());
        root.setLabel("Error", EN);
        root.setComment(error, EN);
    }

    private Individual describeAccount(OrcidProfile orcidProfile, OntModel m, Individual person) {
        String orcidURI = orcidProfile.getOrcidIdentifier().getUri();
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
        String orcId = orcidProfile.getOrcidIdentifier().getPath();
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
        OrcidHistory history = orcidProfile.getOrcidHistory();
        if (history != null) {
            if (history.isClaimed().booleanValue()) {
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

                if (history.isClaimed().booleanValue()) {
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

    private Individual describePerson(OrcidProfile orcidProfile, OntModel m) {
        String orcidUri = orcidProfile.getOrcidIdentifier().getUri();
        Individual person = m.createIndividual(orcidUri, FOAF.Person);
        person.addRDFType(PROV.Person);

        if (orcidProfile.getOrcidBio() == null) {
            return person;
        }
        OrcidBio orcidBio = orcidProfile.getOrcidBio();
        if (orcidBio == null) {
            return person;
        }

        describePersonalDetails(orcidBio.getPersonalDetails(), person, m);
        describeContactDetails(orcidBio.getContactDetails(), person, m);
        describeBiography(orcidBio.getBiography(), person, m);
        describeResearcherUrls(orcidBio.getResearcherUrls(), person, m);
        return person;
    }

    private void describeResearcherUrls(ResearcherUrls researcherUrls, Individual person, OntModel m) {
        if (researcherUrls == null || researcherUrls.getResearcherUrl() == null) {
            return;
        }
        for (ResearcherUrl url : researcherUrls.getResearcherUrl()) {
            Individual page = m.createIndividual(url.getUrl().getValue(), null);
            String urlName = getUrlName(url);
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

    private String getUrlName(ResearcherUrl url) {
        if (url.getUrlName() == null) {
            return null;
        }
        return url.getUrlName().getContent().toLowerCase();
    }

    private boolean isFoaf(String urlName) {
        if (urlName == null) {
            return false;
        }
        return urlName.equals(URL_NAME_FOAF);
    }

    private boolean isWebID(String urlName) {
        if (urlName == null) {
            return false;
        }
        return urlName.equals(URL_NAME_WEBID);
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

    private void describeBiography(Biography biography, Individual person, OntModel m) {
        if (biography != null) {
            // FIXME: Which language is the biography written in? Can't assume
            // EN
            person.addProperty(FOAF.plan, biography.getContent());
        }
    }

    private void describeContactDetails(ContactDetails contactDetails, Individual person, OntModel m) {
        if (contactDetails == null) {
            return;
        }

        List<Email> emails = contactDetails.getEmail();
        if (emails != null) {
            for (Email email : emails) {
                if (email.isCurrent()) {

                    Individual mbox = m.createIndividual("mailto:" + email.getValue(), null);
                    person.addProperty(FOAF.mbox, mbox);
                }
            }
        }

        Address addr = contactDetails.getAddress();
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

    private void describePersonalDetails(PersonalDetails personalDetails, Individual person, OntModel m) {
        if (personalDetails == null) {
            return;
        }

        if (personalDetails.getCreditName() != null) {
            // User has provided full name
            String creditName = personalDetails.getCreditName().getContent();
            person.addProperty(FOAF.name, creditName);
            person.addLabel(creditName, null);
        } else if (personalDetails.getGivenNames() != null && personalDetails.getFamilyName() != null) {
            //@formatter:off
            // Naive fallback assuming givenNames ~= first name and familyName ~= lastName
            // See http://www.w3.org/International/questions/qa-personal-names for further
            // considerations -- we don't report this as foaf:name as we can't be sure of the ordering.
            //@formatter:on

            // NOTE: ORCID gui is westernized asking for "First name" and
            // "Last name" and assuming the above mapping
            String label = personalDetails.getGivenNames().getContent() + " " + personalDetails.getFamilyName().getContent();
            person.addLabel(label, null);
        }

        if (personalDetails.getGivenNames() != null) {
            person.addProperty(FOAF.givenName, personalDetails.getGivenNames().getContent());
        }
        if (personalDetails.getFamilyName() != null) {
            person.addProperty(FOAF.familyName, personalDetails.getFamilyName().getContent());
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
