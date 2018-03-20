package org.orcid.core.manager;

import java.util.List;

public interface BackupCodeManager {
    
    boolean verify(String orcid, String code);
    
    List<String> createBackupCodes(String orcid);

    void removeUnusedBackupCodes(String orcid);
    
}
