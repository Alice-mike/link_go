package com.soonvein.cloud.model.impl;

import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;
import com.soonvein.cloud.base.RestApi;
import com.soonvein.cloud.base.BaseApi;
import com.soonvein.cloud.bean.CodeInfo;
import com.soonvein.cloud.bean.ResultResponse;
import com.soonvein.cloud.bean.UpdateMessage;
import com.soonvein.cloud.bean.UserResponse;
import com.soonvein.cloud.bean.LessonResponse;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.bean.RestResponse;
import com.soonvein.cloud.bean.ReturnBean;
import com.soonvein.cloud.bean.SignedResponse;
import com.soonvein.cloud.bean.Voucher;
import com.soonvein.cloud.model.IHttpClientHelper;

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
    public Observable<RestResponse> sendSMS(String accountSid, JsonObject params) {
        return RestApi.getInstance().getBaseService().sendTemplateSMS(accountSid, params);
    }

    @Override
    public Observable<ReturnBean> bindVeinMemeber(String phone, String deviceID, String veinFingerID1, String veinFingerID2, String veinFingerID3) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("phone", phone);
            params.addProperty("deviceID", deviceID);
            params.addProperty("veinFingerID1", veinFingerID1);
            params.addProperty("veinFingerID2", veinFingerID2);
            params.addProperty("veinFingerID3", veinFingerID3);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().bindVeinMemeber(params);
    }

    @Override
    public Observable<Member> getMemberInfoByVein(String phone, String deviceID, String veinFingerID) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("phone", phone);
            params.addProperty("deviceID", deviceID);
            params.addProperty("veinFingerID", veinFingerID);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().getMemberInfoByVein(params);
    }

//    public Boolean getFlag() {
//        return flag;
//    }
//
//    public void setFlag(Boolean flag) {
//        this.flag = flag;
//    }

    @Override
    public Observable<Member> getMemInfo(String phone, String deviceID) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("phone", phone);
            params.addProperty("deviceID", deviceID);

        } catch (Exception e) {
                Logger.e("HttpClientHelper"+e.getMessage());
        }

        return BaseApi.getInstance().getBaseService().getMemInfo(params);
    }

    @Override
    public Observable<Member> getMemInfo(String memID) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("memID", memID);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().getMemInfoByMemID(params);
    }

    @Override
    public Observable<ReturnBean> getCardInfo(String memID) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("memID", memID);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().getCardInfo(params);
    }

    @Override
    public Observable<ReturnBean> getLastSignedTime(String memID) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("memID", memID);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().getLastSignedTime(params);
    }

    @Override
    public Observable<ReturnBean> signedMember(String memID, String cardID) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("memID", memID);
            params.addProperty("cardID", cardID);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().signedMember(params);
    }

    @Override
    public Observable<SignedResponse> signedMember(String phone, String deviceID, String veinFingerID) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("deviceID", deviceID);
            params.addProperty("veinFingerID", veinFingerID);
            params.addProperty("phone", phone);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().signed(params);
    }


    @Override
    public Observable<Voucher> consumeRecord(String phoneNum, String deviceID, String veinFingerID, String price, String method, String mark) {
        JsonObject params = new JsonObject();
        try {
            params.addProperty("phone", phoneNum);
            params.addProperty("deviceID", deviceID);
            params.addProperty("veinFingerID", veinFingerID);
            params.addProperty("price", price);
            params.addProperty("method", method);
            params.addProperty("mark", mark);
        } catch (Exception e) {
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().consumeRecord(params);
    }

    @Override
    public Observable<UserResponse> verifyUserEliminateLesson(String deviceID, int userType, String veinFingerID) {
            JsonObject params=new JsonObject();
        try {
            params.addProperty("deviceID",deviceID);
            params.addProperty("userType",userType);
            params.addProperty("veinFingerID",veinFingerID);
        }catch (Exception e){
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().verifyUserEliminateLesson(params);
    }

    @Override
    public Observable<LessonResponse> selectLesson(String deviceID, String type, String lessonId, String memberid, String coachid,String clerkid) {
        JsonObject params=new JsonObject();
        try {
            params.addProperty("deviceID",deviceID);
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
    public Observable<LessonResponse> eliminateLesson(String deviceID,String type, String memberid, String coachid, String clerkid) {
        JsonObject params=new JsonObject();
        try {
            params.addProperty("deviceID",deviceID);
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
    public Observable<LessonResponse> eliminateLesson(String deviceID, String memberid, String coachid) {
        JsonObject params=new JsonObject();
        try {
            params.addProperty("deviceID",deviceID);
            params.addProperty("memberid",memberid);
            params.addProperty("coachid",coachid);

        }catch (Exception e){
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().eliminateLesson(params);
    }
    @Override
    public Observable<CodeInfo> signedCodeInfo(String deviceID) {
        JsonObject params=new JsonObject();
        try {
            params.addProperty("deviceID",deviceID);
        }catch (Exception e){
            Logger.e("HttpClientHelper"+e.getMessage());
        }
        return BaseApi.getInstance().getBaseService().singnedCodeInfo(params);
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
}
