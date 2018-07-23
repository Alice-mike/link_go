package com.soonvein.cloud.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Shaozy on 2016/8/10.
 */
public class Member extends ResultResponse {

    @SerializedName("memID")
    public String memID;

    @SerializedName("name")
    public String name;

    @SerializedName("phone")
    public String phone;

    @SerializedName("sex")
    public String sex;

    @SerializedName("isSend")
    public int isSend;
    @SerializedName("cardTypeName")
    public String cardTypeName;
    @SerializedName("beginTime")
    public String beginTime;
    @SerializedName("endTime")
    public String endTime;
    @SerializedName("cardInfo")
    public CardInfo cardInfo;

    @SerializedName("veinFingerID1")
    public String veinFingerID1;

    @SerializedName("veinFingerID2")
    public String veinFingerID2;

    @SerializedName("veinFingerID3")
    public String veinFingerID3;

    public ArrayList<CardInfo> cardInfos;

    public String getMemID() {
        return memID;
    }

    public void setMemID(String memID) {
        this.memID = memID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getIsSend() {
        return isSend;
    }

    public boolean needVerify(){
        if(getIsSend() == 1)
            return true;
        else
            return false;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public ArrayList<CardInfo> getCardInfos() {
        return cardInfos;
    }

    public void setCardInfos(ArrayList<CardInfo> cardInfos) {
        this.cardInfos = cardInfos;
    }

    public String getVeinFingerID1() {
        return veinFingerID1;
    }

    public void setVeinFingerID1(String veinFingerID1) {
        this.veinFingerID1 = veinFingerID1;
    }

    public String getVeinFingerID2() {
        return veinFingerID2;
    }

    public void setVeinFingerID2(String veinFingerID2) {
        this.veinFingerID2 = veinFingerID2;
    }

    public String getVeinFingerID3() {
        return veinFingerID3;
    }

    public void setVeinFingerID3(String veinFingerID3) {
        this.veinFingerID3 = veinFingerID3;
    }

    @Override
    public String toString() {
        return "Member{" +
                "memID='" + memID + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", sex='" + sex + '\'' +
                ", isSend=" + isSend +
                ",cardTypeName"+cardTypeName+'\''+
                ",beginTime"+beginTime+'\''+
                ",endTime"+endTime+'\''+
                ", cardInfos=" + cardInfos +
                ", veinFingerID1='" + veinFingerID1 + '\'' +
                ", veinFingerID2='" + veinFingerID2 + '\'' +
                ", veinFingerID3='" + veinFingerID3 + '\'' +
                '}';
    }
}
