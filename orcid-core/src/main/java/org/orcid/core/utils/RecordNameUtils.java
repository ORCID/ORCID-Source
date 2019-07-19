package org.orcid.core.utils;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.jpa.entities.RecordNameEntity;

public class RecordNameUtils {

    public static String getPublicName(RecordNameEntity recordName) {
        if(recordName == null) {
            return null;
        }
        if (Visibility.PUBLIC.name().equals(recordName.getVisibility())) {
            if (!StringUtils.isBlank(recordName.getCreditName())) {
                return recordName.getCreditName();
            } else {
                return recordName.getGivenNames() + (StringUtils.isBlank(recordName.getFamilyName()) ? "" : " " + recordName.getFamilyName());
            }
        }
        return null;
    }
    
    public static String getDisplayName(RecordNameEntity recordNameEntity) {
        if(recordNameEntity == null) {
            return null;
        }
        if (StringUtils.isNotBlank(recordNameEntity.getCreditName()) && Visibility.PUBLIC.name().equals(recordNameEntity.getVisibility())) {
            return recordNameEntity.getCreditName();
        }
        
        return buildName(recordNameEntity.getGivenNames(), recordNameEntity.getFamilyName());
    }
    
    public static String getCreditName(RecordNameEntity recordNameEntity) {
        if(recordNameEntity == null) {
            return null;
        }
        if (StringUtils.isNotBlank(recordNameEntity.getCreditName())) {
            return recordNameEntity.getCreditName();
        }
        
        return buildName(recordNameEntity.getGivenNames(), recordNameEntity.getFamilyName());
    }        
    
    private static String buildName(String givenNames, String familyName) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(givenNames)) {
            builder.append(givenNames);
        }
        if (StringUtils.isNotBlank(familyName)) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(familyName);
        }
        return builder.length() > 0 ? builder.toString() : null;
    }

}
