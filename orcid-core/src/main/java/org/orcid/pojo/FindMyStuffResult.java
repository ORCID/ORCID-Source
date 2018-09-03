package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

public class FindMyStuffResult {

    public String serviceName;
    public List<Result> results;

    public List<Result> getResults() {
        if (results == null)
            results = new ArrayList<Result>();
        return results;
    }
    public void setResults(List<Result> results) {
        this.results = results;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }    
    
    public static class Result{
        String id;
        String idType;
        String title;
        public Result(String id, String idType, String title) {
            super();
            this.id = id;
            this.idType = idType;
            this.title = title;
        }
    }
    
}
