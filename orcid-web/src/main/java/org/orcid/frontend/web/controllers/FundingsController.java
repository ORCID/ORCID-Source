package org.orcid.frontend.web.controllers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ProfileFundingManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.utils.v3.activities.FundingComparators;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.common.FundingType;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.Errors;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.ajaxForm.FundingTitleForm;
import org.orcid.pojo.ajaxForm.OrgDefinedFundingSubType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitleForm;
import org.orcid.pojo.ajaxForm.Visibility;
import org.orcid.pojo.grouping.FundingGroup;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Angel Montenegro
 */
@Controller("fundingsController")
@RequestMapping(value = { "/fundings" })
public class FundingsController extends BaseWorkspaceController {
    private static final Pattern LANGUAGE_CODE = Pattern.compile("([a-zA-Z]{2})(_[a-zA-Z]{2}){0,2}");
    private static final String DEFAULT_FUNDING_EXTERNAL_IDENTIFIER_TYPE = "Grant number";
    private static final String DEFAULT_FUNDING_EXTERNAL_IDENTIFIER_TYPE_CODE = "grant_number";
    
    @Resource(name = "profileFundingManagerV3")
    private ProfileFundingManager profileFundingManager;

    @Resource
    private OrgDisambiguatedManager orgDisambiguatedManager;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource(name = "activityManagerV3")
    private ActivityManager cacheManager;

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    /**
     * Returns a blank funding form
     * */
    @RequestMapping(value = "/funding.json", method = RequestMethod.GET)
    public @ResponseBody
    FundingForm getFunding() {
        FundingForm result = new FundingForm();
        result.setAmount(new Text());
        result.setCurrencyCode(Text.valueOf(""));
        result.setDescription(new Text());
        result.setFundingName(new Text());
        result.setFundingType(Text.valueOf(""));
        result.setSourceName(new String());
        result.setAssertionOriginClientId(new String());
        result.setAssertionOriginName(new String());
        result.setAssertionOriginOrcid(new String());

        OrgDefinedFundingSubType subtype = new OrgDefinedFundingSubType();
        subtype.setAlreadyIndexed(false);
        subtype.setSubtype(Text.valueOf(""));
        result.setOrganizationDefinedFundingSubType(subtype);

        FundingTitleForm title = new FundingTitleForm();
        title.setTitle(new Text());
        TranslatedTitleForm tt = new TranslatedTitleForm();
        tt.setContent(new String());
        tt.setLanguageCode(new String());
        tt.setLanguageName(new String());
        title.setTranslatedTitle(tt);
        result.setFundingTitle(title);
        result.setUrl(new Text());
        
        ProfileEntity profile = profileEntityCacheManager.retrieve(getEffectiveUserOrcid());
        org.orcid.jaxb.model.v3.release.common.Visibility defaultVis = null;
        if (profile.getActivitiesVisibilityDefault() != null) {
            defaultVis = org.orcid.jaxb.model.v3.release.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
        } else {
            defaultVis = org.orcid.jaxb.model.v3.release.common.Visibility.valueOf(OrcidVisibilityDefaults.FUNDING_DEFAULT.getVisibility().name());
        }
        Visibility v = Visibility.valueOf(defaultVis);
        
        result.setVisibility(v);
        Date startDate = new Date();
        result.setStartDate(startDate);
        startDate.setDay("");
        startDate.setMonth("");
        startDate.setYear("");
        Date endDate = new Date();
        result.setEndDate(endDate);
        endDate.setDay("");
        endDate.setMonth("");
        endDate.setYear("");

        // Set empty external identifier
        List<ActivityExternalIdentifier> emptyExternalIdentifiers = new ArrayList<ActivityExternalIdentifier>();
        ActivityExternalIdentifier f = new ActivityExternalIdentifier();
        f.setExternalIdentifierType(Text.valueOf(DEFAULT_FUNDING_EXTERNAL_IDENTIFIER_TYPE));
        f.setUrl(new Text());
        f.setExternalIdentifierId(new Text());
        f.setRelationship(Text.valueOf(Relationship.SELF.value()));
        emptyExternalIdentifiers.add(f);
        result.setExternalIdentifiers(emptyExternalIdentifiers);

        result.setCity(new Text());
        result.setCountry(Text.valueOf(""));
        result.setRegion(new Text());

        return result;
    }
    
