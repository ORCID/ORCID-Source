package org.orcid.core.manager.v3.validator;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.ActivityTitleValidationException;
import org.orcid.core.exception.ActivityTypeValidationException;
import org.orcid.core.exception.InvalidDisambiguatedOrgException;
import org.orcid.core.exception.InvalidFuzzyDateException;
import org.orcid.core.exception.InvalidOrgException;
import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.rc2.common.Amount;
import org.orcid.jaxb.model.v3.rc2.common.Contributor;
import org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes;
import org.orcid.jaxb.model.v3.rc2.common.ContributorEmail;
import org.orcid.jaxb.model.v3.rc2.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.rc2.common.ContributorRole;
import org.orcid.jaxb.model.v3.rc2.common.Country;
import org.orcid.jaxb.model.v3.rc2.common.CreditName;
import org.orcid.jaxb.model.v3.rc2.common.Day;
import org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc2.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc2.common.Month;
import org.orcid.jaxb.model.v3.rc2.common.Organization;
import org.orcid.jaxb.model.v3.rc2.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.rc2.common.OrganizationDefinedFundingSubType;
import org.orcid.jaxb.model.v3.rc2.common.PublicationDate;
import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.jaxb.model.v3.rc2.common.SourceClientId;
import org.orcid.jaxb.model.v3.rc2.common.SourceName;
import org.orcid.jaxb.model.v3.rc2.common.SourceOrcid;
import org.orcid.jaxb.model.v3.rc2.common.Subtitle;
import org.orcid.jaxb.model.v3.rc2.common.Title;
import org.orcid.jaxb.model.v3.rc2.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.rc2.common.TranslatedTitle;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.common.Year;
import org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.rc2.record.Citation;
import org.orcid.jaxb.model.v3.rc2.record.CitationType;
import org.orcid.jaxb.model.v3.rc2.record.Education;
import org.orcid.jaxb.model.v3.rc2.record.Employment;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc2.record.Funding;
import org.orcid.jaxb.model.v3.rc2.record.FundingContributor;
import org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes;
import org.orcid.jaxb.model.v3.rc2.record.FundingContributorRole;
import org.orcid.jaxb.model.v3.rc2.record.FundingContributors;
import org.orcid.jaxb.model.v3.rc2.record.FundingTitle;
import org.orcid.jaxb.model.v3.rc2.record.FundingType;
import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.PeerReviewSubjectType;
import org.orcid.jaxb.model.v3.rc2.record.PeerReviewType;
import org.orcid.jaxb.model.v3.rc2.record.Relationship;
import org.orcid.jaxb.model.v3.rc2.record.Role;
import org.orcid.jaxb.model.v3.rc2.record.SequenceType;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.jaxb.model.v3.rc2.record.WorkContributors;
import org.orcid.jaxb.model.v3.rc2.record.WorkTitle;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class ActivityValidatorTest {

    @Resource(name = "activityValidatorV3")
    private ActivityValidator activityValidator;
    
    /**
     * VALIDATE WORKS
     * */
    @Test
    public void validateWork_validWorkTest() {
        Work work = getWork();
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityTitleValidationException.class)
    public void validateWork_emptyTitleTest() {
        Work work = getWork();
        work.getWorkTitle().getTitle().setContent(null);
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityTitleValidationException.class)
    public void validateWorkBlankTitleTest() {
        Work work = getWork();
        work.getWorkTitle().getTitle().setContent("  ");
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateWork_emptyTranslatedTitleWithLanguageCodeTest() {
        Work work = getWork();
        work.getWorkTitle().getTranslatedTitle().setContent(null);
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityTypeValidationException.class)
    public void validateWork_translatedTitleWithNoLanguageCodeTest() {
        Work work = getWork();
        work.getWorkTitle().getTranslatedTitle().setLanguageCode(null);
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityTypeValidationException.class)
    public void validateWork_emptyTypeTest() {
        Work work = getWork();
        work.setWorkType(null);
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }       
    
    @Test
    public void validateWork_invalidPublicationDateTest() {
        try {
            Work work = getWork();
            work.getPublicationDate().getYear().setValue("invalid");
            activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
            fail();
        } catch(InvalidFuzzyDateException e) {
            
        }
        
        try {
            Work work = getWork();
            work.getPublicationDate().getMonth().setValue("invalid");
            activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
            fail();
        } catch(InvalidFuzzyDateException e) {
            
        }
        
        try {
            Work work = getWork();
            work.getPublicationDate().getDay().setValue("invalid");
            activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
            fail();
        } catch(InvalidFuzzyDateException e) {
            
        }
        
        try {
            Work work = getWork();
            work.setPublicationDate(new PublicationDate(null, new Month(1), new Day(1)));
            activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
            fail();
        } catch(InvalidFuzzyDateException e) {
            
        }
        
        try {
            Work work = getWork();
            work.setPublicationDate(new PublicationDate(new Year(2017), null, new Day(1)));
            activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
            fail();
        } catch(InvalidFuzzyDateException e) {
            
        }
        
        try {
            Work work = getWork();
            work.setPublicationDate(new PublicationDate(null, null, new Day(1)));
            activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
            fail();
        } catch(InvalidFuzzyDateException e) {
            
        }
        
        try {
            Work work = getWork();
            //Invalid 2 digits year
            work.setPublicationDate(new PublicationDate(new Year(25), new Month(1), new Day(1)));
            activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
            fail();
        } catch(InvalidFuzzyDateException e) {
            
        }
        
        try {
            Work work = getWork();
            //Invalid 3 digits month
            work.setPublicationDate(new PublicationDate(new Year(2017), new Month(100), new Day(1)));
            activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
            fail();
        } catch(InvalidFuzzyDateException e) {
            
        }
        
        try {
            Work work = getWork();
            //Invalid 3 digits day
            work.setPublicationDate(new PublicationDate(new Year(2017), new Month(1), new Day(100)));
            activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
            fail();
        } catch(InvalidFuzzyDateException e) {
            
        }
        
        Work work = getWork();
        work.setPublicationDate(new PublicationDate(new Year(2017), new Month(1), null));
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
        
        work = getWork();
        work.setPublicationDate(new PublicationDate(new Year(2017), null, null));
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
                
        work = getWork();
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityTypeValidationException.class)
    public void validateWork_invalidCitationTypeTest() {
        Work work = getWork();
        work.getWorkCitation().setWorkCitationType(null);
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);        
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateWork_emptyCitationTest() {
        Work work = getWork();
        work.getWorkCitation().setCitation(null);
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateWork_contributorOrcidInvalidOrcidTest() {
        Work work = getWork();
        work.getWorkContributors().getContributor().get(0).getContributorOrcid().setPath("invalid");
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateWork_contributorOrcidInvalidUriTest() {
        Work work = getWork();
        work.getWorkContributors().getContributor().get(0).getContributorOrcid().setUri("http://invalid.org");
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateWork_emptyContributorCreditNameTest() {
        Work work = getWork();
        work.getWorkContributors().getContributor().get(0).getCreditName().setContent("");
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateWork_emptyContributorEmailTest() {
        Work work = getWork();
        work.getWorkContributors().getContributor().get(0).getContributorEmail().setValue("");
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityTypeValidationException.class)
    public void validateWork_emptyCountryTest() {
        Work work = getWork();        
        work.getCountry().setValue((Iso3166Country) null);
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidPutCodeException.class)
    public void validateWork_invalidPutCodeTest() {
        Work work = getWork();
        work.setPutCode(1L);
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void validateWork_changeVisibilityTest() {
        Work work = getWork();
        work.setVisibility(Visibility.LIMITED);
        activityValidator.validateWork(work, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityIdentifierValidationException.class)
    public void validateWork_invalidExternalIdentifierTypeTest() {
        Work work = getWork();
        work.getExternalIdentifiers().getExternalIdentifier().get(0).setType("invalid");
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityIdentifierValidationException.class)
    public void validateWork_emptyExternalIdentifierValueTest() {
        Work work = getWork();
        work.getExternalIdentifiers().getExternalIdentifier().get(0).setValue("");;
        activityValidator.validateWork(work, null, true, true, Visibility.PUBLIC);
    }
    
    public Work getWork() {
        Work work = new Work();
        work.setCountry(new Country(Iso3166Country.US));
        work.setJournalTitle(new Title("journal-title"));
        work.setLanguageCode("en");
        work.setPublicationDate(new PublicationDate(getFuzzyDate()));
        work.setShortDescription("short-description");
        work.setUrl(new Url("http://test.orcid.org"));
        work.setVisibility(Visibility.PUBLIC);
        work.setWorkCitation(new Citation("citation", CitationType.FORMATTED_HARVARD));
        ContributorAttributes attributes = new ContributorAttributes();
        attributes.setContributorRole(ContributorRole.ASSIGNEE);
        attributes.setContributorSequence(SequenceType.FIRST);
        
        ContributorOrcid contributorOrcid = new ContributorOrcid();
        contributorOrcid.setHost("http://test.orcid.org");
        contributorOrcid.setPath("0000-0000-0000-0000");
        contributorOrcid.setUri("https://test.orcid.org/0000-0000-0000-0000");
        
        Contributor contributor = new Contributor();
        contributor.setContributorAttributes(attributes);
        contributor.setContributorOrcid(contributorOrcid);
        contributor.setCreditName(new CreditName("credit name"));
        contributor.setContributorEmail(new ContributorEmail("email@test.orcid.org"));
        
        WorkContributors contributors = new WorkContributors(Stream.of(contributor).collect(Collectors.toList()));        
        work.setWorkContributors(contributors);
        work.setWorkExternalIdentifiers(getExternalIDs());                        
        work.setWorkTitle(getWorkTitle());
        
        work.setWorkType(WorkType.ARTISTIC_PERFORMANCE);
        return work;
    }
    
    /**
     * VALIDATE FUNDING
     * */
    @Test
    public void validateFunding_validFundingTest() {
        Funding funding = getFunding();
        activityValidator.validateFunding(funding, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityTitleValidationException.class)
    public void validateFunding_emptyTitleTest() {
        Funding funding = getFunding();
        funding.getTitle().getTitle().setContent(null);
        activityValidator.validateFunding(funding, null, true, true, Visibility.PUBLIC);
    }        
    
    @Test(expected = ActivityIdentifierValidationException.class)
    public void validateFunding_emptyExternalIdentifiersTest() {
        Funding funding = getFunding();
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        activityValidator.validateFunding(funding, null, true, true, Visibility.PUBLIC);
    }
    
    @Test
    public void validateFunding_invalidCurrencyCodeTest() {
        try {
            Funding funding = getFunding();
            funding.getAmount().setCurrencyCode(null);
            activityValidator.validateFunding(funding, null, true, true, Visibility.PUBLIC);
            fail();
        } catch(Exception e) {
            
        }
        
        try {
            Funding funding = getFunding();
            funding.getAmount().setContent(null);
            activityValidator.validateFunding(funding, null, true, true, Visibility.PUBLIC);
            fail();
        } catch(OrcidValidationException e) {
            
        }
    }
    
    @Test(expected = InvalidPutCodeException.class)
    public void validateFunding_invalidPutCodeTest() {
        Funding funding = getFunding();
        funding.setPutCode(1L);
        activityValidator.validateFunding(funding, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void validateFunding_dontChangeVisibilityTest() {
        Funding funding = getFunding();
        funding.setVisibility(Visibility.LIMITED);
        activityValidator.validateFunding(funding, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityIdentifierValidationException.class)
    public void validateFunding_invalidExternalIdentifiersTest() {
        Funding funding = getFunding();
        funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType(null);
        activityValidator.validateFunding(funding, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidOrgException.class)
    public void validateFundingWithoutOrg() {
        Funding f = getFunding();
        f.setOrganization(null);
        activityValidator.validateFunding(f, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidDisambiguatedOrgException.class)
    public void validateFundingWithoutDisambiguatedOrg() {
        Funding f = getFunding();
        f.getOrganization().setDisambiguatedOrganization(null);
        activityValidator.validateFunding(f, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidDisambiguatedOrgException.class)
    public void validateFundingWithoutDisambiguatedOrgId() {
        Funding f = getFunding();
        f.getOrganization().getDisambiguatedOrganization().setDisambiguatedOrganizationIdentifier(null);
        activityValidator.validateFunding(f, null, false, true, Visibility.PUBLIC);
    }
    
    public Funding getFunding() {
        Funding funding = new Funding();
        Amount amount = new Amount();
        amount.setContent("1000");
        amount.setCurrencyCode("USD");        
        funding.setAmount(amount);
        FundingContributor contributor = new FundingContributor();

        FundingContributorAttributes attributes = new FundingContributorAttributes();
        attributes.setContributorRole(FundingContributorRole.LEAD);
        
        ContributorOrcid contributorOrcid = new ContributorOrcid();
        contributorOrcid.setHost("http://test.orcid.org");
        contributorOrcid.setPath("0000-0000-0000-0000");
        contributorOrcid.setUri("https://test.orcid.org/0000-0000-0000-0000");
        
        contributor.setContributorAttributes(attributes);
        contributor.setContributorOrcid(contributorOrcid);
        
        FundingContributors contributors = new FundingContributors();
        contributors.getContributor().add(contributor);
        
        funding.setContributors(contributors);
        funding.setDescription("description");
        funding.setEndDate(getFuzzyDate());
        funding.setExternalIdentifiers(getExternalIDs());
                
        funding.setOrganization(getOrganization());
        funding.setOrganizationDefinedType(new OrganizationDefinedFundingSubType("subtype"));
        funding.setStartDate(getFuzzyDate());
        
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title("title"));
        title.setTranslatedTitle(new TranslatedTitle("translated title", "en"));        
        funding.setTitle(title);
        
        funding.setType(FundingType.AWARD);
        funding.setUrl(new Url("http://test.orcid.org"));
        funding.setVisibility(Visibility.PUBLIC);
        
        return funding;
    }
    
    /**
     * VALIDATE AFFILIATIONS
     * */
    @Test
    public void validateEmployment_validEmploymentTest() {
        Employment employment = getEmployment();
        activityValidator.validateAffiliation(employment, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidPutCodeException.class)
    public void validateEmployment_invalidPutCodeTest() {
        Employment employment = getEmployment();
        employment.setPutCode(1L);
        activityValidator.validateAffiliation(employment, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void validateEmployment_dontChangeVisibilityTest() {
        Employment employment = getEmployment();
        employment.setVisibility(Visibility.LIMITED);
        activityValidator.validateAffiliation(employment, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidOrgException.class)
    public void validateEmploymentWithoutOrg() {
        Employment e = getEmployment();
        e.setOrganization(null);
        activityValidator.validateAffiliation(e, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidDisambiguatedOrgException.class)
    public void validateEmploymentWithoutDisambiguatedOrg() {
        Employment e = getEmployment();
        e.getOrganization().setDisambiguatedOrganization(null);
        activityValidator.validateAffiliation(e, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidDisambiguatedOrgException.class)
    public void validateEmploymentWithoutDisambiguatedOrgId() {
        Employment e = getEmployment();
        e.getOrganization().getDisambiguatedOrganization().setDisambiguatedOrganizationIdentifier(null);
        activityValidator.validateAffiliation(e, null, false, true, Visibility.PUBLIC);
    }
    
    @Test
    public void validateEducation_validEducationTest() {
        Education education = getEducation();
        activityValidator.validateAffiliation(education, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidPutCodeException.class)
    public void validateEducation_invalidPutCodeTest() {
        Education education = getEducation();
        education.setPutCode(1L);
        activityValidator.validateAffiliation(education, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void validateEducation_dontChangeVisibilityTest() {
        Education education = getEducation();
        education.setVisibility(Visibility.LIMITED);
        activityValidator.validateAffiliation(education, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidOrgException.class)
    public void validateEducationWithoutOrg() {
        Education e = getEducation();
        e.setOrganization(null);
        activityValidator.validateAffiliation(e, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidDisambiguatedOrgException.class)
    public void validateEducationWithoutDisambiguatedOrg() {
        Education e = getEducation();
        e.getOrganization().setDisambiguatedOrganization(null);
        activityValidator.validateAffiliation(e, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidDisambiguatedOrgException.class)
    public void validateEducationWithoutDisambiguatedOrgId() {
        Education e = getEducation();
        e.getOrganization().getDisambiguatedOrganization().setDisambiguatedOrganizationIdentifier(null);
        activityValidator.validateAffiliation(e, null, false, true, Visibility.PUBLIC);
    }
    
    public Employment getEmployment() {
        Employment employment = new Employment();
        employment.setDepartmentName("department name");
        employment.setEndDate(getFuzzyDate());
        employment.setOrganization(getOrganization());
        employment.setRoleTitle("role");
        employment.setStartDate(getFuzzyDate());
        employment.setVisibility(Visibility.PUBLIC);
        return employment;
    }
    
    public Education getEducation() {
        Education education = new Education();
        education.setDepartmentName("department name");
        education.setEndDate(getFuzzyDate());
        education.setOrganization(getOrganization());
        education.setRoleTitle("role");
        education.setStartDate(getFuzzyDate());
        education.setVisibility(Visibility.PUBLIC);
        return education;
    }        
    
    /**
     * VALIDATE PEER REVIEW
     * */
    @Test
    public void validatePeerReview_validPeerReviewTest() {
        PeerReview pr = getPeerReviewWithWorkTypeAsSubjectType();
        activityValidator.validatePeerReview(pr, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityIdentifierValidationException.class)
    public void validatePeerReview_invalidExternalIdentifiersTest() {        
        PeerReview pr = getPeerReviewWithWorkTypeAsSubjectType();
        pr.getExternalIdentifiers().getExternalIdentifier().get(0).setType(null);
        activityValidator.validatePeerReview(pr, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidPutCodeException.class)
    public void validatePeerReview_invalidPutCodeTest() {   
        Source source = mock(Source.class);
        when(source.getSourceName()).thenReturn(new SourceName("Source name"));
        PeerReview pr = getPeerReviewWithWorkTypeAsSubjectType();
        pr.setPutCode(1L);
        activityValidator.validatePeerReview(pr, source, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityTypeValidationException.class)
    public void validatePeerReview_invalidPeerReviewTypeTest() {        
        PeerReview pr = getPeerReviewWithWorkTypeAsSubjectType();
        pr.setType(null);
        activityValidator.validatePeerReview(pr, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityIdentifierValidationException.class)
    public void validatePeerReview_noExternalIdentifiersTest() {
        PeerReview pr = getPeerReviewWithWorkTypeAsSubjectType();
        pr.getExternalIdentifiers().getExternalIdentifier().clear();
        activityValidator.validatePeerReview(pr, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityIdentifierValidationException.class)
    public void validatePeerReview_emptyExternalIdentifierValueTest() {
        PeerReview pr = getPeerReviewWithWorkTypeAsSubjectType();
        pr.getExternalIdentifiers().getExternalIdentifier().get(0).setValue("");
        activityValidator.validatePeerReview(pr, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = ActivityIdentifierValidationException.class)
    public void validatePeerReview_invalidSubjectExternalIdentifiersTest() {
        PeerReview pr = getPeerReviewWithWorkTypeAsSubjectType();
        pr.getSubjectExternalIdentifier().setType(null);
        activityValidator.validatePeerReview(pr, null, true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void validatePeerReview_dontChangeVisibilityTest() {        
        PeerReview pr = getPeerReviewWithWorkTypeAsSubjectType();
        pr.setVisibility(Visibility.LIMITED);
        activityValidator.validatePeerReview(pr, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidOrgException.class)
    public void validatePeerReviewWithoutOrg() {
        PeerReview pr = getPeerReviewWithWorkTypeAsSubjectType();
        pr.setOrganization(null);
        activityValidator.validatePeerReview(pr, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidDisambiguatedOrgException.class)
    public void validatePeerReviewWithoutDisambiguatedOrg() {
        PeerReview pr = getPeerReviewWithWorkTypeAsSubjectType();
        pr.getOrganization().setDisambiguatedOrganization(null);
        activityValidator.validatePeerReview(pr, null, false, true, Visibility.PUBLIC);
    }
    
    @Test(expected = InvalidDisambiguatedOrgException.class)
    public void validatePeerReviewWithoutDisambiguatedOrgId() {
        PeerReview pr = getPeerReviewWithWorkTypeAsSubjectType();
        pr.getOrganization().getDisambiguatedOrganization().setDisambiguatedOrganizationIdentifier(null);
        activityValidator.validatePeerReview(pr, null, false, true, Visibility.PUBLIC);
    }
    
    @Test
    public void validatePeerReviewWithFundingTypeAsSubjectType() {
        PeerReview pr = getPeerReviewWithFundingTypeAsSubjectType();
        activityValidator.validatePeerReview(pr, null, false, true, Visibility.PUBLIC);
    }
    
    private PeerReview getPeerReviewWithFundingTypeAsSubjectType() {
        PeerReview peerReview = getPeerReviewWithWorkTypeAsSubjectType();
        peerReview.setSubjectType(PeerReviewSubjectType.AWARD);
        return peerReview;
    }
    
    private PeerReview getPeerReviewWithWorkTypeAsSubjectType() {
        PeerReview peerReview = new PeerReview();
        peerReview.setCompletionDate(getFuzzyDate());
        peerReview.setExternalIdentifiers(getExternalIDs());
        peerReview.setGroupId("group-id");        
        peerReview.setOrganization(getOrganization());
        peerReview.setRole(Role.CHAIR);
        peerReview.setSubjectContainerName(new Title("subject-container-name"));
        peerReview.setSubjectExternalIdentifier(getExternalID());
        peerReview.setSubjectName(getWorkTitle());
        peerReview.setSubjectType(PeerReviewSubjectType.ARTISTIC_PERFORMANCE);
        peerReview.setSubjectUrl(new Url("http://test.orcid.org"));
        peerReview.setType(PeerReviewType.EVALUATION);
        peerReview.setUrl(new Url("http://test.orcid.org"));
        peerReview.setVisibility(Visibility.PUBLIC);
        return peerReview;
    }
    
    /**
     * VALIDATE GROUP ID RECORD
     * */
    @Test
    public void validateGroupId_validTest() {   
        SourceEntity source = mock(SourceEntity.class);
        when(source.getCachedSourceName()).thenReturn("source name");
        GroupIdRecord g = getGroupIdRecord();
        activityValidator.validateGroupIdRecord(g, true, source);
    }
    
    @Test(expected = InvalidPutCodeException.class)
    public void validateGroupId_invalidPutCodeTest() { 
        SourceEntity source = mock(SourceEntity.class);
        when(source.getCachedSourceName()).thenReturn("source name");
        GroupIdRecord g = getGroupIdRecord();
        g.setPutCode(1L);
        activityValidator.validateGroupIdRecord(g, true, source);
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateGroupId_invalidGroupIdTest() {
        SourceEntity source = mock(SourceEntity.class);
        when(source.getCachedSourceName()).thenReturn("source name");
        GroupIdRecord g = getGroupIdRecord();
        g.setGroupId("invalid");
        activityValidator.validateGroupIdRecord(g, true, source);
    }
    
    public GroupIdRecord getGroupIdRecord() {
        GroupIdRecord g = new GroupIdRecord();
        g.setDescription("description");
        g.setGroupId("orcid-generated:0123456789");
        g.setName("group-name");
        g.setType("group-type");
        return g;
    }
    
    /**
     * VALIDATE DUPLICATED EXTERNAL IDENTIFIERS
     * */
    @SuppressWarnings("deprecation")
    @Test
    public void validateDuplicatedExtIds_noDuplicatesTest() {                
        Source source1 = mock(Source.class);
        when(source1.getSourceName()).thenReturn(new SourceName("source name"));
        when(source1.getSourceClientId()).thenReturn(new SourceClientId("APP-00000000000000"));
        
        SourceOrcid sourceOrcid = new SourceOrcid();
        sourceOrcid.setPath("0000-0000-0000-0000");
        Source source2 = mock(Source.class);
        when(source2.getSourceName()).thenReturn(new SourceName("other source name"));
        when(source2.getSourceOrcid()).thenReturn(sourceOrcid);
        ExternalIDs extIds1 = getExternalIDs();
        
        ExternalIDs extIds2 = getExternalIDs();
        activityValidator.checkExternalIdentifiersForDuplicates(extIds1, extIds2, source2, source1);
    }
    
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void validateDuplicatedExtIds_duplicatesFoundTest() {
        Source source1 = new Source();
        source1.setSourceName(new SourceName("source name"));
        source1.setSourceClientId(new SourceClientId("APP-00000000000000"));
        
        SourceClientId sourceClientId = new SourceClientId();
        sourceClientId.setPath("APP-00000000000000");
        Source source2 = new Source();
        source2.setSourceName(new SourceName("source name"));
        source2.setSourceClientId(sourceClientId);
        ExternalIDs extIds1 = getExternalIDs();
        
        ExternalIDs extIds2 = getExternalIDs();
        activityValidator.checkExternalIdentifiersForDuplicates(extIds1, extIds2, source2, source1);
    }
    
    /**
     * COMMON
     * */
    public FuzzyDate getFuzzyDate() {
        return new FuzzyDate(new Year(2017), new Month(1), new Day(1));
    }
    
    public ExternalIDs getExternalIDs() {
        ExternalID id1 = getExternalID();        
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(id1);
        return extIds;
    }
    
    public ExternalID getExternalID() {
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
        return id1;
    }
    
    public WorkTitle getWorkTitle() {
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("title"));
        title.setSubtitle(new Subtitle("subtitle"));
        title.setTranslatedTitle(new TranslatedTitle("translated title", "en"));
        return title;
    }
    
    public Organization getOrganization() {
        Organization org = new Organization();
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        address.setRegion("region");
        org.setAddress(address);
        org.setName("name");
        org.setDisambiguatedOrganization(getDisambiguatedOrganization());
        return org;
    }

    private DisambiguatedOrganization getDisambiguatedOrganization() {
        DisambiguatedOrganization disambiguatedOrganization = new DisambiguatedOrganization();
        disambiguatedOrganization.setDisambiguatedOrganizationIdentifier("some-identifier");
        disambiguatedOrganization.setDisambiguationSource(OrgDisambiguatedSourceType.FUNDREF.name());
        return disambiguatedOrganization;
    }
    
    //validate normalization is being used
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void checkDupesNormalized(){
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("agr");
        id1.setValue("UPPER");
        ExternalIDs ids1 = new ExternalIDs();
        ids1.getExternalIdentifier().add(id1);
        
        ExternalID id2 = new ExternalID();
        id2.setRelationship(Relationship.SELF);
        id2.setType("agr");
        id2.setValue("upper");
        id2.setNormalized(new TransientNonEmptyString("upper"));
        ExternalIDs ids2 = new ExternalIDs();
        ids2.getExternalIdentifier().add(id2);
        
        Source source1 = new Source();
        source1.setSourceName(new SourceName("source name"));
        source1.setSourceClientId(new SourceClientId("APP-00000000000000"));
        
        Source source2 = new Source();
        source2.setSourceName(new SourceName("source name"));
        SourceClientId sourceClientId = new SourceClientId();
        sourceClientId.setPath("APP-00000000000000");
        source2.setSourceClientId(sourceClientId);
        activityValidator.checkExternalIdentifiersForDuplicates(ids1, ids2, source2, source1);
    }
}
