package org.orcid.pojo;

public class LockAccounts {
    
    private String orcidsToLock;
    
    private String lockReason;
    
    private String description;

    public String getOrcidsToLock() {
        return orcidsToLock;
    }

    public void setOrcidsToLock(String orcidsToLock) {
        this.orcidsToLock = orcidsToLock;
    }

    public String getLockReason() {
        return lockReason;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
