package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "statistic_values")
public class StatisticValuesEntity implements Serializable {

    //TODO
    private static final long serialVersionUID = -3187757614938904329L;
    
    private StatisticKeyEntity key;
    private String statisticName;
    private float statisticValue;
    
    public StatisticValuesEntity(){
        
    }
    
    public StatisticValuesEntity(StatisticKeyEntity key, String statisticName, float statisticValue){
        this.key = key;
        this.statisticName = statisticName;
        this.statisticValue = statisticValue;
    }
    
    @Id
    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "key_id", nullable = false)
    public StatisticKeyEntity getKey(){
        return key;
    }
    
    @Column(name = "statistic_name")
    public String getStatisticName(){
        return statisticName;
    }
    
    @Column(name="statistic_value")
    public float getStatisticValue(){
        return statisticValue;
    }
    
    public void setKey(StatisticKeyEntity key){
        this.key = key;
    }
    
    public void setStatisticName(String name){
        this.statisticName = name;
    }

    public void setStatisticValue(float value){
        this.statisticValue = value;
    }
}
