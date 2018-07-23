package com.link.cloud.greendaodemo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by 30541 on 2018/4/25.
 */
@Entity
public class CabinetRecord {
    @Id(autoincrement = true)
    private Long id;
    String memberName;
    String phoneNum;
    String cabinetStating;
    String isUsed;
    String exist;
    String opentime;
    String cabinetNumber;
    @Generated(hash = 1756661690)
    public CabinetRecord(Long id, String memberName, String phoneNum,
            String cabinetStating, String isUsed, String exist, String opentime,
            String cabinetNumber) {
        this.id = id;
        this.memberName = memberName;
        this.phoneNum = phoneNum;
        this.cabinetStating = cabinetStating;
        this.isUsed = isUsed;
        this.exist = exist;
        this.opentime = opentime;
        this.cabinetNumber = cabinetNumber;
    }
    @Generated(hash = 647802881)
    public CabinetRecord() {
    }
    public void setCabinetNumber(String cabinetNumber) {
        this.cabinetNumber = cabinetNumber;
    }

    public void setCabinetStating(String cabinetStating) {
        this.cabinetStating = cabinetStating;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCabinetNumber() {
        return cabinetNumber;
    }

    public String getCabinetStating() {
        return cabinetStating;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getOpentime() {
        return opentime;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getExist() {
        return this.exist;
    }
    public void setExist(String exist) {
        this.exist = exist;
    }
    public String getIsUsed() {
        return this.isUsed;
    }
    public void setIsUsed(String isUsed) {
        this.isUsed = isUsed;
    }
}
