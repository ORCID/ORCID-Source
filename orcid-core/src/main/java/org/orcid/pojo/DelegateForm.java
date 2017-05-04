/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo;

import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.pojo.ajaxForm.Text;

public class DelegateForm {
    Text giverOrcid;
    Text receiverOrcid;
    Text receiverName;
    XMLGregorianCalendar approvalDate;

    public Text getGiverOrcid() {
        return giverOrcid;
    }

    public void setGiverOrcid(Text giverOrcid) {
        this.giverOrcid = giverOrcid;
    }

    public Text getReceiverOrcid() {
        return receiverOrcid;
    }

    public void setReceiverOrcid(Text receiverOrcid) {
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

}
