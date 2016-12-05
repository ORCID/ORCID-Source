/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.publicV2.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_rc3.Iso3166Country;
import org.orcid.jaxb.model.common_rc3.OrcidIdentifier;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc3.Educations;
import org.orcid.jaxb.model.record.summary_rc3.Employments;
import org.orcid.jaxb.model.record.summary_rc3.Fundings;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc3.Works;
import org.orcid.jaxb.model.record_rc3.Address;
import org.orcid.jaxb.model.record_rc3.Biography;
import org.orcid.jaxb.model.record_rc3.Education;
import org.orcid.jaxb.model.record_rc3.Email;
import org.orcid.jaxb.model.record_rc3.Emails;
import org.orcid.jaxb.model.record_rc3.Employment;
import org.orcid.jaxb.model.record_rc3.Funding;
import org.orcid.jaxb.model.record_rc3.History;
import org.orcid.jaxb.model.record_rc3.Keyword;
import org.orcid.jaxb.model.record_rc3.OtherName;
import org.orcid.jaxb.model.record_rc3.PeerReview;
import org.orcid.jaxb.model.record_rc3.Person;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc3.PersonalDetails;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.jaxb.model.record_rc3.ResearcherUrl;
import org.orcid.jaxb.model.record_rc3.Work;
import org.orcid.jaxb.model.record_rc3.WorkType;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-t1-web-context.xml", "classpath:orcid-t1-security-context.xml" })
public class PublicV2ApiServiceDelegatorTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", 
            "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", 
            "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/BiographyEntityData.xml", "/data/RecordNameEntityData.xml");
    
    private final String ORCID = "0000-0000-0000-0003";
    
    @Resource(name = "publicV2ApiServiceDelegator")
    PublicV2ApiServiceDelegator<?, ?, ?, ?, ?, ?, ?, ?, ?> serviceDelegator;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);        
    }
    
    @Before
    public void before() {
        ArrayList<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        Authentication auth = new AnonymousAuthenticationToken("anonymous", "anonymous", roles);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }
    
    @Test
    public void testViewWork() {
        Response response = serviceDelegator.viewWork(ORCID, 11L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("PUBLIC", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(11), work.getPutCode());
        assertEquals("/0000-0000-0000-0003/work/11", work.getPath());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewFunding() {
        Response response = serviceDelegator.viewFunding(ORCID, 10L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(10), funding.getPutCode());
        assertEquals("/0000-0000-0000-0003/funding/10", funding.getPath());
        assertEquals("PUBLIC", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC.value(), funding.getVisibility().value());
        assertEquals("APP-5555555555555555", funding.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewEducation() {
        Response response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals(Long.valueOf(20), education.getPutCode());
        assertEquals("/0000-0000-0000-0003/education/20", education.getPath());
        assertEquals("PUBLIC Department", education.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), education.getVisibility().value());
        assertEquals("APP-5555555555555555", education.getSource().retrieveSourcePath());
    }
    
    @Test
    public void testViewPeerReview() {
        Response response = serviceDelegator.viewPeerReview(ORCID, 9L);
        assertNotNull(response);
        PeerReview peerReview= (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(9), peerReview.getPutCode());
        assertEquals("02", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("02", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2016", peerReview.getCompletionDate().getYear().getValue());
        assertNotNull(peerReview.getExternalIdentifiers());
        assertNotNull(peerReview.getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, peerReview.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(Visibility.PUBLIC.value(), peerReview.getVisibility().value());
        assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewEmployment() {
        Response response = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Long.valueOf(17), employment.getPutCode());
        assertEquals("/0000-0000-0000-0003/employment/17", employment.getPath());
        assertEquals("PUBLIC Department", employment.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), employment.getVisibility().value());  
        assertEquals("APP-5555555555555555", employment.getSource().retrieveSourcePath());
    }
    
    @Test
    public void testViewOtherNames() {
        Response response = serviceDelegator.viewOtherName(ORCID, 13L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals(Long.valueOf(13), otherName.getPutCode());
        assertEquals("Other Name PUBLIC", otherName.getContent());
        assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
        assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
    }
    
    @Test
    public void testViewKeywords() {
        Response response = serviceDelegator.viewKeyword(ORCID, 9L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals(Long.valueOf(9), keyword.getPutCode());
        assertEquals("PUBLIC", keyword.getContent());
        assertEquals(Visibility.PUBLIC.value(), keyword.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/keywords/9", keyword.getPath());
        assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
    }
    
    @Test
    public void testViewExternalIdentifiers() {
        Response response = serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals(Long.valueOf(13), extId.getPutCode());
        assertEquals("public_type", extId.getType());
        assertNotNull(extId.getUrl());
        assertEquals("http://ext-id/public_ref", extId.getUrl().getValue());
        assertEquals(Visibility.PUBLIC.value(), extId.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", extId.getPath());
        assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
    }
    
    @Test
    public void testViewResearcherUrls() {
        Response response = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        assertNotNull(response);
        ResearcherUrl rUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(rUrl);
        assertNotNull(rUrl.getUrl());
        assertEquals("http://www.researcherurl.com?id=13", rUrl.getUrl().getValue());
        assertEquals("public_rurl", rUrl.getUrlName());        
        assertEquals(Visibility.PUBLIC.value(), rUrl.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", rUrl.getPath());
        assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
    }
    
    @Test
    public void testViewEmails() {
        Response response = serviceDelegator.viewEmails(ORCID);
        assertNotNull(response);
        Emails emails = (Emails) response.getEntity();
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(1, emails.getEmails().size());
        Email email = emails.getEmails().get(0);
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", email.getEmail());
        assertTrue(email.isCurrent());
        assertTrue(email.isPrimary());
        assertTrue(email.isVerified());
        assertEquals(Visibility.PUBLIC.value(), email.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/email", emails.getPath());
        assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
    }
    
    @Test
    public void testViewAddress() {
        Response response = serviceDelegator.viewAddress(ORCID, 9L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Long.valueOf(9), address.getPutCode());
        assertNotNull(address.getCountry());
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC.value(), address.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/address/9", address.getPath());
        assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
    }
    
    @Test
    public void testFindPersonalDetails() {
        Response response = serviceDelegator.viewPersonalDetails(ORCID);
        assertNotNull(response);
        PersonalDetails personalDetails = (PersonalDetails) response.getEntity();
        assertNotNull(personalDetails);
        assertNotNull(personalDetails.getBiography());
        assertEquals("Biography for 0000-0000-0000-0003", personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getBiography().getLastModifiedDate());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());
        assertEquals("/0000-0000-0000-0003/biography", personalDetails.getBiography().getPath());
        assertNotNull(personalDetails.getLastModifiedDate());
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getCreatedDate().getValue());
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());
        assertEquals("Family Name", personalDetails.getName().getFamilyName().getContent());
        assertEquals("Given Names", personalDetails.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getName().getVisibility().value());
        assertNotNull(personalDetails.getName().getLastModifiedDate());
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getLastModifiedDate());
        assertEquals(1, personalDetails.getOtherNames().getOtherNames().size());
        assertEquals("Other Name PUBLIC", personalDetails.getOtherNames().getOtherNames().get(0).getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getOtherNames().getOtherNames().get(0).getVisibility().value());
        assertEquals("/0000-0000-0000-0003/other-names", personalDetails.getOtherNames().getPath());
        assertEquals("/0000-0000-0000-0003/personal-details", personalDetails.getPath());
    }
    
    @Test
    public void testFindPerson() {
        Response response = serviceDelegator.viewPerson(ORCID);
        assertNotNull(response);
        Person person = (Person) response.getEntity();
        validatePerson(person);  
    }

    @Test
    public void testFindActivityDetails() {
        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        validateActivities(summary);
    }
    
    @Test
    public void testFindRecord() {
        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        Record record = (Record) response.getEntity();
        validateRecord(record);                       
    }
    
    @Test
    public void testValidateActivitiesUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        validateActivities(summary);
    }
    
    @Test
    public void testValidatePersonUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPerson(ORCID);
        assertNotNull(response);
        Person person = (Person) response.getEntity();
        validatePerson(person);
    }
    
    @Test
    public void testValidateRecordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        Record record = (Record) response.getEntity();
        validateRecord(record);
    }
    
    //Education
    @Test
    public void testGetPublicEducationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(r);
        Education e = (Education) r.getEntity();
        assertNotNull(e);
        assertEquals(Long.valueOf(20), e.getPutCode());
        
    }
    
    @Test
    public void testGetPublicEducations() {
        Response r = serviceDelegator.viewEducations(ORCID);
        assertNotNull(r);
        Educations educations = (Educations) r.getEntity();
        assertNotNull(educations);
        assertNotNull(educations.getSummaries());
        assertEquals(1, educations.getSummaries().size());
        assertEquals(Long.valueOf(20), educations.getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, educations.getSummaries().get(0).getVisibility());
    }
    
    @Test
    public void testGetPublicEducationsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewEducations(ORCID);
        assertNotNull(r);
        Educations educations = (Educations) r.getEntity();
        assertNotNull(educations);
        assertNotNull(educations.getSummaries());
        assertEquals(1, educations.getSummaries().size());
        assertEquals(Long.valueOf(20), educations.getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, educations.getSummaries().get(0).getVisibility());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetLimitedEducationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducation(ORCID, 21L);
        fail();
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetPrivateEducationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducation(ORCID, 22L);
        fail();
    }
    
    //Employment
    @Test
    public void testGetPublicEmploymentUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(r);
        Employment e = (Employment) r.getEntity();
        assertNotNull(e);
        assertEquals(Long.valueOf(17), e.getPutCode());
    }
        
    @Test
    public void testGetPublicEmployments() {
        Response r = serviceDelegator.viewEmployments(ORCID);
        assertNotNull(r);
        Employments employments = (Employments) r.getEntity();
        assertNotNull(employments);
        assertNotNull(employments.getSummaries());
        assertEquals(1, employments.getSummaries().size());
        assertEquals(Long.valueOf(17), employments.getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, employments.getSummaries().get(0).getVisibility());
    }
    
    @Test
    public void testGetPublicEmploymentsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewEmployments(ORCID);
        assertNotNull(r);
        Employments employments = (Employments) r.getEntity();
        assertNotNull(employments);
        assertNotNull(employments.getSummaries());
        assertEquals(1, employments.getSummaries().size());
        assertEquals(Long.valueOf(17), employments.getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, employments.getSummaries().get(0).getVisibility());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetLimitedEmploymentUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment(ORCID, 18L);
        fail();
    }        

    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetPrivateEmploymentUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment(ORCID, 19L);
        fail();
    }
    
    //Funding
    @Test
    public void testGetPublicFundingUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewFunding(ORCID, 10L);
        assertNotNull(r);
        Funding f = (Funding) r.getEntity();
        assertNotNull(f);
        assertEquals(Long.valueOf(10), f.getPutCode());
    }
    
    @Test
    public void testGetPublicFundings() {
        Response r = serviceDelegator.viewFundings(ORCID);
        assertNotNull(r);
        Fundings fundings = (Fundings) r.getEntity();
        assertNotNull(fundings);
        assertNotNull(fundings.getFundingGroup());
        assertEquals(1, fundings.getFundingGroup().size());        
        assertNotNull(fundings.getFundingGroup().get(0).getIdentifiers());
        assertEquals(1, fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("http://test.orcid.org/1.com", fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertNotNull(fundings.getFundingGroup().get(0).getFundingSummary());
        assertEquals(1, fundings.getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(Long.valueOf(10), fundings.getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
    }
    
    @Test
    public void testGetPublicFundingsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewFundings(ORCID);
        assertNotNull(r);
        Fundings fundings = (Fundings) r.getEntity();
        assertNotNull(fundings);
        assertNotNull(fundings.getFundingGroup());
        assertEquals(1, fundings.getFundingGroup().size());        
        assertNotNull(fundings.getFundingGroup().get(0).getIdentifiers());
        assertEquals(1, fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("http://test.orcid.org/1.com", fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertNotNull(fundings.getFundingGroup().get(0).getFundingSummary());
        assertEquals(1, fundings.getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(Long.valueOf(10), fundings.getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetLimitedFundingUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewFunding(ORCID, 11L);
        fail();
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetPrivateFundingUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewFunding(ORCID, 12L);
        fail();
    }
    
    //Work
    @Test
    public void testGetPublicWorkUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewWork(ORCID, 11L);
        assertNotNull(r);
        Work w = (Work) r.getEntity();
        assertNotNull(w);
        assertEquals(Long.valueOf(11), w.getPutCode());
    }
    
    @Test
    public void testGetPublicWorks() {
        Response r = serviceDelegator.viewWorks(ORCID);
        assertNotNull(r);
        Works works = (Works) r.getEntity();
        assertNotNull(works);
        assertNotNull(works.getWorkGroup());
        assertEquals(1, works.getWorkGroup().size());
        assertNotNull(works.getWorkGroup().get(0).getIdentifiers());
        assertEquals(1, works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("1",works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(works.getWorkGroup().get(0).getWorkSummary());
        assertEquals(Long.valueOf(11),works.getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
    }
    
    @Test
    public void testGetPublicWorksUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewWorks(ORCID);
        assertNotNull(r);
        Works works = (Works) r.getEntity();
        assertNotNull(works);
        assertNotNull(works.getWorkGroup());
        assertEquals(1, works.getWorkGroup().size());
        assertNotNull(works.getWorkGroup().get(0).getIdentifiers());
        assertEquals(1, works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("1",works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(works.getWorkGroup().get(0).getWorkSummary());
        assertEquals(Long.valueOf(11),works.getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetLimitedWorkUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewWork(ORCID, 12L);
        fail();
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetPrivateWorkUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewWork(ORCID, 13L);
        fail();
    }
    
    //Peer review
    @Test
    public void testGetPublicPeerReviewUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewPeerReview(ORCID, 9L);
        assertNotNull(r);
        PeerReview p = (PeerReview) r.getEntity();
        assertNotNull(p);
        assertEquals(Long.valueOf(9), p.getPutCode());
    }
    
    @Test
    public void testGetPublicPeerReviews() {
        Response r = serviceDelegator.viewPeerReviews(ORCID);
        assertNotNull(r);
        PeerReviews p = (PeerReviews) r.getEntity();
        assertNotNull(p);
        assertNotNull(p.getPeerReviewGroup());
        assertEquals(1, p.getPeerReviewGroup().size());
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewSummary());
        assertEquals(1, p.getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), p.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
    }
    
    @Test
    public void testGetPublicPeerReviewsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewPeerReviews(ORCID);
        assertNotNull(r);
        PeerReviews p = (PeerReviews) r.getEntity();
        assertNotNull(p);
        assertNotNull(p.getPeerReviewGroup());
        assertEquals(1, p.getPeerReviewGroup().size());
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewSummary());
        assertEquals(1, p.getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), p.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetLimitedPeerReviewUsingToken() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview(ORCID, 10L);
        fail();
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetPrivatePeerReviewUsingToken() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview(ORCID, 11L);
        fail();
    }
    
    //Biography
    @Test
    public void testGetPublicBiographyUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewBiography(ORCID);
        assertNotNull(r);
        Biography b = (Biography) r.getEntity();
        assertNotNull(b);
        assertEquals(Visibility.PUBLIC, b.getVisibility());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetLimitedBiographyUsingToken() {        
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0002", ScopePathType.READ_LIMITED);
        serviceDelegator.viewBiography("0000-0000-0000-0002");
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetPrivateBiographyUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0001", ScopePathType.READ_LIMITED);
        serviceDelegator.viewBiography("0000-0000-0000-0001");
    }
    
    //Address
    @Test
    public void testGetPublicAddressUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewAddress(ORCID, 9L);
        assertNotNull(r);
        Address a = (Address) r.getEntity();
        assertNotNull(a);
        assertEquals(Long.valueOf(9), a.getPutCode());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetLimitedAddressUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewAddress(ORCID, 10L);
        fail();
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetPrivateAddressUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewAddress(ORCID, 10L);
        fail();
    }
    
    //Keyword
    @Test
    public void testGetPublicKeywordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewKeyword(ORCID, 9L);
        assertNotNull(r);
        Keyword k = (Keyword) r.getEntity();
        assertNotNull(k);
        assertEquals(Long.valueOf(9), k.getPutCode());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetLimitedKeywordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewKeyword(ORCID, 10L);
        fail();
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetPrivateKeywordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewKeyword(ORCID, 11L);
        fail();
    }
                   
    //External identifiers
    @Test
    public void testGetPublicExternalIdentifierUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        assertNotNull(r);
        PersonExternalIdentifier e = (PersonExternalIdentifier) r.getEntity();
        assertNotNull(e);
        assertEquals(Long.valueOf(13), e.getPutCode());
        
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetLimitedExternalIdentifierUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewExternalIdentifier(ORCID, 14L);
        fail();
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetPrivateExternalIdentifierUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewExternalIdentifier(ORCID, 15L);
        fail();
    }
    
    //Other names
    @Test
    public void testGetPublicOtherNameUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewOtherName(ORCID, 13L);
        assertNotNull(r);
        OtherName o = (OtherName) r.getEntity();
        assertNotNull(o);
        assertEquals(Long.valueOf(13), o.getPutCode());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetLimitedOtherNameUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewOtherName(ORCID, 14L);
        fail();
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetPrivateOtherNameUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewOtherName(ORCID, 15L);
        fail();
    }
    
    //Researcher urls
    @Test
    public void testGetPublicResearcherUrlUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        assertNotNull(r);
        ResearcherUrl ru = (ResearcherUrl) r.getEntity();
        assertNotNull(ru);
        assertEquals(Long.valueOf(13), ru.getPutCode());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetLimitedResearcherUrlUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearcherUrl(ORCID, 14L);
        fail();
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testGetPrivateResearcherUrlUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearcherUrl(ORCID, 15L);
        fail();
    }
    
    private void validatePerson(Person person) {
        assertNotNull(person);
        assertNotNull(person.getLastModifiedDate());
        assertNotNull(person.getAddresses());
        assertEquals("/0000-0000-0000-0003/address", person.getAddresses().getPath());
        assertNotNull(person.getAddresses().getLastModifiedDate());
        assertEquals(1, person.getAddresses().getAddress().size());
        Address address = person.getAddresses().getAddress().get(0);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC.value(), address.getVisibility().value());
        assertEquals(Long.valueOf(9), address.getPutCode());
        assertEquals("/0000-0000-0000-0003/address/9", address.getPath());
        assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
        assertNotNull(person.getBiography());
        assertEquals(Visibility.PUBLIC.value(), person.getBiography().getVisibility().value());
        assertEquals("Biography for 0000-0000-0000-0003", person.getBiography().getContent());
        assertNotNull(person.getBiography().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/biography", person.getBiography().getPath());        
        assertNotNull(person.getEmails());
        assertEquals(1, person.getEmails().getEmails().size());
        Email email = person.getEmails().getEmails().get(0);
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", email.getEmail());
        assertNotNull(email.getLastModifiedDate());
        assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC.value(), email.getVisibility().value());        
        assertNotNull(person.getExternalIdentifiers());
        assertEquals("/0000-0000-0000-0003/external-identifiers", person.getExternalIdentifiers().getPath());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifiers().size());
        PersonExternalIdentifier extId = person.getExternalIdentifiers().getExternalIdentifiers().get(0);
        assertNotNull(extId);
        assertEquals(Long.valueOf(13), extId.getPutCode());
        assertEquals("public_type", extId.getType());
        assertNotNull(extId.getUrl());
        assertEquals("http://ext-id/public_ref", extId.getUrl().getValue());
        assertEquals(Visibility.PUBLIC.value(), extId.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", extId.getPath());
        assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
        assertNotNull(person.getKeywords());
        assertEquals(1, person.getKeywords().getKeywords().size());
        assertNotNull(person.getKeywords().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/keywords", person.getKeywords().getPath());
        Keyword keyword = person.getKeywords().getKeywords().get(0);
        assertNotNull(keyword);
        assertEquals(Long.valueOf(9), keyword.getPutCode());
        assertEquals("PUBLIC", keyword.getContent());
        assertEquals(Visibility.PUBLIC.value(), keyword.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/keywords/9", keyword.getPath());   
        assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
        assertNotNull(person.getName());
        assertNotNull(person.getName().getLastModifiedDate());
        assertEquals("Credit Name", person.getName().getCreditName().getContent());
        assertEquals("Family Name", person.getName().getFamilyName().getContent());
        assertEquals("Given Names", person.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC.value(), person.getName().getVisibility().value());
        assertNotNull(person.getOtherNames());
        assertEquals("/0000-0000-0000-0003/other-names", person.getOtherNames().getPath());
        assertNotNull(person.getOtherNames().getLastModifiedDate());
        assertEquals(1, person.getOtherNames().getOtherNames().size());
        OtherName otherName = person.getOtherNames().getOtherNames().get(0);
        assertEquals("Other Name PUBLIC", otherName.getContent());
        assertNotNull(otherName.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
        assertEquals(Long.valueOf(13), otherName.getPutCode());
        assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());                
        assertNotNull(person.getResearcherUrls());
        assertEquals(1, person.getResearcherUrls().getResearcherUrls().size());
        assertNotNull(person.getResearcherUrls().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/researcher-urls", person.getResearcherUrls().getPath());
        ResearcherUrl rUrl = person.getResearcherUrls().getResearcherUrls().get(0);
        assertNotNull(rUrl);
        assertNotNull(rUrl.getUrl());
        assertEquals("http://www.researcherurl.com?id=13", rUrl.getUrl().getValue());
        assertEquals("public_rurl", rUrl.getUrlName());        
        assertEquals(Visibility.PUBLIC.value(), rUrl.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", rUrl.getPath());
        assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());        
        assertEquals("/0000-0000-0000-0003/person", person.getPath());
    }
    
    private void validateActivities(ActivitiesSummary summary) {
        assertNotNull(summary);
        // Check works
        assertNotNull(summary.getWorks());
        assertEquals(1, summary.getWorks().getWorkGroup().size());
        assertEquals(Long.valueOf(11), summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
        assertEquals("/0000-0000-0000-0003/work/11", summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath());
        assertEquals("PUBLIC", summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC.value(), summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getVisibility().value());
        
        // Check fundings
        assertNotNull(summary.getFundings());
        assertEquals(1, summary.getFundings().getFundingGroup().size());
        assertEquals(Long.valueOf(10), summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertEquals("/0000-0000-0000-0003/funding/10", summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPath());
        assertEquals("PUBLIC", summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC.value(), summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getVisibility().value());
        
        // Check Educations
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getSummaries());
        assertEquals(1, summary.getEducations().getSummaries().size());
        assertEquals(Long.valueOf(20), summary.getEducations().getSummaries().get(0).getPutCode());
        assertEquals("/0000-0000-0000-0003/education/20", summary.getEducations().getSummaries().get(0).getPath());
        assertEquals("PUBLIC Department", summary.getEducations().getSummaries().get(0).getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), summary.getEducations().getSummaries().get(0).getVisibility().value());
        
        // Check Employments
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getSummaries());
        assertEquals(1, summary.getEmployments().getSummaries().size());
        assertEquals(Long.valueOf(17), summary.getEmployments().getSummaries().get(0).getPutCode());
        assertEquals("/0000-0000-0000-0003/employment/17", summary.getEmployments().getSummaries().get(0).getPath());
        assertEquals("PUBLIC Department", summary.getEmployments().getSummaries().get(0).getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), summary.getEmployments().getSummaries().get(0).getVisibility().value());
    }
    
    private void validateRecord(Record record) {
        assertNotNull(record);
        validatePerson(record.getPerson());
        validateActivities(record.getActivitiesSummary());
        assertNotNull(record.getHistory());
        assertEquals(OrcidType.USER, record.getOrcidType());        
        assertNotNull(record.getPreferences());
        assertEquals(Locale.EN, record.getPreferences().getLocale());        
        History history = record.getHistory();
        assertTrue(history.getClaimed());
        assertNotNull(history.getCompletionDate());
        assertEquals(CreationMethod.INTEGRATION_TEST, history.getCreationMethod());
        assertNull(history.getDeactivationDate());
        assertNotNull(history.getLastModifiedDate());
        assertNotNull(history.getSource());
        assertEquals("APP-5555555555555555", history.getSource().retrieveSourcePath());
        assertNotNull(history.getSubmissionDate());                
        assertNotNull(record.getOrcidIdentifier());
        OrcidIdentifier id = record.getOrcidIdentifier();
        assertEquals("0000-0000-0000-0003", id.getPath());
    }
}
