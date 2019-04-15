package org.orcid.pojo.grouping;

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.FundingForm;

public class FundingGroup extends ActivityGroup {
    
    private static final long serialVersionUID = 1L;

    private String title;
    
    private Date startDate;
    
    private Date endDate;
    
    private String sourceName;
    
    private String source;
    
    private List<FundingForm> fundings;
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<FundingForm> getFundings() {
        return fundings;
    }

    public void setFundings(List<FundingForm> fundings) {
        this.fundings = fundings;
    }
    
    public static FundingGroup valueOf(org.orcid.jaxb.model.v3.release.record.summary.FundingGroup fundingGroup) {
        FundingGroup group = new FundingGroup();
        group.setFundings(new ArrayList<>());
        group.setUserVersionPresent(false);
        
        Long maxDisplayIndex = null;
        for (FundingSummary fundingSummary : fundingGroup.getFundingSummary()) {
            FundingForm fundingForm = FundingForm.valueOf(fundingSummary);
            group.setGroupId(fundingSummary.getPutCode()); // any value fine for group id as long as it's unique
            group.getFundings().add(fundingForm);

            Long displayIndex = Long.parseLong(fundingSummary.getDisplayIndex());
            if (maxDisplayIndex == null || displayIndex > maxDisplayIndex) {
                maxDisplayIndex = displayIndex;
                group.setActivePutCode(fundingSummary.getPutCode());
                group.setDefaultPutCode(fundingSummary.getPutCode());
                group.setActiveVisibility(fundingSummary.getVisibility().name());
                group.setStartDate(fundingSummary.getStartDate() != null ? Date.valueOf(fundingSummary.getStartDate()) : null);
                group.setEndDate(fundingSummary.getEndDate() != null ? Date.valueOf(fundingSummary.getEndDate()) : null);
                group.setTitle(fundingSummary.getTitle().getTitle().getContent());
            }
        }

        if (fundingGroup.getIdentifiers() != null) {
            for (ExternalID extId : fundingGroup.getIdentifiers().getExternalIdentifier()) {
                group.getExternalIdentifiers().add(ActivityExternalIdentifier.valueOf(extId));
            }
        }

        return group;
    }
    
}
