package org.orcid.persistence.jpa.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "org_import_log")
public class OrgImportLogEntity extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String source;
    
    private Date start;
    
    private Date end;
    
    private Long id;
    
    private String file;
    
    private boolean successful;
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "org_import_log_seq")
    @SequenceGenerator(name = "org_import_log_seq", sequenceName = "org_import_log_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "source_type")
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Column(name = "start_time", nullable = false)
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    @Column(name = "end_time", nullable = false)
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Column
    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    @Column
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
    
}
