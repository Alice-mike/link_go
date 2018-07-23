package com.link.cloud.model.impl;

import android.widget.EditText;

import com.google.gson.JsonObject;
import com.link.cloud.bean.CabinetNumberData;
import com.link.cloud.bean.DeviceData;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.Lockdata;
import com.link.cloud.bean.MessagetoJson;
import com.link.cloud.bean.ResultResponse;
import com.orhanobut.logger.Logger;
import com.link.cloud.base.RestApi;
import com.link.cloud.base.BaseApi;
import com.link.cloud.bean.CodeInfo;
import com.link.cloud.bean.UpdateMessage;
import com.link.cloud.bean.UserResponse;
import com.link.cloud.bean.LessonResponse;
import com.link.cloud.bean.Member;
import com.link.cloud.bean.RestResponse;
import com.link.cloud.bean.ReturnBean;
import com.link.cloud.bean.SignedResponse;
import com.link.cloud.bean.Voucher;
import com.link.cloud.model.IHttpClientHelper;

import java.util.concurrent.ExecutionException;

import rx.Observable;

/**
 * Created by Shaozy on 2016/8/10.
 */
public class HttpClientHelper implements IHttpClientHelper {
    public Boolean flag=true;
    private static final HttpClientHelper ourInstance = new HttpClientHelper();


    public static HttpClientHelper getInstance() {
        return ourInstance;
    }


    public HttpClientHelper() {
    }


    @Override
    public Observable<UpdateMessage> deviceUpgrade(String deviceID) {
        JsonObject params=new JsonObject();
        try {
            params.addProperty("deviceID",deviceID);
        }catch (Exception e){
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().deviceUpgrade(params);
    }

    @Override
    public Observable<DeviceData> getdeviceID(String deviceTargetValue,int deviceType) {
        JsonObject pareams=new JsonObject();
        try {
            pareams.addProperty("deviceType",deviceType);
            pareams.addProperty("deviceTargetValue",deviceTargetValue);
        }catch (Exception e){
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().getdeviceId(pareams);
    }

    @Override
    public Observable<Lockdata> isOpenCabinet(String deviceId,String uid, String fromType) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("deviceId", deviceId);
            params.addProperty("uid", uid);
            params.addProperty("fromType",fromType);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().isOpenCabinet(params);
    }
    @Override
    public Observable<ResultResponse> clearCabinet(String deviceId, String cabinetNumber) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("deviceId", deviceId);
            params.addProperty("cabinetNumber", cabinetNumber);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().clearCabinet(params);
    }
    @Override
    public Observable<ResultResponse> adminiOpenCabinet(String deviceId, String cabinetNumber) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("deviceId", deviceId);
            params.addProperty("cabinetNumber", cabinetNumber);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().adminiOpenCabinet(params);
    }
    @Override
    public Observable<DownLoadData> downloadFeature(String messageId, String appid, String shopId, String deviceId, String uid) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("messageId", messageId);
            params.addProperty("appid", appid);
            params.addProperty("shopId", shopId);
            params.addProperty("deviceId", deviceId);
            params.addProperty("uid", uid);

        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().downloadFeature(params);
    }
    @Override
    public Observable<DownLoadData> syncUserFeature(String deviceId) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("deviceId", deviceId);

        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().syncUserFeature(params);
    }

    @Override
    public Observable<CabinetNumberData> cabinetNumberList(String deviceId) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("deviceId", deviceId);

        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().cabinetNumberList(params);
    }
}
