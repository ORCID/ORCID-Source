package org.orcid.listener.orcid;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;

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
    Record fetchPublicRecord(String orcid) throws LockedRecordException, DeprecatedRecordException, ExecutionException;

    public byte[] fetchActivity(String orcid, Long putCode, String endpoint) throws IOException, InterruptedException;     
}