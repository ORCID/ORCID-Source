package org.orcid.statistics.jpa.entities;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "statistic_values")
public class StatisticValuesEntity implements Serializable {

    private static final long serialVersionUID = -3187757614988914339L;
    
    private Long id;
    private Long statisticKeyId;
    private String statisticName;
    private long statisticValue;
    
    public StatisticValuesEntity(){
        
    }
    
    public StatisticValuesEntity(Long statisticKeyId, String statisticName, long statisticValue){
        this.statisticKeyId = statisticKeyId;
        this.statisticName = statisticName;
        this.statisticValue = statisticValue;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "values_seq")
    @SequenceGenerator(name = "values_seq", sequenceName = "values_seq", allocationSize = 1)
    @Column(name = "id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id){
        this.id = id;
    }
        
    @Column(name = "key_id", nullable = false)
    public Long getStatisticKeyId(){
        return statisticKeyId;
    }
    
    @Column(name = "statistic_name")
    public String getStatisticName(){
        return statisticName;
    }
    
    @Column(name="statistic_value")
    public long getStatisticValue(){
        return statisticValue;
    }
    
    public void setStatisticKeyId(Long statisticKeyId){
        this.statisticKeyId = statisticKeyId;
    }
    
    public void setStatisticName(String name){
        this.statisticName = name;
    }

    public void setStatisticValue(long value){
        this.statisticValue = value;
    }
    
    public boolean equals(Object otherObject){
        if(otherObject == null)
            return false;
        
        if (!(otherObject instanceof StatisticValuesEntity)){
            return false;
        }
        
        StatisticValuesEntity otherEntity = (StatisticValuesEntity)otherObject;               
        
        if(this.id == null){
            if(otherEntity.getId() != null)
                return false;
        }
        
        if(!this.id.equals(otherEntity.getId()))
            return false;
        
        if(this.statisticKeyId == null){
            if(otherEntity.getStatisticKeyId() != null)
                return false;
        }
        
        return this.statisticKeyId.equals(otherEntity.getStatisticKeyId());       
    }
}
