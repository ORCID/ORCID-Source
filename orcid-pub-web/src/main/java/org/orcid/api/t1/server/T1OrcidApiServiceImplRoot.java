package org.orcid.api.t1.server;

import javax.ws.rs.Path;

import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.jaxb.model.groupid_rc1.GroupIdRecord;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.Work;

/**
 * 
 * @author Will Simpson
 * 
 */
@Path("/")
public class T1OrcidApiServiceImplRoot extends T1OrcidApiServiceImplBase {

    protected PublicV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work> api20ServiceDelegator;

    public void setApi20ServiceDelegator(
            PublicV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work> serviceDelegator) {
        this.api20ServiceDelegator = serviceDelegator;
    }

}
