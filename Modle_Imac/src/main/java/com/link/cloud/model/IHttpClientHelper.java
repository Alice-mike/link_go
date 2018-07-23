package com.link.cloud.model;

import com.google.gson.JsonObject;
import com.link.cloud.bean.CodeInfo;
import com.link.cloud.bean.DeviceData;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.MessagetoJson;
import com.link.cloud.bean.RetrunLessons;
import com.link.cloud.bean.SignUserdata;
import com.link.cloud.bean.UpdateMessage;
import com.link.cloud.bean.UserResponse;
import com.link.cloud.bean.LessonResponse;
import com.link.cloud.bean.Member;
import com.link.cloud.bean.RestResponse;
import com.link.cloud.bean.ReturnBean;
import com.link.cloud.bean.SignedResponse;
import com.link.cloud.bean.Voucher;

import rx.Observable;

/**
 * Created by Shaozy on 2016/8/10.
 */
public interface IHttpClientHelper {


    /**
     * 静脉设备绑定会员接口
     *
     * @param phone         会员手机号码
     * @param deviceID      指静脉设备ID
     * @param veinFingerID1 指静脉ID
     * @param veinFingerID2 指静脉ID
     * @param veinFingerID3 指静脉ID
     * @return Observable<ReturnBean>
     */
    /**
     *
     * @param deviceId  设备id
     * @param userType  用户类型
     * @param numberType  号码类型
     * @param numberValue 号码
     * @param feature    指静脉数据
     * @return
     */
    Observable<Member> bindVeinMemeber(String deviceId,int userType,int numberType,String numberValue,String feature);


    /**
     * 根据设备ID和手机号查询会员信息
     * @param numberType 号码类型
     * @param numberValue 会员手机号码
     * @param deviceID 指静脉设备ID
     * @return Observable<Member>
     */
    Observable<Member> getMemInfo( String deviceID,int numberType,String numberValue);

    /**
     * 查询会员卡信息
     *
     * @param memID 会员ID
     * @return Observable<CardInfo>
     */
    Observable<ReturnBean> getCardInfo(String memID);

    /**
     * 会员签到
     *
     * @param deviceID  设备ID
     * @param veinFingerID 用户指静脉数据
     * @param phoneNum 用户手机号
     * @return Observable<ReturnBean>
     */
    Observable<SignUserdata> signedMember(String deviceID, String veinFingerID, String phoneNum);

    /**
     *
     * @param deviceID  设备ID
     * @param memberid  会员UID
     * @param coachid   教练UID
     * @param clerkid   前台UID
     * @return
     */
    Observable<RetrunLessons>eliminateLesson(String deviceID, int type, String memberid, String coachid, String clerkid);

    /**
     * 选择课程
     * @param deviceID
     * @param type
     * @param lessonId
     * @param memberid
     * @param coachid
     * @return
     */
    Observable<RetrunLessons>selectLesson(String deviceID, int type, String lessonId, String memberid, String coachid, String clerkid);

    /**
     *
     * @param deviceID  设备ID
     * @return
     */
    Observable<UpdateMessage>deviceUpgrade(String deviceID);

    /**
     *
     * @param deviceTargetValue
     * @return
     */
    Observable<DeviceData>getdeviceID(String deviceTargetValue,int devicetype);

    /**
     * 数据同步
     * @param deviceId
     * @return
     */
    Observable<DownLoadData>syncUserFeature(String deviceId);
    /**
     *  下载指静脉数据
     * @param messageId
     * @param appid
     * @param shopId
     * @param deviceId
     * @param uid
     * @return
     */
    Observable<DownLoadData> downloadFeature(String messageId, String appid, String shopId, String deviceId, String uid);

}
