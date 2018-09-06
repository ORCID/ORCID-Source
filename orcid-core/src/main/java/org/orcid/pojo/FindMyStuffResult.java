package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.model.v3.rc1.notification.internal.NotificationFindMyStuff;
import org.orcid.persistence.jpa.entities.NotificationFindMyStuffEntity;

public class FindMyStuffResult {

    public String finderName;
    public List<Result> results;
    private NotificationFindMyStuff notification;

    public NotificationFindMyStuff getNotification() {
        return notification;
    }
    public void setNotification(NotificationFindMyStuff notificationFindMyStuff) {
        this.notification = notificationFindMyStuff;
    }
    public List<Result> getResults() {
        if (results == null)
            results = new ArrayList<Result>();
        return results;
    }
    public void setResults(List<Result> results) {
        this.results = results;
    }
    public String getFinderName() {
        return finderName;
    }
    public void setFinderName(String serviceName) {
        this.finderName = serviceName;
    }    
    
    public static class Result{
        String id;
        String idType;
        String title;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getIdType() {
            return idType;
        }
        public void setIdType(String idType) {
            this.idType = idType;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public Result(String id, String idType, String title) {
            super();
            this.id = id;
            this.idType = idType;
            this.title = title;
        }
    }
    
}
