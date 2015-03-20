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
package org.orcid.api.common.util;

import org.orcid.core.api.OrcidApiConstants;
import org.orcid.jaxb.model.record.Activity;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary.EducationSummary;
import org.orcid.jaxb.model.record.summary.Educations;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.record.summary.Employments;
import org.orcid.jaxb.model.record.summary.Fundings;
import org.orcid.jaxb.model.record.summary.Works;

public class ActivityUtils {

    /**
     * In order to provide a more meaningful put code for activities, we will
     * replace the actual put code for one that represent a path to the activity
     * in the orcid record.
     * 
     * The new put code will follow this pattern:
     * 
     * /orcid/activity-type/putCode
     * 
     * @param Activity
     *            An object that contains a putCode element
     * @param orcid
     *            The activity owner
     * */
    public static void updatePutCodeToPath(Activity activity, String orcid) {
        String putCode = activity.getPutCode();
        String activityType = OrcidApiConstants.ACTIVITY_WORK;

        if (Education.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_EDUCATION;
        } else if (Employment.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_EMPLOYMENT;
        } else if (Funding.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_FUNDING;
        }
        // Build the new put code which contains the path to the activity
        String newPutCode = '/' + orcid + '/' + activityType + '/' + putCode;

        // Update the put code
        activity.setPutCode(newPutCode);
    }
    
    /**
     * In order to provide a more meaningful put code for activities, we will
     * replace the actual put code for one that represent a path to the activity
     * in the orcid record.
     * 
     * The new put code will follow this pattern:
     * 
     * /orcid/activity-type/putCode
     * 
     * @param ActivitiesSummary
     *            An object that contains several elements to update the putCode element
     * @param orcid
     *            The activity owner
     * */
    public static void updatePutCodeToPath(ActivitiesSummary activitiesSummary, String orcid) {
        Educations educations = activitiesSummary.getEducations();
        Employments employments = activitiesSummary.getEmployments();
        Fundings fundings = activitiesSummary.getFundings();
        Works works = activitiesSummary.getWorks();
        
        if(educations != null && !educations.getSummaries().isEmpty()) {
            for(EducationSummary summary : educations.getSummaries()) {
                ActivityUtils.updatePutCodeToPath(summary, orcid);
            }
        }
        
        if(employments != null && !employments.getSummaries().isEmpty()) {
            for(EmploymentSummary summary : employments.getSummaries()) {
                ActivityUtils.updatePutCodeToPath(summary, orcid);
            }
        }
    }

}
