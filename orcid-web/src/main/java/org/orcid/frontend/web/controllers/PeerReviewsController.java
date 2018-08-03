package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.v3.GroupIdRecordManager;
import org.orcid.core.manager.v3.PeerReviewManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.utils.v3.activities.PeerReviewGroupComparator;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviews;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.ajaxForm.PeerReviewForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.grouping.PeerReviewDuplicateGroup;
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

    @RequestMapping(value = "/peer-reviews.json", method = RequestMethod.GET)
    public @ResponseBody List<PeerReviewGroup> getPeerReviewsJson(@RequestParam("sortAsc") boolean sortAsc) {
        List<PeerReviewGroup> peerReviewGroups = new ArrayList<>();
        List<PeerReviewSummary> summaries = peerReviewManager.getPeerReviewSummaryList(getEffectiveUserOrcid());
        PeerReviews peerReviews = peerReviewManager.groupPeerReviews(summaries, false);
        for (org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
            Optional<GroupIdRecord> groupIdRecord = groupIdRecordManager.findByGroupId(group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());
            PeerReviewGroup peerReviewGroup = PeerReviewGroup.getInstance(group, groupIdRecord.get());
            for (PeerReviewDuplicateGroup duplicateGroup : peerReviewGroup.getPeerReviewDuplicateGroups()) {
                for (PeerReviewForm peerReviewForm : duplicateGroup.getPeerReviews()) {
                    if (peerReviewForm.getCountry() != null) {
                        peerReviewForm.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, peerReviewForm.getCountry().getValue())));
                    }
                }
            }
            peerReviewGroups.add(peerReviewGroup);
        }
        peerReviewGroups.sort(new PeerReviewGroupComparator(!sortAsc));
        return peerReviewGroups;
    }
    
    @RequestMapping(value = "/peer-review.json", method = RequestMethod.GET)
    public @ResponseBody PeerReviewForm getPeerReviewJson(@RequestParam("putCode") long putCode) {
        PeerReview peerReview = peerReviewManager.getPeerReview(getEffectiveUserOrcid(), putCode);
        return PeerReviewForm.valueOf(peerReview);
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
