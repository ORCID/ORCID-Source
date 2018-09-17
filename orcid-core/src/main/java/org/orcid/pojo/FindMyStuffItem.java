package org.orcid.pojo;

public class FindMyStuffItem {
    public String id;
    public String idType;
    public String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FindMyStuffItem(String id, String idType, String title) {
        this.id = id;
        this.idType = idType;
        this.title = title;
    }
}