package com.link.cloud.greendaodemo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by 30541 on 2018/3/29.
 */
@Entity
public class CabinetNumber {
    @Id(autoincrement = true)
    private Long id;
    String cabinetLockPlate;
    String circuitNumber;
    String cabinetNumber;
    String isUser;
    @Generated(hash = 2039517505)
    public CabinetNumber(Long id, String cabinetLockPlate, String circuitNumber,
            String cabinetNumber, String isUser) {
        this.id = id;
        this.cabinetLockPlate = cabinetLockPlate;
        this.circuitNumber = circuitNumber;
        this.cabinetNumber = cabinetNumber;
        this.isUser = isUser;
    }
    @Generated(hash = 1091174649)
    public CabinetNumber() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCabinetLockPlate() {
        return this.cabinetLockPlate;
    }
    public void setCabinetLockPlate(String cabinetLockPlate) {
        this.cabinetLockPlate = cabinetLockPlate;
    }
    public String getCircuitNumber() {
        return this.circuitNumber;
    }
    public void setCircuitNumber(String circuitNumber) {
        this.circuitNumber = circuitNumber;
    }
    public String getCabinetNumber() {
        return this.cabinetNumber;
    }
    public void setCabinetNumber(String cabinetNumber) {
        this.cabinetNumber = cabinetNumber;
    }
    public String getIsUser() {
        return this.isUser;
    }
    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }
}
