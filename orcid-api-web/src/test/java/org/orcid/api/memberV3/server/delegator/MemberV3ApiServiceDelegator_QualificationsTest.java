package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_QualificationsTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewQualificationsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewQualifications(ORCID);
    }

    @Test
    public void testViewQualificationReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Qualification qualification = new Qualification();
        qualification.setPutCode(27L);
        when(affiliationsManagerReadOnly.getQualificationAffiliation(eq(ORCID), eq(27L))).thenReturn(qualification);
        Response r = serviceDelegator.viewQualification(ORCID, 27L);
        Qualification element = (Qualification) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/qualification/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewQualificationsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        List<QualificationSummary> summaries = new ArrayList<>();
        when(affiliationsManagerReadOnly.getQualificationSummaryList(eq(ORCID))).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(eq(summaries), anyBoolean())).thenReturn(new ArrayList<>());
        
        Response r = serviceDelegator.viewQualifications(ORCID);
        Qualifications element = (Qualifications) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/qualifications", element.getPath());
    }

    @Test
    public void testViewPublicQualification() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Qualification qualification = createQualification(27L, Visibility.PUBLIC, ORCID, null);
        qualification.setDepartmentName("PUBLIC Department");
        when(affiliationsManagerReadOnly.getQualificationAffiliation(eq(ORCID), eq(27L))).thenReturn(qualification);
        Response response = serviceDelegator.viewQualification(ORCID, 27L);
        assertNotNull(response);
        Qualification result = (Qualification) response.getEntity();
        assertNotNull(result);
        assertEquals(Long.valueOf(27L), result.getPutCode());
        assertEquals("/0000-0000-0000-0003/qualification/27", result.getPath());
        assertEquals("PUBLIC Department", result.getDepartmentName());
        assertEquals(Visibility.PUBLIC, result.getVisibility());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateQualificationWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        when(affiliationsManagerReadOnly.getQualificationAffiliation(eq(ORCID), eq(31L))).thenReturn(createQualification(31L));
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Qualification.class), any(ScopePathType.class));
        serviceDelegator.viewQualification(ORCID, 31L);
        fail();
    }

    @Test
    public void testAddQualification() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        ActivitiesSummary summary = new ActivitiesSummary();
        summary.setQualifications(new Qualifications());
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(ORCID), anyBoolean())).thenReturn(summary);
        
        Qualification saved = createQualification(12345L);
        saved.setDepartmentName("My department name");
        when(affiliationsManager.createQualificationAffiliation(eq(ORCID), any(Qualification.class), anyBoolean())).thenReturn(saved);
        when(apiUtils.buildApiResponse(any(), any(), any(), any())).thenReturn(Response.status(Response.Status.CREATED).build());
        
        Response response = serviceDelegator.createQualification(ORCID, createQualification(null));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    private Qualification createQualification(Long putCode) {
        return createQualification(putCode, Visibility.PUBLIC, null, null);
    }

    private Qualification createQualification(Long putCode, Visibility visibility, String sourceOrcid, String sourceClientId) {
        Qualification d = new Qualification();
        d.setPutCode(putCode);
        d.setVisibility(visibility);
        if (sourceOrcid != null || sourceClientId != null) {
            Source s = new Source();
            if (sourceOrcid != null) s.setSourceOrcid(new SourceOrcid(sourceOrcid));
            if (sourceClientId != null) s.setSourceClientId(new SourceClientId(sourceClientId));
            d.setSource(s);
        }
        return d;
    }
}
