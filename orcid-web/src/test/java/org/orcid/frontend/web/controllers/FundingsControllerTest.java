package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.ProfileFundingManager;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.common.FundingType;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Amount;
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
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.ajaxForm.FundingTitleForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.grouping.FundingGroup;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class FundingsControllerTest {
    private static final String ORCID = "4444-4444-4444-4443";
    private static final String OTHER_ORCID_1 = "4444-4444-4444-4441";
    private static final String OTHER_ORCID_2 = "4444-4444-4444-4442";

    @Mock
    private LocaleManager localeManager;

    @Mock
    private OrgDisambiguatedManager orgDisambiguatedManager;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private ProfileFundingManager profileFundingManager;

    @Mock
    private ProfileFundingManagerReadOnly profileFundingManagerReadOnly;

    @Mock
    private ContributorUtils contributorUtils;

    @Mock
    private LanguagesMap languagesMap;

    @Mock
    private ActivityManager activityManager;

    private final FundingsController fundingController = new FundingsController() {
        @Override
        public String getMessage(String messageCode, Object... messageParams) {
            return messageCode;
        }

        @Override
        public Locale getUserLocale() {
            return localeManager.getLocale();
        }
    };

    @Before
    public void setUp() {
        fundingController.setLocaleManager(localeManager);
        ReflectionTestUtils.setField(fundingController, "orgDisambiguatedManager", orgDisambiguatedManager);
        ReflectionTestUtils.setField(fundingController, "profileEntityCacheManager", profileEntityCacheManager);
        ReflectionTestUtils.setField(fundingController, "profileFundingManager", profileFundingManager);
        ReflectionTestUtils.setField(fundingController, "profileFundingManagerReadOnly", profileFundingManagerReadOnly);
        ReflectionTestUtils.setField(fundingController, "contributorUtils", contributorUtils);
        ReflectionTestUtils.setField(fundingController, "lm", languagesMap);
        ReflectionTestUtils.setField(fundingController, "cacheManager", activityManager);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(ORCID, "password",
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        lenient().when(localeManager.resolveMessage(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(localeManager.getLocale()).thenReturn(Locale.US);
        when(languagesMap.buildLanguageMap(any(Locale.class), eq(false))).thenReturn(buildLanguageMap());
        when(profileEntityCacheManager.retrieve(anyString())).thenReturn(buildProfileEntity());
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testSearchDisambiguated() {
        when(orgDisambiguatedManager.searchOrgsFromSolr(eq("search"), eq(0), eq(0), eq(true))).thenReturn(getListOfMixedOrgsDisambiguated());

        List<Map<String, String>> results = fundingController.searchDisambiguated("search", 0, true);
        assertEquals(4, results.size());
        assertEquals("first", results.get(0).get("value"));
        assertEquals("second", results.get(1).get("value"));
        assertEquals("third", results.get(2).get("value"));
        assertEquals("fourth", results.get(3).get("value"));
    }

    @Test
    public void testValidateAmountLocaleEN_US() {
        when(localeManager.getLocale()).thenReturn(Locale.forLanguageTag("en-US"));
        String[] validAmounts = { "1", "10", "100", "1000", "10000", "100000", "1000000", "10000000", "10000000", "1.0", "1.00", "10.0", "10.00", "100.0", "100.00",
                "1000.0", "1000.00", "1,000", "1,000.0", "1,000.00", "10,000", "100,000", "1,000,000", "10,000,000", "100,000,000", "100,000,000.0", "100,000,000.00",
                "1,000,000,000", "1,000,000,000.0", "1,000,000,000.00", "10,000,000,000", "10,000,000.99" };
        String[] invalidAmounts = { "a", ".", "1 000", "1 000 000", "1,000 000", "1 000,000", "1'000", "1'000'000", "1'000.0", "1'000.00", "$1000", "$100", "1.000.000",
                "1.000,00" };

        for (String amount : validAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertNotNull(form.getAmount().getErrors());
            assertEquals(0, form.getAmount().getErrors().size());
        }

        for (String amount : invalidAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertEquals(1, form.getAmount().getErrors().size());
        }
    }

    @Test
    public void testVAlidateAmountLocaleDE_CH() {
        when(localeManager.getLocale()).thenReturn(Locale.forLanguageTag("de-CH"));
        String[] validAmounts = { "1", "10", "100", "1000", "10000", "100000", "1000000", "10000000", "10000000", "1.0", "1.00", "10.0", "10.00", "100.0", "100.00",
                "1000.0", "1000.00", "1'000", "1'000.0", "1'000.00", "10'000", "100'000", "1'000'000", "10'000'000", "100'000'000", "100'000'000.0", "100'000'000.00",
                "1'000'000'000", "1'000'000'000.0", "1'000'000'000.00", "10'000'000'000", "10'000'000.99" };
        String[] invalidAmounts = { "a", ".", "1 000", "1 000 000", "1,000 000", "1 000,000", "1,000", "1,000,000", "1,000.0", "1,000.00", "$1000", "$100" };

        for (String amount : validAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertNotNull(form.getAmount().getErrors());
            assertEquals(0, form.getAmount().getErrors().size());
        }

        for (String amount : invalidAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertEquals(1, form.getAmount().getErrors().size());
        }
    }

    @Test
    public void testValidateAmountLocaleRU() {
        when(localeManager.getLocale()).thenReturn(Locale.forLanguageTag("ru"));
        String[] validAmounts = { "1", "10", "100", "1000", "10000", "100000", "1000000", "10000000", "10000000", "1,0", "1,00", "10,0", "10,00", "100,0", "100,00",
                "1000,0", "1000,00", "1 000", "1 000,0", "1 000,00", "10 000", "100 000", "1 000 000", "10 000 000", "100 000 000", "100 000 000,0", "100 000 000,00",
                "1 000 000 000", "1 000 000 000,0", "1 000 000 000,00", "10 000 000 000", "10 000 000,99" };
        String[] invalidAmounts = { "a", ".", "1,000,000", "1,000.000", "1 000.000", "1'000", "1'000'000", "1'000.0", "1'000.00", "$1000", "$100", "1.000.000",
                "1.000,00", "1 000 000.0", "1 000 000.00" };

        for (String amount : validAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertNotNull(form.getAmount().getErrors());
            assertEquals(0, form.getAmount().getErrors().size());
        }

        for (String amount : invalidAmounts) {
            FundingForm form = new FundingForm();
            form.setAmount(Text.valueOf(amount));
            form.setCurrencyCode(Text.valueOf("USD"));
            form = fundingController.validateAmount(form);
            assertNotNull(form.getAmount());
            assertEquals(1, form.getAmount().getErrors().size());
        }
    }

    @Test
    public void validateBigDecimalConversionLocaleUS_EN() {
        when(localeManager.getLocale()).thenReturn(Locale.forLanguageTag("en-US"));
        BigDecimal expected100000 = new BigDecimal(100000);
        BigDecimal expected1000 = new BigDecimal(1000);
        BigDecimal expected1000_99 = new BigDecimal("1000.99").setScale(2, RoundingMode.FLOOR);
        BigDecimal expected1 = new BigDecimal(1);
        String[] amounts100000 = { "100000", "100000.0", "100000.00", "100,000", "100,000.0", "100,000.00" };
        String[] amounts1000 = { "1000", "1,000", "1000.0", "1000.00", "1,000.0", "1,000.00" };
        String[] amounts1000_99 = { "1000.99", "1,000.99" };
        String[] amounts1 = { "1", "1.0", "1.00", "1.000" };

        assertBigDecimals(amounts100000, expected100000);
        assertBigDecimals(amounts1000, expected1000);
        assertBigDecimals(amounts1000_99, expected1000_99);
        assertBigDecimals(amounts1, expected1);
    }

    @Test
    public void validateBigDecimalConversionLocaleDE_CH() {
        when(localeManager.getLocale()).thenReturn(Locale.forLanguageTag("de-CH"));
        BigDecimal expected100000 = new BigDecimal(100000);
        BigDecimal expected1000 = new BigDecimal(1000);
        BigDecimal expected1000_99 = new BigDecimal("1000.99").setScale(2, RoundingMode.FLOOR);
        BigDecimal expected1 = new BigDecimal(1);
        String[] amounts100000 = { "100000", "100'000.0", "100'000.00", "100'000" };
        String[] amounts1000 = { "1000", "1'000", "1000.0", "1000.00", "1'000.0", "1'000.00" };
        String[] amounts1000_99 = { "1000.99", "1'000.99" };
        String[] amounts1 = { "1", "1.0", "1.00", "1.000" };

        assertBigDecimals(amounts100000, expected100000);
        assertBigDecimals(amounts1000, expected1000);
        assertBigDecimals(amounts1000_99, expected1000_99);
        assertBigDecimals(amounts1, expected1);
    }

    @Test
    public void validateBigDecimalConversionLocaleRU() {
        when(localeManager.getLocale()).thenReturn(Locale.forLanguageTag("ru"));
        BigDecimal expected100000 = new BigDecimal(100000);
        BigDecimal expected1000 = new BigDecimal(1000);
        BigDecimal expected1000_99 = new BigDecimal("1000.99").setScale(2, RoundingMode.FLOOR);
        BigDecimal expected1 = new BigDecimal(1);
        String[] amounts100000 = { "100000", "100 000,0", "100 000,00", "100 000" };
        String[] amounts1000 = { "1000", "1 000", "1000,0", "1000,00", "1 000,0", "1 000,00" };
        String[] amounts1000_99 = { "1000,99", "1 000,99" };
        String[] amounts1 = { "1", "1,0", "1,00", "1,000" };

        assertBigDecimals(amounts100000, expected100000);
        assertBigDecimals(amounts1000, expected1000);
        assertBigDecimals(amounts1000_99, expected1000_99);
        assertBigDecimals(amounts1, expected1);
    }

    @Test
    public void testGetFundingsJson() {
        when(profileFundingManager.getFundingSummaryList(eq(ORCID))).thenReturn(new ArrayList<>());
        when(profileFundingManager.groupFundings(anyList(), eq(false))).thenReturn(buildFundingsForSorting());

        List<FundingGroup> fundings = fundingController.getFundingsJson("title", true);
        assertNotNull(fundings);
        assertEquals(3, fundings.size());
        assertEquals(1L, fundings.get(0).getGroupId());
        assertEquals(Text.valueOf(1L), fundings.get(0).getFundings().get(0).getPutCode());
        assertEquals(2L, fundings.get(1).getGroupId());
        assertEquals(Text.valueOf(2L), fundings.get(1).getFundings().get(0).getPutCode());
        assertEquals(3L, fundings.get(2).getGroupId());
        assertEquals(Text.valueOf(3L), fundings.get(2).getFundings().get(0).getPutCode());
    }

    @Test
    public void testAddFundingWithoutAmount() throws Exception {
        FundingForm funding = getFundingForm();
        funding.setFundingType(Text.valueOf("award"));

        FundingForm result = fundingController.postFunding(funding);
        assertEquals(funding.getFundingTitle().getTitle(), result.getFundingTitle().getTitle());
        assertEquals(funding.getFundingType(), result.getFundingType());
        assertEquals(funding.getCountry(), result.getCountry());
        assertEquals(funding.getCity(), result.getCity());
        assertEquals(funding.getRegion(), result.getRegion());
        assertNotNull(result.getErrors());
        assertEquals(0, result.getErrors().size());
        verify(profileFundingManager).createFunding(eq(ORCID), any(Funding.class));
    }

    @Test
    public void testAddFunding() throws Exception {
        FundingForm funding = getFundingForm();
        funding.setFundingType(Text.valueOf("award"));
        funding.setAmount(Text.valueOf("1000"));
        funding.setCurrencyCode(Text.valueOf("USD"));

        FundingForm result = fundingController.postFunding(funding);
        assertEquals(funding.getFundingTitle().getTitle(), result.getFundingTitle().getTitle());
        assertEquals(funding.getFundingType(), result.getFundingType());
        assertEquals(funding.getCountry(), result.getCountry());
        assertEquals(funding.getCity(), result.getCity());
        assertEquals(funding.getRegion(), result.getRegion());
        assertNotNull(result.getErrors());
        assertEquals(0, result.getErrors().size());
        assertEquals(fundingController.getAmountAsBigDecimal(funding.getAmount().getValue()),
                fundingController.getAmountAsBigDecimal(result.getAmount().getValue()));
        verify(profileFundingManager).createFunding(eq(ORCID), any(Funding.class));
    }

    @Test
    public void testAddAmountWithoutCurrencyCode() throws Exception {
        FundingForm funding = getFundingForm();
        funding.setFundingType(Text.valueOf("award"));
        funding.setAmount(Text.valueOf("1000"));

        FundingForm result = fundingController.postFunding(funding);
        assertNotNull(result);
        assertNotNull(result.getErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("Invalid.fundings.currency", result.getErrors().get(0));
    }

    @Test
    public void getFunding() {
        when(profileFundingManagerReadOnly.getFunding(eq(ORCID), eq(1L))).thenReturn(createFundingRecord("1", ORCID, "Grant # 1", "2500",
                "USD", "London", "GB", "salary-award"));

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
    public void testEditFunding() throws Exception {
        when(profileFundingManagerReadOnly.getFunding(eq(ORCID), eq(1L))).thenReturn(createFundingRecord("1", ORCID, "Grant # 1", "2500",
                "USD", "London", "GB", "salary-award"));

        FundingForm funding = fundingController.getFundingJson(Long.valueOf("1"));
        funding.getFundingTitle().getTitle().setValue("Grant # 1 - updated");
        funding.getAmount().setValue("3500");
        funding.getCurrencyCode().setValue("CRC");

        FundingForm result = fundingController.postFunding(funding);
        assertNotNull(result);
        assertNotNull(result.getErrors());
        assertEquals(0, result.getErrors().size());

        ArgumentCaptor<Funding> fundingCaptor = ArgumentCaptor.forClass(Funding.class);
        verify(profileFundingManager).updateFunding(eq(ORCID), fundingCaptor.capture());
        Funding updated = fundingCaptor.getValue();
        assertEquals("Grant # 1 - updated", updated.getTitle().getTitle().getContent());
        assertEquals("3500", updated.getAmount().getContent());
        assertEquals("CRC", updated.getAmount().getCurrencyCode());
    }

    @Test
    public void testEditOrgOnExistingFunding() throws Exception {
        when(profileFundingManagerReadOnly.getFunding(eq(ORCID), eq(1L))).thenReturn(createFundingRecord("1", ORCID, "Grant # 1", "2500",
                "USD", "London", "GB", "salary-award"));

        FundingForm funding = fundingController.getFundingJson(Long.valueOf("1"));
        funding.getCity().setValue("San Jose");
        funding.getCountry().setValue("CR");

        fundingController.postFunding(funding);

        ArgumentCaptor<Funding> fundingCaptor = ArgumentCaptor.forClass(Funding.class);
        verify(profileFundingManager).updateFunding(eq(ORCID), fundingCaptor.capture());
        Funding updated = fundingCaptor.getValue();
        assertEquals("San Jose", updated.getOrganization().getAddress().getCity());
        assertEquals(Iso3166Country.CR, updated.getOrganization().getAddress().getCountry());
    }

    @Test
    public void testAddFundingWithInvalidDates() throws Exception {
        FundingForm funding = getFundingForm();

        Date startDate = new Date();
        startDate.setMonth("01");
        funding.setStartDate(startDate);
        funding = fundingController.postFunding(funding);
        assertNotNull(funding);
        assertNotNull(funding.getErrors());
        assertEquals(1, funding.getErrors().size());
        assertEquals("common.dates.invalid", funding.getErrors().get(0));

        funding = getFundingForm();
        Date endDate = new Date();
        endDate.setMonth("01");
        funding.setEndDate(endDate);
        funding = fundingController.postFunding(funding);
        assertNotNull(funding);
        assertNotNull(funding.getErrors());
        assertEquals(1, funding.getErrors().size());
        assertEquals("common.dates.invalid", funding.getErrors().get(0));

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
        assertEquals("fundings.endDate.after", funding.getErrors().get(0));
    }

    @Test
    public void testGetFundingsJsonSortedBySource() {
        when(profileFundingManager.getFundingSummaryList(eq(ORCID))).thenReturn(new ArrayList<>());
        when(profileFundingManager.groupFundings(anyList(), eq(false))).thenReturn(buildFundingsForSourceSort());

        List<FundingGroup> fundings = fundingController.getFundingsJson("source", true);
        assertNotNull(fundings);
        assertEquals(3, fundings.size());
        assertEquals(OTHER_ORCID_1, fundings.get(0).getFundings().get(0).getSource());
        assertEquals(OTHER_ORCID_2, fundings.get(1).getFundings().get(0).getSource());
        assertEquals(ORCID, fundings.get(2).getFundings().get(0).getSource());
    }

    @Test(expected = RuntimeException.class)
    public void testEditFundingFailurePropagates() throws Exception {
        when(profileFundingManagerReadOnly.getFunding(eq(ORCID), eq(3L))).thenReturn(createFundingRecord("3", OTHER_ORCID_1, "Grant # 3", "2500",
                "USD", "London", "GB", "salary-award"));
        doThrow(new RuntimeException("not allowed")).when(profileFundingManager).updateFunding(eq(ORCID), any(Funding.class));

        FundingForm funding = fundingController.getFundingJson(Long.valueOf("3"));
        fundingController.postFunding(funding);
    }

    private void assertBigDecimals(String[] amounts, BigDecimal expected) {
        for (String amount : amounts) {
            try {
                BigDecimal result = fundingController.getAmountAsBigDecimal(amount);
                assertEquals("Amount is: " + result + " but it should be: " + expected, expected, result);
            } catch (Exception e) {
                throw new AssertionError("Amount: " + amount + " couldn't parsed to: " + expected, e);
            }
        }
    }

    private List<OrgDisambiguated> getListOfMixedOrgsDisambiguated() {
        OrgDisambiguated first = new OrgDisambiguated();
        first.setValue("first");
        OrgDisambiguated second = new OrgDisambiguated();
        second.setValue("second");
        OrgDisambiguated third = new OrgDisambiguated();
        third.setValue("third");
        OrgDisambiguated fourth = new OrgDisambiguated();
        fourth.setValue("fourth");
        return Arrays.asList(first, second, third, fourth);
    }

    private Map<String, String> buildLanguageMap() {
        Map<String, String> languages = new HashMap<>();
        languages.put("en", "English");
        languages.put("de", "German");
        languages.put("ru", "Russian");
        return languages;
    }

    private ProfileEntity buildProfileEntity() {
        ProfileEntity profile = new ProfileEntity();
        profile.setActivitiesVisibilityDefault("LIMITED");
        return profile;
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

    private Funding createFundingRecord(String putCode, String sourceOrcid, String title, String amount, String currencyCode,
            String city, String countryCode, String fundingType) {
        Funding funding = new Funding();
        funding.setPutCode(Long.valueOf(putCode));

        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title(title));
        funding.setTitle(fundingTitle);

        Amount fundingAmount = new Amount();
        fundingAmount.setContent(amount);
        fundingAmount.setCurrencyCode(currencyCode);
        funding.setAmount(fundingAmount);

        funding.setType(FundingType.fromValue(fundingType));
        funding.setSource(createSource(sourceOrcid));

        Organization organization = new Organization();
        organization.setName("OrgName");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity(city);
        address.setCountry(Iso3166Country.fromValue(countryCode));
        organization.setAddress(address);
        funding.setOrganization(organization);
        funding.setVisibility(Visibility.PUBLIC);
        return funding;
    }

    private Source createSource(String orcid) {
        Source source = new Source();
        source.setSourceOrcid(new SourceOrcid(orcid));
        return source;
    }

    private Fundings buildFundingsForSorting() {
        Fundings fundings = new Fundings();
        fundings.getFundingGroup().add(createSummaryGroup(1L, "Alpha", OTHER_ORCID_1, 1L));
        fundings.getFundingGroup().add(createSummaryGroup(2L, "Beta", OTHER_ORCID_2, 2L));
        fundings.getFundingGroup().add(createSummaryGroup(3L, "Gamma", ORCID, 3L));
        return fundings;
    }

    private Fundings buildFundingsForSourceSort() {
        Fundings fundings = new Fundings();
        fundings.getFundingGroup().add(createSummaryGroup(1L, "Alpha", OTHER_ORCID_1, 1L));
        fundings.getFundingGroup().add(createSummaryGroup(2L, "Beta", OTHER_ORCID_2, 2L));
        fundings.getFundingGroup().add(createSummaryGroup(3L, "Gamma", ORCID, 3L));
        return fundings;
    }

    private org.orcid.jaxb.model.v3.release.record.summary.FundingGroup createSummaryGroup(Long putCode, String title, String sourceOrcid, Long displayIndex) {
        FundingSummary summary = new FundingSummary();
        summary.setPutCode(putCode);
        summary.setDisplayIndex(String.valueOf(displayIndex));
        summary.setType(FundingType.AWARD);
        summary.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC);
        summary.setSource(createSource(sourceOrcid));

        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title(title));
        summary.setTitle(fundingTitle);

        Organization organization = new Organization();
        organization.setName(title + " Org");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        organization.setAddress(address);
        summary.setOrganization(organization);

        org.orcid.jaxb.model.v3.release.record.summary.FundingGroup group = new org.orcid.jaxb.model.v3.release.record.summary.FundingGroup();
        group.getFundingSummary().add(summary);
        return group;
    }
}
