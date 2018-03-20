package org.orcid.core.utils.v3;

import org.apache.commons.lang.StringUtils;
import org.orcid.persistence.jpa.entities.RecordNameEntity;

public class RecordNameUtils {

    public static String getPublicName(RecordNameEntity recordName) {
        if (org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.equals(recordName.getVisibility())) {
            if (!StringUtils.isBlank(recordName.getCreditName())) {
                return recordName.getCreditName();
            } else {
                return recordName.getGivenNames() + (StringUtils.isBlank(recordName.getFamilyName()) ? "" : " " + recordName.getFamilyName());
            }
        }
        return null;
    }

}
