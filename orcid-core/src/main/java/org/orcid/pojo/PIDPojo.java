package org.orcid.pojo;

public class PIDPojo {

    private String idType="";
    private String idValue="";
    private String normValue ="";
    private String normUrl="";
    
    public PIDPojo(){
        
    }
    
    public PIDPojo(String idType, String idValue, String normValue, String normUrl) {
        super();
        this.idType = idType;
        this.idValue = idValue;
        this.normValue = normValue;
        this.normUrl = normUrl;
    }

    public String getIdType() {
        return idType;
    }
    public void setIdType(String idType) {
        this.idType = idType;
    }
    public String getIdValue() {
        return idValue;
    }
    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }
    public String getNormValue() {
        return normValue;
    }
    public void setNormValue(String normValue) {
        this.normValue = normValue;
    }
    public String getNormUrl() {
        return normUrl;
    }
    public void setNormUrl(String normUrl) {
        this.normUrl = normUrl;
    }
    
    
}
