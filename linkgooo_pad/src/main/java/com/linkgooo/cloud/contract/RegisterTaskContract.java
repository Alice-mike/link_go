package com.soonvein.cloud.contract;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;
import com.soonvein.cloud.R;
import com.soonvein.cloud.base.AbsAPICallback;
import com.soonvein.cloud.bean.Data;
import com.soonvein.cloud.core.BasePresenter;
import com.soonvein.cloud.utils.ReservoirUtils;
import com.soonvein.cloud.BaseApplication;

import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.bean.RestResponse;
import com.soonvein.cloud.core.MvpView;

import java.util.HashMap;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Shaozy on 2016/8/11.
 */
public class RegisterTaskContract extends BasePresenter<RegisterTaskContract.RegisterView> {

    public RegisterTaskContract registerTaskContract;
    public interface RegisterView extends MvpView {

        void sendSMSSuccess(String phoneNum, String code);
        void onSuccess(Member memberInfo);
    }

    public ReservoirUtils reservoirUtils;

    public RegisterTaskContract() {
        this.reservoirUtils = new ReservoirUtils();
    }


    public void sendSMS(String accountSid, String to, String code) {
        JsonArray obj = new JsonArray();
        obj.add(code);
        obj.add("5");
        JsonObject params = new JsonObject();
        try {
            params.addProperty("to", to);
            params.add("datas", obj);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

        this.mCompositeSubscription.add(this.mDataManager.sendSMS(accountSid, params)
                .subscribe(new AbsAPICallback<RestResponse>() {

                    @Override
                    protected void onError(ApiException e) {
                        RegisterTaskContract.this.getMvpView().onError(e);
                    }

                    @Override
                    protected void onPermissionError(ApiException e) {
                        onError(e);
                    }

                    @Override
                    protected void onResultError(ApiException e) {
                        onError(e);
                    }

                    @Override
                    public void onCompleted() {
                        if (RegisterTaskContract.this.mCompositeSubscription != null)
                            RegisterTaskContract.this.mCompositeSubscription.remove(this);
                    }

                    @Override
                    public void onNext(RestResponse rtn) {
                        Logger.e("短信发送成功: " + rtn);
                        //RegisterTaskContract.this.getMvpView().sendSMSSuccess(rtn[0], rtn[1]);
                    }
                }));
        /*
        this.mCompositeSubscription.add(this.mDataManager.sendSMS(accountSid, authToken, to, appId, templateId, obj)
                .subscribe(new AbsAPICallback<RestResponse>() {

                    @Override
                    protected void onError(ApiException e) {
                        RegisterTaskContract.this.getMvpView().onError(e);
                    }

                    @Override
                    protected void onPermissionError(ApiException e) {
                        onError(e);
                    }

                    @Override
                    protected void onResultError(ApiException e) {
                        onError(e);
                    }

                    @Override
                    public void onCompleted() {
                        if (RegisterTaskContract.this.mCompositeSubscription != null)
                            RegisterTaskContract.this.mCompositeSubscription.remove(this);
                    }

                    @Override
                    public void onNext(RestResponse rtn) {
                        Logger.e("短信发送成功: " + rtn);
                        //RegisterTaskContract.this.getMvpView().sendSMSSuccess(rtn[0], rtn[1]);
                    }
                }));*/
    }

    public void sendSMSByCcpSDK(String to, String code) {
        this.mCompositeSubscription.add(Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                //测试
                //subscriber.onNext(true);
                //subscriber.onCompleted();
                HashMap<String, Object> result = null;
                //初始化SDK
                CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
                restAPI.init("app.cloopen.com", "8883");
                Context context = BaseApplication.getInstance().getApplicationContext();
                String accountSid = context.getString(R.string.ACCOUNT_SID);
                String authToken = context.getString(R.string.AUTH_TOKEN);
                String appId = context.getString(R.string.APPID);
                String templateId = context.getString(R.string.TEMPLATEID);

                restAPI.setAccount(accountSid, authToken);
                restAPI.setAppId(appId);
                result = restAPI.sendTemplateSMS(to, templateId, new String[]{code, "5"});

                if ("000000".equals(result.get("statusCode"))) {
                    //正常返回输出data包体信息（map）
                    HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
                    Set<String> keySet = data.keySet();
                    for (String key : keySet) {
                        Object object = data.get(key);
                        Logger.e(key + " = " + object);
                    }
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } else {
                    //异常返回输出错误码和错误信息
                    String statusCode = "", statusMsg = "";
                    if (result.get("statusCode") != null)
                        statusCode = result.get("statusCode").toString();

                    if (result.get("statusMsg") != null)
                        statusMsg = result.get("statusMsg").toString();
                    String msg = "错误码=" + statusCode + " 错误信息= " + statusMsg;
                    ApiException e = new ApiException(new Throwable(msg), ApiException.PARSE_ERROR);
                    e.setDisplayMessage(statusMsg);
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {

                    @Override
                    public void onError(Throwable e) {
                        ApiException ex = null;
                        if (e instanceof ApiException) {
                            ex = (ApiException) e;
                        } else {
                            ex = new ApiException(e, ApiException.UNKNOWN);
                            ex.setDisplayMessage("未知错误");
                        }
                        RegisterTaskContract.this.getMvpView().onError(ex);
                    }

                    @Override
                    public void onCompleted() {
                        if (RegisterTaskContract.this.mCompositeSubscription != null)
                            RegisterTaskContract.this.mCompositeSubscription.remove(this);
                    }

                    @Override
                    public void onNext(Boolean rtn) {
                        RegisterTaskContract.this.getMvpView().sendSMSSuccess(to, code);
                    }
                }));
    }
    public void getMemInfo(String phone, String deviceID) {
        this.mCompositeSubscription.add(this.mDataManager.getMemInfo(phone, deviceID)
                .subscribe(new AbsAPICallback<Member>() {
                    @Override
                    public void onCompleted() {
                        if (RegisterTaskContract.this.mCompositeSubscription != null) {
                            RegisterTaskContract.this.mCompositeSubscription.remove(this);
                        }
                    }
                    @Override
                    protected void onError(ApiException e) {
                        RegisterTaskContract.this.getMvpView().onError(e);
                    }
                    @Override
                    protected void onPermissionError(ApiException e) {
                        RegisterTaskContract.this.getMvpView().onPermissionError(e);
                    }

                    @Override
                    protected void onResultError(ApiException e) {
                        RegisterTaskContract.this.getMvpView().onResultError(e);
                    }

                    @Override
                    public void onNext(Member member) {
                        //准备指静脉设备
                        /*Member member = new Member();
                        member.setMemID("123");
                        member.setName("老王");
                        member.setPhone("13007436471");
                        member.setSex("男");
                        CardInfo cardInfo = new CardInfo();
                        cardInfo.setName("白金卡");
                        cardInfo.setCardID("123");
                        cardInfo.setCardBalance("99.99元");
                        cardInfo.setEndTime("至2016年12月20日");
                        //member.setCardInfo(cardInfo);*/
                        RegisterTaskContract.this.getMvpView().onSuccess(member);
                    }
                }));
    }
}
