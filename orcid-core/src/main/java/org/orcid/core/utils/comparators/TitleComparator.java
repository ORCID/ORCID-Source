package org.orcid.core.utils.comparators;

import java.util.Comparator;

import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;

public class TitleComparator implements Comparator<WorkGroup> {
    @Override
    public int compare(WorkGroup o1, WorkGroup o2) {
        String firstTitle = getTitle(o1.getWorkSummary().get(0));
        String secondTitle = getTitle(o2.getWorkSummary().get(0));
        
        if (firstTitle == null && secondTitle != null) {
            return -1;
        }
        
        if (secondTitle == null && firstTitle != null) {
            return 1;
        }
        
        int comparison = 0;
        if (firstTitle != null && secondTitle != null) {
            comparison = firstTitle.compareTo(secondTitle);
        }
        
        if (comparison == 0) {
            String firstSubtitle = getSubtitle(o1.getWorkSummary().get(0));
            String secondSubtitle = getSubtitle(o2.getWorkSummary().get(0));
            
            if (firstSubtitle == null && secondSubtitle == null) {
                return 0;
            }
            
            if (firstSubtitle == null) {
                return -1;
            }
            
            if (secondSubtitle == null) {
                return 1;
            }
            
            comparison = firstSubtitle.compareTo(secondSubtitle);
        }
        return comparison;
    }
    
    private String getTitle(WorkSummary workSummary) {
        if (workSummary.getTitle() == null) {
            return null;
        }
        
        if (workSummary.getTitle().getTitle() == null) {
            return null;
        }
        
        if (workSummary.getTitle().getTitle().getContent() == null) {
            return null;
        }
        
        return workSummary.getTitle().getTitle().getContent().toLowerCase();
    }
    
    private String getSubtitle(WorkSummary workSummary) {
        if (workSummary.getTitle() == null) {
            return null;
        }
        
        if (workSummary.getTitle().getSubtitle() == null) {
            return null;
        }
        
        if (workSummary.getTitle().getSubtitle().getContent() == null) {
            return null;
        }
        
        return workSummary.getTitle().getSubtitle().getContent().toLowerCase();
    }
}
