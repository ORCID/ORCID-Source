package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

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

    private Integer issnLoaderFailCount;

    private String failReason;

    private Date syncDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "group_id_record_seq")
    @SequenceGenerator(name = "group_id_record_seq", sequenceName = "group_id_record_seq", allocationSize = 1)
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

    @Column(name = "issn_loader_fail_count")
    public Integer getIssnLoaderFailCount() {
        return issnLoaderFailCount;
    }

    public void setIssnLoaderFailCount(Integer issnLoaderFailCount) {
        this.issnLoaderFailCount = issnLoaderFailCount;
    }

    @Column(name = "fail_reason")
    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    @Column(name = "sync_date")
    public Date getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(Date syncDate) {
        this.syncDate = syncDate;
    }

    @Override
    public int compareTo(GroupIdRecordEntity o) {
        return 0;
    }
}