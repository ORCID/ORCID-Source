package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;

import org.orcid.api.common.util.v3.PublicRecordUtils;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PublicRecordApiController {

    @Resource
    private PublicRecordUtils publicRecordUtils;

    private final boolean filterVersionOfIdentifiers = false;

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/record", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            OrcidApiConstants.ORCID_JSON, OrcidApiConstants.VND_ORCID_JSON })
    public @ResponseBody Record viewRecord(@PathVariable("orcid") String orcid) {
        return publicRecordUtils.getPublicRecord(orcid, filterVersionOfIdentifiers);
    }
}
