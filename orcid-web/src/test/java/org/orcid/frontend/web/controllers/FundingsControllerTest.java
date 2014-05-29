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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.orcid.core.locale.LocaleManager;
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

    @Mock
    private LocaleManager localeManager;
         
    @Mock
    private HttpServletRequest servletRequest;
    
    @Resource    
    FundingsController fundingController;

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Override
    protected Authentication getAuthentication() {
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4443");

        OrcidProfileUserDetails details = null;
        if (orcidProfile.getType() != null) {
            details = new OrcidProfileUserDetails(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getContactDetails().getEmail().get(0)
                    .getValue(), orcidProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent(), orcidProfile.getType(),
                    orcidProfile.getClientType(), orcidProfile.getGroupType());
        } else {
            details = new OrcidProfileUserDetails(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getContactDetails().getEmail().get(0)
                    .getValue(), orcidProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent());
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details, "4444-4444-4444-4443", Arrays.asList(OrcidWebRole.ROLE_USER));
        return auth;
    }

    @Before
    public void initMocks() {        
        fundingController.setLocaleManager(localeManager);
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
    public void testValidateAmountLocaleEN_US() {
        when(localeManager.getLocale()).thenReturn(new Locale("en","US"));
        String validAmounts[] = { "1", "10", "100", "1000", "10000", "100000", "1000000", "10000000", "10000000", "1.0", "1.00", "10.0", "10.00", "100.0", "100.00",
                "1000.0", "1000.00", "1,000", "1,000.0", "1,000.00", "10,000", "100,000", "1,000,000", "10,000,000", "100,000,000", "100,000,000.0", "100,000,000.00",
                "1,000,000,000", "1,000,000,000.0", "1,000,000,000.00", "10,000,000,000", "10,000,000.99"};
        String invalidAmounts[] = {"a",".","1 000","1 000 000","1,000 000","1 000,000","1'000","1'000'000","1'000.0","1'000.00","$1000","$100","1.000.000", "1.000,00"};
                        
        for (String amount : validAmounts) {            
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertNotNull(form.getAmount().getErrors());
            assertEquals("The following number has been marked as invalid: " + amount, 0, form.getAmount().getErrors().size());
        }
        
        for (String amount : invalidAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertEquals("The following incorrect number has been marked as valid: " + amount, 1, form.getAmount().getErrors().size());            
        }
    }
    
    @Test
    public void testVAlidateAmountLocaleDE_CH() {
        when(localeManager.getLocale()).thenReturn(new Locale("de","CH"));
        String validAmounts[] = { "1", "10", "100", "1000", "10000", "100000", "1000000", "10000000", "10000000", "1.0", "1.00", "10.0", "10.00", "100.0", "100.00",
                "1000.0", "1000.00", "1'000", "1'000.0", "1'000.00", "10'000", "100'000", "1'000'000", "10'000'000", "100'000'000", "100'000'000.0", "100'000'000.00",
                "1'000'000'000", "1'000'000'000.0", "1'000'000'000.00", "10'000'000'000", "10'000'000.99"};
        String invalidAmounts[] = {"a",".","1 000","1 000 000","1,000 000","1 000,000","1,000","1,000,000","1,000.0","1,000.00","$1000","$100"};
                        
        for (String amount : validAmounts) {            
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertNotNull(form.getAmount().getErrors());
            assertEquals("The following number has been marked as invalid: " + amount, 0, form.getAmount().getErrors().size());
        }
        
        for (String amount : invalidAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertEquals("The following incorrect number has been marked as valid: " + amount, 1, form.getAmount().getErrors().size());            
        }
    }
    
    @Test   
    public void testValidateAmountLocaleRU() {
        when(localeManager.getLocale()).thenReturn(new Locale("ru"));
        String validAmounts[] = { "1", "10", "100", "1000", "10000", "100000", "1000000", "10000000", "10000000", "1,0", "1,00", "10,0", "10,00", "100,0", "100,00",
                "1000,0", "1000,00", "1 000", "1 000,0", "1 000,00", "10 000", "100 000", "1 000 000", "10 000 000", "100 000 000", "100 000 000,0", "100 000 000,00",
                "1 000 000 000", "1 000 000 000,0", "1 000 000 000,00", "10 000 000 000", "10 000 000,99"};
        String invalidAmounts[] = {"a",".","1,000,000","1,000.000","1 000.000","1'000","1'000'000","1'000.0","1'000.00","$1000","$100","1.000.000", "1.000,00","1 000 000.0","1 000 000.00"};
                        
        for (String amount : validAmounts) {            
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertNotNull(form.getAmount().getErrors());
            assertEquals("The following number has been marked as invalid: " + amount, 0, form.getAmount().getErrors().size());
        }
        
        for (String amount : invalidAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertEquals("The following incorrect number has been marked as valid: " + amount, 1, form.getAmount().getErrors().size());            
        }
    }
    
    /**
     * Validate amounts of form ###,###,###.##
     * */
    @Test
    public void validateBigDecimalConversionLocaleUS_EN() {
        when(localeManager.getLocale()).thenReturn(new Locale("en","US"));
        BigDecimal _100000 = new BigDecimal(100000);
        BigDecimal _1000 = new BigDecimal(1000);
        BigDecimal _1000_99 = new BigDecimal(1000.99).setScale(2, RoundingMode.FLOOR);
        BigDecimal _1 = new BigDecimal(1);                
        String amounts100000[] = {"100000","100000.0","100000.00","100,000","100,000.0","100,000.00"};
        String amounts1000[] = { "1000","1,000","1000.0","1000.00","1,000.0","1,000.00" };
        String amounts1000_99[] = {"1000.99","1,000.99"};
        String amounts1[] = { "1", "1.0", "1.00", "1.000" };
        
        for (String amount : amounts100000) {            
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);                
                assertEquals("Amount is: " + result + " but it should be: " + _100000, _100000, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _100000);
            }
        }
        
        for (String amount : amounts1000) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);                
                assertEquals("Amount is: " + result + " but it should be: " + _1000, _1000, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1000);
            }
        }
        
        for(String amount : amounts1000_99) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);   
                assertEquals("Amount is: " + result + " but it should be: " + _1000_99, _1000_99, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1000_99);
            }
        }
        
        for (String amount : amounts1) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);                
                assertEquals("Amount is: " + result + " but it should be: " + _1, _1, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1);
            }
        }
    }

    /**
     * Validate amounts of form ###'###'###.##
     * */
    @Test    
    public void validateBigDecimalConversionLocaleDE_CH() {
        when(localeManager.getLocale()).thenReturn(new Locale("de","CH"));
        BigDecimal _100000 = new BigDecimal(100000);
        BigDecimal _1000 = new BigDecimal(1000);
        BigDecimal _1000_99 = new BigDecimal(1000.99).setScale(2, RoundingMode.FLOOR);
        BigDecimal _1 = new BigDecimal(1);
        String amounts100000[] = {"100000","100'000.0","100'000.00","100'000"};
        String amounts1000[] = { "1000","1'000","1000.0","1000.00","1'000.0","1'000.00"};
        String amounts1000_99[] = {"1000.99","1'000.99"};
        String amounts1[] = { "1", "1.0", "1.00", "1.000" };
        
        for (String amount : amounts100000) {            
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);                
                assertEquals("Amount is: " + result + " but it should be: " + _100000, _100000, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _100000);
            }
        }
        
        for (String amount : amounts1000) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);    
                assertEquals("Amount is: " + result + " but it should be: " + _1000, _1000, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1000);
            }
        }
        
        for(String amount : amounts1000_99) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);                
                assertEquals("Amount is: " + result + " but it should be: " + _1000_99, _1000_99, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1000_99);
            }
        }
        
        for (String amount : amounts1) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);                
                assertEquals("Amount is: " + result + " but it should be: " + _1, _1, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1);
            }
        }
    }
    
    /**
     * Validate amounts of form ### ### ###,##
     * */
    @Test    
    public void validateBigDecimalConversionLocaleRU() {
        when(localeManager.getLocale()).thenReturn(new Locale("ru"));
        BigDecimal _100000 = new BigDecimal(100000);
        BigDecimal _1000 = new BigDecimal(1000);
        BigDecimal _1000_99 = new BigDecimal(1000.99).setScale(2, RoundingMode.FLOOR);
        BigDecimal _1 = new BigDecimal(1);
        String amounts100000[] = {"100000","100 000,0","100 000,00","100 000"};
        String amounts1000[] = { "1000","1 000","1000,0","1000,00","1 000,0","1 000,00"};
        String amounts1000_99[] = {"1000,99","1 000,99"};
        String amounts1[] = { "1", "1,0", "1,00", "1,000" };
        
        for (String amount : amounts100000) {            
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);                
                assertEquals("Amount is: " + result + " but it should be: " + _100000, _100000, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _100000);
            }
        }
        
        for (String amount : amounts1000) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);                
                assertEquals("Amount is: " + result + " but it should be: " + _1000, _1000, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1000);
            }
        }
        
        for(String amount : amounts1000_99) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);
                assertEquals("Amount is: " + result + " but it should be: " + _1000_99, _1000_99, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1000_99);
            }
        }
        
        for (String amount : amounts1) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);                
                assertEquals("Amount is: " + result + " but it should be: " + _1, _1, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1);
            }
        }
    }
    
    @Test
    public void testGetFundings() {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(localeManager.getLocale()).thenReturn(new Locale("us","EN"));
        
        List<String> fundingIds = fundingController.getFundingsJson(servletRequest);
        assertNotNull(fundingIds);
        assertEquals(fundingIds.size(), 3);

        assertTrue(fundingIds.contains("1"));
        assertTrue(fundingIds.contains("2"));
        assertTrue(fundingIds.contains("3"));
    }

}
