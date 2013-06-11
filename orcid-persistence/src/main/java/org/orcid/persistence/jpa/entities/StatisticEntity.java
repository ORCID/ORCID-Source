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
@Table(name = "statistic")
public class StatisticEntity implements Serializable {

    //TODO
    private static final long serialVersionUID = -3187757614938904329L;
    
    private StatisticHistoryEntity history;
    private String name;
    private float resultingValue;
    
    @Id
    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "id", nullable = false)
    public StatisticHistoryEntity getHistory(){
        return history;
    }
    
    @Column(name = "name")
    public String getName(){
        return name;
    }
    
    @Column(name="resulting_value")
    public float getResultingValue(){
        return resultingValue;
    }
    
    public void setHistory(StatisticHistoryEntity history){
        this.history = history;
    }
    
    public void setName(String name){
        this.name = name;
    }

    public void setResultingValue(float value){
        this.resultingValue = value;
    }
}
