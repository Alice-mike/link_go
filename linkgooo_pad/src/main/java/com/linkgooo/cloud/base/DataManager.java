package com.soonvein.cloud.base;

import android.support.v4.util.Pair;

import com.google.gson.JsonObject;
import com.soonvein.cloud.bean.CardInfo;
import com.soonvein.cloud.bean.CodeInfo;
import com.soonvein.cloud.bean.LessonResponse;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.bean.RestResponse;
import com.soonvein.cloud.bean.ResultResponse;
import com.soonvein.cloud.bean.ReturnBean;
import com.soonvein.cloud.bean.SignedResponse;
import com.soonvein.cloud.bean.UpdateMessage;
import com.soonvein.cloud.bean.UserResponse;
import com.soonvein.cloud.bean.Voucher;
import com.soonvein.cloud.model.impl.DeviceHelper;
import com.soonvein.cloud.model.impl.HttpClientHelper;
import com.soonvein.cloud.utils.ReservoirUtils;
import com.soonvein.cloud.utils.RxUtils;
import com.wedone.sdk.UserData;

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

    public Observable<RestResponse> sendSMS(String appId, JsonObject params) {
        return this.httpClientHelper.sendSMS(appId, params)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }

    public Observable<ReturnBean> bindVeinMemeber(String phone, String deviceID, String veinFingerID1, String veinFingerID2, String veinFingerID3) {
        return this.httpClientHelper.bindVeinMemeber(phone, deviceID, veinFingerID1, veinFingerID2, veinFingerID3)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }

    public Observable<Member> getMemberInfoByVein(String phone, String deviceID, String veinFingerID) {
        return this.httpClientHelper.getMemberInfoByVein(phone, deviceID, veinFingerID)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }

    public Observable<Member> getMemInfo(String phone, String deviceID) {
        return this.httpClientHelper.getMemInfo(phone, deviceID)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }

    public Observable<Member> getMemInfo(String memID) {
        return this.httpClientHelper.getMemInfo(memID)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }

    public Observable<ArrayList<CardInfo>> getCardInfo(String memID) {
        return this.httpClientHelper.getCardInfo(memID)
                .map(returnBean -> returnBean.cardInfo)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }

    public Observable<ReturnBean> getLastSignedTime(String memID) {
        return this.httpClientHelper.getLastSignedTime(memID)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }

    public Observable<ReturnBean> signedMember(String memID, String cardID) {
        return this.httpClientHelper.signedMember(memID, cardID)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }

    public Observable<SignedResponse> signedMember(String phone, String deviceID, String veinFingerID) {
        return this.httpClientHelper.signedMember(phone, deviceID, veinFingerID)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }

    public Observable<Voucher> consumeRecord(String phoneNum, String deviceID, String veinFingerID, String price, String method, String mark) {
        return this.httpClientHelper.consumeRecord(phoneNum, deviceID, veinFingerID, price, method, mark)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<UserResponse> verifyUserEliminateLesson(String deviceID, int userType, String veinFingerID){
        return  this.httpClientHelper.verifyUserEliminateLesson(deviceID,userType,veinFingerID).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<LessonResponse>eliminateLesson(String deviceID,String type,String memberid, String coachid, String clerkid){
        return this.httpClientHelper.eliminateLesson(deviceID,type,memberid,coachid,clerkid).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<LessonResponse>selectLesson(String deviceID, String type, String lessonId, String memberid, String coachid, String clerkid){
        return this.httpClientHelper.selectLesson(deviceID,type,lessonId,memberid,coachid,clerkid).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<CodeInfo>signedCodeInfo(String deviceID){
        return this.httpClientHelper.signedCodeInfo(deviceID).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<UpdateMessage>deviceUpgrade(String deviceID){
        return this.httpClientHelper.deviceUpgrade(deviceID).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<Long> initSDK() {
        return this.deviceHelper.initSDK();
    }

    public Boolean getFingerStatus(byte bFingerStatus, int nTimes, int nInterval) {
        return this.deviceHelper.getFingerStatus(bFingerStatus, nTimes, nInterval);
    }

    public Pair<byte[], short[]> registerTemplate() {
        return this.deviceHelper.registerTemplate();
    }

    public Pair<ApiException, UserData> registerTemplate(UserData regUserData, Member member) {
        return this.deviceHelper.registerTemplate(regUserData, member);
    }

    public Boolean verifyTemplate(UserData regUserData, UserData matchUserData, short wSecurityLevel_1Vn) {
        return this.deviceHelper.verifyTemplate(regUserData, matchUserData, wSecurityLevel_1Vn);
    }

    public Pair<ApiException, UserData> verifyTemplate(UserData regUserData) {
        return this.deviceHelper.verifyTemplate(regUserData);
    }

    public long saveTemplate(UserData regUserData) {
        return this.deviceHelper.saveTemplate(regUserData);
    }

    public Pair<ApiException, String> getVerifyTemplate() {
        return this.deviceHelper.getVerifyTemplate();
    }
}
