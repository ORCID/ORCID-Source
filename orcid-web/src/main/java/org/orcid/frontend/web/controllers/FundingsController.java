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

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.FundingExternalIdentifierDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedSolrDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.persistence.jpa.entities.FundingExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.solr.entities.OrgDisambiguatedSolrDocument;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.FundingExternalIdentifierForm;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.ajaxForm.FundingTitleForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitle;
import org.orcid.pojo.ajaxForm.Visibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(FundingsController.class);
    private static final String GRANT_MAP = "GRANT_MAP";
    private static final Pattern LANGUAGE_CODE = Pattern.compile("([a-zA-Z]{2})(_[a-zA-Z]{2}){0,2}");
    private static final String DEFAULT_FUNDING_EXTERNAL_IDENTIFIER_TYPE = "Grant number";
    private static final String DEFAULT_FUNDING_EXTERNAL_IDENTIFIER_TYPE_CODE = "grant_number";

    @Resource
    private ProfileDao profileDao;

    @Resource
    ProfileFundingDao profileFundingDao;

    @Resource
    FundingExternalIdentifierDao fundingExternalIdentifierDao;

    @Resource
    private Jaxb2JpaAdapter jaxb2JpaAdapter;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;

    @Resource
    private OrgDisambiguatedSolrDao orgDisambiguatedSolrDao;

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;

    /**
     * Returns a blank funding form
     * */
    @RequestMapping(value = "/funding.json", method = RequestMethod.GET)
    public @ResponseBody
    FundingForm getFunding(HttpServletRequest request) {
        FundingForm result = new FundingForm();
        result.setAmount(new Text());
        result.setCurrencyCode(Text.valueOf(""));
        result.setDescription(new Text());
        result.setFundingName(new Text());
        result.setFundingType(Text.valueOf(""));
        result.setSourceName(new String());
        FundingTitleForm title = new FundingTitleForm();
        title.setTitle(new Text());
        TranslatedTitle tt = new TranslatedTitle();
        tt.setContent(new String());
        tt.setLanguageCode(new String());
        tt.setLanguageName(new String());
        title.setTranslatedTitle(tt);
        result.setFundingTitle(title);
        result.setUrl(new Text());
        OrcidProfile profile = getEffectiveProfile();
        Visibility v = Visibility.valueOf(profile.getOrcidInternal().getPreferences().getWorkVisibilityDefault().getValue());
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

        // Set empty contributor
        Contributor contr = new Contributor();
        List<Contributor> contrList = new ArrayList<Contributor>();
        Text rText = new Text();
        rText.setValue("");
        contr.setContributorRole(rText);
        Text sText = new Text();
        sText.setValue("");
        contr.setContributorSequence(sText);
        contrList.add(contr);
        result.setContributors(contrList);

        // Set empty external identifier
        List<FundingExternalIdentifierForm> emptyExternalIdentifiers = new ArrayList<FundingExternalIdentifierForm>();
        FundingExternalIdentifierForm f = new FundingExternalIdentifierForm();
        f.setType(Text.valueOf(DEFAULT_FUNDING_EXTERNAL_IDENTIFIER_TYPE));
        f.setUrl(new Text());
        f.setValue(new Text());
        emptyExternalIdentifiers.add(f);
        result.setExternalIdentifiers(emptyExternalIdentifiers);

        result.setCity(new Text());
        result.setCountry(Text.valueOf(""));
        result.setRegion(new Text());

        return result;
    }

    /**
     * Returns a blank funding form
     * */
    @RequestMapping(value = "/funding.json", method = RequestMethod.DELETE)
    public @ResponseBody
    FundingForm deleteFundingJson(HttpServletRequest request, @RequestBody FundingForm funding) {
        Funding delFunding = funding.toOrcidFunding();
        OrcidProfile currentProfile = getEffectiveProfile();
        FundingList fundings = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getFundings();
        FundingForm deletedFunding = new FundingForm();
        if (fundings != null) {
            List<Funding> fundingList = fundings.getFundings();
            Iterator<Funding> iterator = fundingList.iterator();
            while (iterator.hasNext()) {
                Funding orcidFunding = iterator.next();
                if (delFunding.equals(orcidFunding)) {
                    iterator.remove();
                    deletedFunding = funding;
                }
            }
            fundings.setFundings(fundingList);
            currentProfile.getOrcidActivities().setFundings(fundings);
            profileFundingDao.removeProfileFunding(currentProfile.getOrcidIdentifier().getPath(), funding.getPutCode().getValue());
        }
        return deletedFunding;
    }

    /**
     * List fundings associated with a profile
     * */
    @RequestMapping(value = "/fundingIds.json", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getFundingsJson(HttpServletRequest request) {
        // Get cached profile
        List<String> fundingIds = createFundingIdList(request);
        return fundingIds;
    }

    /**
     * Create a funding id list and sorts a map associated with the list in in
     * the session
     * 
     */
    private List<String> createFundingIdList(HttpServletRequest request) {
        OrcidProfile currentProfile = getEffectiveProfile();
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);
        FundingList fundings = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getFundings();

        HashMap<String, FundingForm> fundingsMap = new HashMap<>();
        List<String> fundingIds = new ArrayList<String>();
        if (fundings != null) {
            for (Funding funding : fundings.getFundings()) {
                try {
                    FundingForm form = FundingForm.valueOf(funding);
                    if (funding.getType() != null) {
                        form.setFundingTypeForDisplay(getMessage(buildInternationalizationKey(FundingType.class, funding.getType().value())));
                    }
                    // Set translated title language name
                    if (!(funding.getTitle().getTranslatedTitle() == null) && !StringUtils.isEmpty(funding.getTitle().getTranslatedTitle().getLanguageCode())) {
                        String languageName = languages.get(funding.getTitle().getTranslatedTitle().getLanguageCode());
                        form.getFundingTitle().getTranslatedTitle().setLanguageName(languageName);
                    }
                    form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, funding.getOrganization().getAddress().getCountry().name())));
                    fundingsMap.put(funding.getPutCode(), form);
                    fundingIds.add(funding.getPutCode());
                } catch (Exception e) {
                    LOGGER.error("Failed to parse as Grant. Put code" + funding.getPutCode());
                }
            }
            request.getSession().setAttribute(GRANT_MAP, fundingsMap);
        }
        return fundingIds;
    }

    /**
     * List fundings associated with a profile
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/fundings.json", method = RequestMethod.GET)
    public @ResponseBody
    List<FundingForm> getFundingJson(HttpServletRequest request, @RequestParam(value = "fundingIds") String fundingIdsStr) {
        List<FundingForm> fundingList = new ArrayList<>();
        FundingForm funding = null;
        String[] fundingIds = fundingIdsStr.split(",");

        if (fundingIds != null) {
            HashMap<String, FundingForm> fundingsMap = (HashMap<String, FundingForm>) request.getSession().getAttribute(GRANT_MAP);
            // this should never happen, but just in case.
            if (fundingsMap == null) {
                createFundingIdList(request);
                fundingsMap = (HashMap<String, FundingForm>) request.getSession().getAttribute(GRANT_MAP);
            }
            for (String fundingId : fundingIds) {
                funding = fundingsMap.get(fundingId);
                fundingList.add(funding);
            }
        }

        return fundingList;
    }

    /**
     * Persist a funding object on database
     * */
    @RequestMapping(value = "/funding.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm postGrant(HttpServletRequest request, @RequestBody FundingForm funding) {
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
        validateCity(funding);
        validateRegion(funding);
        validateCountry(funding);

        copyErrors(funding.getFundingName(), funding);
        copyErrors(funding.getAmount(), funding);
        copyErrors(funding.getCurrencyCode(), funding);
        copyErrors(funding.getFundingTitle().getTitle(), funding);
        copyErrors(funding.getFundingTitle().getTranslatedTitle(), funding);
        copyErrors(funding.getDescription(), funding);
        copyErrors(funding.getUrl(), funding);
        copyErrors(funding.getEndDate(), funding);
        copyErrors(funding.getFundingType(), funding);

        for (FundingExternalIdentifierForm extId : funding.getExternalIdentifiers()) {
            copyErrors(extId.getType(), funding);
            copyErrors(extId.getUrl(), funding);
            copyErrors(extId.getValue(), funding);
        }

        // If there are no errors, persist to DB
        if (funding.getErrors().isEmpty()) {
            // Set the credit name
            setContributorsCreditName(funding);
            // Set default type for external identifiers
            setTypeToExternalIdentifiers(funding);
            // Update on database
            ProfileEntity userProfile = profileDao.find(getEffectiveUserOrcid());
            ProfileFundingEntity profileGrantEntity = jaxb2JpaAdapter.getNewProfileFundingEntity(funding.toOrcidFunding(), userProfile);
            profileGrantEntity.setSource(userProfile);
            // Persists the profile funding object
            ProfileFundingEntity newProfileGrant = profileFundingDao.addProfileFunding(profileGrantEntity);

            // Persist the external identifiers
            SortedSet<FundingExternalIdentifierEntity> externalIdentifiers = profileGrantEntity.getExternalIdentifiers();

            if (externalIdentifiers != null && !externalIdentifiers.isEmpty()) {
                for (FundingExternalIdentifierEntity externalIdentifier : externalIdentifiers) {
                    externalIdentifier.setProfileFunding(newProfileGrant);
                    fundingExternalIdentifierDao.createFundingExternalIdentifier(externalIdentifier);
                }
            }

            // Transform it back into a OrcidGrant to add it into the cached
            // object
            Funding newFunding = jpa2JaxbAdapter.getFunding(newProfileGrant);
            // Update the fundings on the cached object
            OrcidProfile currentProfile = getEffectiveProfile();
            // Initialize activities if needed
            if (currentProfile.getOrcidActivities() == null) {
                currentProfile.setOrcidActivities(new OrcidActivities());
            }
            // Initialize fundings if needed
            if (currentProfile.getOrcidActivities().getFundings() == null) {
                currentProfile.getOrcidActivities().setFundings(new FundingList());
            }

            // Set the new funding into the cached object
            currentProfile.getOrcidActivities().getFundings().getFundings().add(newFunding);
        }

        return funding;
    }

    private void removeEmptyExternalIds(FundingForm funding) {
        List<FundingExternalIdentifierForm> extIds = funding.getExternalIdentifiers();
        List<FundingExternalIdentifierForm> updatedExtIds = new ArrayList<FundingExternalIdentifierForm>();
        // For all external identifiers
        for (FundingExternalIdentifierForm extId : extIds) {
            // Keep only the ones that contains a value or url
            if (!PojoUtil.isEmpty(extId.getValue()) || !PojoUtil.isEmpty(extId.getUrl())) {
                updatedExtIds.add(extId);
            }
        }

        funding.setExternalIdentifiers(updatedExtIds);
    }

    private void setContributorsCreditName(FundingForm funding) {
        OrcidProfile profile = getEffectiveProfile();
        String creditName = null;
        Visibility creditNameVisibility = null;
        if (profile.getOrcidBio() != null && profile.getOrcidBio().getPersonalDetails() != null && profile.getOrcidBio().getPersonalDetails().getCreditName() != null) {
            creditName = profile.getOrcidBio().getPersonalDetails().getCreditName().getContent();
            creditNameVisibility = Visibility.valueOf(profile.getOrcidBio().getPersonalDetails().getCreditName().getVisibility());
        }
        if (funding != null && funding.getContributors() != null && !funding.getContributors().isEmpty()) {
            for (Contributor contributor : funding.getContributors()) {
                if (!PojoUtil.areAllEmtpy(contributor.getContributorRole(), contributor.getContributorSequence())) {
                    if (!PojoUtil.isEmpty(creditName))
                        contributor.setCreditName(Text.valueOf(creditName));
                    if (creditNameVisibility != null) {
                        contributor.setCreditNameVisibility(creditNameVisibility);
                    } else {
                        contributor.setCreditNameVisibility(Visibility.valueOf(OrcidVisibilityDefaults.CREDIT_NAME_DEFAULT.getVisibility()));
                    }
                }
            }
        }
    }

    private void setTypeToExternalIdentifiers(FundingForm funding) {
        if (funding == null || funding.getExternalIdentifiers() == null || funding.getExternalIdentifiers().isEmpty())
            return;
        for (FundingExternalIdentifierForm extId : funding.getExternalIdentifiers()) {
            extId.setType(Text.valueOf(DEFAULT_FUNDING_EXTERNAL_IDENTIFIER_TYPE_CODE));
        }
    }

    /**
     * Saves an affiliation
     * */
    @RequestMapping(value = "/funding.json", method = RequestMethod.PUT)
    public @ResponseBody
    FundingForm updateProfileFundingJson(HttpServletRequest request, @RequestBody FundingForm fundingForm) {
        // Get cached profile
        OrcidProfile currentProfile = getEffectiveProfile();
        FundingList orcidGrants = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getFundings();
        if (orcidGrants != null) {
            List<Funding> fundings = orcidGrants.getFundings();
            if (fundings != null) {
                for (Funding funding : fundings) {
                    if (funding.getPutCode().equals(fundingForm.getPutCode().getValue())) {
                        // Update the privacy of the funding
                        profileFundingDao.updateProfileFunding(currentProfile.getOrcidIdentifier().getPath(), fundingForm.getPutCode().getValue(), fundingForm
                                .getVisibility().getVisibility());
                    }
                }
            }
        }
        return fundingForm;
    }

    /**
     * Validators
     * */
    @RequestMapping(value = "/funding/amountValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateAmount(@RequestBody FundingForm funding) {
        funding.getAmount().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(funding.getAmount())) {            
            String amount = funding.getAmount().getValue();
            long lAmount = 0;
            // TODO Chck this regex
            String pattern = "[0-9]{1,3}(?:[0-9]*(?:[.,][0-9]{2})?|(?:,[0-9]{3})*(?:\\.[0-9]{2})?|(?:\\.[0-9]{3})*(?:,[0-9]{2})?)";
            if (!amount.matches(pattern)) {
                setError(funding.getAmount(), "Invalid.fundings.amount");
            }

            if (lAmount < 0)
                setError(funding.getAmount(), "Invalid.fundings.amount");
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
                setError(funding.getFundingName(), "fundings.length_less_1000");
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
                setError(funding.getFundingTitle().getTitle(), "fundings.length_less_1000");
        }
        return funding;
    }

    @RequestMapping(value = "/funding/translatedTitleValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateTranslatedTitle(@RequestBody FundingForm funding) {
        funding.getFundingTitle().getTranslatedTitle().setErrors(new ArrayList<String>());
        if (funding.getFundingTitle().getTranslatedTitle() != null) {

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
                    setError(funding.getFundingTitle().getTranslatedTitle(), "fundings.length_less_1000");
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
        funding.getUrl().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(funding.getUrl()) && funding.getUrl().getValue().length() > 350)
            setError(funding.getUrl(), "fundings.length_less_350");
        return funding;
    }

    @RequestMapping(value = "/funding/datesValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateDates(@RequestBody FundingForm funding) {
        funding.getStartDate().setErrors(new ArrayList<String>());
        funding.getEndDate().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(funding.getStartDate()) && !PojoUtil.isEmpty(funding.getEndDate())) {
            if (funding.getStartDate().toJavaDate().after(funding.getEndDate().toJavaDate()))
                setError(funding.getEndDate(), "fundings.endDate.after");
        }
        return funding;
    }

    @RequestMapping(value = "/funding/externalIdentifiersValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateExternalIdentifiers(@RequestBody FundingForm funding) {
        if (funding.getExternalIdentifiers() != null && !funding.getExternalIdentifiers().isEmpty()) {
            for (FundingExternalIdentifierForm extId : funding.getExternalIdentifiers()) {
                if (!PojoUtil.isEmpty(extId.getType()) && extId.getType().getValue().length() > 255)
                    setError(extId.getType(), "fundings.lenght_less_255");
                if (!PojoUtil.isEmpty(extId.getUrl()) && extId.getUrl().getValue().length() > 350)
                    setError(extId.getUrl(), "fundings.length_less_350");
                if (!PojoUtil.isEmpty(extId.getValue()) && extId.getValue().getValue().length() > 2084)
                    setError(extId.getValue(), "fundings.length_less_2084");
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

    @RequestMapping(value = "/funding/cityValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateCity(@RequestBody FundingForm funding) {
        funding.getCity().setErrors(new ArrayList<String>());
        if (funding.getCity().getValue() == null || funding.getCity().getValue().trim().length() == 0) {
            setError(funding.getCity(), "NotBlank.fundings.city");
        } else {
            if (funding.getCity().getValue().trim().length() > 1000) {
                setError(funding.getCity(), "fundings.length_less_1000");
            }
        }
        return funding;
    }

    @RequestMapping(value = "/funding/regionValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateRegion(@RequestBody FundingForm funding) {
        funding.getRegion().setErrors(new ArrayList<String>());
        if (funding.getRegion().getValue() != null && funding.getRegion().getValue().trim().length() > 1000) {
            setError(funding.getRegion(), "fundings.length_less_1000");
        }
        return funding;
    }

    @RequestMapping(value = "/funding/countryValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateCountry(@RequestBody FundingForm funding) {
        funding.getCountry().setErrors(new ArrayList<String>());
        if (funding.getCountry().getValue() == null || funding.getCountry().getValue().trim().length() == 0) {
            setError(funding.getCountry(), "NotBlank.fundings.country");
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
        for (OrgDisambiguatedSolrDocument orgDisambiguatedDocument : orgDisambiguatedSolrDao.getOrgs(query, 0, limit, fundersOnly)) {
            Map<String, String> datum = createDatumFromOrgDisambiguated(orgDisambiguatedDocument);
            datums.add(datum);
        }
        return datums;
    }

    private Map<String, String> createDatumFromOrgDisambiguated(OrgDisambiguatedSolrDocument orgDisambiguatedDocument) {
        Map<String, String> datum = new HashMap<>();
        datum.put("value", orgDisambiguatedDocument.getOrgDisambiguatedName());
        datum.put("city", orgDisambiguatedDocument.getOrgDisambiguatedCity());
        datum.put("region", orgDisambiguatedDocument.getOrgDisambiguatedRegion());
        datum.put("country", orgDisambiguatedDocument.getOrgDisambiguatedCountry());
        datum.put("orgType", orgDisambiguatedDocument.getOrgDisambiguatedType());
        datum.put("disambiguatedFundingIdentifier", Long.toString(orgDisambiguatedDocument.getOrgDisambiguatedId()));
        return datum;
    }

    /**
     * fetch disambiguated by id
     */
    @RequestMapping(value = "/disambiguated/id/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> getDisambiguated(@PathVariable("id") Long id) {
        OrgDisambiguatedEntity orgDisambiguatedEntity = orgDisambiguatedDao.find(id);
        Map<String, String> datum = new HashMap<>();
        datum.put("value", orgDisambiguatedEntity.getName());
        datum.put("city", orgDisambiguatedEntity.getCity());
        datum.put("region", orgDisambiguatedEntity.getRegion());
        datum.put("country", orgDisambiguatedEntity.getCountry().value());
        datum.put("sourceId", orgDisambiguatedEntity.getSourceId());
        datum.put("sourceType", orgDisambiguatedEntity.getSourceType());
        return datum;
    }
}
