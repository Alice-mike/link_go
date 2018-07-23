package com.link.cloud.model;

import com.google.gson.JsonObject;
import com.link.cloud.bean.CabinetNumberData;
import com.link.cloud.bean.CodeInfo;
import com.link.cloud.bean.DeviceData;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.Lockdata;
import com.link.cloud.bean.MessagetoJson;
import com.link.cloud.bean.ResultResponse;
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
    Observable<DeviceData>getdeviceID(String deviceTargetValue,int deviceType);

    /**
     * 储物柜操作
     * @param openType
     * @param deviceId
     * @param uid
     * @param fromType
     * @return
     */
    Observable<Lockdata>isOpenCabinet(int openType, String deviceId, String uid, String fromType);

    /**
     * 清除储物柜使用信息
     * @param deviceId
     * @param cabinetNumber
     * @return
     */
    Observable<ResultResponse>clearCabinet(String deviceId,String cabinetNumber);

    /**
     *管理员开柜
     * @param deviceId
     * @param cabinetNumber
     * @return
     */
    Observable<ResultResponse>adminiOpenCabinet(String deviceId,String cabinetNumber);

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

    /**
     * 数据同步
     * @param deviceId
     * @return
     */
    Observable<DownLoadData>syncUserFeature(String deviceId);

    /**
     * 查询柜号列表
     * @param deviceId
     * @return
     */
    Observable<CabinetNumberData>cabinetNumberList(String deviceId);
}
