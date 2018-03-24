package org.orcid.pojo;

import java.io.Serializable;

import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.jaxb.model.v3.dev1.common.OrcidIdentifier;
import org.orcid.pojo.ajaxForm.Text;

public class DelegateForm implements Serializable {
    private static final long serialVersionUID = 5765957766718405465L;
    OrcidIdentifier giverOrcid;
    Text giverName;
    OrcidIdentifier receiverOrcid;
    Text receiverName;
    XMLGregorianCalendar approvalDate;
    XMLGregorianCalendar lastModifiedDate;

    public OrcidIdentifier getGiverOrcid() {
        return giverOrcid;
    }

    public void setGiverOrcid(OrcidIdentifier giverOrcid) {
        this.giverOrcid = giverOrcid;
    }

    public Text getGiverName() {
        return giverName;
    }

    public void setGiverName(Text giverName) {
        this.giverName = giverName;
    }

    public OrcidIdentifier getReceiverOrcid() {
        return receiverOrcid;
    }

    public void setReceiverOrcid(OrcidIdentifier receiverOrcid) {
        this.receiverOrcid = receiverOrcid;
    }

    public Text getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(Text receiverName) {
        this.receiverName = receiverName;
    }

    public XMLGregorianCalendar getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(XMLGregorianCalendar approvalDate) {
        this.approvalDate = approvalDate;
    }

    public XMLGregorianCalendar getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(XMLGregorianCalendar lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
