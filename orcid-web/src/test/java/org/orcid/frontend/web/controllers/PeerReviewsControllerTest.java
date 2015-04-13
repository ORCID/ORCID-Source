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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PeerReviewForm;
import org.orcid.pojo.ajaxForm.PeerReviewSubjectForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitle;
import org.orcid.pojo.ajaxForm.WorkExternalIdentifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PeerReviewsControllerTest extends BaseControllerTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrgsEntityData.xml",
            "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewSubjectEntityData.xml", "/data/PeerReviewEntityData.xml");

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Resource
    protected PeerReviewsController peerReviewsController;

    @Mock
    private HttpServletRequest servletRequest;

    @Override
    protected Authentication getAuthentication() {
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4446");

        OrcidProfileUserDetails details = null;
        if (orcidProfile.getType() != null) {
            details = new OrcidProfileUserDetails(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getContactDetails().getEmail().get(0)
                    .getValue(), orcidProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent(), orcidProfile.getType(),
                    orcidProfile.getGroupType());
        } else {
            details = new OrcidProfileUserDetails(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getContactDetails().getEmail().get(0)
                    .getValue(), orcidProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent());
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details, "4444-4444-4444-4446", Arrays.asList(OrcidWebRole.ROLE_USER));
        return auth;
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
        assertNotNull(ids);

        assertEquals(1, ids.size());
        assertEquals("1", ids.get(0));
        PeerReviewForm peerReview = peerReviewsController.getPeerReviewJson("1");
        assertNotNull(peerReview);
        assertEquals("http://peer_review.com", peerReview.getUrl().getValue());
    }

    @Test
    public void testAddPeerReview() {
        PeerReviewForm form = getForm();
        try {
            PeerReviewForm newForm = peerReviewsController.postPeerReview(form);
            assertNotNull(newForm);
            assertFalse(PojoUtil.isEmpty(newForm.getPutCode()));

            String putCode = newForm.getPutCode().getValue();
            newForm = peerReviewsController.getPeerReviewJson(putCode);

            assertEquals(newForm.getCity(), form.getCity());
            assertEquals(newForm.getRegion(), form.getRegion());
            assertEquals(newForm.getCountry(), form.getCountry());
            assertEquals(newForm.getOrgName(), form.getOrgName());
            assertEquals(newForm.getCompletionDate(), form.getCompletionDate());
            assertEquals(newForm.getExternalIdentifiers(), form.getExternalIdentifiers());
            assertEquals(newForm.getRole(), form.getRole());
            assertEquals(newForm.getSubjectForm(), form.getSubjectForm());
            assertEquals(newForm.getType(), form.getType());
            assertEquals(newForm.getUrl(), form.getUrl());
            assertEquals(newForm.getVisibility(), form.getVisibility());
        } catch (NullPointerException npe) {
            fail();

        }
    }

    @Test
    public void testValidatePeerReviewFields() {
        PeerReviewForm form = peerReviewsController.getEmptyPeerReview();
        form = peerReviewsController.postPeerReview(form);
        assertNotNull(form);
        assertNotNull(form.getErrors());
        assertEquals(form.getErrors().size(), 5);
        assertTrue(form.getErrors().contains(peerReviewsController.getMessage("org.name.not_blank")));
        assertTrue(form.getErrors().contains(peerReviewsController.getMessage("org.city.not_blank")));
        assertTrue(form.getErrors().contains(peerReviewsController.getMessage("common.country.not_blank")));
        assertTrue(form.getErrors().contains(peerReviewsController.getMessage("peer_review.subject.work_type.not_blank")));
        assertTrue(form.getErrors().contains(peerReviewsController.getMessage("common.title.not_blank")));
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
        peerReviewsController.deletePeerReviewJson(servletRequest, newForm);
        PeerReviewForm deleted = peerReviewsController.getPeerReviewJson(putCode);
        assertNull(deleted);
    }

    private PeerReviewForm getForm() {
        PeerReviewForm form = new PeerReviewForm();
        form.setCity(Text.valueOf("The City"));
        form.setCountry(Text.valueOf("CR"));
        form.setOrgName(Text.valueOf("OrgName"));
        form.setRegion(Text.valueOf("The Region"));
        form.setRole(Text.valueOf("REVIEWER"));
        form.setType(Text.valueOf("EVALUATION"));
        form.setUrl(Text.valueOf("http://url.com"));
        form.setVisibility(Visibility.LIMITED);

        Date completionDate = new Date();
        completionDate.setDay("01");
        completionDate.setMonth("01");
        completionDate.setYear("2015");
        form.setCompletionDate(completionDate);

        WorkExternalIdentifier wei = new WorkExternalIdentifier();
        wei.setWorkExternalIdentifierId(Text.valueOf("extId1"));
        wei.setWorkExternalIdentifierType(Text.valueOf("bibcode"));
        List<WorkExternalIdentifier> extIds = new ArrayList<WorkExternalIdentifier>();
        extIds.add(wei);
        form.setExternalIdentifiers(extIds);

        PeerReviewSubjectForm subjectForm = new PeerReviewSubjectForm();
        subjectForm.setJournalTitle(Text.valueOf("Journal Title"));
        subjectForm.setSubtitle(Text.valueOf("Subtitle"));
        subjectForm.setTitle(Text.valueOf("Title"));

        TranslatedTitle translated = new TranslatedTitle();
        translated.setContent("Translated title");
        translated.setLanguageCode("es");
        subjectForm.setTranslatedTitle(translated);
        subjectForm.setUrl(Text.valueOf("http://subject.com"));
        subjectForm.setWorkExternalIdentifiers(extIds);
        subjectForm.setWorkType(Text.valueOf("book-review"));
        form.setSubjectForm(subjectForm);
        return form;
    }
}
