package org.orcid.pojo;

import java.io.Serializable;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.common.Organization;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.Date;

public class ResearchResource implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date createdDate;

    private String path;
    
    private String displayIndex;

    private String source;

    private String sourceName;

    private String putCode;

    private String url;

    private List<ActivityExternalIdentifier> externalIdentifiers;

    private String title;    
    
    private String translatedTitle;
    
    private String translatedTitleLanguageCode;
    
    private Date startDate;
   
    private Date  endDate;
    
    private Visibility visibility;
    
    private List<Org> hosts;

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public ResearchResource fromValue(ResearchResourceSummary researchResourceSummary) {
        for (Organization organization : researchResourceSummary.getProposal().getHosts()) {
            Org org = Org.valueOf(organization);
        }
    }

}
