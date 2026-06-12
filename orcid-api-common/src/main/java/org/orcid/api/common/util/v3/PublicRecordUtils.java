package org.orcid.api.common.util.v3;

import jakarta.annotation.Resource;

import org.orcid.api.publicV3.server.security.PublicAPISecurityManagerV3;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.core.version.impl.Api3_0LastModifiedDatesHelper;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.springframework.stereotype.Component;

@Component
public class PublicRecordUtils {

    @Resource(name = "recordManagerReadOnlyV3")
    private RecordManagerReadOnly recordManagerReadOnly;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "publicAPISecurityManagerV3")
    private PublicAPISecurityManagerV3 publicAPISecurityManagerV3;

    @Resource(name = "sourceUtilsReadOnlyV3")
    private SourceUtils sourceUtilsReadOnly;

    public Record getPublicRecord(String orcid, boolean filterVersionOfIdentifiers) {
        orcidSecurityManager.checkProfile(orcid);
        Record record = recordManagerReadOnly.getPublicRecord(orcid, filterVersionOfIdentifiers);
        publicAPISecurityManagerV3.filter(record);
        if (record.getPerson() != null) {
            sourceUtilsReadOnly.setSourceName(record.getPerson());
        }
        if (record.getActivitiesSummary() != null) {
            ActivityUtils.cleanEmptyFields(record.getActivitiesSummary());
            sourceUtilsReadOnly.setSourceName(record.getActivitiesSummary());
        }
        ElementUtils.setPathToRecord(record, orcid);
        Api3_0LastModifiedDatesHelper.calculateLastModified(record);
        return record;
    }
}
