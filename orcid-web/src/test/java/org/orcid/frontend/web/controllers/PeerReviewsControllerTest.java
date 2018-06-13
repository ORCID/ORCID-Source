package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PeerReviewForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitleForm;
import org.orcid.pojo.ajaxForm.Visibility;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class PeerReviewsControllerTest extends BaseControllerTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrgsEntityData.xml",
            "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml");

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Resource
    protected PeerReviewsController peerReviewsController;
    
    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;

    @Mock
    private HttpServletRequest servletRequest;

    @Override
    protected Authentication getAuthentication() {
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4446");
        OrcidProfileUserDetails details = (OrcidProfileUserDetails) orcidUserDetailsService.loadUserByUsername(orcidProfile.retrieveOrcidPath());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("4444-4444-4444-4446", null, Arrays.asList(OrcidWebRole.ROLE_USER));
        auth.setDetails(details);
        return auth;
    }

    @Before
    public void init() {
        orcidProfileManager.updateLastModifiedDate("4444-4444-4444-4446");
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Lists.reverse(DATA_FILES));
    }

    @Test
    public void testGetPeerReview() {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        List<String> ids = peerReviewsController.getPeerReviewIdsJson(servletRequest);
        List<String> existingIds = new ArrayList<String>();
        existingIds.add("1");
        existingIds.add("3");
        existingIds.add("4");
        existingIds.add("5");

        assertNotNull(ids);

        assertTrue(ids.containsAll(existingIds));
    }

    @Test
    public void testAddPeerReview() {
        PeerReviewForm form = getForm();
        try {
            PeerReviewForm newForm = peerReviewsController.postPeerReview(form);
            assertNotNull(newForm);
            assertFalse(PojoUtil.isEmpty(newForm.getPutCode()));

            String putCode = newForm.getPutCode().getValue();
            newForm = peerReviewsController.getPeerReviewJson(Long.valueOf(putCode));

            assertEquals(form.getCity(), newForm.getCity());
            assertEquals(form.getRegion(), newForm.getRegion());
            assertEquals(form.getCountry(), newForm.getCountry());
            assertEquals(form.getOrgName(), newForm.getOrgName());
            assertEquals(form.getCompletionDate(), newForm.getCompletionDate());
            assertEquals(form.getExternalIdentifiers(), newForm.getExternalIdentifiers());
            assertEquals(form.getRole(), newForm.getRole());
            assertEquals(form.getType(), newForm.getType());
            assertEquals(form.getUrl(), newForm.getUrl());
            assertEquals(form.getVisibility(), newForm.getVisibility());
            assertEquals(form.getGroupId(), newForm.getGroupId());
            assertEquals(form.getSubjectContainerName(), newForm.getSubjectContainerName());
            assertEquals(form.getSubjectExternalIdentifier(), newForm.getSubjectExternalIdentifier());
            assertEquals(form.getSubjectName(), newForm.getSubjectName());
            assertEquals(form.getSubjectType(), newForm.getSubjectType());
            assertEquals(form.getSubjectUrl(), newForm.getSubjectUrl());
        } catch (NullPointerException npe) {
            fail();

        }
    }

    @Test
    public void testValidatePeerReviewFields() {
        PeerReviewForm form = peerReviewsController.getEmptyPeerReview();
        form.getGroupId().setValue("bad-group-id");
        form = peerReviewsController.postPeerReview(form);
        assertNotNull(form);
        assertNotNull(form.getErrors());
        assertEquals(6, form.getErrors().size());
        assertTrue(form.getErrors().contains(peerReviewsController.getMessage("org.name.not_blank")));
        assertTrue(form.getErrors().contains(peerReviewsController.getMessage("org.city.not_blank")));
        assertTrue(form.getErrors().contains(peerReviewsController.getMessage("common.country.not_blank")));
        assertTrue(form.getErrors().contains(peerReviewsController.getMessage("peer_review.subject.work_type.not_blank")));
        assertTrue(form.getErrors().contains(peerReviewsController.getMessage("common.title.not_blank")));
        assertTrue(form.getErrors().contains(peerReviewsController.getMessage("peer_review.group_id.not_valid")));
    }

    @Test
    public void testAddWithInvalidGroupId() {
        PeerReviewForm form = getForm();
        form.getGroupId().setValue("bad-group-id");
        PeerReviewForm newForm = peerReviewsController.postPeerReview(form);
        assertNotNull(newForm);
        assertTrue(PojoUtil.isEmpty(newForm.getPutCode()));
        assertTrue(form.getErrors().contains(peerReviewsController.getMessage("peer_review.group_id.not_valid")));
    }

    @Test
    public void testDeletePeerReview() {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        PeerReviewForm form = getForm();
        PeerReviewForm newForm = peerReviewsController.postPeerReview(form);
        assertNotNull(newForm);
        assertFalse(PojoUtil.isEmpty(newForm.getPutCode()));

        String putCode = newForm.getPutCode().getValue();
        peerReviewsController.deletePeerReviewJson(newForm);
        try {
            peerReviewsController.getPeerReviewJson(Long.valueOf(putCode));
            fail();
        } catch (NoResultException nre) {

        }

    }

    private PeerReviewForm getForm() {
        PeerReviewForm form = new PeerReviewForm();
        form.setCity(Text.valueOf("The City"));
        form.setCountry(Text.valueOf("CR"));
        form.setOrgName(Text.valueOf("OrgName"));
        form.setRegion(Text.valueOf("The Region"));
        form.setRole(Text.valueOf("reviewer"));
        form.setType(Text.valueOf("evaluation"));
        form.setUrl(Text.valueOf("http://orcid.org"));
        form.setVisibility(Visibility.valueOf(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED));

        Date completionDate = new Date();
        completionDate.setDay("01");
        completionDate.setMonth("01");
        completionDate.setYear("2015");
        form.setCompletionDate(completionDate);

        ActivityExternalIdentifier wei = new ActivityExternalIdentifier();
        wei.setExternalIdentifierId(Text.valueOf("extId1"));
        wei.setExternalIdentifierType(Text.valueOf("bibcode"));
        wei.setRelationship(Text.valueOf(Relationship.SELF.value()));
        wei.setUrl(Text.valueOf("http://myurl.com"));
        List<ActivityExternalIdentifier> extIds = new ArrayList<ActivityExternalIdentifier>();
        extIds.add(wei);
        form.setExternalIdentifiers(extIds);
        form.setSubjectContainerName(Text.valueOf("Journal Title"));
        form.setSubjectName(Text.valueOf("Title"));
        TranslatedTitleForm translated = new TranslatedTitleForm();
        translated.setContent("Translated title");
        translated.setLanguageCode("es");
        form.setTranslatedSubjectName(translated);
        form.setSubjectUrl(Text.valueOf("http://subject.com"));
        form.setSubjectExternalIdentifier(wei);
        form.setSubjectType(Text.valueOf("book-review"));
        form.setGroupId(Text.valueOf("issn:0000001"));

        return form;
    }
}
