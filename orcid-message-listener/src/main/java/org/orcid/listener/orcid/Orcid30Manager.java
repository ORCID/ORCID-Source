package org.orcid.listener.orcid;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResources;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
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
    Record fetchPublicRecord(BaseMessage message) throws LockedRecordException, DeprecatedRecordException, ExecutionException;
    
    ResearchResource fetchResearchResource(String orcid, Long putCode);
    
    ResearchResources fetchResearchResources(String orcid);

    public byte[] fetchActivity(String orcid, Long putCode, String endpoint) throws IOException, InterruptedException;
}