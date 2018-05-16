package org.orcid.listener.orcid;

import java.util.concurrent.ExecutionException;

import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record_v2.Affiliation;
import org.orcid.jaxb.model.record_v2.AffiliationType;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.utils.listener.BaseMessage;

public interface Orcid20Manager {

    /**
     * Fetches the public record
     * 
     * Caches based on message.
     * 
     * @param orcid
     * @return Record
     * @throws ExecutionException 
     */
    Record fetchPublicRecord(BaseMessage message) throws LockedRecordException, DeprecatedRecordException, ExecutionException;

    /**
     * Fetches the public activities
     * 
     * @param orcid
     * @return Activities
     */
    ActivitiesSummary fetchPublicActivitiesSummary(BaseMessage message) throws LockedRecordException, DeprecatedRecordException;

    Affiliation fetchAffiliation(String orcid, Long putCode, AffiliationType type);

    Funding fetchFunding(String orcid, Long putCode);

    Work fetchWork(String orcid, Long putCode);

    PeerReview fetchPeerReview(String orcid, Long putCode);

}