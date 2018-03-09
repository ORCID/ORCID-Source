package org.orcid.api.lod;

import static org.orcid.core.api.OrcidApiConstants.APPLICATION_RDFXML;
import static org.orcid.core.api.OrcidApiConstants.JSON_LD;
import static org.orcid.core.api.OrcidApiConstants.N_TRIPLES;
import static org.orcid.core.api.OrcidApiConstants.TEXT_N3;
import static org.orcid.core.api.OrcidApiConstants.TEXT_TURTLE;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.jaxb.model.groupid_rc1.GroupIdRecord;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.Work;

@Path(OrcidApiConstants.EXPERIMENTAL_RDF_V1 )
public class ExperimentalRDFResource {

    protected PublicV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work> serviceDelegator;

    public void setServiceDelegator(
            PublicV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work> serviceDelegator) {
        this.serviceDelegator = serviceDelegator;
    }
    
    //Record 
    @GET
    @Produces(value = { APPLICATION_RDFXML, TEXT_TURTLE, TEXT_N3, JSON_LD, N_TRIPLES })
    @Path(OrcidApiConstants.RECORD_SIMPLE)
    public Response viewRecord(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewRecord(orcid);
    }
}
