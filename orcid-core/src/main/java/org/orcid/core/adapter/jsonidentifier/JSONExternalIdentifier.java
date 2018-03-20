package org.orcid.core.adapter.jsonidentifier;

public class JSONExternalIdentifier {
    
    protected String type;
    
    protected String value;
    
    protected JSONUrl url;
    
    protected String relationship;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public JSONUrl getUrl() {
        return url;
    }

    public void setUrl(JSONUrl url) {
        this.url = url;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

}
