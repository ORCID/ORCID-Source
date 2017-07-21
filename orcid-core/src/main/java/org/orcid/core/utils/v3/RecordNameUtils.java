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
