package org.orcid.core.adapter.jsonidentifier.converter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.release.record.*;
import org.orcid.jaxb.model.v3.release.record.summary.*;

/**
 * Utility for creating JAXB contexts with v3.release model classes needed for unmarshaling.
 * Uses the same approach as OrcidValidationJaxbContextResolver.
 */
public class JaxbContextUtil {
    
    private static final Map<Class<?>, String> V3_RELEASE_CLASSES_MAP = new HashMap<>();
    static {
        V3_RELEASE_CLASSES_MAP.put(GroupIdRecord.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(NotificationPermission.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Address.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Addresses.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Education.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Email.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Employment.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Funding.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Keyword.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Keywords.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Name.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.OtherName.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.OtherNames.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.PeerReview.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.ResearcherUrl.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.ResearcherUrls.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Work.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.WorkBulk.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Educations.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Employments.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Distinction.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.InvitedPosition.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Membership.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Qualification.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(org.orcid.jaxb.model.v3.release.record.Service.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(ExternalID.class, "v3");
        V3_RELEASE_CLASSES_MAP.put(ExternalIDs.class, "v3");
    }
    
    /**
     * Create a JAXBContext with essential v3.release model classes for unmarshaling.
     * Only includes Education, Funding, Work, PeerReview and their dependencies.
     */
    public static JAXBContext createV3ReleaseContext() throws JAXBException {
        Class<?>[] classes = V3_RELEASE_CLASSES_MAP.keySet().toArray(new Class[0]);
        return JAXBContext.newInstance(classes);
    }
}
