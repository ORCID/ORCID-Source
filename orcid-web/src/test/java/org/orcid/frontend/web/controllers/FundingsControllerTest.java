package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.CountryManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ProfileFundingManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.common.FundingType;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Amount;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.FundingTitle;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.ajaxForm.FundingTitleForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitleForm;
import org.orcid.pojo.grouping.FundingGroup;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class FundingsControllerTest {

    private static final String ORCID = "4444-4444-4444-4443";

    @Spy
    @InjectMocks
    private FundingsController fundingController;

    @Mock
    private ProfileFundingManager profileFundingManager;

    @Mock
    private OrgDisambiguatedManager orgDisambiguatedManager;

    @Mock
    private LocaleManager localeManager;

    @Mock
    private LanguagesMap lm;

    @Mock
    private ProfileEntityManager profileEntityManager;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private ActivityManager activityManager;

    @Mock
    private ContributorUtils contributorUtils;

    @Mock
    private CountryManager countryManager;

    @Mock
    private VisibilityFilter visibilityFilter;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private EmailManagerReadOnly emailManagerReadOnly;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private BaseControllerUtil baseControllerUtil;

    @Before
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
        
        UserDetails userDetails = new User(ORCID, "password", new ArrayList<>());
        Mockito.lenient().when(baseControllerUtil.getCurrentUser(any())).thenReturn(userDetails);
        ReflectionTestUtils.setField(fundingController, "baseControllerUtil", baseControllerUtil);
        
        // Manually inject mocks into BaseController/BaseWorkspaceController fields
        ReflectionTestUtils.setField(fundingController, "localeManager", localeManager);
        ReflectionTestUtils.setField(fundingController, "sourceManager", sourceManager);
        ReflectionTestUtils.setField(fundingController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(fundingController, "emailManagerReadOnly", emailManagerReadOnly);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(ORCID, "password");
        Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(auth);

        // Mocking the message behavior
        Mockito.lenient().when(localeManager.resolveMessage(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.lenient().when(localeManager.resolveMessage(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Mock getEffectiveUserOrcid to avoid calling real security context logic if needed
        Mockito.doReturn(ORCID).when(fundingController).getEffectiveUserOrcid();
        Mockito.lenient().doReturn(Locale.ENGLISH).when(fundingController).getUserLocale();
        
        // Spy on getMessage to return the code itself to avoid NPE
        Mockito.lenient().doAnswer(invocation -> invocation.getArgument(0)).when(fundingController).getMessage(anyString(), any());
        Mockito.lenient().doAnswer(invocation -> invocation.getArgument(0)).when(fundingController).getMessage(anyString());
    }

    @Test
    public void testSearchDisambiguated() {
        when(orgDisambiguatedManager.searchOrgsFromSolr(eq("search"), eq(0), eq(0), eq(true))).thenReturn(getListOfMixedOrgsDiambiguated());

        List<Map<String, String>> results = fundingController.searchDisambiguated("search", 0, true);
        assertEquals(4, results.size());
        assertEquals("first", results.get(0).get("value"));
        assertEquals("second", results.get(1).get("value"));
        assertEquals("third", results.get(2).get("value"));
        assertEquals("fourth", results.get(3).get("value"));
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
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(new Locale("en", "US"));
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
        Locale locale = new Locale("de", "CH");
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(locale);
        Mockito.lenient().doReturn(locale).when(fundingController).getUserLocale();
        
        // Controller forces ' as grouping separator for German languages
        String validAmounts[] = { "1", "1000", "1'000", "1'000'000", "1'000.99" };
        String invalidAmounts[] = { "a", ".", "1 000", "1,000", "$1000" };

        for (String amount : validAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertNotNull(form.getAmount().getErrors());
            assertEquals("The following number has been marked as invalid: " + amount + " for locale " + locale + ". Errors: " + form.getAmount().getErrors(), 0, form.getAmount().getErrors().size());
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
        Locale locale = new Locale("ru");
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(locale);
        Mockito.lenient().doReturn(locale).when(fundingController).getUserLocale();
        
        // RU uses space as grouping separator and comma as decimal separator
        String validAmounts[] = { "1", "1000", "1 000", "1 000 000", "1 000,99" };
        String invalidAmounts[] = { "a", ".", "1,000,000", "1'000", "$1000" };

        for (String amount : validAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertNotNull(form.getAmount().getErrors());
            assertEquals("The following number has been marked as invalid: " + amount + ". Errors: " + form.getAmount().getErrors(), 0, form.getAmount().getErrors().size());
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
    public void validateBigDecimalConversionLocaleUS_EN() {
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(new Locale("en", "US"));
        BigDecimal _100000 = new BigDecimal(100000);
        BigDecimal _1000 = new BigDecimal(1000);
        BigDecimal _1000_99 = new BigDecimal("1000.99").setScale(2, RoundingMode.FLOOR);
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

    @Test
    public void validateBigDecimalConversionLocaleDE_CH() {
        Locale locale = new Locale("de", "CH");
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(locale);
        Mockito.lenient().doReturn(locale).when(fundingController).getUserLocale();
        BigDecimal _100000 = new BigDecimal(100000);
        BigDecimal _1000 = new BigDecimal(1000);
        BigDecimal _1000_99 = new BigDecimal("1000.99").setScale(2, RoundingMode.FLOOR);
        BigDecimal _1 = new BigDecimal(1);
        
        String amounts100000[] = { "100000", "100'000" };
        String amounts1000[] = { "1000", "1'000" };
        String amounts1000_99[] = { "1'000.99" };
        String amounts1[] = { "1" };

        for (String amount : amounts100000) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);
                assertEquals("Amount is: " + result + " but it should be: " + _100000, _100000, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _100000 + " for locale " + locale + ". Error: " + e.getMessage());
            }
        }

        for (String amount : amounts1000) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);
                assertEquals("Amount is: " + result + " but it should be: " + _1000, _1000, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1000 + ". Error: " + e.getMessage());
            }
        }

        for (String amount : amounts1000_99) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);
                assertEquals("Amount is: " + result + " but it should be: " + _1000_99, _1000_99, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1000_99 + ". Error: " + e.getMessage());
            }
        }

        for (String amount : amounts1) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);
                assertEquals("Amount is: " + result + " but it should be: " + _1, _1, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1 + ". Error: " + e.getMessage());
            }
        }
    }

    @Test
    public void validateBigDecimalConversionLocaleRU() {
        Locale locale = new Locale("ru");
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(locale);
        Mockito.lenient().doReturn(locale).when(fundingController).getUserLocale();
        BigDecimal _100000 = new BigDecimal(100000);
        BigDecimal _1000 = new BigDecimal(1000);
        BigDecimal _1000_99 = new BigDecimal("1000.99").setScale(2, RoundingMode.FLOOR);
        BigDecimal _1 = new BigDecimal(1);
        
        String amounts100000[] = { "100000", "100 000" };
        String amounts1000[] = { "1000", "1 000" };
        String amounts1000_99[] = { "1 000,99" };
        String amounts1[] = { "1" };

        for (String amount : amounts100000) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);
                assertEquals("Amount is: " + result + " but it should be: " + _100000, _100000, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _100000 + ". Error: " + e.getMessage());
            }
        }

        for (String amount : amounts1000) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);
                assertEquals("Amount is: " + result + " but it should be: " + _1000, _1000, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1000 + ". Error: " + e.getMessage());
            }
        }

        for (String amount : amounts1000_99) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);
                assertEquals("Amount is: " + result + " but it should be: " + _1000_99, _1000_99, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1000_99 + ". Error: " + e.getMessage());
            }
        }

        for (String amount : amounts1) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);
                assertEquals("Amount is: " + result + " but it should be: " + _1, _1, result);
            } catch (Exception e) {
                fail("Amount: " + amount + " couldn't parsed to: " + _1 + ". Error: " + e.getMessage());
            }
        }
    }

    @Test
    public void testGetFundingsJson() {
        HttpSession session = mock(HttpSession.class);
        Mockito.lenient().when(servletRequest.getSession()).thenReturn(session);
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        List<FundingSummary> summaries = new ArrayList<>();
        summaries.add(createFundingSummary(1L));
        summaries.add(createFundingSummary(2L));
        summaries.add(createFundingSummary(3L));

        Fundings fundingsJaxb = new Fundings();
        fundingsJaxb.getFundingGroup().add(createFundingGroupJaxb(1L));
        fundingsJaxb.getFundingGroup().add(createFundingGroupJaxb(2L));
        fundingsJaxb.getFundingGroup().add(createFundingGroupJaxb(3L));

        when(profileFundingManager.getFundingSummaryList(ORCID)).thenReturn(summaries);
        when(profileFundingManager.groupFundings(summaries, false)).thenReturn(fundingsJaxb);

        List<FundingGroup> fundings = fundingController.getFundingsJson("title", true);
        assertNotNull(fundings);
        assertEquals(3, fundings.size());
        assertEquals(1L, (long) fundings.get(0).getGroupId());
        assertEquals("1", fundings.get(0).getFundings().get(0).getPutCode().getValue());
    }

    @Test
    public void testAddFundingWithoutAmount() throws Exception {
        HttpSession session = mock(HttpSession.class);
        Mockito.lenient().when(servletRequest.getSession()).thenReturn(session);
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));
        
        FundingForm funding = new FundingForm();
        funding.setAmount(new Text());
        funding.setCurrencyCode(Text.valueOf(""));
        funding.setFundingType(Text.valueOf("award"));
        FundingTitleForm title = new FundingTitleForm();
        title.setTitle(Text.valueOf("Title"));
        funding.setFundingTitle(title);
        funding.setCountry(Text.valueOf("CR"));
        funding.setCity(Text.valueOf("SJ"));
        funding.setRegion(Text.valueOf("SJ"));
        funding.setFundingName(Text.valueOf("OrgName"));
        funding.setDescription(Text.valueOf("Desc"));
        funding.setUrl(Text.valueOf("http://test.com"));

        when(profileFundingManager.createFunding(eq(ORCID), any(Funding.class), eq(false))).thenAnswer(invocation -> {
            Funding f = invocation.getArgument(1);
            f.setPutCode(100L);
            return f;
        });

        FundingForm result = fundingController.postFunding(funding);
        assertEquals(funding.getFundingTitle().getTitle().getValue(), result.getFundingTitle().getTitle().getValue());
        assertEquals(0, funding.getErrors().size());
    }

    @Test
    public void testAddFunding() throws Exception {
        HttpSession session = mock(HttpSession.class);
        Mockito.lenient().when(servletRequest.getSession()).thenReturn(session);
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        FundingForm funding = new FundingForm();
        funding.setAmount(Text.valueOf("1000"));
        funding.setCurrencyCode(Text.valueOf("USD"));
        funding.setFundingType(Text.valueOf("award"));
        FundingTitleForm title = new FundingTitleForm();
        title.setTitle(Text.valueOf("Title"));
        funding.setFundingTitle(title);
        funding.setCountry(Text.valueOf("CR"));
        funding.setCity(Text.valueOf("SJ"));
        funding.setRegion(Text.valueOf("SJ"));
        funding.setFundingName(Text.valueOf("OrgName"));
        funding.setDescription(Text.valueOf("Desc"));
        funding.setUrl(Text.valueOf("http://test.com"));

        when(profileFundingManager.createFunding(eq(ORCID), any(Funding.class), eq(false))).thenAnswer(invocation -> {
            Funding f = invocation.getArgument(1);
            f.setPutCode(100L);
            return f;
        });

        FundingForm result = fundingController.postFunding(funding);
        assertEquals(0, funding.getErrors().size());
        assertEquals(new BigDecimal("1000"), fundingController.getAmountAsBigDecimal(result.getAmount().getValue()));
    }

    @Test
    public void testAddAmountWithoutCurrencyCode() throws Exception {
        HttpSession session = mock(HttpSession.class);
        Mockito.lenient().when(servletRequest.getSession()).thenReturn(session);
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));
        
        FundingForm funding = new FundingForm();
        funding.setAmount(Text.valueOf("1000"));
        funding.setCurrencyCode(Text.valueOf(""));
        funding.setFundingType(Text.valueOf("award"));
        FundingTitleForm title = new FundingTitleForm();
        title.setTitle(Text.valueOf("Title"));
        funding.setFundingTitle(title);
        funding.setCountry(Text.valueOf("CR"));
        funding.setCity(Text.valueOf("SJ"));
        funding.setRegion(Text.valueOf("SJ"));
        funding.setFundingName(Text.valueOf("OrgName"));
        funding.setDescription(Text.valueOf("Desc"));
        funding.setUrl(Text.valueOf("http://test.com"));

        FundingForm result = fundingController.postFunding(funding);
        assertNotNull(result.getErrors());
        assertEquals(1, result.getErrors().size());
    }

    @Test
    public void getFunding() {
        HttpSession session = mock(HttpSession.class);
        Mockito.lenient().when(servletRequest.getSession()).thenReturn(session);
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        Funding funding = createFunding(1L, "Grant # 1", "salary-award", "2500", "USD");
        when(profileFundingManager.getFunding(ORCID, 1L)).thenReturn(funding);
        when(lm.buildLanguageMap(any(), anyBoolean())).thenReturn(new HashMap<>());

        FundingForm form = fundingController.getFundingJson(1L);
        assertNotNull(form);
        assertEquals("Grant # 1", form.getFundingTitle().getTitle().getValue());
        assertEquals("salary-award", form.getFundingType().getValue());
        assertEquals("2,500", form.getAmount().getValue());
        assertEquals("USD", form.getCurrencyCode().getValue());
    }

    @Test
    public void testEditOtherSourceThrowsError() throws Exception {
        HttpSession session = mock(HttpSession.class);
        Mockito.lenient().when(servletRequest.getSession()).thenReturn(session);
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        Funding funding = createFunding(3L, "Other Source", "award", "100", "USD");
        Source otherSource = new Source();
        otherSource.setSourceOrcid(new SourceOrcid("other-orcid"));
        funding.setSource(otherSource);
        
        when(profileFundingManager.getFunding(ORCID, 3L)).thenReturn(funding);
        // Also mock for editFunding check
        when(profileFundingManager.updateFunding(eq(ORCID), any(Funding.class), eq(false)))
            .thenThrow(new RuntimeException("You can't edit a funding item that you haven't created"));
        
        FundingForm form = fundingController.getFundingJson(3L);
        form.setPutCode(Text.valueOf("3"));
        form.getFundingTitle().getTitle().setValue("Updated Title");
        form.setCity(Text.valueOf("San Jose"));
        form.setCountry(Text.valueOf("CR"));
        form.setFundingName(Text.valueOf("Org Name"));
        form.setFundingType(Text.valueOf("award"));
        form.setDescription(Text.valueOf("Desc"));
        form.setUrl(Text.valueOf("http://test.com"));
        
        boolean throwsError = false;
        try {
            fundingController.postFunding(form);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("You can't edit a funding item that you haven't created")) {
                throwsError = true;
            }
        }
        assertEquals("Should have thrown a RuntimeException for editing other source", true, throwsError);
    }

    @Test
    public void testEditFunding() throws Exception {
        HttpSession session = mock(HttpSession.class);
        Mockito.lenient().when(servletRequest.getSession()).thenReturn(session);
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        Funding funding = createFunding(1L, "Grant # 1", "salary-award", "2500", "USD");
        Mockito.lenient().when(profileFundingManager.getFunding(ORCID, 1L)).thenReturn(funding);
        when(lm.buildLanguageMap(any(), anyBoolean())).thenReturn(new HashMap<>());
        when(profileFundingManager.updateFunding(eq(ORCID), any(Funding.class), eq(false))).thenAnswer(invocation -> invocation.getArgument(1));

        FundingForm form = fundingController.getFundingJson(1L);
        form.getFundingTitle().getTitle().setValue("Grant # 1 - updated");
        TranslatedTitleForm translatedTitle = new TranslatedTitleForm();
        translatedTitle.setContent("Grant # 1 - translated title");
        translatedTitle.setLanguageCode("en");
        form.getFundingTitle().setTranslatedTitle(translatedTitle);
        form.getAmount().setValue("3500");
        form.getCurrencyCode().setValue("CRC");
        form.setDescription(Text.valueOf("Desc"));
        form.setUrl(Text.valueOf("http://test.com"));

        fundingController.postFunding(form);
        
        // Mock updated funding for the next getFundingJson call
        Funding updatedFunding = createFunding(1L, "Grant # 1 - updated", "salary-award", "3500", "CRC");
        FundingTitle updatedTitle = new FundingTitle();
        updatedTitle.setTitle(new Title("Grant # 1 - updated"));
        updatedTitle.setTranslatedTitle(new org.orcid.jaxb.model.v3.release.common.TranslatedTitle("Grant # 1 - translated title", "en"));
        updatedFunding.setTitle(updatedTitle);
        when(profileFundingManager.getFunding(ORCID, 1L)).thenReturn(updatedFunding);

        FundingForm updated = fundingController.getFundingJson(1L);
        assertEquals("Grant # 1 - updated", updated.getFundingTitle().getTitle().getValue());
        assertEquals("3,500", updated.getAmount().getValue());
        assertEquals("CRC", updated.getCurrencyCode().getValue());
    }

    @Test
    public void testEditOrgOnExistingFunding() throws Exception {
        HttpSession session = mock(HttpSession.class);
        Mockito.lenient().when(servletRequest.getSession()).thenReturn(session);
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        Funding funding = createFunding(1L, "Grant # 1", "salary-award", "2500", "USD");
        funding.getOrganization().getAddress().setCity("London");
        funding.getOrganization().getAddress().setCountry(Iso3166Country.GB);
        
        Mockito.lenient().when(profileFundingManager.getFunding(ORCID, 1L)).thenReturn(funding);
        when(profileFundingManager.updateFunding(eq(ORCID), any(Funding.class), eq(false))).thenAnswer(invocation -> invocation.getArgument(1));
        when(lm.buildLanguageMap(any(), anyBoolean())).thenReturn(new HashMap<>());

        FundingForm form = fundingController.getFundingJson(1L);
        assertEquals("London", form.getCity().getValue());
        assertEquals("GB", form.getCountry().getValue());
        
        form.getCity().setValue("San Jose");
        form.getCountry().setValue("CR");
        form.setDescription(Text.valueOf("Desc"));
        form.setUrl(Text.valueOf("http://test.com"));

        fundingController.postFunding(form);
        
        Funding updatedFunding = createFunding(1L, "Grant # 1", "salary-award", "2500", "USD");
        updatedFunding.getOrganization().getAddress().setCity("San Jose");
        updatedFunding.getOrganization().getAddress().setCountry(Iso3166Country.CR);
        when(profileFundingManager.getFunding(ORCID, 1L)).thenReturn(updatedFunding);

        FundingForm updated = fundingController.getFundingJson(1L);
        assertEquals("San Jose", updated.getCity().getValue());
        assertEquals("CR", updated.getCountry().getValue());
    }

    @Test
    public void testAddFundingWithInvalidDates() throws Exception {
        FundingForm funding = createBlankFundingForm();
        funding.setFundingType(Text.valueOf("award"));
        funding.setCity(Text.valueOf("city"));
        funding.setCountry(Text.valueOf("CR"));
        funding.setFundingName(Text.valueOf("Name"));
        FundingTitleForm title = new FundingTitleForm();
        title.setTitle(Text.valueOf("title"));
        funding.setFundingTitle(title);
        funding.setDescription(Text.valueOf("Desc"));
        funding.setUrl(Text.valueOf("http://test.com"));

        // Check valid start date
        Date startDate = new Date();
        startDate.setMonth("01");
        funding.setStartDate(startDate);
        funding = fundingController.postFunding(funding);
        assertNotNull(funding.getErrors());
        assertEquals(1, funding.getErrors().size());

        // Check valid end date
        funding = createBlankFundingForm();
        funding.setFundingType(Text.valueOf("award"));
        funding.setCity(Text.valueOf("city"));
        funding.setCountry(Text.valueOf("CR"));
        funding.setFundingName(Text.valueOf("Name"));
        funding.setFundingTitle(title);
        funding.setDescription(Text.valueOf("Desc"));
        funding.setUrl(Text.valueOf("http://test.com"));
        Date endDate = new Date();
        endDate.setMonth("01");
        funding.setEndDate(endDate);
        funding = fundingController.postFunding(funding);
        assertEquals(1, funding.getErrors().size());

        // Check end date is after start date
        funding = createBlankFundingForm();
        funding.setFundingType(Text.valueOf("award"));
        funding.setCity(Text.valueOf("city"));
        funding.setCountry(Text.valueOf("CR"));
        funding.setFundingName(Text.valueOf("Name"));
        funding.setFundingTitle(title);
        funding.setDescription(Text.valueOf("Desc"));
        funding.setUrl(Text.valueOf("http://test.com"));

        startDate = new Date();
        startDate.setMonth("01");
        startDate.setYear("2015");
        endDate = new Date();
        endDate.setMonth("01");
        endDate.setYear("2014");
        funding.setStartDate(startDate);
        funding.setEndDate(endDate);

        funding = fundingController.postFunding(funding);
        assertEquals(1, funding.getErrors().size());
    }

    @Test
    public void testGetFundingsJsonSortedBySource() {
        HttpSession session = mock(HttpSession.class);
        Mockito.lenient().when(servletRequest.getSession()).thenReturn(session);
        Mockito.lenient().when(localeManager.getLocale()).thenReturn(new Locale("us", "EN"));

        List<FundingSummary> summaries = new ArrayList<>();
        Fundings fundingsJaxb = new Fundings();
        
        org.orcid.jaxb.model.v3.release.record.summary.FundingGroup group1 = createFundingGroupJaxb(1L);
        group1.getFundingSummary().get(0).setSource(createSource("4444-4444-4444-4441"));
        
        org.orcid.jaxb.model.v3.release.record.summary.FundingGroup group2 = createFundingGroupJaxb(2L);
        group2.getFundingSummary().get(0).setSource(createSource("4444-4444-4444-4442"));
        
        org.orcid.jaxb.model.v3.release.record.summary.FundingGroup group3 = createFundingGroupJaxb(3L);
        group3.getFundingSummary().get(0).setSource(createSource("4444-4444-4444-4443"));
        
        fundingsJaxb.getFundingGroup().add(group1);
        fundingsJaxb.getFundingGroup().add(group2);
        fundingsJaxb.getFundingGroup().add(group3);

        when(profileFundingManager.getFundingSummaryList(ORCID)).thenReturn(summaries);
        when(profileFundingManager.groupFundings(summaries, false)).thenReturn(fundingsJaxb);

        List<FundingGroup> fundings = fundingController.getFundingsJson("source", true);
        assertNotNull(fundings);
        assertEquals(3, fundings.size());
        assertEquals("4444-4444-4444-4441", fundings.get(0).getFundings().get(0).getSource());
        assertEquals("4444-4444-4444-4443", fundings.get(2).getFundings().get(0).getSource());
    }

    private FundingSummary createFundingSummary(Long putCode) {
        FundingSummary summary = new FundingSummary();
        summary.setPutCode(putCode);
        summary.setSource(createSource(ORCID));
        summary.setType(org.orcid.jaxb.model.common.FundingType.AWARD);
        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title("Title"));
        summary.setTitle(fundingTitle);
        summary.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC);
        summary.setOrganization(createOrganization());
        summary.setCreatedDate(new CreatedDate());
        summary.setLastModifiedDate(new LastModifiedDate());
        return summary;
    }

    private org.orcid.jaxb.model.v3.release.record.summary.FundingGroup createFundingGroupJaxb(Long putCode) {
        org.orcid.jaxb.model.v3.release.record.summary.FundingGroup group = new org.orcid.jaxb.model.v3.release.record.summary.FundingGroup();
        FundingSummary summary = createFundingSummary(putCode);
        summary.setDisplayIndex("0");
        group.getFundingSummary().add(summary);
        return group;
    }

    private Source createSource(String orcid) {
        Source source = new Source();
        source.setSourceOrcid(new SourceOrcid(orcid));
        return source;
    }

    private Organization createOrganization() {
        Organization org = new Organization();
        org.setName("Org Name");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("City");
        address.setCountry(Iso3166Country.US);
        org.setAddress(address);
        return org;
    }

    private Funding createFunding(Long putCode, String title, String type, String amount, String currency) {
        Funding funding = new Funding();
        funding.setPutCode(putCode);
        funding.setType(org.orcid.jaxb.model.common.FundingType.fromValue(type));
        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title(title));
        funding.setTitle(fundingTitle);
        if (amount != null) {
            Amount amountObj = new Amount();
            amountObj.setContent(amount);
            amountObj.setCurrencyCode(currency);
            funding.setAmount(amountObj);
        }
        funding.setOrganization(createOrganization());
        funding.setSource(createSource(ORCID));
        return funding;
    }

    private FundingForm createBlankFundingForm() {
        FundingForm result = new FundingForm();
        result.setAmount(new Text());
        result.setCurrencyCode(Text.valueOf(""));
        result.setDescription(new Text());
        result.setFundingName(new Text());
        result.setFundingType(Text.valueOf(""));
        result.setFundingTitle(new FundingTitleForm());
        result.getFundingTitle().setTitle(new Text());
        result.getFundingTitle().setTranslatedTitle(new TranslatedTitleForm());
        result.setExternalIdentifiers(new ArrayList<>());
        result.setStartDate(new Date());
        result.setEndDate(new Date());
        result.setCity(new Text());
        result.setRegion(new Text());
        result.setCountry(new Text());
        result.setUrl(new Text());
        return result;
    }
}
