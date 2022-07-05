package org.orcid.utils.jersey.marshaller;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.Work;

public class ORCIDMarshaller {
    private final JAXBContext jaxbContext_2_0_api;
    private final JAXBContext jaxbContext_2_0_activities_api;

    private final JAXBContext jaxbContext_3_0_api;
    private final JAXBContext jaxbContext_3_0_activities_api;

    public ORCIDMarshaller() throws JAXBException {
        // Initialize JAXBContext
        this.jaxbContext_2_0_api = JAXBContext.newInstance(Record.class, ActivitiesSummary.class, OrcidError.class);
        this.jaxbContext_2_0_activities_api = JAXBContext.newInstance(Education.class, Employment.class, Funding.class, Work.class, PeerReview.class);
        this.jaxbContext_3_0_api = JAXBContext.newInstance(org.orcid.jaxb.model.v3.release.record.Record.class,
                org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary.class, org.orcid.jaxb.model.v3.release.error.OrcidError.class);
        this.jaxbContext_3_0_activities_api = JAXBContext.newInstance(org.orcid.jaxb.model.v3.release.record.Distinction.class,
                org.orcid.jaxb.model.v3.release.record.Education.class, org.orcid.jaxb.model.v3.release.record.Employment.class,
                org.orcid.jaxb.model.v3.release.record.Funding.class, org.orcid.jaxb.model.v3.release.record.InvitedPosition.class,
                org.orcid.jaxb.model.v3.release.record.Membership.class, org.orcid.jaxb.model.v3.release.record.PeerReview.class,
                org.orcid.jaxb.model.v3.release.record.Qualification.class, org.orcid.jaxb.model.v3.release.record.ResearchResource.class,
                org.orcid.jaxb.model.v3.release.record.Service.class, org.orcid.jaxb.model.v3.release.record.Work.class);
    }
    
    public byte[] toXML(Object object) throws JAXBException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Marshaller marshaller = null;
        Class<?> c = object.getClass();
        if (org.orcid.jaxb.model.record_v2.Record.class.isAssignableFrom(c) || org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.error_v2.OrcidError.class.isAssignableFrom(c)) {
            marshaller = jaxbContext_2_0_api.createMarshaller();
        } else if (org.orcid.jaxb.model.record_v2.Education.class.isAssignableFrom(c) || org.orcid.jaxb.model.record_v2.Employment.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.record_v2.Funding.class.isAssignableFrom(c) || org.orcid.jaxb.model.record_v2.Work.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.record_v2.PeerReview.class.isAssignableFrom(c)) {
            marshaller = jaxbContext_2_0_activities_api.createMarshaller();
        } else if (org.orcid.jaxb.model.v3.release.record.Record.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.error.OrcidError.class.isAssignableFrom(c)) {
            marshaller = jaxbContext_3_0_api.createMarshaller();
        } else if (org.orcid.jaxb.model.v3.release.record.Distinction.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.Education.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.Employment.class.isAssignableFrom(c) || org.orcid.jaxb.model.v3.release.record.Funding.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.InvitedPosition.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.Membership.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.PeerReview.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.Qualification.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.ResearchResource.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.Service.class.isAssignableFrom(c) || org.orcid.jaxb.model.v3.release.record.Work.class.isAssignableFrom(c)) {
            marshaller = jaxbContext_3_0_activities_api.createMarshaller();
        } else {
            throw new IllegalArgumentException("Unable to unmarshall class " + c);
        }
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(object, baos);
        return baos.toByteArray();
    }
}
