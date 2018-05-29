package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.BaseTest;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdCommonName;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.GivenNames;
import org.orcid.test.TargetProxyHelper;

/**
 * @author Declan Newman (declan) Date: 13/03/2012
 */
public class OrcidJaxbCopyManagerTest extends BaseTest {
    
    private static final String CLIENT_1 = "0000-0000-0000-0000";

    private Unmarshaller unmarshaller;

    private OrcidMessage protectedOrcidMessage;
    private OrcidMessage publicOrcidMessage;

    @Mock
    private SourceManager mockSourceManager;
    
    @Resource
    private SourceManager sourceManager;
    
    @Resource
    private OrcidJaxbCopyManager orcidJaxbCopyManager;

    public OrcidJaxbCopyManagerTest() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
    }

    @Before
    public void init() throws JAXBException {
        protectedOrcidMessage = getOrcidMessage("/orcid-protected-full-message-latest.xml");
        publicOrcidMessage = getOrcidMessage("/orcid-full-message-no-visibility-latest.xml");
    
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orcidJaxbCopyManager, "sourceManager", mockSourceManager);        
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceClient(new ClientDetailsEntity(CLIENT_1));
        when(mockSourceManager.retrieveSourceEntity()).thenReturn(sourceEntity);
    }
    
    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(orcidJaxbCopyManager, "sourceManager", sourceManager);
    }

    @Test
    public void testUpdateOtherNamePreservingVisibility() throws Exception {

        OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();

        PersonalDetails existingOrcidPersonalDetails = protectedOrcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails();
        PersonalDetails updatedOrcidPersonalDetails = publicOrcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails();

        OtherNames existingOtherNames = existingOrcidPersonalDetails.getOtherNames();
        assertEquals(Visibility.LIMITED, existingOtherNames.getVisibility());
        assertTrue(existingOtherNames.getOtherName().size() == 2);
        assertEquals("Josiah S Carberry", existingOtherNames.getOtherName().get(0).getContent());
        assertEquals("Josiah Carberry", existingOtherNames.getOtherName().get(1).getContent());

        // check content and visibility update updates the content
        OtherNames updatedOtherNames = new OtherNames();
        updatedOtherNames.getOtherName().clear();
        updatedOtherNames.addOtherName("Another 1",null);
        updatedOtherNames.addOtherName("Another 2",null);
        updatedOtherNames.setVisibility(Visibility.PRIVATE);
        updatedOrcidPersonalDetails.setOtherNames(updatedOtherNames);

        Address existingContactDetailsAddress = existingOrcidBioProtected.getContactDetails().getAddress();
        assertEquals(Iso3166Country.US, existingContactDetailsAddress.getCountry().getValue());
        existingContactDetailsAddress.getCountry().setVisibility(Visibility.LIMITED);

        Address nullVisibilityContactAddress = new Address();
        nullVisibilityContactAddress.setCountry(new Country(Iso3166Country.BM));
        nullVisibilityContactAddress.getCountry().setVisibility(null);
        updatedOrcidBioPublic.getContactDetails().setAddress(nullVisibilityContactAddress);

        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        existingOtherNames = existingOrcidPersonalDetails.getOtherNames();
        assertTrue(existingOtherNames.getOtherName().size() == 2);
        assertEquals("Another 1", existingOtherNames.getOtherName().get(0).getContent());
        assertEquals("Another 2", existingOtherNames.getOtherName().get(1).getContent());
        assertEquals(Visibility.PRIVATE, existingOtherNames.getVisibility());

        // check content and visibility update of null
        updatedOtherNames = new OtherNames();
        updatedOtherNames.getOtherName().clear();
        updatedOtherNames.addOtherName("Yet Another 1",null);
        updatedOtherNames.addOtherName("Yet Another 2",null);
        updatedOtherNames.setVisibility(null);
        updatedOrcidPersonalDetails.setOtherNames(updatedOtherNames);

        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);

        assertEquals(2, existingOrcidBioProtected.getPersonalDetails().getOtherNames().getOtherName().size());
        assertEquals("Yet Another 1", existingOrcidBioProtected.getPersonalDetails().getOtherNames().getOtherName().get(0).getContent());
        assertEquals("Yet Another 2", existingOrcidBioProtected.getPersonalDetails().getOtherNames().getOtherName().get(1).getContent());
        assertEquals(Visibility.PRIVATE, existingOrcidBioProtected.getPersonalDetails().getOtherNames().getVisibility());
        existingContactDetailsAddress = existingOrcidBioProtected.getContactDetails().getAddress();
        assertEquals(Visibility.LIMITED, existingContactDetailsAddress.getCountry().getVisibility());
        assertEquals(Iso3166Country.BM, existingContactDetailsAddress.getCountry().getValue());

    }

    @Test
    public void testUpdateCreditNamePreservingVisibility() throws Exception {

        OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();

        PersonalDetails existingOrcidPersonalDetails = protectedOrcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails();
        PersonalDetails updatedOrcidPersonalDetails = publicOrcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails();

        // set default of credit to limited on existing and null on updated
        // -should remain as limited and not 'downgrade'
        assertEquals(existingOrcidPersonalDetails.getCreditName().getContent(), "J. S. Carberry");
        assertEquals(Visibility.LIMITED, existingOrcidPersonalDetails.getCreditName().getVisibility());

        updatedOrcidPersonalDetails.setCreditName(new CreditName("Don"));
        updatedOrcidPersonalDetails.getCreditName().setVisibility(Visibility.PRIVATE);

        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        assertEquals(existingOrcidPersonalDetails.getCreditName().getContent(), "Don");
        assertEquals(Visibility.PRIVATE, existingOrcidPersonalDetails.getCreditName().getVisibility());

        updatedOrcidPersonalDetails.setCreditName(new CreditName("Jimmy"));
        updatedOrcidPersonalDetails.getCreditName().setVisibility(null);

        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        assertEquals("Don", existingOrcidBioProtected.getPersonalDetails().getCreditName().getContent());
        assertEquals(Visibility.PRIVATE, existingOrcidPersonalDetails.getCreditName().getVisibility());

    }

    @Test
    public void testUpdatedBiographyToExistingPreservingVisibility() throws Exception {

        OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();
        assertEquals(Visibility.LIMITED, existingOrcidBioProtected.getBiography().getVisibility());
        String existingContent = "Josiah Stinkney Carberry is a fictional professor.";
        assertEquals(existingContent, existingOrcidBioProtected.getBiography().getContent().trim());
        assertNull(updatedOrcidBioPublic.getBiography().getVisibility());
        updatedOrcidBioPublic.setBiography(new Biography("A new bio"));
        updatedOrcidBioPublic.getBiography().setVisibility(Visibility.PRIVATE);
        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);

        // check that changes have propogated
        assertEquals(Visibility.PRIVATE, existingOrcidBioProtected.getBiography().getVisibility());
        assertEquals("A new bio", existingOrcidBioProtected.getBiography().getContent());

        // reset the variable and attempt to override the content and visibility
        updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();
        updatedOrcidBioPublic.setBiography(new Biography("A new and impoved bio"));

        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        // check that the old values have been retained
        assertEquals(Visibility.PRIVATE, existingOrcidBioProtected.getBiography().getVisibility());
        assertEquals("A new bio", existingOrcidBioProtected.getBiography().getContent());

    }

    @Test
    public void testUpdatedResearcherUrlToExistingPreservingVisibility() throws Exception {
        OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();

        ResearcherUrls existingResearcherUrls = existingOrcidBioProtected.getResearcherUrls();
        assertEquals(Visibility.PUBLIC, existingResearcherUrls.getVisibility());
        assertTrue(existingResearcherUrls.getResearcherUrl().size() == 3);
        assertEquals("http://library.brown.edu/about/hay/carberry.php", existingResearcherUrls.getResearcherUrl().get(0).getUrl().getValue());
        assertEquals("http://en.wikipedia.org/wiki/Josiah_S._Carberry", existingResearcherUrls.getResearcherUrl().get(1).getUrl().getValue());
        assertEquals("http://www.brown.edu/Administration/News_Bureau/Databases/Encyclopedia/search.php?serial=C0070", existingResearcherUrls.getResearcherUrl().get(2)
                .getUrl().getValue());

        ResearcherUrls updatedResearcherUrls = new ResearcherUrls();
        ResearcherUrl onlyUrl = new ResearcherUrl(new Url("http://library.brown.edu/about/hay/carberry.html"),null);
        updatedResearcherUrls.getResearcherUrl().add(onlyUrl);
        updatedResearcherUrls.setVisibility(Visibility.LIMITED);
        updatedOrcidBioPublic.setResearcherUrls(updatedResearcherUrls);

        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        assertTrue(existingOrcidBioProtected.getResearcherUrls().getResearcherUrl().size() == 1);
        assertEquals("http://library.brown.edu/about/hay/carberry.html", existingOrcidBioProtected.getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(existingOrcidBioProtected.getResearcherUrls(), updatedOrcidBioPublic.getResearcherUrls());
        assertEquals(Visibility.LIMITED, existingOrcidBioProtected.getResearcherUrls().getVisibility());

        updatedResearcherUrls = new ResearcherUrls();
        onlyUrl = new ResearcherUrl(new Url("http://library.brown.edu/about/hay/carberry.jsp"),null);
        updatedResearcherUrls.getResearcherUrl().add(onlyUrl);
        updatedResearcherUrls.setVisibility(null);
        updatedOrcidBioPublic.setResearcherUrls(updatedResearcherUrls);

        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        assertTrue(existingOrcidBioProtected.getResearcherUrls().getResearcherUrl().size() == 1);
        assertEquals("http://library.brown.edu/about/hay/carberry.jsp", existingOrcidBioProtected.getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(existingOrcidBioProtected.getResearcherUrls(), updatedOrcidBioPublic.getResearcherUrls());
        assertEquals(Visibility.LIMITED, existingOrcidBioProtected.getResearcherUrls().getVisibility());

    }

    @Test
    public void testUpdatedAffilationsToExistingPreservingVisibility() throws Exception {

        OrcidProfile existingOrcidProfile = protectedOrcidMessage.getOrcidProfile();

        // create a copy of the profile data for doing a merge
        OrcidProfile updatedOrcidProfile = getOrcidMessage("/orcid-public-full-message-latest.xml").getOrcidProfile();

        Affiliations existingAffiliations = existingOrcidProfile.getOrcidActivities().getAffiliations();
        List<Affiliation> existingAffilationsList = existingAffiliations.getAffiliation();
        Affiliations updatedAffiliations = updatedOrcidProfile.getOrcidActivities().getAffiliations();
        List<Affiliation> updatedAffilationsList = updatedAffiliations.getAffiliation();

        assertEquals("New College", existingAffilationsList.get(0).getOrganization().getName());
        assertEquals("Brown University", existingAffilationsList.get(1).getOrganization().getName());
        assertEquals(Visibility.PUBLIC, existingAffilationsList.get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, existingAffilationsList.get(1).getVisibility());
        assertEquals(4, existingAffilationsList.size());
        assertEquals(4, updatedAffilationsList.size());

        // to test:
        // updating affiliations retains visibility when null - changes content
        updatedAffilationsList.get(0).getOrganization().setName("new affiliation name");
        updatedAffilationsList.get(0).setVisibility(null);
        orcidJaxbCopyManager.copyAffiliationsToExistingPreservingVisibility(existingAffiliations, updatedAffiliations);
        assertEquals("new affiliation name", existingAffilationsList.get(0).getOrganization().getName());
        assertEquals(Visibility.PUBLIC, existingAffilationsList.get(0).getVisibility());

        // updating affiliations changes visibility when populated - changes
        // content
        updatedAffilationsList.get(0).getOrganization().setName("a seperate affiliation name");
        updatedAffilationsList.get(0).setVisibility(Visibility.PRIVATE);
        orcidJaxbCopyManager.copyAffiliationsToExistingPreservingVisibility(existingAffiliations, updatedAffiliations);
        assertEquals("a seperate affiliation name", existingAffilationsList.get(0).getOrganization().getName());
        assertEquals(Visibility.PRIVATE, existingAffilationsList.get(0).getVisibility());

        // adding new affiliations with a null visibility adds an extra element
        // with the def
        Affiliation extraAffiliation = new Affiliation();
        Organization organization = new Organization();
        extraAffiliation.setOrganization(organization);
        organization.setName("extra affiliation");
        updatedAffilationsList.add(extraAffiliation);

        orcidJaxbCopyManager.copyAffiliationsToExistingPreservingVisibility(existingAffiliations, updatedAffiliations);
        assertEquals(5, existingAffilationsList.size());

        assertEquals("a seperate affiliation name", existingAffilationsList.get(0).getOrganization().getName());
        assertEquals("Brown University", existingAffilationsList.get(1).getOrganization().getName());
        assertEquals("extra affiliation", existingAffilationsList.get(4).getOrganization().getName());

        assertEquals(Visibility.PRIVATE, existingAffilationsList.get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, existingAffilationsList.get(1).getVisibility());
        assertEquals(Visibility.PUBLIC, existingAffilationsList.get(4).getVisibility());
    }

    @Test
    public void testUpdatedContactDetailsToExistingPreservingVisibility() throws Exception {
    	when(mockSourceManager.retrieveSourceOrcid()).thenReturn("APP-0000000000000000");
        
    	SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceClient(new ClientDetailsEntity("APP-0000000000000000"));
        when(mockSourceManager.retrieveSourceEntity()).thenReturn(sourceEntity);
        
    	OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();

        ContactDetails existingContactDetails = existingOrcidBioProtected.getContactDetails();
        ContactDetails updatedContactDetails = updatedOrcidBioPublic.getContactDetails();

        assertEquals("josiah_carberry@brown.edu", existingContactDetails.retrievePrimaryEmail().getValue());
        assertEquals(Visibility.LIMITED, existingContactDetails.retrievePrimaryEmail().getVisibility());
        String[] alternativeEmails = new String[] { "josiah_carberry_1@brown.edu" };
        for (String alternativeEmail : alternativeEmails) {
            Email email = existingContactDetails.getEmailByString(alternativeEmail);
            assertNotNull(email);
            assertEquals(Visibility.LIMITED, email.getVisibility());
        }
        assertEquals(2, existingContactDetails.getEmail().size());

        Address existingAddress = existingContactDetails.getAddress();
        assertTrue(Iso3166Country.US.equals(existingAddress.getCountry().getValue()) && existingAddress.getCountry().getVisibility() == null);

        Address updatedAddress = new Address();
        Country country = new Country(Iso3166Country.GB);
        country.setVisibility(Visibility.LIMITED);
        updatedAddress.setCountry(country);
        updatedOrcidBioPublic.getContactDetails().setAddress(updatedAddress);

        List<Email> updatedEmailList = new ArrayList<>();
        Email updatedMainEmail = new Email("jimmyb@semantico.com");
        updatedMainEmail.setSourceClientId("APP-0000000000000000");
        updatedMainEmail.setVisibility(Visibility.PUBLIC);
        updatedMainEmail.setPrimary(true);
        updatedEmailList.add(updatedMainEmail);
        String[] updatedAlternativeEmails = new String[] { "jimmyb1@semantico.com"};
        for (String alternativeEmail : updatedAlternativeEmails) {
            Email email = new Email(alternativeEmail);
            email.setPrimary(false);
            email.setVerified(false);
            email.setVisibility(Visibility.PRIVATE);
            email.setSourceClientId("APP-0000000000000000");
            updatedEmailList.add(email);
        }
        updatedContactDetails.getEmail().clear();
        updatedContactDetails.getEmail().addAll(updatedEmailList);

        updatedContactDetails.setAddress(updatedAddress);

        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        existingContactDetails = existingOrcidBioProtected.getContactDetails();
        assertEquals("josiah_carberry@brown.edu", existingContactDetails.retrievePrimaryEmail().getValue());
        assertEquals(Visibility.LIMITED, existingContactDetails.retrievePrimaryEmail().getVisibility());

        //Emails remain unchanged
        assertEquals(2, existingContactDetails.getEmail().size());
        assertEquals(Iso3166Country.GB, existingContactDetails.getAddress().getCountry().getValue());
        assertEquals(OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility(), existingContactDetails.getAddress().getCountry().getVisibility());

        updatedContactDetails = new ContactDetails();
        updatedOrcidBioPublic.setContactDetails(updatedContactDetails);
        updatedAddress = new Address();
        country = new Country(Iso3166Country.AU);
        country.setVisibility(null);
        updatedAddress.setCountry(country);
        updatedContactDetails.setAddress(updatedAddress);

        updatedEmailList = new ArrayList<>();
        updatedMainEmail = new Email("jimmyb1@semantico.com");
        updatedMainEmail.setVisibility(Visibility.PUBLIC);
        updatedEmailList.add(updatedMainEmail);

        String[] moreAlternativeEmails = new String[] {"jimmyb3@semantico.com"};
        for (String alternativeEmail : moreAlternativeEmails) {
            Email email = new Email(alternativeEmail);
            email.setPrimary(false);
            email.setVisibility(Visibility.PRIVATE);
            email.setSourceClientId("APP-0000000000000000");
            updatedEmailList.add(email);
        }
        updatedContactDetails.getEmail().clear();
        updatedContactDetails.getEmail().addAll(updatedEmailList);

        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);

        existingContactDetails = existingOrcidBioProtected.getContactDetails();

      //Emails remain unchanged
        assertEquals(2, existingContactDetails.getEmail().size());

        assertEquals(Iso3166Country.AU, existingContactDetails.getAddress().getCountry().getValue());
    }

    @Test
    public void testUpdatedKeywordsPreservingVisibility() throws Exception {
        OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();

        Keywords existingKeywords = existingOrcidBioProtected.getKeywords();
        Keywords updatedKeywords = updatedOrcidBioPublic.getKeywords();

        // set keywords to limited on existing and null on updated
        // -should remain as limited and not 'downgrade'
        // -should remain as limited and not 'downgrade'

        assertEquals("Bilocation", existingKeywords.getKeyword().get(0).getContent());
        assertEquals("Pavement Studies", updatedKeywords.getKeyword().get(0).getContent());
        assertEquals(Visibility.PUBLIC, existingKeywords.getVisibility());
        assertNull(updatedKeywords.getVisibility());

        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        existingKeywords = existingOrcidBioProtected.getKeywords();
        assertEquals("Pavement Studies", existingKeywords.getKeyword().get(0).getContent());
        assertEquals(Visibility.PUBLIC, existingKeywords.getVisibility());

        assertEquals("Pavement Studies", existingKeywords.getKeyword().get(0).getContent());
        updatedKeywords.getKeyword().get(0).setContent("Toast Studies");
        updatedKeywords.setVisibility(Visibility.LIMITED);
        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        assertEquals("Toast Studies", existingKeywords.getKeyword().get(0).getContent());
        assertEquals(Visibility.LIMITED, existingKeywords.getVisibility());

    }

    @Test
    public void testCopyUpdatedExternalIdentifiersToExistingPreservingVisibility() throws Exception {
        OrcidBio protectedOrcidBio = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio publicOrcidBio = publicOrcidMessage.getOrcidProfile().getOrcidBio();

        assertNull(publicOrcidBio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.PUBLIC, protectedOrcidBio.getExternalIdentifiers().getVisibility());

        // first time save upgrades null to public
        orcidJaxbCopyManager.copyUpdatedExternalIdentifiersToExistingPreservingVisibility(publicOrcidBio, protectedOrcidBio);
        assertEquals(Visibility.PUBLIC, publicOrcidBio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.PUBLIC, protectedOrcidBio.getExternalIdentifiers().getVisibility());

        // now changing the updated one propogates change
        protectedOrcidBio.getExternalIdentifiers().setVisibility(Visibility.LIMITED);
        orcidJaxbCopyManager.copyUpdatedExternalIdentifiersToExistingPreservingVisibility(publicOrcidBio, protectedOrcidBio);
        assertEquals(Visibility.LIMITED, publicOrcidBio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.LIMITED, protectedOrcidBio.getExternalIdentifiers().getVisibility());

        // when existing is null - set to the value if existing
        publicOrcidBio.getExternalIdentifiers().setVisibility(null);
        protectedOrcidBio.getExternalIdentifiers().setVisibility(Visibility.LIMITED);
        orcidJaxbCopyManager.copyUpdatedExternalIdentifiersToExistingPreservingVisibility(publicOrcidBio, protectedOrcidBio);
        assertEquals(Visibility.LIMITED, publicOrcidBio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.LIMITED, protectedOrcidBio.getExternalIdentifiers().getVisibility());

        // when existing is null - defaults to public and protected update
        // ignored?
        protectedOrcidBio.getExternalIdentifiers().setVisibility(Visibility.PRIVATE);
        orcidJaxbCopyManager.copyUpdatedExternalIdentifiersToExistingPreservingVisibility(publicOrcidBio, protectedOrcidBio);
        assertEquals(Visibility.PRIVATE, publicOrcidBio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.PRIVATE, protectedOrcidBio.getExternalIdentifiers().getVisibility());

    }

    @Test
    public void testCopyUpdatedWorksToExistingWithVisibility() throws Exception {
        OrcidWorks worksToUpdate = publicOrcidMessage.getOrcidProfile().retrieveOrcidWorks();
        OrcidWorks existingWorks = protectedOrcidMessage.getOrcidProfile().retrieveOrcidWorks();
        checkWorksVisibility(null, worksToUpdate.getOrcidWork());

        // update is private, so the update should persist and alter the
        // existing as well

        assertEquals("Work title 1", existingWorks.getOrcidWork().get(0).getWorkTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC, existingWorks.getOrcidWork().get(0).getVisibility());
        assertEquals("Work title 2", existingWorks.getOrcidWork().get(1).getWorkTitle().getTitle().getContent());
        assertNull(existingWorks.getOrcidWork().get(1).getVisibility());
        assertEquals("Work Title 3", existingWorks.getOrcidWork().get(2).getWorkTitle().getTitle().getContent());
        assertNull(existingWorks.getOrcidWork().get(2).getVisibility());

        worksToUpdate.getOrcidWork().get(0).getWorkTitle().getTitle().setContent("updated-work-title-1");
        worksToUpdate.getOrcidWork().get(0).setVisibility(Visibility.PRIVATE);
        worksToUpdate.getOrcidWork().get(1).getWorkTitle().getTitle().setContent("updated-work-title-2");
        worksToUpdate.getOrcidWork().get(2).getWorkTitle().getTitle().setContent("updated-work-title-3");

        orcidJaxbCopyManager.copyUpdatedWorksPreservingVisbility(existingWorks, worksToUpdate);
        assertEquals("updated-work-title-1", existingWorks.getOrcidWork().get(0).getWorkTitle().getTitle().getContent());
        assertEquals("updated-work-title-2", existingWorks.getOrcidWork().get(1).getWorkTitle().getTitle().getContent());
        assertEquals("updated-work-title-3", existingWorks.getOrcidWork().get(2).getWorkTitle().getTitle().getContent());
        // check the update was copied across
        assertEquals(Visibility.PRIVATE, existingWorks.getOrcidWork().get(0).getVisibility());
        // check that as null was supplied as an update, the works default was
        // used
        assertEquals(Visibility.PUBLIC, existingWorks.getOrcidWork().get(1).getVisibility());
    }

    private void checkWorksVisibility(Visibility expectedVisibility, Collection<OrcidWork> works) {
        for (OrcidWork work : works) {
            assertEquals("Wrong visibility for work : " + work.getWorkTitle().getTitle().getContent(), expectedVisibility, work.getVisibility());
        }
    }

    private OrcidMessage getOrcidMessage(String s) throws JAXBException {
        InputStream inputStream = OrcidJaxbCopyManagerTest.class.getResourceAsStream(s);
        OrcidMessage orcidMessage = (OrcidMessage) unmarshaller.unmarshal(inputStream);
        // Put codes are needed for these tests
        int putCode = 1;
        for (Affiliation affiliation : orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation()) {
            affiliation.setPutCode(String.valueOf(putCode));
            putCode++;
        }
        return orcidMessage;

    }
    
    @Test
    public void testCopyToAPrivateBio() {
        final String privateSufix = "private_"; 
        final String publicSufix = "public_";
        OrcidBio privateBio = getBio(privateSufix, Visibility.PRIVATE, 3);
        OrcidBio publicBio = getBio(publicSufix, Visibility.PUBLIC, 3);
        
        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(privateBio, publicBio);
        assertEquals(privateSufix + "My Biography", privateBio.getBiography().getContent());
        assertEquals(Visibility.PRIVATE, privateBio.getBiography().getVisibility());
        assertEquals(Iso3166Country.US, privateBio.getContactDetails().getAddress().getCountry().getValue());
        assertEquals(Visibility.PRIVATE, privateBio.getContactDetails().getAddress().getCountry().getVisibility());
        //Remains same as client cannot add/update emails.
        assertEquals(3, privateBio.getContactDetails().getEmail().size());
        assertEquals("private_Email0", privateBio.getContactDetails().getEmail().get(0).getValue());
        assertEquals("private_Email1", privateBio.getContactDetails().getEmail().get(1).getValue());
        assertEquals("private_Email2", privateBio.getContactDetails().getEmail().get(2).getValue());
        assertEquals(3, privateBio.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("public_CommonName0", privateBio.getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals("public_CommonName1", privateBio.getExternalIdentifiers().getExternalIdentifier().get(1).getExternalIdCommonName().getContent());
        assertEquals("public_CommonName2", privateBio.getExternalIdentifiers().getExternalIdentifier().get(2).getExternalIdCommonName().getContent());
        assertTrue(privateBio.getExternalIdentifiers().getExternalIdentifier().containsAll(publicBio.getExternalIdentifiers().getExternalIdentifier()));
        assertEquals(Visibility.PUBLIC, privateBio.getExternalIdentifiers().getVisibility());
        assertEquals(3, privateBio.getKeywords().getKeyword().size());
        assertEquals("public_Keyword0", privateBio.getKeywords().getKeyword().get(0).getContent());
        assertEquals("public_Keyword1", privateBio.getKeywords().getKeyword().get(1).getContent());
        assertEquals("public_Keyword2", privateBio.getKeywords().getKeyword().get(2).getContent());
        assertTrue(privateBio.getKeywords().getKeyword().containsAll(publicBio.getKeywords().getKeyword()));
        assertEquals(Visibility.PUBLIC, privateBio.getKeywords().getVisibility());
        assertEquals(3, privateBio.getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.rurl.com/public_/0", privateBio.getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals("http://www.rurl.com/public_/1", privateBio.getResearcherUrls().getResearcherUrl().get(1).getUrl().getValue());
        assertEquals("http://www.rurl.com/public_/2", privateBio.getResearcherUrls().getResearcherUrl().get(2).getUrl().getValue());
        assertTrue(privateBio.getResearcherUrls().getResearcherUrl().containsAll(publicBio.getResearcherUrls().getResearcherUrl()));
        assertEquals(Visibility.PUBLIC, privateBio.getResearcherUrls().getVisibility());
        assertEquals(privateSufix + "Credit name", privateBio.getPersonalDetails().getCreditName().getContent());
        assertEquals(Visibility.PRIVATE, privateBio.getPersonalDetails().getCreditName().getVisibility());
        assertEquals(publicSufix + "Family", privateBio.getPersonalDetails().getFamilyName().getContent());
        assertEquals(publicSufix + "Given", privateBio.getPersonalDetails().getGivenNames().getContent());
        assertEquals(3, privateBio.getPersonalDetails().getOtherNames().getOtherName().size());
        assertEquals("public_Other0", privateBio.getPersonalDetails().getOtherNames().getOtherName().get(0).getContent());
        assertEquals("public_Other1", privateBio.getPersonalDetails().getOtherNames().getOtherName().get(1).getContent());
        assertEquals("public_Other2", privateBio.getPersonalDetails().getOtherNames().getOtherName().get(2).getContent());
        assertTrue(privateBio.getPersonalDetails().getOtherNames().getOtherName().containsAll(publicBio.getPersonalDetails().getOtherNames().getOtherName()));        
    }

    @Test
    public void testbiographyCopyDontFailOnEmpyVisibility() {
        OrcidBio existing = getBio("bio1 ", Visibility.PUBLIC, 1);
        existing.getBiography().setContent("Old biography");
        existing.getBiography().setVisibility(null);
        OrcidBio updated = getBio("bio1 ", Visibility.LIMITED, 1);
        updated.getBiography().setContent("New biography");
        updated.getBiography().setVisibility(null);
        
        assertEquals("Old biography", existing.getBiography().getContent());
        orcidJaxbCopyManager.copyUpdatedShortDescriptionToExistingPreservingVisibility(existing, updated);
        assertEquals(OrcidVisibilityDefaults.SHORT_DESCRIPTION_DEFAULT.getVisibility(), updated.getBiography().getVisibility());
        //It will not be updated if the visibility is null or PRIVATE
        assertEquals("Old biography", existing.getBiography().getContent());
        
        
        //Do it again but now set a visibility to the existing bio
        existing.getBiography().setContent("Old biography");
        existing.getBiography().setVisibility(Visibility.LIMITED);
        updated.getBiography().setContent("New biography");
        updated.getBiography().setVisibility(null);
        
        orcidJaxbCopyManager.copyUpdatedShortDescriptionToExistingPreservingVisibility(existing, updated);
        //The visibility will be the same
        assertEquals(Visibility.LIMITED, updated.getBiography().getVisibility());
        //And the content will change
        assertEquals("New biography", existing.getBiography().getContent());
    }
    
    private OrcidBio getBio(String sufix, Visibility visibility, int max) {
        OrcidBio orcidBio = new OrcidBio();
        Biography bio = new Biography(sufix + "My Biography", visibility);
        orcidBio.setBiography(bio);
        ContactDetails contactDetails = new ContactDetails();
        Address address = new Address();
        Country country = new Country(visibility.equals(Visibility.PRIVATE) ? Iso3166Country.US : Iso3166Country.CR);
        country.setVisibility(visibility);
        address.setCountry(country);
        contactDetails.setAddress(address);
        List<Email> emails = new ArrayList<Email>();
        for(int i = 0; i < max; i++) {
            Email email = new Email();
            email.setValue(sufix + "Email" + i);
            if(i == 0) {
                email.setPrimary(true);
            }
            email.setVisibility(visibility);
            emails.add(email);
        }
        
        contactDetails.setEmail(emails);
        orcidBio.setContactDetails(contactDetails);
        
        ExternalIdentifiers extIds = new ExternalIdentifiers();
        extIds.setVisibility(visibility);
        
        for(int i = 0; i < max; i++) {
            ExternalIdentifier extId = new ExternalIdentifier();             
            extId.setExternalIdCommonName(new ExternalIdCommonName(sufix + "CommonName" + i));
            extId.setExternalIdReference(new ExternalIdReference(sufix + "Reference" + i));
            extIds.getExternalIdentifier().add(extId);
        }        
        orcidBio.setExternalIdentifiers(extIds);
        Keywords keywords = new Keywords();
        keywords.setVisibility(visibility);
        
        for(int i = 0; i < max; i++) {
            Keyword k = new Keyword();
            k.setContent(sufix + "Keyword" + i);
            keywords.getKeyword().add(k);
        }
        orcidBio.setKeywords(keywords);
        
        PersonalDetails personalDetails = new PersonalDetails();
        CreditName creditName = new CreditName(sufix + "Credit name");
        creditName.setVisibility(visibility);
        personalDetails.setCreditName(creditName);
        FamilyName familyName = new FamilyName(sufix + "Family");
        personalDetails.setFamilyName(familyName);
        GivenNames givenNames = new GivenNames();
        givenNames.setContent(sufix + "Given");
        personalDetails.setGivenNames(givenNames);
        OtherNames other = new OtherNames();
        other.setVisibility(visibility);
        for(int i = 0; i < max; i++) {
            other.addOtherName(sufix + "Other" + i,null);
        }        
        personalDetails.setOtherNames(other);        
        orcidBio.setPersonalDetails(personalDetails);
        
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setVisibility(visibility);
        for(int i = 0; i < max; i++) {
            ResearcherUrl rUrl = new ResearcherUrl();
            rUrl.setUrl(new Url("http://www.rurl.com/" + sufix + "/" + i));
            rUrl.setUrlName(new UrlName(sufix + "Url" + i));
            researcherUrls.getResearcherUrl().add(rUrl);
        }
        orcidBio.setResearcherUrls(researcherUrls);
        return orcidBio;
    }
    
}