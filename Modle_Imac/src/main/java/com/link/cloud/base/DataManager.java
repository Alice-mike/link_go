package com.link.cloud.base;

import com.google.gson.JsonObject;
import com.link.cloud.bean.CardInfo;
import com.link.cloud.bean.CodeInfo;
import com.link.cloud.bean.DeviceData;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.LessonResponse;
import com.link.cloud.bean.Member;
import com.link.cloud.bean.MessagetoJson;
import com.link.cloud.bean.RestResponse;
import com.link.cloud.bean.RetrunLessons;
import com.link.cloud.bean.ReturnBean;
import com.link.cloud.bean.SignUserdata;
import com.link.cloud.bean.SignedResponse;
import com.link.cloud.bean.UpdateMessage;
import com.link.cloud.bean.UserResponse;
import com.link.cloud.bean.Voucher;
import com.link.cloud.model.impl.DeviceHelper;
import com.link.cloud.model.impl.HttpClientHelper;
import com.link.cloud.utils.ReservoirUtils;
import com.link.cloud.utils.RxUtils;


import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Shaozy on 2016/8/10.
 */
public class DataManager {
    private static DataManager dataManager;
    private HttpClientHelper httpClientHelper;
    private DeviceHelper deviceHelper;
    public ReservoirUtils reservoirUtils;
    public synchronized static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }
    private DataManager() {
        this.httpClientHelper = HttpClientHelper.getInstance();
        this.deviceHelper = DeviceHelper.getInstance();
        this.reservoirUtils = new ReservoirUtils();
    }

    public Observable<Member> bindVeinMemeber(String deviceId,int userType,int numberType,String numberValue, String feature) {
        return this.httpClientHelper.bindVeinMemeber(deviceId, userType, numberType, numberValue, feature)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<Member> getMemInfo(String deviceID,int numberType,String numberValue) {
        return this.httpClientHelper.getMemInfo(deviceID,numberType,numberValue)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<ArrayList<CardInfo>> getCardInfo(String memID) {
        return this.httpClientHelper.getCardInfo(memID)
                .map(returnBean -> returnBean.cardInfo)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<SignUserdata> signedMember(String deviceId, String uid, String fromType) {
        return this.httpClientHelper.signedMember(deviceId, uid, fromType)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<RetrunLessons>eliminateLesson(String deviceID,int type,String memberid, String coachid, String clerkid){
        return this.httpClientHelper.eliminateLesson(deviceID,type,memberid,coachid,clerkid).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<RetrunLessons>selectLesson(String deviceID, int type, String lessonId, String memberid, String coachid, String clerkid){
        return this.httpClientHelper.selectLesson(deviceID,type,lessonId,memberid,coachid,clerkid).compose(RxUtils.applyIOToMainThreadSchedulers());
    }

    public Observable<UpdateMessage>deviceUpgrade(String deviceID){
        return this.httpClientHelper.deviceUpgrade(deviceID).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<DeviceData>getdeviceID(String deviceTargetValue,int devicetype){
        return this.httpClientHelper.getdeviceID(deviceTargetValue,devicetype).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<DownLoadData>syncUserFeature(String deviceId){
        return this.httpClientHelper.syncUserFeature(deviceId).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<DownLoadData>downloadFeature(String messageId, String appid, String shopId, String deviceId, String uid){
        return this.httpClientHelper.downloadFeature(messageId,appid,shopId,deviceId,uid).compose(RxUtils.applyIOToMainThreadSchedulers());
    }

}
