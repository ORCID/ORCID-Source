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
@Table(name = "backup_code")
public class BackupCodeEntity extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orcid;
    
    private String hashedCode;
    
    private Date usedDate;
    
    private Long id;
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "backup_code_seq")
    @SequenceGenerator(name = "backup_code_seq", sequenceName = "backup_code_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name = "orcid", length = 19)
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    
    @Column(name = "hashed_code")
    public String getHashedCode() {
        return hashedCode;
    }

    public void setHashedCode(String hashedCode) {
        this.hashedCode = hashedCode;
    }

    @Column(name = "used_date")
    public Date getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(Date usedDate) {
        this.usedDate = usedDate;
    }

}
