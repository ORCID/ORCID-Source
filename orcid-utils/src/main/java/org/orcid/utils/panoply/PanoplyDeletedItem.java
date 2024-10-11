package org.orcid.utils.panoply;

public class PanoplyDeletedItem {
    private Long id;
    private String dwTable;
    private Long itemId;
    private String clientSourceId;
    private String orcid;

    public final String DW_ORG_AFFILIATION_RELATION = "dw_org_affiliation_relation";
    public final String DW_WORK = "dw_work";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDwTable() {
        return dwTable;
    }

    public void setDwTable(String dwTable) {
        this.dwTable = dwTable;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getClientSourceId() {
        return clientSourceId;
    }

    public void setClientSourceId(String clientSourceId) {
        this.clientSourceId = clientSourceId;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Override
    public String toString() {
        return "PanoplyDeletedItem{" + "id=" + id + ", dwTable='" + dwTable + '\'' + ", itemId='" + itemId + '\'' + ", clientSourceId='" + clientSourceId + '\''
                + ", orcid='" + orcid + '\'' + '}';
    }

}
