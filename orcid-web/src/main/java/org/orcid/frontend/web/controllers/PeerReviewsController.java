package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.groupIds.issn.IssnGroupIdPatternMatcher;
import org.orcid.core.groupIds.issn.IssnPortalUrlBuilder;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.v3.GroupIdRecordManager;
import org.orcid.core.manager.v3.PeerReviewManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.utils.v3.activities.PeerReviewGroupComparator;
import org.orcid.core.utils.v3.activities.PeerReviewMinimizedSummaryComparator;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.PeerReviewMinimizedSummary;
import org.orcid.pojo.ajaxForm.PeerReviewForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.grouping.PeerReviewDuplicateGroup;
import org.orcid.pojo.grouping.PeerReviewGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private static final Logger log = LoggerFactory.getLogger(PeerReviewsController.class);
    @Resource
    private IssnPortalUrlBuilder issnPortalUrlBuilder;
    
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
        List<PeerReviewGroup> peerReviewGroups = getPeerReviewsGrouped(peerReviewManager.getPeerReviewSummaryList(getEffectiveUserOrcid()));
        peerReviewGroups.sort(new PeerReviewGroupComparator(!sortAsc));
        return peerReviewGroups;
    }

    @RequestMapping(value = "/peer-reviews-minimized.json", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<List<PeerReviewMinimizedSummary>> getPeerReviewsMinimizedJson(@RequestParam("sortAsc") boolean sortAsc){
        List<PeerReviewMinimizedSummary> peerReviewMinimizedSummaryList = peerReviewManager.getPeerReviewMinimizedSummaryList(getEffectiveUserOrcid(), false);
        if (peerReviewMinimizedSummaryList.size() == 0) {
            return new ResponseEntity<List<PeerReviewMinimizedSummary>>(peerReviewMinimizedSummaryList, HttpStatus.OK);
        }
        peerReviewMinimizedSummaryList.sort(new PeerReviewMinimizedSummaryComparator((!sortAsc)));
        return new ResponseEntity<List<PeerReviewMinimizedSummary>>(peerReviewMinimizedSummaryList, HttpStatus.OK);
    }

    @RequestMapping(value = "/peer-reviews-by-group-id.json", method = RequestMethod.GET)
    public @ResponseBody List<PeerReviewGroup> getPeerReviewsJsonByGroupId(@RequestParam("groupId") String groupId, @RequestParam("sortAsc") boolean sortAsc) {
        List<PeerReviewGroup> peerReviewGroups = getPeerReviewsGrouped(peerReviewManager.getPeerReviewSummaryListByGroupId(getEffectiveUserOrcid(), groupId, false));
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
    @RequestMapping(value = "/{groupId}/visibility/{visibilityStr}", method = RequestMethod.POST)
    public @ResponseBody boolean updateVisibilitys(@PathVariable("groupId") String groupIdString,
            @PathVariable("visibilityStr") String visibilityStr) {
        if (PojoUtil.isEmpty(groupIdString)) {
            return false;
        }
        // make sure this is a users work
        String orcid = getEffectiveUserOrcid();
        try {
            long groupId = Long.parseLong(groupIdString);
            GroupIdRecord groupIdRecord = groupIdRecordManager.getGroupIdRecord(groupId);
            if (groupIdRecord == null) {
                return false;
            }
            return peerReviewManager.updateVisibilitiesByGroupId(orcid, groupIdRecord.getGroupId(), Visibility.fromValue(visibilityStr));
        } catch(GroupIdRecordNotFoundException e) {
            log.warn("Unable to find group_id_record with id {}", groupIdString);
        } catch(NumberFormatException nfe) {
            log.warn("Invalid id for a group id {}", groupIdString);
        }
        return false;
    }

    private List<PeerReviewGroup> getPeerReviewsGrouped(List<PeerReviewSummary> summaries) {
        List<PeerReviewGroup> peerReviewGroups = new ArrayList<>();
        PeerReviews peerReviews = peerReviewManager.groupPeerReviews(summaries, false);
        for (org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
            Optional<GroupIdRecord> groupIdRecord = groupIdRecordManager.findByGroupId(group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());
            if (groupIdRecord.isPresent()) {
                GroupIdRecord record = groupIdRecord.get();
                PeerReviewGroup peerReviewGroup = PeerReviewGroup.getInstance(group, record);
                String g = record.getGroupId();
                if (IssnGroupIdPatternMatcher.isIssnGroupType(g)) {
                    String issn = IssnGroupIdPatternMatcher.getIssnFromIssnGroupId(g);
                    peerReviewGroup.setUrl(issnPortalUrlBuilder.buildIssnPortalUrlForIssn(issn));
                    peerReviewGroup.setGroupType("ISSN");
                    peerReviewGroup.setGroupIdValue(issn);
                }

                for (PeerReviewDuplicateGroup duplicateGroup : peerReviewGroup.getPeerReviewDuplicateGroups()) {
                    for (PeerReviewForm peerReviewForm : duplicateGroup.getPeerReviews()) {
                        if (peerReviewForm.getCountry() != null) {
                            peerReviewForm.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, peerReviewForm.getCountry().getValue())));
                        }
                    }
                }
                peerReviewGroups.add(peerReviewGroup);
            }
        }
        return peerReviewGroups;
    }
}
