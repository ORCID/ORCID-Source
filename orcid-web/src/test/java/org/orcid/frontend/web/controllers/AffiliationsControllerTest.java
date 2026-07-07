package org.orcid.frontend.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.CountryManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.AffiliationsManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.jaxb.model.v3.release.record.Affiliation;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.ajaxForm.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AffiliationsControllerTest {

    private static final String ORCID = "0000-0000-0000-0001";

    @Mock
    private OrgDisambiguatedManager orgDisambiguatedManager;

    @Mock
    private AffiliationsManager affiliationsManager;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    @Mock
    private EncryptionManager encryptionManager;

    @Mock
    private CountryManager countryManager;

    @Mock
    private ProfileEntityManager profileEntityManager;

    @Mock
    private VisibilityFilter visibilityFilter;

    @Mock
    private LocaleManager localeManager;

    @Mock
    private EmailManagerReadOnly emailManagerReadOnly;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AffiliationsController affiliationsController = new AffiliationsController() {
        @Override
        public String getCurrentUserOrcid() {
            return ORCID;
        }

        @Override
        public String getEffectiveUserOrcid() {
            return ORCID;
        }

        @Override
        public String getMessage(String messageCode, Object... messageParams) {
            return messageCode;
        }

        @Override
        public void validateUrl(Text url) {
            // No-op for testing
        }

        @Override
        public boolean validDate(org.orcid.pojo.ajaxForm.Date date) {
            return true;
        }

        @Override
        public void setError(org.orcid.pojo.ajaxForm.ErrorsInterface ei, String msg) {
            ei.getErrors().add(msg);
        }
    };

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(request.getSession()).thenReturn(session);
        when(request.getSession(anyBoolean())).thenReturn(session);
    }

    @Test
    public void removeAffiliationJsonDeprecatedTest() {
        AffiliationForm form = new AffiliationForm();
        form.setPutCode(Text.valueOf("123"));
        AffiliationForm result = affiliationsController.removeAffiliationJson(request, form);
        assertEquals(form, result);
        verify(affiliationsManager).removeAffiliation(eq(ORCID), eq(123L));
    }

    @Test
    public void removeAffiliationJsonTest() {
        Errors result = affiliationsController.removeAffiliationJson("123");
        assertNotNull(result);
        verify(affiliationsManager).removeAffiliation(eq(ORCID), eq(123L));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getAffiliationJsonTest() {
        HashMap<String, AffiliationForm> affiliationsMap = new HashMap<>();
        AffiliationForm form = new AffiliationForm();
        form.setPutCode(Text.valueOf("123"));
        affiliationsMap.put("123", form);
        when(session.getAttribute("AFFILIATIONS_MAP")).thenReturn(affiliationsMap);

        List<AffiliationForm> result = affiliationsController.getAffiliationJson(request, "123");
        assertEquals(1, result.size());
        assertEquals(form, result.get(0));
    }

    @Test
    public void getAffiliationTest() {
        ProfileEntity profile = new ProfileEntity();
        profile.setActivitiesVisibilityDefault("LIMITED");
        when(profileEntityCacheManager.retrieve(eq(ORCID))).thenReturn(profile);

        AffiliationForm result = affiliationsController.getAffiliation(request);
        assertNotNull(result);
        assertEquals("limited", result.getVisibility().getVisibility().value());
        assertTrue(result.getAffiliationName().isRequired());
        assertTrue(result.getCountry().isRequired());
    }

    @Test
    public void getAffiliationDetailsTest() {
        Employment emp = createMockAffiliation(123L);
        when(affiliationsManager.getEmploymentAffiliation(eq(ORCID), eq(123L))).thenReturn(emp);

        AffiliationForm result = affiliationsController.getAffiliationDetails(123L, "employment");
        assertNotNull(result);
        assertEquals("123", result.getPutCode().getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAffiliationDetails_invalidTypeTest() {
        affiliationsController.getAffiliationDetails(123L, "invalid");
    }

    @Test
    public void postAffiliation_addTest() throws Exception {
        org.orcid.pojo.ajaxForm.AffiliationForm form = createValidForm();
        form.setPutCode(null);

        Employment emp = createMockAffiliation(456L);
        when(affiliationsManager.createEmploymentAffiliation(eq(ORCID), any(Employment.class), eq(false))).thenReturn(emp);

        org.orcid.pojo.ajaxForm.AffiliationForm result = affiliationsController.postAffiliation(request, form);
        assertEquals("456", result.getPutCode().getValue());
        verify(affiliationsManager).createEmploymentAffiliation(eq(ORCID), any(Employment.class), eq(false));
    }

    @Test
    public void postAffiliation_editTest() throws Exception {
        org.orcid.pojo.ajaxForm.AffiliationForm form = createValidForm();
        form.setPutCode(Text.valueOf("123"));
        form.setSource(ORCID);

        Employment emp = createMockAffiliation(123L);
        when(affiliationsManager.updateEmploymentAffiliation(eq(ORCID), any(Employment.class), eq(false))).thenReturn(emp);

        org.orcid.pojo.ajaxForm.AffiliationForm result = affiliationsController.postAffiliation(request, form);
        assertEquals("123", result.getPutCode().getValue());
        verify(affiliationsManager).updateEmploymentAffiliation(eq(ORCID), any(Employment.class), eq(false));
    }

    @Test
    public void postAffiliation_validationErrorTest() throws Exception {
        AffiliationForm form = createValidForm();
        form.getAffiliationName().setValue(""); // Invalid

        AffiliationForm result = affiliationsController.postAffiliation(request, form);
        assertFalse(result.getErrors().isEmpty());
        verify(affiliationsManager, never()).createEmploymentAffiliation(any(), any(), anyBoolean());
    }

    @Test
    public void getAffiliationsJsonTest() {
        List<Affiliation> affiliations = new ArrayList<>();
        Employment emp = createMockAffiliation(123L);
        affiliations.add(emp);
        when(affiliationsManager.getAffiliations(eq(ORCID))).thenReturn(affiliations);

        List<String> result = affiliationsController.getAffiliationsJson(request);
        assertEquals(1, result.size());
        assertEquals("123", result.get(0));
        verify(session).setAttribute(eq("AFFILIATIONS_MAP"), anyMap());
    }

    @Test
    public void getEmploymentSummaryListTest() {
        List<EmploymentSummary> list = new ArrayList<>();
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(eq(ORCID))).thenReturn(list);
        when(affiliationsManagerReadOnly.groupAffiliations(anyList(), anyBoolean())).thenReturn(new ArrayList<>());

        Employments result = affiliationsController.getEmploymentSummaryList();
        assertNotNull(result);
    }

    @Test
    public void updateAffiliationVisibilityTest() {
        AffiliationForm form = new AffiliationForm();
        form.setPutCode(Text.valueOf("123"));
        Visibility v = new Visibility();
        v.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC);
        form.setVisibility(v);

        AffiliationForm result = affiliationsController.updateAffiliationVisibility(request, form);
        assertEquals(form, result);
        verify(affiliationsManager).updateVisibility(eq(ORCID), eq(123L), eq(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC));
    }

    @Test
    public void updateAffiliationVisibilitiesTest() {
        ArrayList<Long> result = affiliationsController.updateAffiliationVisibilities("123,456", "LIMITED");
        assertEquals(2, result.size());
        assertTrue(result.contains(123L));
        assertTrue(result.contains(456L));
        verify(affiliationsManager).updateVisibilities(eq(ORCID), eq(result), eq(org.orcid.jaxb.model.v3.release.common.Visibility.LIMITED));
    }

    @Test
    public void searchDisambiguatedTest() {
        OrgDisambiguated org = new OrgDisambiguated();
        org.setValue("Test Org");
        when(orgDisambiguatedManager.searchOrgsFromSolr(anyString(), anyInt(), anyInt(), anyBoolean())).thenReturn(Collections.singletonList(org));

        List<Map<String, String>> result = affiliationsController.searchDisambiguated("test", 10);
        assertEquals(1, result.size());
        assertEquals("Test Org", result.get(0).get("value"));
    }

    @Test
    public void getDisambiguatedTest() {
        OrgDisambiguated org = new OrgDisambiguated();
        org.setValue("Test Org");
        when(orgDisambiguatedManager.findInDB(eq(123L))).thenReturn(org);

        Map<String, String> result = affiliationsController.getDisambiguated(123L);
        assertEquals("Test Org", result.get("value"));
    }

    @Test
    public void validationMethodsTest() {
        AffiliationForm form = new AffiliationForm();
        form.setAffiliationName(Text.valueOf(""));
        affiliationsController.affiliationNameValidate(form);
        assertFalse(form.getAffiliationName().getErrors().isEmpty());

        form.setCity(Text.valueOf(""));
        affiliationsController.cityValidate(form);
        assertFalse(form.getCity().getErrors().isEmpty());

        form.setCountry(Text.valueOf(""));
        affiliationsController.countryValidate(form);
        assertFalse(form.getCountry().getErrors().isEmpty());
    }

    @Test
    public void getGroupedAffiliationsTest() {
        when(affiliationsManager.getGroupedAffiliations(eq(ORCID), anyBoolean())).thenReturn(new HashMap<>());
        when(affiliationsManagerReadOnly.getFeaturedFlag(eq(ORCID))).thenReturn(123L);

        AffiliationGroupContainer result = affiliationsController.getGroupedAffiliations();
        assertNotNull(result);
    }

    @Test
    public void updateToMaxDisplayTest() {
        when(affiliationsManager.updateToMaxDisplay(eq(ORCID), eq(123L))).thenReturn(true);
        assertTrue(affiliationsController.updateToMaxDisplay(123L));
    }

    @Test
    public void setFeaturedAffiliationTest() {
        Map<String, Long> payload = new HashMap<>();
        payload.put("putCode", 123L);
        when(affiliationsManager.setOnlyFeatured(eq(ORCID), eq(123L))).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = affiliationsController.setFeaturedAffiliation(payload);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().get("ok"));
    }

    @Test
    public void setFeaturedAffiliation_clearTest() {
        Map<String, Long> payload = new HashMap<>();
        payload.put("putCode", null);

        ResponseEntity<Map<String, Object>> response = affiliationsController.setFeaturedAffiliation(payload);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(affiliationsManager).clearFeatured(eq(ORCID));
    }

    private Employment createMockAffiliation(Long putCode) {
        Employment emp = new Employment();
        emp.setPutCode(putCode);
        emp.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.PRIVATE);
        org.orcid.jaxb.model.v3.release.common.CreatedDate createdDate = new org.orcid.jaxb.model.v3.release.common.CreatedDate();
        createdDate.setValue(org.orcid.utils.DateUtils.convertToXMLGregorianCalendar(new java.util.Date()));
        emp.setCreatedDate(createdDate);
        emp.setLastModifiedDate(new org.orcid.jaxb.model.v3.release.common.LastModifiedDate(createdDate.getValue()));
        org.orcid.jaxb.model.v3.release.common.Organization org = new org.orcid.jaxb.model.v3.release.common.Organization();
        org.setName("Org");
        org.setAddress(new org.orcid.jaxb.model.v3.release.common.OrganizationAddress());
        org.getAddress().setCity("City");
        emp.setOrganization(org);
        return emp;
    }

    private org.orcid.pojo.ajaxForm.AffiliationForm createValidForm() {
        org.orcid.pojo.ajaxForm.AffiliationForm form = new org.orcid.pojo.ajaxForm.AffiliationForm();
        form.setAffiliationName(Text.valueOf("Org"));
        form.setCity(Text.valueOf("City"));
        form.setRegion(Text.valueOf("Region"));
        form.setCountry(Text.valueOf("US"));
        form.setAffiliationType(Text.valueOf("employment"));
        form.setStartDate(new org.orcid.pojo.ajaxForm.Date());
        form.setEndDate(new org.orcid.pojo.ajaxForm.Date());
        form.setUrl(Text.valueOf("http://test.com"));
        form.setRoleTitle(Text.valueOf("Role"));
        form.setDepartmentName(Text.valueOf("Dept"));
        return form;
    }
}