    /**
     * Deletes a funding by putCode
     * */
    @RequestMapping(value = "/funding.json", method = RequestMethod.DELETE)
    public @ResponseBody Errors deleteFundingByPutCodeJson(@RequestParam(value = "id") String fundingId) {
        Errors errors = new Errors();
        boolean deleted = profileFundingManager.removeProfileFunding(getEffectiveUserOrcid(), Long.valueOf(fundingId));        
        if(!deleted) {
                //TODO: Log error in case the affiliation wasn't deleted
        }
        return errors;
    }

    @RequestMapping(value = "/fundingGroups.json", method = RequestMethod.GET)
    public @ResponseBody List<FundingGroup> getFundingsJson(@RequestParam("sort") String sort, @RequestParam("sortAsc") boolean sortAsc) {
        List<FundingGroup> fundingGroups = new ArrayList<>();
        List<FundingSummary> summaries = profileFundingManager.getFundingSummaryList(getEffectiveUserOrcid());
        Fundings fundings = profileFundingManager.groupFundings(summaries, false);
        for (org.orcid.jaxb.model.v3.release.record.summary.FundingGroup group : fundings.getFundingGroup()) {
            FundingGroup fundingGroup = FundingGroup.valueOf(group);
            for(org.orcid.jaxb.model.v3.release.record.summary.FundingSummary summary : group.getFundingSummary()) {
                if(summary.getSource().retrieveSourcePath().equals(getCurrentUserOrcid())) {
                    fundingGroup.setUserVersionPresent(true);
                    break;
                }
            }
            for(FundingForm summaryForm : fundingGroup.getFundings()) {
                 if(summaryForm.getFundingType().getValue() != null) {
                     summaryForm.setFundingTypeForDisplay(getMessage(buildInternationalizationKey(org.orcid.jaxb.model.message.FundingType.class, summaryForm.getFundingType().getValue())));
                 }         
            }
            fundingGroups.add(fundingGroup);
        }

        fundingGroups.sort(FundingComparators.getInstance(sort, sortAsc));
        return fundingGroups;
    }
    
