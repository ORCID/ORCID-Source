package org.orcid.pojo.grouping;

import java.io.Serializable;
import java.util.List;

public class WorkGroupingSuggestions implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<WorkGroupingSuggestion> suggestions;
    
    private boolean moreAvailable;
    
    public WorkGroupingSuggestions() {
        
    }

    public List<WorkGroupingSuggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<WorkGroupingSuggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public boolean isMoreAvailable() {
        return moreAvailable;
    }

    public void setMoreAvailable(boolean moreAvailable) {
        this.moreAvailable = moreAvailable;
    }
    
}
