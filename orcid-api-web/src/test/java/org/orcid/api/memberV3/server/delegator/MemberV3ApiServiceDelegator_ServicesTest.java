package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc1.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc1.common.Url;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.Address;
import org.orcid.jaxb.model.v3.rc1.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc1.record.Distinction;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc1.record.Keyword;
import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.OtherName;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc1.record.Qualification;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc1.record.Service;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkBulk;
import org.orcid.jaxb.model.v3.rc1.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Services;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_ServicesTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USE THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegatorV3_0_rc1")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword> serviceDelegator;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewServicesWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewServices(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewServiceWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewService(ORCID, 47L);
    }

    @Test
    public void testViewServiceReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewService(ORCID, 47L);
        Service element = (Service) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/service/47", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewServiceSummaryWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewServiceSummary(ORCID, 47L);
    }

    @Test
    public void testViewServicesReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewServices(ORCID);
        Services element = (Services) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/services", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewServiceSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewServiceSummary(ORCID, 47L);
        ServiceSummary element = (ServiceSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/service/47", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPublicService() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(response);
        Service service = (Service) response.getEntity();
        assertNotNull(service);
        Utils.verifyLastModified(service.getLastModifiedDate());
        assertEquals(Long.valueOf(47L), service.getPutCode());
        assertEquals("/0000-0000-0000-0003/service/47", service.getPath());
        assertEquals("PUBLIC Department", service.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), service.getVisibility().value());
    }

    @Test
    public void testViewLimitedService() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewService(ORCID, 50L);
        assertNotNull(response);
        Service service = (Service) response.getEntity();
        assertNotNull(service);
        Utils.verifyLastModified(service.getLastModifiedDate());
        assertEquals(Long.valueOf(50L), service.getPutCode());
        assertEquals("/0000-0000-0000-0003/service/50", service.getPath());
        assertEquals("SELF LIMITED Department", service.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), service.getVisibility().value());
    }

    @Test
    public void testViewPrivateService() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewService(ORCID, 49L);
        assertNotNull(response);
        Service service = (Service) response.getEntity();
        assertNotNull(service);
        Utils.verifyLastModified(service.getLastModifiedDate());
        assertEquals(Long.valueOf(49L), service.getPutCode());
        assertEquals("/0000-0000-0000-0003/service/49", service.getPath());
        assertEquals("PRIVATE Department", service.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), service.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateServiceWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewService(ORCID, 51L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewServiceThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Service 47 belongs to 0000-0000-0000-0003
        serviceDelegator.viewService("4444-4444-4444-4446", 47L);
        fail();
    }

    @Test
    public void testViewServices() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewServices(ORCID);
        assertNotNull(r);
        Services services = (Services) r.getEntity();
        assertNotNull(services);
        assertEquals("/0000-0000-0000-0003/services", services.getPath());
        Utils.verifyLastModified(services.getLastModifiedDate());
        assertNotNull(services.getSummaries());
        assertEquals(4, services.getSummaries().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for (ServiceSummary summary : services.getSummaries()) {
            Utils.verifyLastModified(summary.getLastModifiedDate());
            if (Long.valueOf(47).equals(summary.getPutCode())) {
                assertEquals("PUBLIC Department", summary.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(48).equals(summary.getPutCode())) {
                assertEquals("LIMITED Department", summary.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(49).equals(summary.getPutCode())) {
                assertEquals("PRIVATE Department", summary.getDepartmentName());
                found3 = true;
            } else if (Long.valueOf(50).equals(summary.getPutCode())) {
                assertEquals("SELF LIMITED Department", summary.getDepartmentName());
                found4 = true;
            } else {
                fail("Invalid service found: " + summary.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
    }

    @Test
    public void testReadPublicScope_Services() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(r);
        assertEquals(Service.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewServiceSummary(ORCID, 47L);
        assertNotNull(r);
        assertEquals(ServiceSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewService(ORCID, 48L);
        serviceDelegator.viewServiceSummary(ORCID, 48L);
        
        // Private that am the source of should work
        serviceDelegator.viewService(ORCID, 49L);
        serviceDelegator.viewServiceSummary(ORCID, 49L);
        
        
        // Limited that am not the source of should fail
        try {
            serviceDelegator.viewService(ORCID, 50L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewServiceSummary(ORCID, 50L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private that am not the source of should fails
        try {
            serviceDelegator.viewService(ORCID, 50L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewServiceSummary(ORCID, 50L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddService() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary originalSummary = (ActivitiesSummary) response.getEntity();
        assertNotNull(originalSummary);
        Utils.verifyLastModified(originalSummary.getLastModifiedDate());
        assertNotNull(originalSummary.getServices());
        Utils.verifyLastModified(originalSummary.getServices().getLastModifiedDate());
        assertNotNull(originalSummary.getServices().getSummaries());
        assertNotNull(originalSummary.getServices().getSummaries().get(0));
        Utils.verifyLastModified(originalSummary.getServices().getSummaries().get(0).getLastModifiedDate());
        assertEquals(4, originalSummary.getServices().getSummaries().size());

        response = serviceDelegator.createService(ORCID, (Service) Utils.getAffiliation(AffiliationType.SERVICE));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary summaryWithNewElement = (ActivitiesSummary) response.getEntity();
        assertNotNull(summaryWithNewElement);
        Utils.verifyLastModified(summaryWithNewElement.getLastModifiedDate());
        assertNotNull(summaryWithNewElement.getServices());
        Utils.verifyLastModified(summaryWithNewElement.getServices().getLastModifiedDate());
        assertNotNull(summaryWithNewElement.getServices().getSummaries());
        assertEquals(5, summaryWithNewElement.getServices().getSummaries().size());
        
        boolean haveNew = false;

        for (ServiceSummary serviceSummary : summaryWithNewElement.getServices().getSummaries()) {
            assertNotNull(serviceSummary.getPutCode());
            Utils.verifyLastModified(serviceSummary.getLastModifiedDate());
            if (serviceSummary.getPutCode().equals(putCode)) {
                assertEquals("My department name", serviceSummary.getDepartmentName());
                haveNew = true;
            } else {
                assertTrue(originalSummary.getServices().getSummaries().contains(serviceSummary));
            }
        }
        
        assertTrue(haveNew);
        
        //Remove new element
        serviceDelegator.deleteAffiliation(ORCID, putCode);
    }
    
    @Test(expected = OrcidValidationException.class)
    public void testAddServiceNoStartDate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Service service = (Service) Utils.getAffiliation(AffiliationType.SERVICE);
        service.setStartDate(null);
        serviceDelegator.createService(ORCID, service);
    }
    
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddServicesDuplicateExternalIDs() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);

        ExternalID e1 = new ExternalID();
        e1.setRelationship(Relationship.SELF);
        e1.setType("erm");
        e1.setUrl(new Url("https://orcid.org"));
        e1.setValue("err");

        ExternalID e2 = new ExternalID();
        e2.setRelationship(Relationship.SELF);
        e2.setType("err");
        e2.setUrl(new Url("http://bbc.co.uk"));
        e2.setValue("erm");

        ExternalIDs externalIDs = new ExternalIDs();
        externalIDs.getExternalIdentifier().add(e1);
        externalIDs.getExternalIdentifier().add(e2);

        Service service = (Service) Utils.getAffiliation(AffiliationType.SERVICE);
        service.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createService(ORCID, service);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());

        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        try {
            Service duplicate = (Service) Utils.getAffiliation(AffiliationType.SERVICE);
            duplicate.setExternalIDs(externalIDs);
            serviceDelegator.createService(ORCID, duplicate);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode);
        }
    }


    @Test
    public void testUpdateService() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(response);
        Service service = (Service) response.getEntity();
        assertNotNull(service);
        assertEquals("PUBLIC Department", service.getDepartmentName());
        assertEquals("PUBLIC", service.getRoleTitle());
        Utils.verifyLastModified(service.getLastModifiedDate());

        LastModifiedDate before = service.getLastModifiedDate();

        service.setDepartmentName("Updated department name");
        service.setRoleTitle("The updated role title");
        
        // disambiguated org is required in API v3
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        service.getOrganization().setDisambiguatedOrganization(disambiguatedOrg);

        response = serviceDelegator.updateService(ORCID, 47L, service);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(response);
        service = (Service) response.getEntity();
        assertNotNull(service);
        Utils.verifyLastModified(service.getLastModifiedDate());
        assertTrue(service.getLastModifiedDate().after(before));
        assertEquals("Updated department name", service.getDepartmentName());
        assertEquals("The updated role title", service.getRoleTitle());

        // Rollback changes
        service.setDepartmentName("PUBLIC Department");
        service.setRoleTitle("PUBLIC");

        response = serviceDelegator.updateService(ORCID, 47L, service);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateServiceYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewService(ORCID, 50L);
        assertNotNull(response);
        Service service = (Service) response.getEntity();
        assertNotNull(service);
        service.setDepartmentName("Updated department name");
        service.setRoleTitle("The updated role title");
        serviceDelegator.updateService(ORCID, 50L, service);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateServiceChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(response);
        Service service = (Service) response.getEntity();
        assertNotNull(service);
        assertEquals(Visibility.PUBLIC, service.getVisibility());

        service.setVisibility(Visibility.PRIVATE);

        response = serviceDelegator.updateService(ORCID, 47L, service);
        fail();
    }

    @Test
    public void testUpdateServiceLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(response);
        Service service = (Service) response.getEntity();
        assertNotNull(service);
        assertEquals(Visibility.PUBLIC, service.getVisibility());
        
        service.setVisibility(null);

        response = serviceDelegator.updateService(ORCID, 47L, service);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        service = (Service) response.getEntity();
        assertNotNull(service);
        assertEquals(Visibility.PUBLIC, service.getVisibility());
    }
    
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateServiceDuplicateExternalIDs() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);

        ExternalID e1 = new ExternalID();
        e1.setRelationship(Relationship.SELF);
        e1.setType("erm");
        e1.setUrl(new Url("https://orcid.org"));
        e1.setValue("err");

        ExternalID e2 = new ExternalID();
        e2.setRelationship(Relationship.SELF);
        e2.setType("err");
        e2.setUrl(new Url("http://bbc.co.uk"));
        e2.setValue("erm");

        ExternalIDs externalIDs = new ExternalIDs();
        externalIDs.getExternalIdentifier().add(e1);
        externalIDs.getExternalIdentifier().add(e2);

        Service service = (Service) Utils.getAffiliation(AffiliationType.SERVICE);
        service.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createService(ORCID, service);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode1 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        Service another = (Service) Utils.getAffiliation(AffiliationType.SERVICE);
        response = serviceDelegator.createService(ORCID, another);
        
        map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        resultWithPutCode = (List<?>) map.get("Location");
        Long putCode2 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
        
        response = serviceDelegator.viewService(ORCID, putCode2);
        another = (Service) response.getEntity();
        another.setExternalIDs(externalIDs);
        
        try {
            serviceDelegator.updateService(ORCID, putCode2, another);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode1);
            serviceDelegator.deleteAffiliation(ORCID, putCode2);
        }
    }

    @Test
    public void testDeleteService() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0002", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewService("0000-0000-0000-0002", 1006L);
        assertNotNull(response);
        Service service = (Service) response.getEntity();
        assertNotNull(service);

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1006L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        try {
            serviceDelegator.viewService("0000-0000-0000-0002", 1006L);
            fail(); 
        } catch(NoResultException nre) {
            
        } catch(Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteServiceYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteAffiliation(ORCID, 50L);
        fail();
    }

}
