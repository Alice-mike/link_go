package com.link.cloud.greendaodemo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 30541 on 2018/5/5.
 */
@Entity
public class UserCabinetCount {
    @Id(autoincrement = true)
    Long id;
    String CabinetCount;
    String CabinetUsed;
    String Cabinetsurpul;
    @Generated(hash = 1363594965)
    public UserCabinetCount(Long id, String CabinetCount, String CabinetUsed,
            String Cabinetsurpul) {
        this.id = id;
        this.CabinetCount = CabinetCount;
        this.CabinetUsed = CabinetUsed;
        this.Cabinetsurpul = Cabinetsurpul;
    }
    @Generated(hash = 198209238)
    public UserCabinetCount() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCabinetCount() {
        return this.CabinetCount;
    }
    public void setCabinetCount(String CabinetCount) {
        this.CabinetCount = CabinetCount;
    }
    public String getCabinetUsed() {
        return this.CabinetUsed;
    }
    public void setCabinetUsed(String CabinetUsed) {
        this.CabinetUsed = CabinetUsed;
    }
    public String getCabinetsurpul() {
        return this.Cabinetsurpul;
    }
    public void setCabinetsurpul(String Cabinetsurpul) {
        this.Cabinetsurpul = Cabinetsurpul;
    }
}
