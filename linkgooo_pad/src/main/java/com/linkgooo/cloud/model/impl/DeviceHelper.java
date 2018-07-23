package com.soonvein.cloud.model.impl;

import android.support.v4.util.Pair;

import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.base.DeviceApi;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.model.IDeviceHelper;
import com.wedone.sdk.UserData;

import rx.Observable;

/**
 * Created by Shaozy on 2016/8/10.
 */
public class DeviceHelper implements IDeviceHelper {

    private static final DeviceHelper ourInstance = new DeviceHelper();


    public static DeviceHelper getInstance() {
        return ourInstance;
    }


    private DeviceHelper() {
    }

    @Override
    public Observable<Long> initSDK() {
        return DeviceApi.getInstance().initSDK();
    }

    @Override
    public Boolean getFingerStatus(byte bFingerStatus, int nTimes, int nInterval) {
        return DeviceApi.getInstance().getFingerStatus(bFingerStatus, nTimes, nInterval);
    }

    @Override
    public Pair<byte[], short[]> registerTemplate() {
        return DeviceApi.getInstance().registerTemplate();
    }

    @Override
    public Pair<ApiException, UserData> registerTemplate(UserData regUserData, Member member) {
        return DeviceApi.getInstance().registerTemplate(regUserData, member);
    }

    @Override
    public Boolean verifyTemplate(UserData regUserData, UserData matchUserData, short wSecurityLevel_1Vn) {
        return DeviceApi.getInstance().verifyTemplate(regUserData, matchUserData, wSecurityLevel_1Vn);
    }

    @Override
    public Pair<ApiException, UserData> verifyTemplate(UserData regUserData) {
        return DeviceApi.getInstance().verifyTemplate(regUserData);
    }

    @Override
    public long saveTemplate(UserData regUserData) {
        return DeviceApi.getInstance().saveTemplate(regUserData);
    }

    @Override
    public Pair<ApiException, String> getVerifyTemplate() {
        return DeviceApi.getInstance().getVerifyTemplate();
    }
}
