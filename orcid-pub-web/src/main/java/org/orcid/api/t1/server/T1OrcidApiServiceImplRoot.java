package org.orcid.api.t1.server;

import static org.orcid.core.api.OrcidApiConstants.BIO_SEARCH_PATH;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.core.togglz.Features;
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

    /**
     * Gets the JSON representation any Orcid Profiles (BIO) only relevant to
     * the given query
     * 
     * @param query
     * @return
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BIO_SEARCH_PATH)
    public Response searchByQueryJSON(String query) {
        Map<String, List<String>> queryParams = uriInfo.getQueryParameters();
        Response jsonQueryResults = null;
        if (Features.PUB_API_2_0_BY_DEFAULT.isActive()) {
            jsonQueryResults = api20ServiceDelegator.searchByQuery(queryParams);
        } else {
            jsonQueryResults = orcidApiServiceDelegator.publicSearchByQuery(queryParams);
            registerSearchMetrics(jsonQueryResults);
        }
        return jsonQueryResults;
    }

    /**
     * Gets the XML representation any Orcid Profiles (BIO) only relevant to the
     * given query
     * 
     * @param query
     * @return
     */
    @Override
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(BIO_SEARCH_PATH)
    public Response searchByQueryXML(String query) {
        Map<String, List<String>> queryParams = uriInfo.getQueryParameters();
        Response xmlQueryResults = null;
        if (Features.PUB_API_2_0_BY_DEFAULT.isActive()) {
            xmlQueryResults = api20ServiceDelegator.searchByQuery(queryParams);
        } else {
            xmlQueryResults = orcidApiServiceDelegator.publicSearchByQuery(queryParams);
            registerSearchMetrics(xmlQueryResults);
        }
        return xmlQueryResults;
    }
}
