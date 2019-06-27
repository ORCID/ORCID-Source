package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author Will Simpson
 *
 */
@Entity
@Table(name = "notification_item")
public class NotificationItemEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String itemType;
    private String itemName;
    private String externalIdType;
    private String externalIdValue;
    private String actionType;
    private String additionalInfo;
    
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "notification_item_seq")
    @SequenceGenerator(name = "notification_item_seq", sequenceName = "notification_item_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "item_type")
    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    @Column(name = "item_name")
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Column(name = "external_id_type")
    public String getExternalIdType() {
        return externalIdType;
    }

    public void setExternalIdType(String externalIdType) {
        this.externalIdType = externalIdType;
    }

    @Column(name = "external_id_value")
    public String getExternalIdValue() {
        return externalIdValue;
    }

    public void setExternalIdValue(String externalIdValue) {
        this.externalIdValue = externalIdValue;
    }

    @Column(name = "action_type")
    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    @Column(name = "additional_info")
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }           
}
