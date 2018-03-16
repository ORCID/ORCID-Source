package org.orcid.statistics.jpa.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "statistic_key")
public class StatisticKeyEntity implements Serializable {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
    private static final long serialVersionUID = -3187757614938904329L;
    private Long id;
    private Date generationDate;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "key_seq")
    @SequenceGenerator(name = "key_seq", sequenceName = "key_seq")
    @Column(name = "id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id){
        this.id = id;
    }
    
    @Column(name = "generation_date")
    public Date getGenerationDate(){
        return generationDate;
    }
    
    public void setGenerationDate(Date date){
        generationDate = date;
    }
    
    @Override
    public boolean equals(Object otherObject){
        if(otherObject == null)
            return false;
        
        if (!(otherObject instanceof StatisticKeyEntity)){
            return false;
        }
        
        StatisticKeyEntity otherKey = (StatisticKeyEntity)otherObject;                
        
        if(this.id == null){
            if(otherKey.getId() != null)
                return false;
        }
        
        if(!this.id.equals(otherKey.getId()))
            return false;
        
        return true;
    }
}
