package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the group_id_record database table.
 * 
 */
@Entity
@Table(name = "group_id_record")
public class GroupIdRecordEntity extends SourceAwareEntity<Long> implements Comparable<GroupIdRecordEntity> {

    private static final long serialVersionUID = 3102454956983620497L;

    private Long id;

    private String groupName;

    private String groupId;

    private String groupDescription;

    private String groupType;    

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "group_id_record_seq")
    @SequenceGenerator(name = "group_id_record_seq", sequenceName = "group_id_record_seq")
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "group_description")
    public String getGroupDescription() {
        return this.groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    @Column(name = "group_id")
    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Column(name = "group_name")
    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Column(name = "group_type")
    public String getGroupType() {
        return this.groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    @Override
    public int compareTo(GroupIdRecordEntity o) {
        return 0;
    }
}