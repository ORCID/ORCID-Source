package org.orcid.listener.orcid;

import java.util.concurrent.ExecutionException;

import org.orcid.jaxb.model.v3.rc2.record.Affiliation;
import org.orcid.jaxb.model.v3.rc2.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc2.record.Funding;
import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.Record;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.jaxb.model.v3.rc2.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResources;
import org.orcid.listener.exception.V3DeprecatedRecordException;
import org.orcid.listener.exception.V3LockedRecordException;
import org.orcid.utils.listener.BaseMessage;

public interface Orcid30Manager {

    /**
     * Fetches the public record
     * 
     * Caches based on message.
     * 
     * @param orcid
     * @return Record
     * @throws ExecutionException 
     */
    Record fetchPublicRecord(BaseMessage message) throws V3LockedRecordException, V3DeprecatedRecordException, ExecutionException;

    /**
     * Fetches the public activities
     * 
     * @param orcid
     * @return Activities
     */
    ActivitiesSummary fetchPublicActivitiesSummary(BaseMessage message) throws V3LockedRecordException, V3DeprecatedRecordException;

    Affiliation fetchAffiliation(String orcid, Long putCode, AffiliationType type);

    Funding fetchFunding(String orcid, Long putCode);

    Work fetchWork(String orcid, Long putCode);

    PeerReview fetchPeerReview(String orcid, Long putCode);
    
    ResearchResource fetchResearchResource(String orcid, Long putCode);
    
    ResearchResources fetchResearchResources(String orcid);

}