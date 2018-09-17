package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.model.v3.rc1.notification.internal.NotificationFindMyStuff;

public class FindMyStuffResult {

    public String finderName;
    public List<FindMyStuffItem> results;
    public int total;
    private NotificationFindMyStuff notification;

    public NotificationFindMyStuff getNotification() {
        return notification;
    }
    public void setNotification(NotificationFindMyStuff notificationFindMyStuff) {
        this.notification = notificationFindMyStuff;
    }
    public List<FindMyStuffItem> getResults() {
        if (results == null)
            results = new ArrayList<FindMyStuffItem>();
        return results;
    }
    public void setResults(List<FindMyStuffItem> results) {
        this.results = results;
    }
    public String getFinderName() {
        return finderName;
    }
    public void setFinderName(String serviceName) {
        this.finderName = serviceName;
    }
    public void setTotal(int totalResults) {
        this.total=totalResults;
    }    
    public int getTotal(){
        return total;
    }
    
}
