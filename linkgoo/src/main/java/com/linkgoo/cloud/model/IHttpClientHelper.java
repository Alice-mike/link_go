package com.soonvein.cloud.model;

import com.google.gson.JsonObject;
import com.soonvein.cloud.bean.CodeInfo;
import com.soonvein.cloud.bean.LessonMessage;
import com.soonvein.cloud.bean.ResultResponse;
import com.soonvein.cloud.bean.UpdateMessage;
import com.soonvein.cloud.bean.UserResponse;
import com.soonvein.cloud.bean.LessonResponse;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.bean.RestResponse;
import com.soonvein.cloud.bean.ReturnBean;
import com.soonvein.cloud.bean.SignedResponse;
import com.soonvein.cloud.bean.Voucher;

import rx.Observable;

/**
 * Created by Shaozy on 2016/8/10.
 */
public interface IHttpClientHelper {

    /**
     * 调用云通讯SDK发送验证码
     */
    Observable<RestResponse> sendSMS(String accountSid, JsonObject params);

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
    Observable<ReturnBean> bindVeinMemeber(String phone, String deviceID, String veinFingerID1, String veinFingerID2, String veinFingerID3);

    /**
     * 通过指静脉查询会员ID
     *
     * @param phone        会员手机号码
     * @param deviceID     指静脉设备ID
     * @param veinFingerID 指静脉ID
     * @return Observable<Member>
     */
    Observable<Member> getMemberInfoByVein(String phone, String deviceID, String veinFingerID);

    /**
     * 根据设备ID和手机号查询会员信息
     *
     * @param phone    会员手机号码
     * @param deviceID 指静脉设备ID
     * @return Observable<Member>
     */
    Observable<Member> getMemInfo(String phone, String deviceID);

    /**
     * 通过会员ID查询会员信息
     *
     * @param memID 会员ID
     * @return Observable<Member>
     */
    Observable<Member> getMemInfo(String memID);

    /**
     * 查询会员卡信息
     *
     * @param memID 会员ID
     * @return Observable<CardInfo>
     */
    Observable<ReturnBean> getCardInfo(String memID);

    /**
     * 会员的上次签到时间
     *
     * @param memID 会员ID
     * @return Observable<ReturnBean>
     */
    Observable<ReturnBean> getLastSignedTime(String memID);

    /**
     * 会员签到
     *
     * @param memID  会员ID
     * @param cardID 会员卡ID
     * @return Observable<ReturnBean>
     */
    Observable<ReturnBean> signedMember(String memID, String cardID);

    /**
     * 会员签到
     *
     * @param deviceID  设备ID
     * @param veinFingerID 用户指静脉数据
     * @param phoneNum 用户手机号
     * @return Observable<ReturnBean>
     */
    Observable<SignedResponse> signedMember(String deviceID, String veinFingerID, String phoneNum);

    /**
     * 指静脉消费接口
     *
     * @param deviceID     指静脉设备ID
     * @param veinFingerID 指静脉ID
     * @param price        消费金额
     * @param method       扣款方式
     * @param mark         备注
     * @return Observable<ReturnBean>
     */
    Observable<Voucher> consumeRecord(String phoneNum, String deviceID, String veinFingerID, String price, String method, String mark);

    /**
     *
     * @param deviceID  设备ID
     * @param userType    1.教练 2.会员 3。员工
     * @param veinFingerID  用户指静脉数据
     * @return
     */
    Observable<UserResponse>verifyUserEliminateLesson(String deviceID, int userType, String veinFingerID);

    /**
     *
     * @param deviceID  设备ID
     * @param memberid  会员UID
     * @param coachid   教练UID
     * @param clerkid   前台UID
     * @return
     */
    Observable<LessonResponse>eliminateLesson(String deviceID,String type,String memberid,String coachid,String clerkid);
    /**
     * @param deviceID  设备ID
     * @param memberid  会员UID
     * @param coachid   教练UID
     * @return
     */
    Observable<LessonResponse>eliminateLesson(String deviceID,String memberid,String coachid);
    /**
     * @param deviceID 设备ID
     * @return
     */
    Observable<CodeInfo>signedCodeInfo(String deviceID);

    /**
     * 选择课程
     * @param deviceID
     * @param type
     * @param lessonId
     * @param memberid
     * @param coachid
     * @return
     */
    Observable<LessonResponse>selectLesson(String deviceID, String type, String lessonId, String memberid, String coachid, String clerkid);

    /**
     *
     * @param deviceID  设备ID
     * @return
     */
    Observable<UpdateMessage>deviceUpgrade(String deviceID);
}
