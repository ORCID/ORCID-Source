package org.orcid.pojo;

/**
 * 
 * @author Will Simpson
 *
 */
public class RemoteUser {

    private String userId;
    private String idType;

    public RemoteUser(String userId, String idType) {
        this.userId = userId;
        this.idType = idType;
    }

    public String getUserId() {
        return userId;
    }

    public String getIdType() {
        return idType;
    }

}
