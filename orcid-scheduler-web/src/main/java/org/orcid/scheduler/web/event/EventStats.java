package org.orcid.scheduler.web.event;

public interface EventStats {
    
    void saveEventStats();
    
    void deleteEvents();
   
    void savePapiEventStats();
    
    void deletePapiEvents();
}
