package org.orcid.persistence.jpa.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "public_api_daily_rate_limit")
public class PublicApiDailyRateLimitEntity implements OrcidEntity<Long>{
    
    private static final long serialVersionUID = 7137838021634312424L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "papi_daily_limit_seq")
    @SequenceGenerator(name = "papi_daily_limit_seq", sequenceName = "papi_daily_limit_seq", allocationSize = 1)
    private Long id;

    @Column(name = "client_id", nullable = true)
    private String clientId;

    @Column(name = "ip_address", nullable = true)
    private String ipAddress;

    @Column(name = "request_count", nullable = false)
    private Long requestCount;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;
    
    @Column(name = "date_created", nullable = false)
    private Date dateCreated;
    
    @Column(name = "last_modified", nullable = false)
    private Date lastModified;

    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Long requestCount) {
        this.requestCount = requestCount;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }
   
    
    public Date getDateCreated() {
        return dateCreated;
    }    

    void setDateCreated(Date date) {
        this.dateCreated = date;
    }
    
    public Date getLastModified() {
        return lastModified;
    }

    void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    @PreUpdate
    void preUpdate() {
        lastModified = new Date();
    }

    @PrePersist
    void prePersist() {
        Date now = new Date();
        dateCreated = now;
        lastModified = now;
    }
    
    public static <I extends Serializable, E extends OrcidEntity<I>> Map<I, E> mapById(Collection<E> entities) {
        Map<I, E> map = new HashMap<I, E>(entities.size());
        for (E entity : entities) {
            map.put(entity.getId(), entity);
        }
        return map;
    }

}

