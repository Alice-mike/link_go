package com.soonvein.cloud.model;

import android.support.v4.util.Pair;

import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.Member;
import com.wedone.sdk.UserData;

import rx.Observable;

/**
 * Created by Shaozy on 2016/8/10.
 */
public interface IDeviceHelper {
    //初始化SDK
    Observable<Long> initSDK();
    //判断手指状态
    Boolean getFingerStatus(byte bFingerStatus, int nTimes, int nInterval);
    //注册模板
    Pair<byte[], short[]> registerTemplate();
    Pair<ApiException, UserData> registerTemplate(UserData regUserData, Member member);
    //验证注册模板
    Pair<ApiException, UserData> verifyTemplate(UserData regUserData);
    Boolean verifyTemplate(UserData regUserData, UserData matchUserData, short wSecurityLevel_1Vn);
    //保存注册模板
    long saveTemplate(UserData regUserData);
    //获取验证模板
    Pair<ApiException, String> getVerifyTemplate();
}
