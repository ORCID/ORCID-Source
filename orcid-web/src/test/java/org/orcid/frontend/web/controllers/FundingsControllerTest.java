package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.security.OrcidRoles;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.ajaxForm.FundingTitleForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitleForm;
import org.orcid.pojo.grouping.FundingGroup;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:test-frontend-web-servlet.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class FundingsControllerTest extends BaseControllerTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/RecordNameEntityData.xml");

    @Mock
    private LocaleManager localeManager;

    @Mock
    private HttpServletRequest servletRequest;

    @Resource
    FundingsController fundingController;
    
    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;
    
    @Resource(name = "profileEntityManagerReadOnlyV3")
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;
    
    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;

    @Override
    protected Authentication getAuthentication() {
        String orcid = "4444-4444-4444-4443";
        ProfileEntity p = profileEntityManagerReadOnly.findByOrcid(orcid);
        Email e = emailManagerReadOnly.findPrimaryEmail(orcid);
        List<GrantedAuthority> roles = Arrays.asList(new SimpleGrantedAuthority(OrcidRoles.ROLE_USER.name()));
        UserDetails details = new User(orcid,
                "password", roles);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(orcid, p.getPassword(), roles);
        auth.setDetails(details);
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
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Lists.reverse(DATA_FILES));
    }
    
    @Test
    public void testSearchDisambiguated() {
        OrgDisambiguatedManager mockOrgDisambiguatedManager = Mockito.mock(OrgDisambiguatedManager.class);
        OrgDisambiguatedManager oldOrgDisambiguatedManager = (OrgDisambiguatedManager) ReflectionTestUtils.getField(fundingController, "orgDisambiguatedManager");
        ReflectionTestUtils.setField(fundingController, "orgDisambiguatedManager", mockOrgDisambiguatedManager);
        
        Mockito.when(mockOrgDisambiguatedManager.searchOrgsFromSolr(Mockito.eq("search"), Mockito.eq(0), Mockito.eq(0), Mockito.eq(true))).thenReturn(getListOfMixedOrgsDiambiguated());
        
        List<Map<String, String>> results = fundingController.searchDisambiguated("search", 0, true);
        assertEquals(4, results.size());
        assertEquals("first", results.get(0).get("value"));
        assertEquals("second", results.get(1).get("value"));
        assertEquals("third", results.get(2).get("value"));
        assertEquals("fourth", results.get(3).get("value"));
        
        ReflectionTestUtils.setField(fundingController, "orgDisambiguatedManager", oldOrgDisambiguatedManager);
    }

    private List<OrgDisambiguated> getListOfMixedOrgsDiambiguated() {
        OrgDisambiguated first = new OrgDisambiguated();
        first.setValue("first");
        first.setSourceType(OrgDisambiguatedSourceType.FUNDREF.name());
        
        OrgDisambiguated second = new OrgDisambiguated();
        second.setValue("second");
        second.setSourceType(OrgDisambiguatedSourceType.RINGGOLD.name());
        
        OrgDisambiguated third = new OrgDisambiguated();
        third.setValue("third");
        third.setSourceType(OrgDisambiguatedSourceType.GRID.name());
        
        OrgDisambiguated fourth = new OrgDisambiguated();
        fourth.setValue("fourth");
        fourth.setSourceType(OrgDisambiguatedSourceType.LEI.name());
        
        return Arrays.asList(first, second, third, fourth);
    }

    @Test
    public void testValidateAmountLocaleEN_US() {
        when(localeManager.getLocale()).thenReturn(new Locale("en", "US"));
        String validAmounts[] = { "1", "10", "100", "1000", "10000", "100000", "1000000", "10000000", "10000000", "1.0", "1.00", "10.0", "10.00", "100.0", "100.00",
                "1000.0", "1000.00", "1,000", "1,000.0", "1,000.00", "10,000", "100,000", "1,000,000", "10,000,000", "100,000,000", "100,000,000.0", "100,000,000.00",
                "1,000,000,000", "1,000,000,000.0", "1,000,000,000.00", "10,000,000,000", "10,000,000.99" };
        String invalidAmounts[] = { "a", ".", "1 000", "1 000 000", "1,000 000", "1 000,000", "1'000", "1'000'000", "1'000.0", "1'000.00", "$1000", "$100", "1.000.000",
                "1.000,00" };

        for (String amount : validAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertNotNull(form.getAmount().getErrors());
            assertEquals("The following number has been marked as invalid: " + amount, 0, form.getAmount().getErrors().size());
        }

        for (String amount : invalidAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertEquals("The following incorrect number has been marked as valid: " + amount, 1, form.getAmount().getErrors().size());
        }
    }

    @Test
    public void testVAlidateAmountLocaleDE_CH() {
        when(localeManager.getLocale()).thenReturn(new Locale("de", "CH"));
        String validAmounts[] = { "1", "10", "100", "1000", "10000", "100000", "1000000", "10000000", "10000000", "1.0", "1.00", "10.0", "10.00", "100.0", "100.00",
                "1000.0", "1000.00", "1'000", "1'000.0", "1'000.00", "10'000", "100'000", "1'000'000", "10'000'000", "100'000'000", "100'000'000.0", "100'000'000.00",
                "1'000'000'000", "1'000'000'000.0", "1'000'000'000.00", "10'000'000'000", "10'000'000.99" };
        String invalidAmounts[] = { "a", ".", "1 000", "1 000 000", "1,000 000", "1 000,000", "1,000", "1,000,000", "1,000.0", "1,000.00", "$1000", "$100" };

        for (String amount : validAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertNotNull(form.getAmount().getErrors());
            assertEquals("The following number has been marked as invalid: " + amount, 0, form.getAmount().getErrors().size());
        }

        for (String amount : invalidAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
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
                "1 000 000 000", "1 000 000 000,0", "1 000 000 000,00", "10 000 000 000", "10 000 000,99" };
        String invalidAmounts[] = { "a", ".", "1,000,000", "1,000.000", "1 000.000", "1'000", "1'000'000", "1'000.0", "1'000.00", "$1000", "$100", "1.000.000",
                "1.000,00", "1 000 000.0", "1 000 000.00" };

        for (String amount : validAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertNotNull(form.getAmount().getErrors());
            assertEquals("The following number has been marked as invalid: " + amount, 0, form.getAmount().getErrors().size());
        }

        for (String amount : invalidAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertEquals("The following incorrect number has been marked as valid: " + amount, 1, form.getAmount().getErrors().size());
        }
    }

    /**
     * Validate amounts of form ###,###,###.##
     */
    @Test
    public void validateBigDecimalConversionLocaleUS_EN() {
        when(localeManager.getLocale()).thenReturn(new Locale("en", "US"));
        BigDecimal _100000 = new BigDecimal(100000);
        BigDecimal _1000 = new BigDecimal(1000);
        BigDecimal _1000_99 = new BigDecimal(1000.99).setScale(2, RoundingMode.FLOOR);
        BigDecimal _1 = new BigDecimal(1);
        String amounts100000[] = { "100000", "100000.0", "100000.00", "100,000", "100,000.0", "100,000.00" };
        String amounts1000[] = { "1000", "1,000", "1000.0", "1000.00", "1,000.0", "1,000.00" };
        String amounts1000_99[] = { "1000.99", "1,000.99" };
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

        for (String amount : amounts1000_99) {
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
     */
    @Test
    public void validateBigDecimalConversionLocaleDE_CH() {
        when(localeManager.getLocale()).thenReturn(new Locale("de", "CH"));
        BigDecimal _100000 = new BigDecimal(100000);
        BigDecimal _1000 = new BigDecimal(1000);
        BigDecimal _1000_99 = new BigDecimal(1000.99).setScale(2, RoundingMode.FLOOR);
        BigDecimal _1 = new BigDecimal(1);
        String amounts100000[] = { "100000", "100'000.0", "100'000.00", "100'000" };
        String amounts1000[] = { "1000", "1'000", "1000.0", "1000.00", "1'000.0", "1'000.00" };
        String amounts1000_99[] = { "1000.99", "1'000.99" };
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

        for (String amount : amounts1000_99) {
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
     */
    @Test
    public void validateBigDecimalConversionLocaleRU() {
        when(localeManager.getLocale()).thenReturn(new Locale("ru"));
        BigDecimal _100000 = new BigDecimal(100000);
        BigDecimal _1000 = new BigDecimal(1000);
        BigDecimal _1000_99 = new BigDecimal(1000.99).setScale(2, RoundingMode.FLOOR);
        BigDecimal _1 = new BigDecimal(1);
        String amounts100000[] = { "100000", "100 000,0", "100 000,00", "100 000" };
        String amounts1000[] = { "1000", "1 000", "1000,0", "1000,00", "1 000,0", "1 000,00" };
        String amounts1000_99[] = { "1000,99", "1 000,99" };
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

        for (String amount : amounts1000_99) {
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
    public void testGetFundingsJson() {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        List<FundingGroup> fundings = fundingController.getFundingsJson("title", true);
        assertNotNull(fundings);
        assertEquals(3, fundings.size());
        assertEquals(1l, fundings.get(0).getGroupId());
        assertEquals(1, fundings.get(0).getFundings().size());
        assertEquals(Text.valueOf(1l), fundings.get(0).getFundings().get(0).getPutCode());
        assertEquals(2l, fundings.get(1).getGroupId());
        assertEquals(1, fundings.get(1).getFundings().size());
        assertEquals(Text.valueOf(2l), fundings.get(1).getFundings().get(0).getPutCode());
        assertEquals(3l, fundings.get(2).getGroupId());
        assertEquals(1, fundings.get(2).getFundings().size());
        assertEquals(Text.valueOf(3l), fundings.get(2).getFundings().get(0).getPutCode());
    }

    @Test
    @Rollback(true)
    public void testAddFundingWithoutAmount() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));
        FundingForm funding = fundingController.getFunding();
        funding.setFundingType(Text.valueOf("award"));
        FundingTitleForm title = new FundingTitleForm();
        title.setTitle(Text.valueOf("Title"));
        funding.setFundingTitle(title);
        funding.setCountry(Text.valueOf("CR"));
        funding.setCity(Text.valueOf("SJ"));
        funding.setRegion(Text.valueOf("SJ"));
        funding.setFundingName(Text.valueOf("OrgName"));

        FundingForm result = fundingController.postFunding(funding);
        assertEquals(funding.getFundingTitle().getTitle(), result.getFundingTitle().getTitle());
        assertEquals(funding.getFundingType(), result.getFundingType());
        assertEquals(funding.getCountry(), result.getCountry());
        assertEquals(funding.getCity(), result.getCity());
        assertEquals(funding.getRegion(), result.getRegion());
        assertEquals(funding.getCountry(), result.getCountry());
        assertNotNull(funding.getErrors());
        assertEquals(0, funding.getErrors().size());
    }

    @Test
    @Rollback(true)
    public void testAddFunding() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));
        FundingForm funding = fundingController.getFunding();
        funding.setFundingType(Text.valueOf("award"));
        FundingTitleForm title = new FundingTitleForm();
        title.setTitle(Text.valueOf("Title"));
        funding.setFundingTitle(title);
        funding.setCountry(Text.valueOf("CR"));
        funding.setCity(Text.valueOf("SJ"));
        funding.setRegion(Text.valueOf("SJ"));
        funding.setAmount(Text.valueOf("1000"));
        funding.setCurrencyCode(Text.valueOf("USD"));
        funding.setFundingName(Text.valueOf("OrgName"));
        FundingForm result = fundingController.postFunding(funding);
        assertEquals(funding.getFundingTitle().getTitle(), result.getFundingTitle().getTitle());
        assertEquals(funding.getFundingType(), result.getFundingType());
        assertEquals(funding.getCountry(), result.getCountry());
        assertEquals(funding.getCity(), result.getCity());
        assertEquals(funding.getRegion(), result.getRegion());
        assertEquals(funding.getCountry(), result.getCountry());
        assertNotNull(funding.getErrors());
        assertEquals(0, funding.getErrors().size());
        BigDecimal expected = fundingController.getAmountAsBigDecimal(funding.getAmount().getValue());
        BigDecimal resulting = fundingController.getAmountAsBigDecimal(result.getAmount().getValue());
        assertEquals(expected, resulting);
    }

    @Test
    public void testAddAmountWithoutCurrencyCode() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));
        FundingForm funding = fundingController.getFunding();
        funding.setFundingType(Text.valueOf("award"));
        FundingTitleForm title = new FundingTitleForm();
        title.setTitle(Text.valueOf("Title"));
        funding.setFundingTitle(title);
        funding.setCountry(Text.valueOf("CR"));
        funding.setCity(Text.valueOf("SJ"));
        funding.setRegion(Text.valueOf("SJ"));
        funding.setAmount(Text.valueOf("1000"));
        funding.setFundingName(Text.valueOf("OrgName"));
        FundingForm result = fundingController.postFunding(funding);
        assertNotNull(result);
        assertNotNull(result.getErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals(fundingController.getMessage("Invalid.fundings.currency"), result.getErrors().get(0));

    }

    @Test
    public void getFunding() {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        FundingForm funding = fundingController.getFundingJson(Long.valueOf("1"));
        assertNotNull(funding);
        assertNotNull(funding.getFundingTitle());
        assertFalse(PojoUtil.isEmpty(funding.getFundingTitle().getTitle()));
        assertEquals("Grant # 1", funding.getFundingTitle().getTitle().getValue());
        assertFalse(PojoUtil.isEmpty(funding.getFundingType()));
        assertEquals("salary-award", funding.getFundingType().getValue());
        assertFalse(PojoUtil.isEmpty(funding.getAmount()));
        assertEquals("2,500", funding.getAmount().getValue());
        assertFalse(PojoUtil.isEmpty(funding.getCurrencyCode()));
        assertEquals("USD", funding.getCurrencyCode().getValue());
    }

    @Test
    @Rollback(true)
    public void testEditOtherSourceThrowsError() {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        FundingForm funding = fundingController.getFundingJson(Long.valueOf("3"));
        boolean throwsError = false;
        try {
            fundingController.postFunding(funding);
        } catch (Exception e) {
            throwsError = true;
        }
        assertEquals(throwsError, true);
    }

    @Test
    @Rollback(true)
    public void testEditFunding() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        FundingForm funding = fundingController.getFundingJson(Long.valueOf("1"));
        funding.getFundingTitle().getTitle().setValue("Grant # 1 - updated");
        TranslatedTitleForm translatedTitle = new TranslatedTitleForm();
        translatedTitle.setContent("Grant # 1 - translated title");
        translatedTitle.setLanguageCode("en");
        funding.getFundingTitle().setTranslatedTitle(translatedTitle);
        funding.getAmount().setValue("3500");
        funding.getCurrencyCode().setValue("CRC");

        fundingController.postFunding(funding);
        // Fetch the funding again
        FundingForm updated = fundingController.getFundingJson(Long.valueOf("1"));
        assertNotNull(updated);
        assertNotNull(updated.getFundingTitle());
        assertFalse(PojoUtil.isEmpty(updated.getFundingTitle().getTitle()));
        assertEquals("Grant # 1 - updated", updated.getFundingTitle().getTitle().getValue());
        assertEquals("Grant # 1 - translated title", updated.getFundingTitle().getTranslatedTitle().getContent());
        assertEquals("en", updated.getFundingTitle().getTranslatedTitle().getLanguageCode());
        assertFalse(PojoUtil.isEmpty(updated.getFundingType()));
        assertEquals("salary-award", updated.getFundingType().getValue());
        assertFalse(PojoUtil.isEmpty(updated.getAmount()));
        assertEquals("3,500", updated.getAmount().getValue());
        assertFalse(PojoUtil.isEmpty(updated.getCurrencyCode()));
        assertEquals("CRC", updated.getCurrencyCode().getValue());
    }

    @Test
    @Rollback(true)
    public void testEditOrgOnExistingFunding() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        FundingForm funding = fundingController.getFundingJson(Long.valueOf("1"));
        // Check old org
        assertEquals("London", funding.getCity().getValue());
        assertEquals("GB", funding.getCountry().getValue());
        // Update org
        funding.getCity().setValue("San Jose");
        funding.getCountry().setValue("CR");

        fundingController.postFunding(funding);
        // Fetch the funding again
        FundingForm updated = fundingController.getFundingJson(Long.valueOf("1"));
        assertNotNull(updated);
        // Check new org
        assertEquals("San Jose", funding.getCity().getValue());
        assertEquals("CR", funding.getCountry().getValue());
    }

    @Test
    public void testAddFundingWithInvalidDates() throws Exception {
        FundingForm funding = getFundingForm();

        // Check valid start date
        Date startDate = new Date();
        startDate.setMonth("01");
        funding.setStartDate(startDate);
        funding = fundingController.postFunding(funding);
        assertNotNull(funding);
        assertNotNull(funding.getErrors());
        assertEquals(1, funding.getErrors().size());
        assertEquals(fundingController.getMessage("common.dates.invalid"), funding.getErrors().get(0));

        // Check valid end date
        funding = getFundingForm();
        Date endDate = new Date();
        endDate.setMonth("01");
        funding.setEndDate(endDate);
        funding = fundingController.postFunding(funding);
        assertNotNull(funding);
        assertNotNull(funding.getErrors());
        assertEquals(1, funding.getErrors().size());
        assertEquals(fundingController.getMessage("common.dates.invalid"), funding.getErrors().get(0));

        // Check end date is after start date
        funding = getFundingForm();

        startDate = new Date();
        startDate.setMonth("01");
        startDate.setYear("2015");

        endDate = new Date();
        endDate.setMonth("01");
        endDate.setYear("2014");

        funding.setStartDate(startDate);
        funding.setEndDate(endDate);

        funding = fundingController.postFunding(funding);
        assertNotNull(funding);
        assertNotNull(funding.getErrors());
        assertEquals(1, funding.getErrors().size());
        assertEquals(fundingController.getMessage("fundings.endDate.after"), funding.getErrors().get(0));
    }

    @Test
    public void testGetFundingsJsonSortedBySource() {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        List<FundingGroup> fundings = fundingController.getFundingsJson("source", true);
        assertNotNull(fundings);
        assertEquals(3, fundings.size());
        assertEquals("4444-4444-4444-4441", fundings.get(0).getFundings().get(0).getSource());
        assertEquals("4444-4444-4444-4443", fundings.get(2).getFundings().get(0).getSource());
    }

    private FundingForm getFundingForm() {
        FundingForm funding = fundingController.getFunding();
        funding.setFundingType(Text.valueOf("award"));
        funding.setCity(Text.valueOf("city"));
        funding.setCountry(Text.valueOf("CR"));
        funding.setFundingName(Text.valueOf("Name"));
        FundingTitleForm title = new FundingTitleForm();
        title.setTitle(Text.valueOf("title"));
        funding.setFundingTitle(title);
        return funding;
    }
}
