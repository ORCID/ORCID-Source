package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.v3.GroupIdRecordManager;
import org.orcid.core.manager.v3.PeerReviewManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviews;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.PeerReviewForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.grouping.PeerReviewGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Resource(name = "peerReviewManagerV3")
    private PeerReviewManager peerReviewManager;

    @Resource
    private OrgDisambiguatedManager orgDisambiguatedManager;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;

    @Resource(name = "groupIdRecordManagerV3")
    private GroupIdRecordManager groupIdRecordManager;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    /**
     * List fundings associated with a profile
     */
    @RequestMapping(value = "/peer-review-ids.json", method = RequestMethod.GET)
    public @ResponseBody List<String> getPeerReviewIdsJson(HttpServletRequest request) {
        // Get cached profile
        List<String> peerReviewIds = createPeerReviewIdList(request);
        return peerReviewIds;
    }

    /**
     * Create a funding id list and sorts a map associated with the list in in
     * the session
     * 
     */
    private List<String> createPeerReviewIdList(HttpServletRequest request) {
        String orcid = getCurrentUserOrcid();
        List<PeerReview> peerReviews = peerReviewManager.findPeerReviews(orcid);

        Map<String, String> languages = lm.buildLanguageMap(getUserLocale(), false);
        HashMap<Long, PeerReviewForm> peerReviewMap = new HashMap<>();
        List<String> peerReviewIds = new ArrayList<String>();

        if (peerReviews != null) {
            for (PeerReview peerReview : peerReviews) {
                try {
                    PeerReviewForm form = PeerReviewForm.valueOf(peerReview);

                    if (form.getExternalIdentifiers() != null && !form.getExternalIdentifiers().isEmpty()) {
                        for (ActivityExternalIdentifier wExtId : form.getExternalIdentifiers()) {
                            if (PojoUtil.isEmpty(wExtId.getRelationship())) {
                                wExtId.setRelationship(Text.valueOf(Relationship.SELF.value()));
                            }
                        }
                    }

                    if (form.getTranslatedSubjectName() != null) {
                        // Set translated title language name
                        if (!(form.getTranslatedSubjectName() == null) && !StringUtils.isEmpty(form.getTranslatedSubjectName().getLanguageCode())) {
                            String languageName = languages.get(form.getTranslatedSubjectName().getLanguageCode());
                            form.getTranslatedSubjectName().setLanguageName(languageName);
                        }
                    }

                    form.setCountryForDisplay(
                            getMessage(buildInternationalizationKey(CountryIsoEntity.class, peerReview.getOrganization().getAddress().getCountry().name())));

                    // Set the numeric id (the table id in the group_id_record
                    // table) of the group id
                    if (form.getGroupId() != null && !PojoUtil.isEmpty(form.getGroupId().getValue())) {
                        GroupIdRecord groupId = groupIdRecordManager.findByGroupId(form.getGroupId().getValue()).get();
                        form.setGroupIdPutCode(Text.valueOf(groupId.getPutCode()));
                    }

                    peerReviewMap.put(peerReview.getPutCode(), form);
                    peerReviewIds.add(String.valueOf(peerReview.getPutCode()));
                } catch (Exception e) {
                    LOGGER.error("Failed to parse as PeerReview. Put code" + peerReview.getPutCode(), e);
                }
            }
            request.getSession().setAttribute(PEER_REVIEW_MAP, peerReviewMap);
        }
        return peerReviewIds;
    }

    @RequestMapping(value = "/peer-reviews.json", method = RequestMethod.GET)
    public @ResponseBody List<PeerReviewGroup> getPeerReviewsJson() {
        List<PeerReviewGroup> peerReviewGroups = new ArrayList<>();
        List<PeerReviewSummary> summaries = peerReviewManager.getPeerReviewSummaryList(getEffectiveUserOrcid());
        PeerReviews peerReviews = peerReviewManager.groupPeerReviews(summaries, false);
        for (org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
            Optional<GroupIdRecord> groupIdRecord = groupIdRecordManager.findByGroupId(group.getPeerReviewSummary().get(0).getGroupId());
            PeerReviewGroup peerReviewGroup = PeerReviewGroup.valueOf(group, groupIdRecord.get());
            peerReviewGroups.add(peerReviewGroup);
            for (PeerReviewForm peerReviewForm : peerReviewGroup.getPeerReviews()) {
                if (peerReviewForm.getCountry() != null) {
                    peerReviewForm.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, peerReviewForm.getCountry().getValue())));
                }
            }
        }
        return peerReviewGroups;
    }

    /**
     * Persist a funding object on database
     */
    /**
     * List peer reviews associated with a profile
     */
    @RequestMapping(value = "/get-peer-review.json", method = RequestMethod.GET)
    public @ResponseBody PeerReviewForm getPeerReviewJson(@RequestParam(value = "peerReviewId") Long peerReviewId) {
        PeerReview peerReview = peerReviewManager.getPeerReview(getEffectiveUserOrcid(), peerReviewId);
        if (peerReview != null) {
            return PeerReviewForm.valueOf(peerReview);
        }

        return null;
    }

    @RequestMapping(value = "/{peerReviewIdsStr}", method = RequestMethod.DELETE)
    public @ResponseBody List<String> removePeerReviews(@PathVariable("peerReviewIdsStr") String peerReviewIdsStr) {
        List<String> peerReviewIds = Arrays.asList(peerReviewIdsStr.split(","));
        String orcid = getEffectiveUserOrcid();

        for (String id : peerReviewIds) {
            peerReviewManager.removePeerReview(orcid, Long.valueOf(id));
        }

        return peerReviewIds;
    }

    /**
     * Typeahead
     */

    /**
     * Search DB for disambiguated affiliations to suggest to user
     */
    @RequestMapping(value = "/disambiguated/name/{query}", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, String>> searchDisambiguated(@PathVariable("query") String query, @RequestParam(value = "limit") int limit) {
        List<Map<String, String>> datums = new ArrayList<>();
        for (OrgDisambiguated orgDisambiguated : orgDisambiguatedManager.searchOrgsFromSolr(query, 0, limit, false)) {
            datums.add(orgDisambiguated.toMap());
        }
        return datums;
    }

    /**
     * fetch disambiguated by id
     */
    @RequestMapping(value = "/disambiguated/id/{id}", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getDisambiguated(@PathVariable("id") Long id) {
        OrgDisambiguated orgDisambiguated = orgDisambiguatedManager.findInDB(id);
        return orgDisambiguated.toMap();
    }

    public Locale getUserLocale() {
        return localeManager.getLocale();
    }

    @RequestMapping(value = "/updateToMaxDisplay.json", method = RequestMethod.GET)
    public @ResponseBody boolean updateToMaxDisplay(HttpServletRequest request, @RequestParam(value = "putCode") Long putCode) {
        return peerReviewManager.updateToMaxDisplay(getEffectiveUserOrcid(), putCode);
    }

    /**
     * updates visibility of a peer review
     */
    @RequestMapping(value = "/{peerReviewIdsStr}/visibility/{visibilityStr}", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Long> updateVisibilitys(@PathVariable("peerReviewIdsStr") String peerReviewIdsStr,
            @PathVariable("visibilityStr") String visibilityStr) {
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
