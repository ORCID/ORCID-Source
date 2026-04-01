package org.orcid.api.common.util.v3;

import org.orcid.core.api.publicapi.v3.security.PublicAPISecurityManagerV3;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.core.version.impl.Api3_0LastModifiedDatesHelper;
import org.orcid.jaxb.model.v3.release.record.Record;

public class PublicRecordUtils {

    private PublicRecordUtils() {
    }

    public static Record getPublicRecord(String orcid, RecordManagerReadOnly recordManagerReadOnly, OrcidSecurityManager orcidSecurityManager,
            PublicAPISecurityManagerV3 publicAPISecurityManagerV3, SourceUtils sourceUtilsReadOnly, boolean filterVersionOfIdentifiers) {
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

