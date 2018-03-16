package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orcid.jaxb.model.notification.permission_v2.ItemType;

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
    private ItemType itemType;
    private String itemName;
    private String externalIdType;
    private String externalIdValue;
    
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

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type")
    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
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
}
