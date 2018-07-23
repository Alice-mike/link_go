package com.link.cloud.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 30541 on 2018/3/7.
 */

public class UserInfo extends ResultResponse{
    @SerializedName("uid")
    String uid;
    @SerializedName("sex")
    int sex;
    @SerializedName("phone")
    String phone;
    @SerializedName("name")
    String name;
    @SerializedName("img")
    String img;
    @SerializedName("userType")
    int userType;

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getSex() {
        return sex;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public int getUserType() {
        return userType;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
