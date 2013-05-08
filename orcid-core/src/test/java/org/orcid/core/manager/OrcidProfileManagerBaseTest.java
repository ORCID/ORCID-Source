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
package org.orcid.core.manager;

import java.util.GregorianCalendar;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Before;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.impl.OrcidProfileManagerImpl;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.ContributorAttributes;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.EndDate;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.StartDate;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkSource;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.SubjectEntity;

/**
 * @author Will Simpson
 */
public class OrcidProfileManagerBaseTest extends BaseTest {

    protected static final String APPLICATION_ORCID = "2222-2222-2222-2228";

    protected static final String DELEGATE_ORCID = "1111-1111-1111-1115";

    protected static final String TEST_ORCID = "4444-4444-4444-4447";

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Resource
    protected ProfileDao profileDao;

    @Resource
    protected ClientDetailsDao clientDetailsDao;

    @Resource
    protected OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    @Resource
    protected EncryptionManager encryptionManager;

    @Resource
    protected GenericDao<SubjectEntity, String> subjectDao;

    @Mock
    protected OrcidIndexManager orcidIndexManager;

    /**
     * The classes loaded from the app context are in fact proxies to the
     * OrcidProfileManagerImpl class, required for transactionality. However we
     * can only return the proxied interface from the app context
     * <p/>
     * We need to mock the call to the OrcidIndexManager whenever a persist
     * method is called, but this dependency is only accessible on the impl (as
     * it should be).
     * <p/>
     * To preserve the transactionality AND allow us to mock a dependency that
     * exists on the Impl we use the getTargetObject() method in the superclass
     * 
     * @throws Exception
     */
    @Before
    public void initMocks() throws Exception {

        OrcidProfileManagerImpl orcidProfileManagerImpl = getTargetObject(orcidProfileManager, OrcidProfileManagerImpl.class);
        orcidProfileManagerImpl.setOrcidIndexManager(orcidIndexManager);
    }

    protected OrcidProfile createFullOrcidProfile() {
        OrcidProfile profile2 = new OrcidProfile();
        profile2.setPassword("password");
        profile2.setVerificationCode("1234");
        profile2.setOrcid(TEST_ORCID);
        OrcidBio bio = new OrcidBio();
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new Email("will@orcid.org"));
        bio.setContactDetails(contactDetails);
        profile2.setOrcidBio(bio);
        PersonalDetails personalDetails = new PersonalDetails();

