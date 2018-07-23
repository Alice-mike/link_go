package com.link.cloud.greendaodemo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by 30541 on 2018/2/26.
 */
@Entity
public class Person {
    //设置Id,为Long类型,并将其设置为自增
    @Id(autoincrement = true)
    private Long id;
    private int userType;
    private String uid;
    private String name;
    private String number;
    private int sex;
    private String pos;
    private String img;
    private String cardname;
    private String cardnumber;
    private String begintime;
    private String endtime;
    private String fingermodel;


    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getPos() {
        return pos;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }

    public void setCardname(String cardname) {
        this.cardname = cardname;
    }

    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public Long getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public int getUserType() {
        return userType;
    }

    public String getImg() {
        return img;
    }

    public int getSex() {
        return sex;
    }

    public String getCardname() {
        return cardname;
    }

    public String getCardnumber() {
        return cardnumber;
    }

    public String getBegintime() {
        return begintime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setFingermodel(String fingermodel) {
        this.fingermodel = fingermodel;
    }

    public String getFingermodel() {
        return fingermodel;
    }
    
    public Person() {
    }

    @Generated(hash = 1508411966)
    public Person(Long id, int userType, String uid, String name, String number,
            int sex, String pos, String img, String cardname, String cardnumber,
            String begintime, String endtime, String fingermodel) {
        this.id = id;
        this.userType = userType;
        this.uid = uid;
        this.name = name;
        this.number = number;
        this.sex = sex;
        this.pos = pos;
        this.img = img;
        this.cardname = cardname;
        this.cardnumber = cardnumber;
        this.begintime = begintime;
        this.endtime = endtime;
        this.fingermodel = fingermodel;
    }
    @Override
    public String toString() {
        return "Person{"+ "person_id"+id+'\''+
                ",userid"+uid+'\''+
                ",name"+name+'\''+
                ",phone"+number+'\''+
                ",sex"+sex+'\''+
                ",img"+img+'\''+"}";
    }
}
