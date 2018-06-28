package org.orcid.core.admin;

public enum LockReason {
    
    SPAM("Spam"), DISPUTE("Dispute"), UNCLAIMED("Unclaimed"), OTHER("Other"), INSTITUTION("Institution"), TEST("Test");
    
    private String label;
    
    LockReason(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    public static LockReason getLockReasonByLabel(String label) {
        for (LockReason lockReason : LockReason.values()) {
            if (lockReason.getLabel().equals(label)) {
                return lockReason;
            }
        }
        return null;
    }

}
