package org.orcid.frontend.web.controllers;

import jakarta.annotation.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.apache.commons.lang3.StringUtils;
import org.orcid.api.common.util.v3.PublicRecordUtils;
import org.orcid.api.publicV3.server.security.PublicAPISecurityManagerV3;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.v3.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Country;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class PublicRecordApiController {

    @Resource(name = "recordManagerReadOnlyV3")
    private RecordManagerReadOnly recordManagerReadOnly;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "publicAPISecurityManagerV3")
    private PublicAPISecurityManagerV3 publicAPISecurityManagerV3;

    @Resource(name = "groupIdRecordManagerReadOnlyV3")
    private GroupIdRecordManagerReadOnly groupIdRecordManagerReadOnlyV3;

    private final ObjectMapper mapper;

    private final boolean filterVersionOfIdentifiers = false;

    public PublicRecordApiController() {
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JaxbAnnotationModule());
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/record", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            OrcidApiConstants.ORCID_JSON, OrcidApiConstants.VND_ORCID_JSON })
    public @ResponseBody String viewRecord(HttpServletRequest request, @PathVariable("orcid") String orcid) throws JsonProcessingException {
        orcidSecurityManager.checkProfile(orcid);
        request.setAttribute(SourceEntityUtils.DO_NOT_POPULATE_SOURCES, true);
        Record publicRecord = recordManagerReadOnly.getPublicRecord(orcid, filterVersionOfIdentifiers);
        if(publicRecord != null && publicRecord.getActivitiesSummary() != null && publicRecord.getActivitiesSummary().getPeerReviews() != null && publicRecord.getActivitiesSummary().getPeerReviews().getPeerReviewGroup() != null) {
            for(PeerReviewGroup prg : publicRecord.getActivitiesSummary().getPeerReviews().getPeerReviewGroup()) {
                for(PeerReviewDuplicateGroup prdg : prg.getPeerReviewGroup()) {
                    for(PeerReviewSummary summary : prdg.getPeerReviewSummary()) {
                        // This is a hack, the peer reviews come from the DB with the `group_id`, however, we need to display the name of that group id,
                        // which is in the group_id_record.group_name, so, we will replace the review-group-id with the group_name
                        String groupId = summary.getGroupId();
                        if(StringUtils.isNotBlank(groupId)) {
                            Optional<GroupIdRecord> opt = groupIdRecordManagerReadOnlyV3.findByGroupId(groupId);
                            opt.ifPresent(groupIdRecord -> summary.setGroupId(groupIdRecord.getName()));
                        }
                    }
                }
            }
        }
        return mapper.writeValueAsString(publicRecord);
    }
}
