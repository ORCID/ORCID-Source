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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.PeerReview;
import org.orcid.jaxb.model.record.PeerReviewType;
import org.orcid.jaxb.model.record.Role;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedSolrDao;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.solr.entities.OrgDisambiguatedSolrDocument;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PeerReviewForm;
import org.orcid.pojo.ajaxForm.PeerReviewSubjectForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitle;
import org.orcid.pojo.ajaxForm.WorkExternalIdentifier;
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
@Controller("peerReviewsController")
@RequestMapping(value = { "/peer-reviews" })
public class PeerReviewsController extends BaseWorkspaceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PeerReviewsController.class);
    private static final String PEER_REVIEW_MAP = "PEER_REVIEW_MAP";

    @Resource
    private LocaleManager localeManager;

    @Resource
    private PeerReviewManager peerReviewManager;

    @Resource
    private OrgDisambiguatedSolrDao orgDisambiguatedSolrDao;

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;    

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    /**
     * Returns a blank peer review form
     * */
    @RequestMapping(value = "/peer-review.json", method = RequestMethod.GET)
    public @ResponseBody PeerReviewForm getEmptyPeerReview() {
        Date emptyDate = new Date();
        emptyDate.setDay(StringUtils.EMPTY);
        emptyDate.setMonth(StringUtils.EMPTY);
        emptyDate.setYear(StringUtils.EMPTY);

        TranslatedTitle emptyTranslatedTitle = new TranslatedTitle();
        emptyTranslatedTitle.setErrors(Collections.<String> emptyList());
        emptyTranslatedTitle.setContent(StringUtils.EMPTY);
        emptyTranslatedTitle.setLanguageCode(StringUtils.EMPTY);
        emptyTranslatedTitle.setLanguageName(StringUtils.EMPTY);

        WorkExternalIdentifier emptyExternalId = new WorkExternalIdentifier();
        emptyExternalId.setErrors(new ArrayList<String>());
        emptyExternalId.setWorkExternalIdentifierId(Text.valueOf(StringUtils.EMPTY));
        emptyExternalId.getWorkExternalIdentifierId().setRequired(false);
        emptyExternalId.setWorkExternalIdentifierType(Text.valueOf(StringUtils.EMPTY));
        emptyExternalId.getWorkExternalIdentifierType().setRequired(false);

        List<WorkExternalIdentifier> emptyExtIdsList = new ArrayList<WorkExternalIdentifier>();
        emptyExtIdsList.add(emptyExternalId);

        PeerReviewForm form = new PeerReviewForm();

        form.setErrors(Collections.<String> emptyList());

        form.setCompletionDate(emptyDate);
        form.setCreatedDate(emptyDate);

        form.setCity(Text.valueOf(StringUtils.EMPTY));
        form.setRegion(Text.valueOf(StringUtils.EMPTY));
        form.getRegion().setRequired(false);
        form.setCountry(Text.valueOf(StringUtils.EMPTY));
        form.setCountryForDisplay(StringUtils.EMPTY);
        form.setDisambiguationSource(Text.valueOf(StringUtils.EMPTY));
        form.getDisambiguationSource().setRequired(false);
        form.setOrgName(Text.valueOf(StringUtils.EMPTY));

        form.setPutCode(Text.valueOf(StringUtils.EMPTY));
        form.getPutCode().setRequired(false);
        form.setRole(Text.valueOf(Role.REVIEWER.value()));
        form.setType(Text.valueOf(PeerReviewType.REVIEW.value()));
        form.setUrl(Text.valueOf(StringUtils.EMPTY));
        form.getUrl().setRequired(false);
        form.setExternalIdentifiers(emptyExtIdsList);

        PeerReviewSubjectForm subjectForm = new PeerReviewSubjectForm();
        subjectForm.setErrors(Collections.<String> emptyList());
        subjectForm.setJournalTitle(Text.valueOf(StringUtils.EMPTY));
        subjectForm.getJournalTitle().setRequired(false);
        subjectForm.setPutCode(Text.valueOf(StringUtils.EMPTY));
        subjectForm.getPutCode().setRequired(false);
        subjectForm.setSubtitle(Text.valueOf(StringUtils.EMPTY));
        subjectForm.getSubtitle().setRequired(false);
        subjectForm.setTitle(Text.valueOf(StringUtils.EMPTY));
        subjectForm.setTranslatedTitle(emptyTranslatedTitle);
        subjectForm.setUrl(Text.valueOf(StringUtils.EMPTY));
        subjectForm.getUrl().setRequired(false);
        subjectForm.setWorkExternalIdentifiers(emptyExtIdsList);
        subjectForm.setWorkType(Text.valueOf(StringUtils.EMPTY));        

        form.setSubjectForm(subjectForm);
        return form;
    }

    /**
     * List fundings associated with a profile
     * */
    @RequestMapping(value = "/peer-review-ids.json", method = RequestMethod.GET)
    public @ResponseBody List<String> getPeerReviewIdsJson(HttpServletRequest request) {
        // Get cached profile
        List<String> fundingIds = createPeerReviewIdList(request);
        return fundingIds;
    }

    /**
     * Create a funding id list and sorts a map associated with the list in in
     * the session
     * 
     */
    private List<String> createPeerReviewIdList(HttpServletRequest request) {
        OrcidProfile currentProfile = getEffectiveProfile();
        java.util.Date lastModified = currentProfile.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        List<PeerReview> peerReviews = peerReviewManager.findPeerReviews(currentProfile.getOrcidIdentifier().getPath(), lastModified.getTime());
        
        Map<String, String> languages = lm.buildLanguageMap(getUserLocale(), false);
        HashMap<String, PeerReviewForm> peerReviewMap = new HashMap<>();
        List<String> peerReviewIds = new ArrayList<String>();

        if (peerReviews != null) {
            for (PeerReview peerReview : peerReviews) {
                try {
                    PeerReviewForm form = PeerReviewForm.valueOf(peerReview);

                    if (form.getSubjectForm() != null && form.getSubjectForm().getTitle() != null) {
                        // Set translated title language name
                        if (!(form.getSubjectForm().getTranslatedTitle() == null) && !StringUtils.isEmpty(form.getSubjectForm().getTranslatedTitle().getLanguageCode())) {
                            String languageName = languages.get(form.getSubjectForm().getTranslatedTitle().getLanguageCode());
                            form.getSubjectForm().getTranslatedTitle().setLanguageName(languageName);
                        }
                    }

                    form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, peerReview.getOrganization().getAddress().getCountry()
                            .name())));
                    peerReviewMap.put(peerReview.getPutCode(), form);
                    peerReviewIds.add(peerReview.getPutCode());
                } catch (Exception e) {
                    LOGGER.error("Failed to parse as PeerReview. Put code" + peerReview.getPutCode(), e);
                }
            }
            request.getSession().setAttribute(PEER_REVIEW_MAP, peerReviewMap);
        }
        return peerReviewIds;
    }

    /**
     * List peer reviews associated with a profile
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/get-peer-reviews.json", method = RequestMethod.GET)
    public @ResponseBody List<PeerReviewForm> getPeerReviewsJson(HttpServletRequest request, @RequestParam(value = "peerReviewIds") String peerReviewIdsStr) {
        List<PeerReviewForm> peerReviewList = new ArrayList<>();
        PeerReviewForm peerReview = null;
        String[] peerReviewIds = peerReviewIdsStr.split(",");

        if (peerReviewIds != null) {
            HashMap<String, PeerReviewForm> peerReviewMap = (HashMap<String, PeerReviewForm>) request.getSession().getAttribute(PEER_REVIEW_MAP);
            // this should never happen, but just in case.
            if (peerReviewMap == null) {
                createPeerReviewIdList(request);
                peerReviewMap = (HashMap<String, PeerReviewForm>) request.getSession().getAttribute(PEER_REVIEW_MAP);
            }
            for (String peerReviewId : peerReviewIds) {
                peerReview = peerReviewMap.get(peerReviewId);
                peerReviewList.add(peerReview);
            }
        }

        return peerReviewList;
    }

    /**
     * Persist a funding object on database
     * */
    @RequestMapping(value = "/peer-review.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm postPeerReview(@RequestBody PeerReviewForm peerReview) {
        // Reset errors
        peerReview.setErrors(new ArrayList<String>());
        // Remove empty external identifiers
        removeEmptyExternalIds(peerReview);
        // Remove empty external identifiers on subject
        removeEmptyExternalIdsOnSubject(peerReview);

        validateOrgName(peerReview);
        validateCity(peerReview);
        validateRegion(peerReview);
        validateCountry(peerReview);
        validateUrl(peerReview);               
        copyErrors(peerReview.getOrgName(), peerReview);
        copyErrors(peerReview.getCity(), peerReview);
        copyErrors(peerReview.getRegion(), peerReview);
        copyErrors(peerReview.getCountry(), peerReview);
        copyErrors(peerReview.getUrl(), peerReview);
        
        if(peerReview.getCompletionDate() != null) {
            validateCompletionDate(peerReview);
            copyErrors(peerReview.getCompletionDate(), peerReview);
        }
        if (peerReview.getSubjectForm() != null) {
            validateSubjectType(peerReview);
            validateSubjectTitle(peerReview);
            validateSubjectUrl(peerReview);
            PeerReviewSubjectForm subjectForm = peerReview.getSubjectForm();
            copyErrors(subjectForm.getWorkType(), peerReview);
            copyErrors(subjectForm.getTitle(), peerReview);
            copyErrors(subjectForm.getUrl(), peerReview);            
        }

        // If there are no errors, persist to DB
        if (peerReview.getErrors().isEmpty()) {
            if (PojoUtil.isEmpty(peerReview.getPutCode())) {
                peerReview = addPeerReview(peerReview);
            } else {
                peerReview = editPeerReview(peerReview);
            }
        }

        return peerReview;
    }

    /**
     * List peer reviews associated with a profile
     * */
    @RequestMapping(value = "/get-peer-review.json", method = RequestMethod.GET)
    public @ResponseBody PeerReviewForm getPeerReviewJson(@RequestParam(value = "peerReviewId") String peerReviewIdStr) {
        PeerReviewForm result = null;
        try {
            PeerReview peerReview = peerReviewManager.getPeerReview(getEffectiveUserOrcid(), peerReviewIdStr);
            if (peerReview != null) {
                result = PeerReviewForm.valueOf(peerReview);
            }
        } catch (NoResultException nre) {
            // There is no peer review with that id, just return a null one
        }
        return result;
    }

    /**
     * Deletes a peer review
     * */
    @RequestMapping(value = "/peer-review.json", method = RequestMethod.DELETE)
    public @ResponseBody PeerReviewForm deletePeerReviewJson(HttpServletRequest request, @RequestBody PeerReviewForm peerReview) {
        if (peerReview == null || PojoUtil.isEmpty(peerReview.getPutCode())) {
            return null;
        }
        peerReviewManager.removePeerReview(getEffectiveUserOrcid(), peerReview.getPutCode().getValue());
        return peerReview;
    }

    @RequestMapping(value = "/{peerReviewIdsStr}", method = RequestMethod.DELETE)
    public @ResponseBody List<String> removePeerReviews(@PathVariable("peerReviewIdsStr") String peerReviewIdsStr) {
        List<String> peerReviewIds = Arrays.asList(peerReviewIdsStr.split(","));
        String orcid = getEffectiveUserOrcid();

        for (String id : peerReviewIds) {
            peerReviewManager.removePeerReview(orcid, id);
        }

        return peerReviewIds;
    }

    private PeerReviewForm addPeerReview(PeerReviewForm peerReviewForm) {
        String userOrcid = getEffectiveUserOrcid();
        PeerReview peerReview = peerReviewForm.toPeerReview();
        peerReview = peerReviewManager.createPeerReview(userOrcid, peerReview);
        peerReviewForm = PeerReviewForm.valueOf(peerReview);
        return peerReviewForm;
    }

    private PeerReviewForm editPeerReview(PeerReviewForm peerReviewForm) {
        String userOrcid = getEffectiveUserOrcid();
        PeerReview peerReview = peerReviewForm.toPeerReview();
        peerReview = peerReviewManager.updatePeerReview(userOrcid, peerReview);
        return PeerReviewForm.valueOf(peerReview);
    }

    private void removeEmptyExternalIds(PeerReviewForm peerReview) {
        List<WorkExternalIdentifier> extIds = peerReview.getExternalIdentifiers();
        List<WorkExternalIdentifier> updatedExtIds = new ArrayList<WorkExternalIdentifier>();
        if (extIds != null) {
            // For all external identifiers
            for (WorkExternalIdentifier extId : extIds) {
                // Keep only the ones that contains a value or url
                if (!PojoUtil.isEmpty(extId.getWorkExternalIdentifierId())) {
                    updatedExtIds.add(extId);
                }
            }
        }

        if (updatedExtIds.isEmpty()) {
            WorkExternalIdentifier wei = new WorkExternalIdentifier();
            updatedExtIds.add(wei);
        }

        peerReview.setExternalIdentifiers(updatedExtIds);
    }

    private void removeEmptyExternalIdsOnSubject(PeerReviewForm peerReview) {
        if (peerReview.getSubjectForm() == null) {
            return;
        }
        List<WorkExternalIdentifier> extIds = peerReview.getSubjectForm().getWorkExternalIdentifiers();
        List<WorkExternalIdentifier> updatedExtIds = new ArrayList<WorkExternalIdentifier>();
        if (extIds != null) {
            // For all external identifiers
            for (WorkExternalIdentifier extId : extIds) {
                // Keep only the ones that contains a value or url
                if (!PojoUtil.isEmpty(extId.getWorkExternalIdentifierId())) {
                    updatedExtIds.add(extId);
                }
            }
        }

        if (updatedExtIds.isEmpty()) {
            WorkExternalIdentifier wei = new WorkExternalIdentifier();
            updatedExtIds.add(wei);
        }

        peerReview.getSubjectForm().setWorkExternalIdentifiers(updatedExtIds);
    }

    @RequestMapping(value = "/orgNameValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateOrgName(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getOrgName() == null) {
            peerReview.setOrgName(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getOrgName().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(peerReview.getOrgName())) {
            setError(peerReview.getOrgName(), "org.name.not_blank");
        } else {
            if (peerReview.getOrgName().getValue().trim().length() > 1000) {
                setError(peerReview.getOrgName(), "common.length_less_1000");
            }
        }
        return peerReview;
    }

    @RequestMapping(value = "/cityValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateCity(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getCity() == null) {
            peerReview.setCity(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getCity().setErrors(new ArrayList<String>());
        if (peerReview.getCity().getValue() == null || peerReview.getCity().getValue().trim().length() == 0) {
            setError(peerReview.getCity(), "org.city.not_blank");
        } else {
            if (peerReview.getCity().getValue().trim().length() > 1000) {
                setError(peerReview.getCity(), "common.length_less_1000");
            }
        }
        return peerReview;
    }

    @RequestMapping(value = "/regionValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateRegion(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getRegion() == null) {
           peerReview.setRegion(Text.valueOf(StringUtils.EMPTY)); 
        }
        peerReview.getRegion().setErrors(new ArrayList<String>());
        if (peerReview.getRegion().getValue() != null && peerReview.getRegion().getValue().trim().length() > 1000) {
            setError(peerReview.getRegion(), "common.length_less_1000");
        }
        return peerReview;
    }

    @RequestMapping(value = "/countryValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateCountry(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getCountry() == null) {
            peerReview.setCountry(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getCountry().setErrors(new ArrayList<String>());
        if (peerReview.getCountry().getValue() == null || peerReview.getCountry().getValue().trim().length() == 0) {
            setError(peerReview.getCountry(), "common.country.not_blank");
        }
        return peerReview;
    }

    @RequestMapping(value = "/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateUrl(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getUrl() == null) {
            peerReview.setUrl(Text.valueOf(StringUtils.EMPTY));
        }
        validateUrl(peerReview.getUrl());
        return peerReview;
    }

    @RequestMapping(value = "/completionDateValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateCompletionDate(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getCompletionDate() == null) {
            peerReview.setCompletionDate(new Date());
        }
        
        peerReview.getCompletionDate().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(peerReview.getCompletionDate().getMonth()) && PojoUtil.isEmpty(peerReview.getCompletionDate().getYear())) {
            setError(peerReview.getCompletionDate(), "common.dates.invalid");
        }
        
        return peerReview;
    }

    @RequestMapping(value = "/subject/titleValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateSubjectTitle(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getSubjectForm().getTitle() == null) {
            peerReview.getSubjectForm().setTitle(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getSubjectForm().getTitle().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(peerReview.getSubjectForm().getTitle())) {
            setError(peerReview.getSubjectForm().getTitle(), "common.title.not_blank");
        } else if (peerReview.getSubjectForm().getTitle().getValue().length() > 100) {
            setError(peerReview.getSubjectForm().getTitle(), "common.length_less_1000");
        }
        return peerReview;
    }

    @RequestMapping(value = "/subject/typeValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateSubjectType(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getSubjectForm().getWorkType() == null) {
            peerReview.getSubjectForm().setWorkType(Text.valueOf(StringUtils.EMPTY)); 
        }
        peerReview.getSubjectForm().getWorkType().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(peerReview.getSubjectForm().getWorkType())) {
            setError(peerReview.getSubjectForm().getWorkType(), "peer_review.subject.work_type.not_blank");
        }
        return peerReview;
    }

    @RequestMapping(value = "/subject/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateSubjectUrl(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getSubjectForm().getUrl() == null) {
            peerReview.getSubjectForm().setUrl(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getSubjectForm().getUrl().setErrors(new ArrayList<String>());
        validateUrl(peerReview.getSubjectForm().getUrl());
        return peerReview;
    }

    @RequestMapping(value = "/roleValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateRole(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getRole() == null) {
            peerReview.setRole(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getRole().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(peerReview.getRole())) {
            setError(peerReview.getRole(), "peer_review.role.not_blank");
        }

        return peerReview;
    }

    @RequestMapping(value = "/typeValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateType(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getType() == null){
            peerReview.setType(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getType().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(peerReview.getType())) {
            setError(peerReview.getType(), "peer_review.type.not_blank");
        }

        return peerReview;
    }

    /**
     * Typeahead
     * */

    /**
     * Search DB for disambiguated affiliations to suggest to user
     */
    @RequestMapping(value = "/disambiguated/name/{query}", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, String>> searchDisambiguated(@PathVariable("query") String query, @RequestParam(value = "limit") int limit) {
        List<Map<String, String>> datums = new ArrayList<>();
        for (OrgDisambiguatedSolrDocument orgDisambiguatedDocument : orgDisambiguatedSolrDao.getOrgs(query, 0, limit)) {
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
        datum.put("disambiguatedOrganizationIdentifier", Long.toString(orgDisambiguatedDocument.getOrgDisambiguatedId()));
        return datum;
    }

    /**
     * fetch disambiguated by id
     */
    @RequestMapping(value = "/disambiguated/id/{id}", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getDisambiguated(@PathVariable("id") Long id) {
        OrgDisambiguatedEntity orgDisambiguatedEntity = orgDisambiguatedDao.find(id);
        Map<String, String> datum = new HashMap<>();
        datum.put("value", orgDisambiguatedEntity.getName());
        datum.put("city", orgDisambiguatedEntity.getCity());
        datum.put("region", orgDisambiguatedEntity.getRegion());
        if (orgDisambiguatedEntity.getCountry() != null)
            datum.put("country", orgDisambiguatedEntity.getCountry().value());
        datum.put("sourceId", orgDisambiguatedEntity.getSourceId());
        datum.put("sourceType", orgDisambiguatedEntity.getSourceType());
        return datum;
    }

    public Locale getUserLocale() {
        return localeManager.getLocale();
    }

    @RequestMapping(value = "/updateToMaxDisplay.json", method = RequestMethod.GET)
    public @ResponseBody boolean updateToMaxDisplay(HttpServletRequest request, @RequestParam(value = "putCode") String putCode) {
        return peerReviewManager.updateToMaxDisplay(getEffectiveUserOrcid(), putCode);
    }

    /**
     * updates visibility of a peer review
     * */
    @RequestMapping(value = "/{peerReviewIdsStr}/visibility/{visibilityStr}", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Long> updateVisibilitys(@PathVariable("peerReviewIdsStr") String peerReviewIdsStr, @PathVariable("visibilityStr") String visibilityStr) {
        ArrayList<Long> peerReviewIds = new ArrayList<Long>();
        if (PojoUtil.isEmpty(peerReviewIdsStr)) {
            return peerReviewIds;
        }
        // make sure this is a users work
        String orcid = getEffectiveUserOrcid();
        for (String peerReviewId : peerReviewIdsStr.split(","))
            peerReviewIds.add(new Long(peerReviewId));
        peerReviewManager.updateVisibilities(orcid, peerReviewIds, Visibility.fromValue(visibilityStr));
        return peerReviewIds;
    }

}
