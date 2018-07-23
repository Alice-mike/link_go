package com.link.cloud.model.impl;
import com.google.gson.JsonObject;
import com.link.cloud.bean.DeviceData;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.RetrunLessons;
import com.link.cloud.bean.SignUserdata;
import com.orhanobut.logger.Logger;
import com.link.cloud.base.BaseApi;
import com.link.cloud.bean.UpdateMessage;
import com.link.cloud.bean.LessonResponse;
import com.link.cloud.bean.Member;
import com.link.cloud.bean.ReturnBean;
import com.link.cloud.model.IHttpClientHelper;
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
    public Observable<Member> bindVeinMemeber( String deviceId,int userType,int numberType,String numberValue, String feature) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("deviceId", deviceId);
            params.addProperty("userType",userType);
            params.addProperty("numberType",numberType);
            params.addProperty("numberValue",numberValue);
            params.addProperty("feature", feature);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().bindVeinMemeber(params);
    }
//    public Boolean getFlag() {
//        return flag;
//    }
//
//    public void setFlag(Boolean flag) {
//        this.flag = flag;
//    }
    @Override
    public Observable<Member> getMemInfo(String deviceID,int numberType,String numberValue) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("deviceId", deviceID);
            params.addProperty("numberType",numberType);
            params.addProperty("numberValue", numberValue);
        } catch (Exception e) {
                Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().getMemInfo(params);
    }
    @Override
    public Observable<ReturnBean> getCardInfo(String memID) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("memId", memID);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().getCardInfo(params);
    }
    @Override
    public Observable<SignUserdata> signedMember(String deviceId, String uid, String fromType) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("deviceId", deviceId);
            params.addProperty("uid", uid);
            params.addProperty("fromType", fromType);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().signMember(params);
    }
    @Override
    public Observable<RetrunLessons> selectLesson(String deviceID, int type, String lessonId, String memberid, String coachid, String clerkid) {
        JsonObject params=new JsonObject();
        try {
            params.addProperty("deviceId",deviceID);
            params.addProperty("type",type);
            params.addProperty("lessonId",lessonId);
            params.addProperty("memberid",memberid);
            params.addProperty("coachid",coachid);
            params.addProperty("clerkid",clerkid);
        }catch (Exception e){
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().selectLesson(params);
    }
    @Override
    public Observable<RetrunLessons> eliminateLesson(String deviceID,int type, String memberid, String coachid, String clerkid) {
        JsonObject params=new JsonObject();
        try {
            params.addProperty("deviceId",deviceID);
            params.addProperty("type",type);
            params.addProperty("memberid",memberid);
            params.addProperty("coachid",coachid);
            params.addProperty("clerkid",clerkid);
        }catch (Exception e){
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().eliminateLesson(params);
    }
    @Override
    public Observable<UpdateMessage> deviceUpgrade(String deviceID) {
        JsonObject params=new JsonObject();
        try {
            params.addProperty("deviceId",deviceID);
        }catch (Exception e){
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().deviceUpgrade(params);
    }
    @Override
    public Observable<DeviceData> getdeviceID(String deviceTargetValue,int devicetype) {
        JsonObject pareams=new JsonObject();
        try {
            pareams.addProperty("deviceTargetValue",deviceTargetValue);
            pareams.addProperty("deviceType",devicetype);
        }catch (Exception e){
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().getdeviceId(pareams);
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
}
