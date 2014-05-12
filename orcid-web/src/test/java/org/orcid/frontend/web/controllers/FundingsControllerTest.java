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
package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.ajaxForm.Text;
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
public class FundingsControllerTest extends BaseControllerTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml",
            "/data/WorksEntityData.xml", "/data/ProfileWorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/WebhookEntityData.xml");

    @Resource
    FundingsController fundingController;

    @Resource
    protected OrcidProfileManager orcidProfileManager;
    
    @Override
    protected Authentication getAuthentication() {
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4443");

        OrcidProfileUserDetails details = null;
        if(orcidProfile.getType() != null){             
                details = new OrcidProfileUserDetails(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getContactDetails().getEmail()
                    .get(0).getValue(), orcidProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent(), orcidProfile.getType(), orcidProfile.getClientType(), orcidProfile.getGroupType());
        } else {
                details = new OrcidProfileUserDetails(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getContactDetails().getEmail()
                    .get(0).getValue(), orcidProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent());
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details, "4444-4444-4444-4443", Arrays.asList(OrcidWebRole.ROLE_USER));
        return auth;
    }
    
    @Before
    public void init() {
        assertNotNull(fundingController);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES, null);
    }
    
    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Lists.reverse(DATA_FILES), null);
    }
    
    @Test
    public void testValidateAmount() {
        String validAmounts[] = { "1", "10", "100", "1000", "10000", "100000", "1000000", "10000000", "100000000", "1000000000", "1.0", "1.00", "1,0", "1,00", "10.0",
                "10,0", "10.00", "10,00", "100.0", "100.00", "100,0", "100,00", "1000.0", "1000.00", "1000,0", "1000,00", "10000.0", "10000.00", "10000,0", "10000,00",
                "1.000", "1,000", "1,000.0", "1,000.00", "10,000", "10,000.0", "10,000.00", "100,000", "100,000.0", "100,000.00", "1,000,000", "1,000,000.0", "1,000,000.00", "10,000,000",
                "10,000,000.0", "10,000,000.00", "100,000,000", "100,000,000.0", "100,000,000.00", "12345678901234567890", "1.000.000", "10.000.000", "100.000.000", "1.000.000.000", "1,650.00", "1,650,000" };

        String invalidAmounts[] = {"a", "1a", "1000a", "1234567890a", "0.", ".0", "1 000", "18 000", "180 000 000", "100,000:00", "1,000.000", "10,000.000", "100,000.000", "1,000.000.000", "10,10,00", "1234,123,123"};
        
        for (String amount : validAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertNotNull(form.getAmount().getErrors());
            assertEquals(form.getAmount().getErrors().size(), 0);
        }

        for (String amount : invalidAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertEquals(form.getAmount().getErrors().size(), 1);
            assertEquals(form.getAmount().getErrors().get(0), "Amount should be a numeric value");
        }
    }
    
    @Test
    public void testGetFundings() {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);     
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        
        List<String> fundingIds = fundingController.getFundingsJson(servletRequest);
        assertNotNull(fundingIds);
        assertEquals(fundingIds.size(), 3);
                
        assertTrue(fundingIds.contains("1"));
        assertTrue(fundingIds.contains("2"));
        assertTrue(fundingIds.contains("3"));
    }

}