        personalDetails.setGivenNames(new GivenNames("William"));
        personalDetails.setFamilyName(new FamilyName("Simpson"));
        personalDetails.setCreditName(new CreditName("W. J. R. Simpson"));
        bio.setPersonalDetails(personalDetails);

        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.getResearcherUrl().add(new ResearcherUrl(new Url("http://www.wjrs.co.uk")));
        bio.setResearcherUrls(researcherUrls);
        Keywords keywords = new Keywords();
        bio.setKeywords(keywords);
        keywords.getKeyword().add(new Keyword("Java"));
        bio.setBiography(new Biography("Will is a software developer at Semantico"));
        return profile2;
    }

    protected OrcidProfile createBasicProfile() {
        OrcidProfile profile = new OrcidProfile();
        profile.setPassword("password");
        profile.setVerificationCode("1234");
        profile.setSecurityQuestionAnswer("random answer");

        profile.setOrcid(TEST_ORCID);
        OrcidBio bio = new OrcidBio();
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new Email("will@semantico.com"));
        bio.setContactDetails(contactDetails);
        profile.setOrcidBio(bio);
        PersonalDetails personalDetails = new PersonalDetails();
        bio.setPersonalDetails(personalDetails);
        personalDetails.setGivenNames(new GivenNames("Will"));
        ResearcherUrls researcherUrls = new ResearcherUrls();
        bio.setResearcherUrls(researcherUrls);
        researcherUrls.getResearcherUrl().add(new ResearcherUrl(new Url("http://www.wjrs.co.uk")));
        OrcidWorks orcidWorks = new OrcidWorks();
        profile.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork = createWork1();

        // TODO JB - needs test
        // orcidWork.setAddedToProfileDate(new
        // AddedToProfileDate(DateUtils.convertToXMLGregorianCalendar("2010-03-04")));

        // orcidWork.setAddedToProfileDate(new
        // AddedToProfileDate(DateUtils.convertToXMLGregorianCalendar("2010-03-04")));

        orcidWorks.getOrcidWork().add(orcidWork);
        OrcidInternal orcidInternal = new OrcidInternal();
        profile.setOrcidInternal(orcidInternal);

        SecurityDetails securityDetails = new SecurityDetails();
        securityDetails.setSecurityQuestionId(new SecurityQuestionId(3));
        orcidInternal.setSecurityDetails(securityDetails);

        Preferences preferences = new Preferences();
        orcidInternal.setPreferences(preferences);

        return profile;
    }

    protected OrcidWork createWork1() {
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Test Title"));
        workTitle.setSubtitle(new Subtitle(""));
        return createWork1(workTitle);
    }

    protected OrcidWork createWork1(WorkTitle workTitle) {
        return createWork(workTitle, createWork1Identifiers(), createWork1Contributors());
    }

    protected OrcidWork createWork2(WorkTitle workTitle) {
        return createWork(workTitle, createWork2Identifiers(), null);
    }

    protected OrcidWork createWork3(WorkTitle workTitle) {
        return createWork(workTitle, createWork3Identifiers(), null);
    }

    protected WorkExternalIdentifiers createWork1Identifiers() {
        WorkExternalIdentifiers work1ExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier work1ExternalIdentifier1 = new WorkExternalIdentifier();
        work1ExternalIdentifier1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work1ExternalIdentifier1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work1-doi1"));
        WorkExternalIdentifier work1ExternalIdentifier2 = new WorkExternalIdentifier();
        work1ExternalIdentifier2.setWorkExternalIdentifierType(WorkExternalIdentifierType.PMID);
        work1ExternalIdentifier2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work1-pmid"));
        work1ExternalIdentifiers.getWorkExternalIdentifier().add(work1ExternalIdentifier1);
        work1ExternalIdentifiers.getWorkExternalIdentifier().add(work1ExternalIdentifier2);
        return work1ExternalIdentifiers;
    }

    protected WorkExternalIdentifiers createWork2Identifiers() {
        WorkExternalIdentifiers work2ExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier work2ExternalIdentifier1 = new WorkExternalIdentifier();
        work2ExternalIdentifier1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work2ExternalIdentifier1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work2-doi1"));
        WorkExternalIdentifier work2ExternalIdentifier2 = new WorkExternalIdentifier();
        work2ExternalIdentifier2.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work2ExternalIdentifier2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work2-doi2"));
        work2ExternalIdentifiers.getWorkExternalIdentifier().add(work2ExternalIdentifier1);
        work2ExternalIdentifiers.getWorkExternalIdentifier().add(work2ExternalIdentifier2);
        return work2ExternalIdentifiers;
    }

    protected WorkExternalIdentifiers createWork3Identifiers() {
        WorkExternalIdentifiers work3ExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier work3ExternalIdentifier1 = new WorkExternalIdentifier();
        work3ExternalIdentifier1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work3ExternalIdentifier1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work3-doi1"));
        WorkExternalIdentifier work3ExternalIdentifier2 = new WorkExternalIdentifier();
        work3ExternalIdentifier2.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work3ExternalIdentifier2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work3-doi2"));
        work3ExternalIdentifiers.getWorkExternalIdentifier().add(work3ExternalIdentifier1);
        work3ExternalIdentifiers.getWorkExternalIdentifier().add(work3ExternalIdentifier2);
        return work3ExternalIdentifiers;
    }

    private WorkContributors createWork1Contributors() {
        WorkContributors workContributors = new WorkContributors();
        Contributor workContributor1 = new Contributor();
        workContributors.getContributor().add(workContributor1);
        workContributor1.setCreditName(new CreditName("Will Simpson"));
        ContributorAttributes contributorAttributes1 = new ContributorAttributes();
        workContributor1.setContributorAttributes(contributorAttributes1);
        contributorAttributes1.setContributorRole(ContributorRole.AUTHOR);
        contributorAttributes1.setContributorSequence(SequenceType.FIRST);
        Contributor workContributor2 = new Contributor();
        workContributors.getContributor().add(workContributor2);
        workContributor2.setCreditName(new CreditName("Josiah Wedgewood"));
        ContributorAttributes contributorAttributes2 = new ContributorAttributes();
        workContributor2.setContributorAttributes(contributorAttributes2);
        contributorAttributes2.setContributorRole(ContributorRole.AUTHOR);
        contributorAttributes2.setContributorSequence(SequenceType.ADDITIONAL);
        return workContributors;
    }

    protected OrcidWork createWork(WorkTitle title, WorkExternalIdentifiers workExternalIdentifiers, WorkContributors workContributors) {
        OrcidWork orcidWork = new OrcidWork();
        orcidWork.setWorkTitle(title);
        orcidWork.setWorkExternalIdentifiers(workExternalIdentifiers);
        orcidWork.setWorkContributors(workContributors);
        orcidWork.setWorkSource(new WorkSource(WorkSource.NULL_SOURCE_PROFILE));
        return orcidWork;
    }

    public Affiliation getAffiliation() throws DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        Affiliation affiliation = new Affiliation();
        affiliation.setStartDate(new StartDate(calendar));
        affiliation.setEndDate(new EndDate(calendar));
        affiliation.setAffiliationType(AffiliationType.CURRENT_INSTITUTION);
        affiliation.setAffiliationName("Past Institution");
        affiliation.setRoleTitle("A Role");
        affiliation.setDepartmentName("A Department");
        Address address = new Address();
        address.setCountry(new Country("United Kingdom"));
        affiliation.setAddress(address);
        return affiliation;
    }

}