    /**
     * Get a funding item by put code
     * */
    @RequestMapping(value = "/fundingDetails.json", method = RequestMethod.GET)
    public @ResponseBody
    FundingForm getFundingJson(@RequestParam(value = "id") Long id) {
        if (id == null)
            return null;        
        Map<String, String> languages = lm.buildLanguageMap(getUserLocale(), false);
        Funding funding = profileFundingManager.getFunding(getEffectiveUserOrcid(), id);
        FundingForm form = FundingForm.valueOf(funding);
               
        if (funding.getType() != null) {
            form.setFundingTypeForDisplay(getMessage(buildInternationalizationKey(org.orcid.jaxb.model.message.FundingType.class, funding.getType().value())));
        }
        // Set translated title language name
        if (!(funding.getTitle().getTranslatedTitle() == null) && !StringUtils.isEmpty(funding.getTitle().getTranslatedTitle().getLanguageCode())) {
            String languageName = languages.get(funding.getTitle().getTranslatedTitle().getLanguageCode());
            form.getFundingTitle().getTranslatedTitle().setLanguageName(languageName);
        }

        // Set the formatted amount
        if (funding.getAmount() != null && StringUtils.isNotBlank(funding.getAmount().getContent())) {
            BigDecimal bigDecimal = new BigDecimal(funding.getAmount().getContent());
            String formattedAmount = formatAmountString(bigDecimal);
            form.setAmount(Text.valueOf(formattedAmount));
        }

        form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, funding.getOrganization().getAddress().getCountry().name())));
        return form;
    }

    /**
     * Persist a funding object on database
     * */
    @RequestMapping(value = "/funding.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm postFunding(@RequestBody FundingForm funding) throws Exception {
        // Reset errors
        funding.setErrors(new ArrayList<String>());
        // Remove empty external identifiers
        removeEmptyExternalIds(funding);

        validateName(funding);
        validateAmount(funding);
        validateCurrency(funding);
        validateTitle(funding);
        validateTranslatedTitle(funding);
        validateDescription(funding);
        validateUrl(funding);
        validateDates(funding);
        validateExternalIdentifiers(funding);
        validateType(funding);
        validateOrganizationDefinedType(funding);
        validateCity(funding);
        validateRegion(funding);
        validateCountry(funding);

        copyErrors(funding.getCity(), funding);
        copyErrors(funding.getRegion(), funding);
        copyErrors(funding.getCountry(), funding);
        copyErrors(funding.getFundingName(), funding);
        copyErrors(funding.getAmount(), funding);
        copyErrors(funding.getCurrencyCode(), funding);
        copyErrors(funding.getFundingTitle().getTitle(), funding);
        copyErrors(funding.getDescription(), funding);
        copyErrors(funding.getUrl(), funding);
        copyErrors(funding.getFundingType(), funding);
        
        if(funding.getStartDate() != null) 
            copyErrors(funding.getStartDate(), funding);
        
        if(funding.getEndDate() != null)
            copyErrors(funding.getEndDate(), funding);

        if (funding.getFundingTitle().getTranslatedTitle() != null)
            copyErrors(funding.getFundingTitle().getTranslatedTitle(), funding);

        if (funding.getOrganizationDefinedFundingSubType() != null)
            copyErrors(funding.getOrganizationDefinedFundingSubType().getSubtype(), funding);

        for (ActivityExternalIdentifier extId : funding.getExternalIdentifiers()) {
            if (extId.getExternalIdentifierType() != null)
                copyErrors(extId.getExternalIdentifierType(), funding);
            if (extId.getUrl() != null)
                copyErrors(extId.getUrl(), funding);
            if (extId.getExternalIdentifierId() != null)
                copyErrors(extId.getExternalIdentifierId(), funding);
        }

        // If there are no errors, persist to DB
        if (funding.getErrors().isEmpty()) {
            if (PojoUtil.isEmpty(funding.getPutCode())) {
                addFunding(funding);
            } else {
                editFunding(funding);
            }
        }

        return funding;
    }

    private void addFunding(FundingForm fundingForm) throws Exception {
        // Set the right value for the amount
        setAmountWithTheCorrectFormat(fundingForm);
        // Set default type for external identifiers
        setTypeToExternalIdentifiers(fundingForm);
        // Add to database
        Funding funding = fundingForm.toFunding();
        funding = profileFundingManager.createFunding(getEffectiveUserOrcid(), funding, false);        

        // Send the new funding sub type for indexing
        if (fundingForm.getOrganizationDefinedFundingSubType() != null && !PojoUtil.isEmpty(fundingForm.getOrganizationDefinedFundingSubType().getSubtype())
                && !fundingForm.getOrganizationDefinedFundingSubType().isAlreadyIndexed())
            profileFundingManager.addFundingSubType(fundingForm.getOrganizationDefinedFundingSubType().getSubtype().getValue(), getEffectiveUserOrcid());
    }

    private void editFunding(FundingForm fundingForm) throws Exception {
        // Set the right value for the amount
        setAmountWithTheCorrectFormat(fundingForm);
        // Set the credit name
        setTypeToExternalIdentifiers(fundingForm);
        
        // Add to database
        Funding funding = fundingForm.toFunding();
        funding = profileFundingManager.updateFunding(getEffectiveUserOrcid(), funding, false);

        // Send the new funding sub type for indexing
        if (fundingForm.getOrganizationDefinedFundingSubType() != null && !PojoUtil.isEmpty(fundingForm.getOrganizationDefinedFundingSubType().getSubtype())
                && !fundingForm.getOrganizationDefinedFundingSubType().isAlreadyIndexed())
            profileFundingManager.addFundingSubType(fundingForm.getOrganizationDefinedFundingSubType().getSubtype().getValue(), getEffectiveUserOrcid());
    }

    private void removeEmptyExternalIds(FundingForm funding) {
        List<ActivityExternalIdentifier> extIds = funding.getExternalIdentifiers();
        List<ActivityExternalIdentifier> updatedExtIds = new ArrayList<ActivityExternalIdentifier>();
        if (extIds != null) {
            // For all external identifiers
            for (ActivityExternalIdentifier extId : extIds) {
                // Keep only the ones that contains a value or url
                if (!PojoUtil.isEmpty(extId.getExternalIdentifierId()) || !PojoUtil.isEmpty(extId.getUrl())) {
                    updatedExtIds.add(extId);
                }
            }
        }
        funding.setExternalIdentifiers(updatedExtIds);
    }

    private void setTypeToExternalIdentifiers(FundingForm funding) {
        if (funding == null || funding.getExternalIdentifiers() == null || funding.getExternalIdentifiers().isEmpty())
            return;
        for (ActivityExternalIdentifier extId : funding.getExternalIdentifiers()) {
            extId.setExternalIdentifierType(Text.valueOf(DEFAULT_FUNDING_EXTERNAL_IDENTIFIER_TYPE_CODE));
        }
    }

    private void setAmountWithTheCorrectFormat(FundingForm funding) throws Exception {
        if (!PojoUtil.isEmpty(funding.getAmount())) {
            String amount = funding.getAmount().getValue();
            BigDecimal bigDecimal = getAmountAsBigDecimal(amount);
            funding.setAmount(Text.valueOf(bigDecimal.toString()));
        }
    }

    /**
     * Updates a funding visibility
     */
    @RequestMapping(value = "/funding.json", method = RequestMethod.PUT)
    public @ResponseBody
    FundingForm updateProfileFundingJson(HttpServletRequest request, @RequestBody FundingForm fundingForm) {
        if(fundingForm != null && !PojoUtil.isEmpty(fundingForm.getPutCode())) {
            profileFundingManager.updateProfileFundingVisibility(getEffectiveUserOrcid(), Long.valueOf(fundingForm.getPutCode().getValue()),
                    fundingForm.getVisibility().getVisibility());
        }        
        return fundingForm;
    }
    
    /**
     * Updates visibility on multiple fundings
     */
    @RequestMapping(value = "/{fundingIdsStr}/visibility/{visibilityStr}", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Long> updateAffiliationVisibilities(@PathVariable("fundingIdsStr") String fundingIdsStr, @PathVariable("visibilityStr") String visibilityStr) {
        String orcid = getEffectiveUserOrcid();
        ArrayList<Long> fundIds = new ArrayList<Long>();
        for (String fundId : fundingIdsStr.split(","))
            fundIds.add(new Long(fundId));
        profileFundingManager.updateProfileFundingVisibilities(orcid, fundIds, org.orcid.jaxb.model.v3.release.common.Visibility.fromValue(visibilityStr));
        return fundIds;
    }

    /**
     * Transforms a string into a BigDecimal
     * 
     * @param amount
     * @return a BigDecimal containing the given amount
     * @throws Exception
     *             if the amount cannot be correctly parse into a BigDecimal
     * */
    public BigDecimal getAmountAsBigDecimal(String amount) throws Exception {
        Locale locale = getUserLocale();
        return getAmountAsBigDecimal(amount, locale);
    }

    /**
     * Transforms a string into a BigDecimal
     * 
     * @param amount
     * @param locale
     * @return a BigDecimal containing the given amount
     * @throws Exception
     *             if the amount cannot be correctly parse into a BigDecimal
     * */
    public BigDecimal getAmountAsBigDecimal(String amount, Locale locale) throws Exception {
        try {
            ParsePosition parsePosition = new ParsePosition(0);
            DecimalFormat numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
            DecimalFormatSymbols symbols = numberFormat.getDecimalFormatSymbols();
            /**
             * When spaces are allowed, the grouping separator is the character
             * 160, which is a non-breaking space So, lets change it so it uses
             * the default space as a separator
             * */
            if (symbols.getGroupingSeparator() == 160) {
                symbols.setGroupingSeparator(' ');
            }
            numberFormat.setDecimalFormatSymbols(symbols);
            Number number = numberFormat.parse(amount, parsePosition);
            if (number == null || parsePosition.getIndex() != amount.length()) {
                throw new Exception();
            }
            return new BigDecimal(number.toString());
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Get a string with the proper amount format
     * 
     * @param local
     * @return an example string showing how the amount should be entered
     * */
    private String getSampleAmountInProperFormat(Locale locale) {
        double example = 1234567.89;
        NumberFormat numberFormatExample = NumberFormat.getNumberInstance(locale);
        return numberFormatExample.format(example);
    }

    /**
     * Format a big decimal based on a locale
     * 
     * @param bigDecimal
     * @param currencyCode
     * @return a string with the number formatted based on the locale
     * */
    private String formatAmountString(BigDecimal bigDecimal) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(localeManager.getLocale());
        return numberFormat.format(bigDecimal);
    }

    /**
     * Validators
     * */
    @RequestMapping(value = "/funding/amountValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateAmount(@RequestBody FundingForm funding) {
        funding.getAmount().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(funding.getAmount())) {
            // Validate amount
            String amount = funding.getAmount().getValue();
            Locale locale = getUserLocale();
            try {
                getAmountAsBigDecimal(amount, locale);
            } catch (Exception pe) {
                setError(funding.getAmount(), "Invalid.fundings.amount", getSampleAmountInProperFormat(locale));
            }
            // Validate if currency code is selected
            if (PojoUtil.isEmpty(funding.getCurrencyCode()))
                setError(funding.getAmount(), "Invalid.fundings.currency");
        } else if (!PojoUtil.isEmpty(funding.getCurrencyCode())) {
            setError(funding.getAmount(), "Invalid.fundings.currency_not_empty");
        }
        return funding;
    }

    @RequestMapping(value = "/funding/currencyValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateCurrency(@RequestBody FundingForm funding) {
        funding.getCurrencyCode().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(funding.getCurrencyCode())) {
            try {
                Currency.getInstance(funding.getCurrencyCode().getValue());
            } catch (IllegalArgumentException iae) {
                setError(funding.getCurrencyCode(), "Invalid.fundings.currency");
            }
        }
        return funding;
    }

    @RequestMapping(value = "/funding/orgNameValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateName(@RequestBody FundingForm funding) {
        funding.getFundingName().setErrors(new ArrayList<String>());
        if (funding.getFundingName().getValue() == null || funding.getFundingName().getValue().trim().length() == 0) {
            setError(funding.getFundingName(), "NotBlank.fundings.name");
        } else {
            if (funding.getFundingName().getValue().trim().length() > 1000) {
                setError(funding.getFundingName(), "common.length_less_1000");
            }
        }
        return funding;
    }

    @RequestMapping(value = "/funding/titleValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateTitle(@RequestBody FundingForm funding) {
        funding.getFundingTitle().getTitle().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(funding.getFundingTitle().getTitle())) {
            setError(funding.getFundingTitle().getTitle(), "NotBlank.fundings.title");
        } else {
            if (funding.getFundingTitle().getTitle().getValue().length() > 1000)
                setError(funding.getFundingTitle().getTitle(), "common.length_less_1000");
        }
        return funding;
    }

    @RequestMapping(value = "/funding/translatedTitleValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateTranslatedTitle(@RequestBody FundingForm funding) {
        if (funding.getFundingTitle().getTranslatedTitle() != null) {
            funding.getFundingTitle().getTranslatedTitle().setErrors(new ArrayList<String>());
            String content = funding.getFundingTitle().getTranslatedTitle().getContent();
            String code = funding.getFundingTitle().getTranslatedTitle().getLanguageCode();

            if (!StringUtils.isEmpty(content)) {
                if (!StringUtils.isEmpty(code)) {
                    if (!LANGUAGE_CODE.matcher(funding.getFundingTitle().getTranslatedTitle().getLanguageCode()).matches()) {
                        setError(funding.getFundingTitle().getTranslatedTitle(), "manual_funding_form_contents.invalid_language_code");
                    }
                } else {
                    setError(funding.getFundingTitle().getTranslatedTitle(), "manual_funding_form_contents.empty_code");
                }
                if (content.length() > 1000) {
                    setError(funding.getFundingTitle().getTranslatedTitle(), "common.length_less_1000");
                }
            } else {
                if (!StringUtils.isEmpty(code)) {
                    setError(funding.getFundingTitle().getTranslatedTitle(), "manual_funding_form_contents.empty_translation");
                }
            }
        }
        return funding;
    }

    @RequestMapping(value = "/funding/descriptionValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateDescription(@RequestBody FundingForm funding) {
        funding.getDescription().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(funding.getDescription()) && funding.getDescription().getValue().length() > 5000)
            setError(funding.getDescription(), "fundings.length_less_5000");
        return funding;
    }

    @RequestMapping(value = "/funding/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateUrl(@RequestBody FundingForm funding) {
        validateUrl(funding.getUrl());
        return funding;
    }

    @RequestMapping(value = "/funding/datesValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateDates(@RequestBody FundingForm funding) {
        boolean hasError = false;
        
        if(funding.getStartDate() != null){
            funding.getStartDate().setErrors(new ArrayList<String>());
            
            if(!PojoUtil.isEmpty(funding.getStartDate().getMonth()) && PojoUtil.isEmpty(funding.getStartDate().getYear())){
                setError(funding.getStartDate(), "common.dates.invalid");
                hasError = true;
            }                
        }
            
        if(funding.getEndDate() != null) {
            funding.getEndDate().setErrors(new ArrayList<String>());
            
            if(!PojoUtil.isEmpty(funding.getEndDate().getMonth()) && PojoUtil.isEmpty(funding.getEndDate().getYear())) {
                setError(funding.getEndDate(), "common.dates.invalid");
                hasError = true;
            }                
        }            
        
        if (!hasError && !PojoUtil.isEmpty(funding.getStartDate()) && !PojoUtil.isEmpty(funding.getEndDate())) {
            if (funding.getStartDate().toJavaDate().after(funding.getEndDate().toJavaDate()))
                setError(funding.getEndDate(), "fundings.endDate.after");
        }
        return funding;
    }

    @RequestMapping(value = "/funding/externalIdentifiersValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateExternalIdentifiers(@RequestBody FundingForm funding) {
        if (funding.getExternalIdentifiers() != null && !funding.getExternalIdentifiers().isEmpty()) {
            for (ActivityExternalIdentifier extId : funding.getExternalIdentifiers()) {
                extId.setErrors(new ArrayList<String>()); 
                if(extId.getExternalIdentifierType() != null)
                    extId.getExternalIdentifierType().setErrors(new ArrayList<String>());
                if(extId.getUrl() != null)
                    extId.getUrl().setErrors(new ArrayList<String>()); 
                if(extId.getExternalIdentifierId() != null)
                    extId.getExternalIdentifierId().setErrors(new ArrayList<String>());
                
                if (!PojoUtil.isEmpty(extId.getExternalIdentifierType()) && extId.getExternalIdentifierType().getValue().length() > 255)
                    setError(extId.getExternalIdentifierType(), "fundings.lenght_less_255");
                if (!PojoUtil.isEmpty(extId.getUrl()))
                    validateUrl(extId.getUrl());
                if (!PojoUtil.isEmpty(extId.getExternalIdentifierId()) && extId.getExternalIdentifierId().getValue().length() > 2084)
                    setError(extId.getExternalIdentifierId(), "fundings.length_less_2084");                
                if(!PojoUtil.isEmpty(extId.getUrl()) && PojoUtil.isEmpty(extId.getExternalIdentifierId())) {
                    setError(extId.getExternalIdentifierId(), "NotBlank.fundings.ext_id.value");
                }                
            }
        }
        return funding;
    }

    @RequestMapping(value = "/funding/typeValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateType(@RequestBody FundingForm funding) {
        funding.getFundingType().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(funding.getFundingType())) {
            setError(funding.getFundingType(), "NotBlank.fundings.type");
        } else {
            try {
                FundingType.fromValue(funding.getFundingType().getValue());
            } catch (IllegalArgumentException iae) {
                setError(funding.getFundingType(), "Invalid.fundings.type");
            }
        }
        return funding;
    }

    @RequestMapping(value = "/funding/organizationDefinedTypeValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateOrganizationDefinedType(@RequestBody FundingForm funding) {
        if (funding.getOrganizationDefinedFundingSubType() == null)
            funding.setOrganizationDefinedFundingSubType(new OrgDefinedFundingSubType());
        if (funding.getOrganizationDefinedFundingSubType().getSubtype() == null)
            funding.getOrganizationDefinedFundingSubType().setSubtype(Text.valueOf(""));
        funding.getOrganizationDefinedFundingSubType().getSubtype().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(funding.getOrganizationDefinedFundingSubType().getSubtype())) {
            String value = funding.getOrganizationDefinedFundingSubType().getSubtype().getValue();
            if (value.length() > 255)
                setError(funding.getOrganizationDefinedFundingSubType().getSubtype(), "fundings.lenght_less_255");
        }
        return funding;
    }

    @RequestMapping(value = "/funding/cityValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateCity(@RequestBody FundingForm funding) {
        funding.getCity().setErrors(new ArrayList<String>());
        if (funding.getCity().getValue() == null || funding.getCity().getValue().trim().length() == 0) {
            setError(funding.getCity(), "NotBlank.fundings.city");
        } else {
            if (funding.getCity().getValue().trim().length() > 1000) {
                setError(funding.getCity(), "common.length_less_1000");
            }
        }
        return funding;
    }

    @RequestMapping(value = "/funding/regionValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateRegion(@RequestBody FundingForm funding) {
        funding.getRegion().setErrors(new ArrayList<String>());
        if (funding.getRegion().getValue() != null && funding.getRegion().getValue().trim().length() > 1000) {
            setError(funding.getRegion(), "common.length_less_1000");
        }
        return funding;
    }

    @RequestMapping(value = "/funding/countryValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateCountry(@RequestBody FundingForm funding) {
        funding.getCountry().setErrors(new ArrayList<String>());
        if (funding.getCountry().getValue() == null || funding.getCountry().getValue().trim().length() == 0) {
            setError(funding.getCountry(), "common.country.not_blank");
        }
        return funding;
    }

    /**
     * Typeahead
     * */

    /**
     * Search DB for disambiguated affiliations to suggest to user
     */
    @RequestMapping(value = "/disambiguated/name/{query}", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, String>> searchDisambiguated(@PathVariable("query") String query, @RequestParam(value = "limit") int limit,
            @RequestParam(value = "funders-only") boolean fundersOnly) {
        List<Map<String, String>> datums = new ArrayList<>();
        for (OrgDisambiguated orgDisambiguated : orgDisambiguatedManager.searchOrgsFromSolr(query, 0, limit, true)) {
            datums.add(orgDisambiguated.toMap());
        }
        return datums;
    }

    /**
     * fetch disambiguated by id
     */
    @RequestMapping(value = "/disambiguated/id/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> getDisambiguated(@PathVariable("id") Long id) {
        OrgDisambiguated orgDisambiguated = orgDisambiguatedManager.findInDB(id);        
        return orgDisambiguated.toMap();
    }

    /**
     * Search DB for org defined funding types
     */
    @RequestMapping(value = "/orgDefinedSubType/{query}", method = RequestMethod.GET)
    public @ResponseBody
    List<String> searchOrgDefinedFundingSubTypes(@PathVariable("query") String query, @RequestParam(value = "limit") int limit) {
        return profileFundingManager.getIndexedFundingSubTypes(query, limit);
    }

    public Locale getUserLocale() {
        return localeManager.getLocale();
    }

    @RequestMapping(value = "/updateToMaxDisplay.json", method = RequestMethod.GET)
    public @ResponseBody
    boolean updateToMaxDisplay(HttpServletRequest request, @RequestParam(value = "putCode") Long putCode) {        
        return profileFundingManager.updateToMaxDisplay(getEffectiveUserOrcid(), putCode);
    }                
}
