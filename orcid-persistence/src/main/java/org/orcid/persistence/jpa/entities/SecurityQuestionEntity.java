package org.orcid.persistence.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Will Simpson
 * 
 */
@Entity
@Table(name = "security_question")
public class SecurityQuestionEntity extends BaseEntity<Integer> {

    private static final long serialVersionUID = 1L;

    private int id;

    private String question;
    
    private String key;

    @Override
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}  
}
