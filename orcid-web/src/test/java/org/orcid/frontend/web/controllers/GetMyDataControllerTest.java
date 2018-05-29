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
package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.jaxb.model.v3.rc1.common.Amount;
import org.orcid.jaxb.model.v3.rc1.common.CreditName;
import org.orcid.jaxb.model.v3.rc1.common.Day;
import org.orcid.jaxb.model.v3.rc1.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc1.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc1.common.Month;
import org.orcid.jaxb.model.v3.rc1.common.Organization;
import org.orcid.jaxb.model.v3.rc1.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.rc1.common.OrganizationHolder;
import org.orcid.jaxb.model.v3.rc1.common.Title;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.common.Year;
import org.orcid.jaxb.model.v3.rc1.record.Affiliation;
import org.orcid.jaxb.model.v3.rc1.record.Biography;
import org.orcid.jaxb.model.v3.rc1.record.Distinction;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.FamilyName;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.FundingTitle;
import org.orcid.jaxb.model.v3.rc1.record.GivenNames;
import org.orcid.jaxb.model.v3.rc1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.Name;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.jaxb.model.v3.rc1.record.Person;
import org.orcid.jaxb.model.v3.rc1.record.Qualification;
import org.orcid.jaxb.model.v3.rc1.record.Service;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkTitle;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class GetMyDataControllerTest {

    private static final String ORCID = "0000-0000-0000-0000";

    @Mock
    private PersonDetailsManagerReadOnly mockPersonDetailsManager;

    @Mock
    private WorkEntityCacheManager mockWorkEntityCacheManager;

    @Mock
    private AffiliationsManagerReadOnly mockAffiliationManagerReadOnly;

    @Mock
    private ProfileFundingManagerReadOnly mockProfileFundingManagerReadOnly;

    @Mock
    private PeerReviewManagerReadOnly mockPeerReviewManagerReadOnly;

    @Mock
    private WorkManagerReadOnly mockWorkManagerReadOnly;

    private GetMyDataController getMyDataController;

    {
        try {
            getMyDataController = new GetMyDataController();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(getMyDataController, "batchSize", 50);
        TargetProxyHelper.injectIntoProxy(getMyDataController, "personDetailsManager", mockPersonDetailsManager);
        TargetProxyHelper.injectIntoProxy(getMyDataController, "workEntityCacheManager", mockWorkEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(getMyDataController, "affiliationManagerReadOnly", mockAffiliationManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(getMyDataController, "profileFundingManagerReadOnly", mockProfileFundingManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(getMyDataController, "peerReviewManagerReadOnly", mockPeerReviewManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(getMyDataController, "workManagerReadOnly", mockWorkManagerReadOnly);

        when(mockPersonDetailsManager.getPersonDetails(anyString())).thenAnswer(new Answer<Person>() {
            @Override
            public Person answer(InvocationOnMock invocation) throws Throwable {
                Person p = new Person();
                p.setBiography(new Biography("Biography", Visibility.LIMITED));
                Name name = new Name();
                name.setVisibility(Visibility.LIMITED);
                name.setFamilyName(new FamilyName("Family Name"));
                name.setGivenNames(new GivenNames("Given Names"));
                name.setCreditName(new CreditName("Credit Name"));
                p.setName(name);
                return p;
            }
        });
        when(mockAffiliationManagerReadOnly.getAffiliations(anyString())).thenAnswer(new Answer<List<Affiliation>>() {

            @Override
            public List<Affiliation> answer(InvocationOnMock invocation) throws Throwable {
                List<Affiliation> affs = new ArrayList<Affiliation>();

                FuzzyDate startDate = new FuzzyDate(new Year(2018), new Month(1), new Day(1));
                FuzzyDate endDate = new FuzzyDate(new Year(2018), new Month(12), new Day(31));

                Distinction d = new Distinction();
                d.setDepartmentName("distinction");
                d.setEndDate(endDate);
                d.setStartDate(startDate);
                d.setPutCode(1L);
                setOrg(d);
                affs.add(d);

                Education e = new Education();
                e.setDepartmentName("education");
                e.setEndDate(endDate);
                e.setStartDate(startDate);
                e.setPutCode(2L);
                setOrg(e);
                affs.add(e);

                Employment emp = new Employment();
                emp.setDepartmentName("employment");
                emp.setEndDate(endDate);
                emp.setStartDate(startDate);
                emp.setPutCode(3L);
                setOrg(emp);
                affs.add(emp);

                InvitedPosition i = new InvitedPosition();
                i.setDepartmentName("invited position");
                i.setEndDate(endDate);
                i.setStartDate(startDate);
                i.setPutCode(4L);
                setOrg(i);
                affs.add(i);

                Membership m = new Membership();
                m.setDepartmentName("membership");
                m.setEndDate(endDate);
                m.setStartDate(startDate);
                m.setPutCode(5L);
                setOrg(m);
                affs.add(m);

                Qualification q = new Qualification();
                q.setDepartmentName("qualification");
                q.setEndDate(endDate);
                q.setStartDate(startDate);
                q.setPutCode(6L);
                setOrg(q);
                affs.add(q);

                Service s = new Service();
                s.setDepartmentName("service");
                s.setEndDate(endDate);
                s.setStartDate(startDate);
                s.setPutCode(7L);
                setOrg(s);
                affs.add(s);

                return affs;
            }

        });

        when(mockProfileFundingManagerReadOnly.getFundingList(anyString())).thenAnswer(new Answer<List<Funding>>() {

            @Override
            public List<Funding> answer(InvocationOnMock invocation) throws Throwable {
                List<Funding> fundings = new ArrayList<Funding>();
                Funding f = new Funding();
                Amount a = new Amount();
                a.setContent("1000");
                a.setCurrencyCode("$");
                f.setAmount(a);
                FundingTitle t = new FundingTitle();
                t.setTitle(new Title("title"));
                f.setTitle(t);
                setOrg(f);
                f.setPutCode(1L);
                fundings.add(f);
                return fundings;
            }
        });

        when(mockPeerReviewManagerReadOnly.findPeerReviews(anyString())).thenAnswer(new Answer<List<PeerReview>>() {

            @Override
            public List<PeerReview> answer(InvocationOnMock invocation) throws Throwable {
                List<PeerReview> peerReviews = new ArrayList<PeerReview>();
                PeerReview p = new PeerReview();
                setOrg(p);
                p.setPutCode(1L);
                peerReviews.add(p);
                return peerReviews;
            }
        });

        when(mockWorkManagerReadOnly.findWorks(anyString(), any())).thenAnswer(new Answer<List<Work>>() {

            @Override
            public List<Work> answer(InvocationOnMock invocation) throws Throwable {
                List<Work> works = new ArrayList<Work>();
                Work w = new Work();
                WorkTitle t = new WorkTitle();
                t.setTitle(new Title("title"));
                w.setPutCode(1L);
                works.add(w);
                return works;
            }

        });

        when(mockWorkManagerReadOnly.getLastModified(anyString())).thenReturn(0L);

        when(mockWorkEntityCacheManager.retrieveWorkLastModifiedList(anyString(), anyLong())).thenAnswer(new Answer<List<WorkLastModifiedEntity>>() {

            @Override
            public List<WorkLastModifiedEntity> answer(InvocationOnMock invocation) throws Throwable {
                List<WorkLastModifiedEntity> works = new ArrayList<WorkLastModifiedEntity>();
                WorkLastModifiedEntity w = new WorkLastModifiedEntity();
                w.setId(1L);
                w.setOrcid(ORCID);
                w.setLastModified(new Date());
                works.add(w);
                return works;
            }

        });

    }

    @Test
    public void testDownload() throws JAXBException, IOException {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        MockHttpServletResponse response = new MockHttpServletResponse();
        getMyDataController.getMyData(response);
        byte[] content = response.getContentAsByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(content);
        ZipInputStream zip = new ZipInputStream(is);
        ZipEntry zipEntry = zip.getNextEntry();

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Person.class, Distinction.class, Education.class, Employment.class, InvitedPosition.class, Membership.class,
                Qualification.class, Service.class, Funding.class, PeerReview.class, Work.class);
        Unmarshaller u = jaxbContext1.createUnmarshaller();

        boolean personFound = false;
        boolean distinctionFound = false;
        boolean educationFound = false;
        boolean employmentFound = false;
        boolean invitedPositionFound = false;
        boolean membershipFound = false;
        boolean qualificationFound = false;
        boolean serviceFound = false;
        boolean fundingFound = false;
        boolean peerReviewFound = false;
        boolean workFound = false;

        while (zipEntry != null) {
            String fileName = zipEntry.getName();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            while ((len = zip.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

            if (fileName.equals("person.xml")) {
                Person x = (Person) u.unmarshal(in);
                assertNotNull(x);
                assertEquals("Biography", x.getBiography().getContent());
                assertEquals(Visibility.LIMITED, x.getBiography().getVisibility());
                personFound = true;
            } else if (fileName.startsWith("affiliations/distinctions")) {
                assertEquals("affiliations/distinctions/1.xml", fileName);
                Distinction x = (Distinction) u.unmarshal(in);
                assertNotNull(x);
                assertEquals("distinction", x.getDepartmentName());
                assertEquals(Long.valueOf(1), x.getPutCode());
                validateOrg(x);
                distinctionFound = true;
            } else if (fileName.startsWith("affiliations/educations")) {
                assertEquals("affiliations/educations/2.xml", fileName);
                Education x = (Education) u.unmarshal(in);
                assertNotNull(x);
                assertEquals("education", x.getDepartmentName());
                assertEquals(Long.valueOf(2), x.getPutCode());
                validateOrg(x);
                educationFound = true;
            } else if (fileName.startsWith("affiliations/employments")) {
                assertEquals("affiliations/employments/3.xml", fileName);
                Employment x = (Employment) u.unmarshal(in);
                assertNotNull(x);
                assertEquals("employment", x.getDepartmentName());
                assertEquals(Long.valueOf(3), x.getPutCode());
                validateOrg(x);
                employmentFound = true;
            } else if (fileName.startsWith("affiliations/invited_positions")) {
                assertEquals("affiliations/invited_positions/4.xml", fileName);
                InvitedPosition x = (InvitedPosition) u.unmarshal(in);
                assertNotNull(x);
                assertEquals("invited position", x.getDepartmentName());
                assertEquals(Long.valueOf(4), x.getPutCode());
                validateOrg(x);
                invitedPositionFound = true;
            } else if (fileName.startsWith("affiliations/memberships")) {
                assertEquals("affiliations/memberships/5.xml", fileName);
                Membership x = (Membership) u.unmarshal(in);
                assertNotNull(x);
                assertEquals("membership", x.getDepartmentName());
                assertEquals(Long.valueOf(5), x.getPutCode());
                validateOrg(x);
                membershipFound = true;
            } else if (fileName.startsWith("affiliations/qualifications")) {
                assertEquals("affiliations/qualifications/6.xml", fileName);
                Qualification x = (Qualification) u.unmarshal(in);
                assertNotNull(x);
                assertEquals("qualification", x.getDepartmentName());
                assertEquals(Long.valueOf(6), x.getPutCode());
                validateOrg(x);
                qualificationFound = true;
            } else if (fileName.startsWith("affiliations/services")) {
                assertEquals("affiliations/services/7.xml", fileName);
                Service x = (Service) u.unmarshal(in);
                assertNotNull(x);
                assertEquals("service", x.getDepartmentName());
                assertEquals(Long.valueOf(7), x.getPutCode());
                validateOrg(x);
                serviceFound = true;
            } else if (fileName.startsWith("fundings")) {
                assertEquals("fundings/1.xml", fileName);
                Funding x = (Funding) u.unmarshal(in);
                assertNotNull(x);
                assertEquals("title", x.getTitle().getTitle().getContent());
                assertEquals("1000", x.getAmount().getContent());
                assertEquals("$", x.getAmount().getCurrencyCode());
                assertEquals(Long.valueOf(1), x.getPutCode());
                validateOrg(x);
                fundingFound = true;
            } else if (fileName.startsWith("peer_reviews")) {
                assertEquals("peer_reviews/1.xml", fileName);
                PeerReview x = (PeerReview) u.unmarshal(in);
                assertNotNull(x);
                validateOrg(x);
                assertEquals(Long.valueOf(1), x.getPutCode());
                peerReviewFound = true;
            } else if (fileName.startsWith("works")) {
                assertEquals("works/1.xml", fileName);
                Work x = (Work) u.unmarshal(in);
                assertNotNull(x);
                assertEquals(Long.valueOf(1), x.getPutCode());
                workFound = true;
            }

            zipEntry = zip.getNextEntry();
        }

        assertTrue(personFound);
        assertTrue(distinctionFound);
        assertTrue(educationFound);
        assertTrue(employmentFound);
        assertTrue(invitedPositionFound);
        assertTrue(membershipFound);
        assertTrue(qualificationFound);
        assertTrue(serviceFound);
        assertTrue(fundingFound);
        assertTrue(peerReviewFound);
        assertTrue(workFound);

        zip.closeEntry();
        zip.close();
    }

    private void setOrg(OrganizationHolder oh) {
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);

        Organization org = new Organization();
        org.setName("Organization");
        org.setAddress(address);

        oh.setOrganization(org);
    }

    private void validateOrg(OrganizationHolder oh) {
        assertNotNull(oh.getOrganization());
        assertEquals("Organization", oh.getOrganization().getName());
        assertEquals("city", oh.getOrganization().getAddress().getCity());
        assertEquals(Iso3166Country.US, oh.getOrganization().getAddress().getCountry());
    }

    private Authentication getAuthentication() {
        List<OrcidWebRole> roles = Arrays.asList(OrcidWebRole.ROLE_USER);
        OrcidProfileUserDetails details = new OrcidProfileUserDetails(ORCID, "user_1@test.orcid.org", null, roles);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(ORCID, null, roles);
        auth.setDetails(details);
        return auth;
    }
}
