package com.apartment.maintenance.entity;


import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "flats")
public class Flat {

    @Id
    @GeneratedValue
    @Column(name = "flat_id")
    private UUID flatId;

    @Column(name = "site_id", nullable = false)
    private UUID siteId;

    @Column(name = "flat_number")
    private String flatNumber;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "owner_phone")
    private String ownerPhone;

    @Column(name = "is_active")
    private Boolean isActive;

    // getters setters

    public UUID getFlatId() {
        return flatId;
    }

    public void setFlatId(UUID flatId) {
        this.flatId = flatId;
    }

    public UUID getSiteId() {
        return siteId;
    }

    public void setSiteId(UUID siteId) {
        this.siteId = siteId;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}