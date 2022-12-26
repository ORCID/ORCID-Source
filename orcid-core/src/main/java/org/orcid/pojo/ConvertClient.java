package org.orcid.pojo;

import java.io.Serializable;

public class ConvertClient implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String clientId;
    
    private String groupId;
    
    private boolean clientNotFound;
    
    private boolean clientDeactivated;
    
    private boolean groupIdNotFound;
    
    private boolean groupIdDeactivated;
    
    private boolean alreadyMember;
    
    private boolean success;
    
    private String error;
    
    private String targetClientType;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isClientNotFound() {
        return clientNotFound;
    }
    
    public boolean isClientDeactivated() {
        return clientDeactivated;
    }

    public void setClientNotFound(boolean clientNotFound) {
        this.clientNotFound = clientNotFound;
    }

    public boolean isGroupIdNotFound() {
        return groupIdNotFound;
    }

    public void setGroupIdNotFound(boolean groupIdNotFound) {
        this.groupIdNotFound = groupIdNotFound;
    }

    public boolean isAlreadyMember() {
        return alreadyMember;
    }

    public void setAlreadyMember(boolean alreadyMember) {
        this.alreadyMember = alreadyMember;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTargetClientType() {
        return targetClientType;
    }

    public void setTargetClientType(String targetClientType) {
        this.targetClientType = targetClientType;
    }

    public boolean isGroupIdDeactivated() {
        return groupIdDeactivated;
    }

    public void setGroupIdDeactivated(boolean groupIdDeactivated) {
        this.groupIdDeactivated = groupIdDeactivated;
    }

    public void setClientDeactivated(boolean b) {
        this.clientDeactivated = b;
    }
    
}
