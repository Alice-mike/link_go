package com.soonvein.cloud.base;

import android.content.Context;
import android.support.v4.util.Pair;
import android.util.Base64;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.BaseApplication;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.utils.Utils;
import com.wedone.sdk.SdkMain;
import com.wedone.sdk.UserData;
import com.wedone.sdk.VeinMatchLib;

import java.util.Arrays;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Shaozy on 2016/8/10.
 */
public class DeviceApi {
    private static DeviceApi ourInstance;

    private static Context context = BaseApplication.getInstance().getApplicationContext();
    private static SdkMain m_SdkMain = null;

    public static DeviceApi getInstance() {
        if (ourInstance == null) ourInstance = new DeviceApi();
        return ourInstance;
    }

    private DeviceApi() {
    }

    /**
     * 初始化SDK需要在主进程，所以这里单独写了一个Observable
     */
    public Observable<Long> initSDK() {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
//                if (BaseApplication.DEBUG)
//                    Logger.e("当前线程ID：" + Thread.currentThread().getId());
                if (m_SdkMain == null) {
                    //新建一个SDK实例
                    m_SdkMain = SdkMain.getInstance();
                }
                //设置工作模式
                //m_SdkMain.FV_SetWorkMode(SDK_WORKMODE_REGISTER);
//                Logger.e("初始化指静脉SDK成功");
                subscriber.onNext(SdkMain.SDK_ERRCODE_SUCCESS);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * Wedone: 等待手指传感器变为某种指定的状态，等待的时间为nInterval*nTimes
     *
     * @参数(IN) byte bFingerStatus: 等待的状态；0：手指已经移开，3：手指已经放置好。
     * @参数(IN) int nTimes: 检测的次数，必须大于0。
     * @参数(IN) int nInterval: 每次检测的间隔，单位为毫秒，建议在500 - 1000毫秒之间。
     * @调用 public
     * @返回 boolean: true=成功的等到了指定的状态：
     * false=没有等到指定的状态就超时了
     */
    public Boolean getFingerStatus(byte bFingerStatus, int nTimes, int nInterval) {
        if (nTimes <= 0) {
            nTimes = 20;
        }

        if (nInterval <= 0) {
            nInterval = 500;
        }

        byte bFingerSt[] = new byte[1];
        long lRetVal = 0;

        for(;;) {
            if (0 == bFingerStatus) {
                //Logger.e("请移开手指...");
            } else if (0x03 == bFingerStatus) {
                //Logger.e("请放手指...");
            } else {
                break;
            }
            lRetVal = m_SdkMain.FV_ReadFingerStatus(bFingerSt);
            if (m_SdkMain.SDK_ERRCODE_SUCCESS != lRetVal) {
//                Logger.e("检测手指放置状态失败，错误码=" + lRetVal + "!");
                return false;
            }
            if (bFingerStatus == bFingerSt[0]) {
                return true;
            }
            try {
                Thread.sleep(nInterval);
            } catch (Exception e) {
//                Logger.e("检测手指放置状态发生错误=" + e.getMessage());
            }
        }
        return false;
    }

    public Pair<byte[], short[]> registerTemplate() {
        if (null == m_SdkMain) {
//            if (BaseApplication.DEBUG)
//                Logger.e("SDK尚未初始化，请先初始化SDK后再操作(Code=" + SdkMain.SDK_ERRCODE_NOTINITED + ")");
            return null;
        }

        long errorCode = 0;
        byte[] bTemplateBuff = new byte[SdkMain.SDK_TEMPLATE_SIZE];//Wedone:准备用来保存模板数据的缓冲区
        //Wedone:调用读取模板的接口
        short[] sReadLen = new short[1]; // 用来保存返回数据长度的缓冲区
        errorCode = m_SdkMain.FV_ReadTemplate(bTemplateBuff, SdkMain.SDK_TEMPLATE_SIZE, sReadLen); //读取指静脉模板的接口

        if (SdkMain.SDK_ERRCODE_SUCCESS != errorCode) {
//            if (BaseApplication.DEBUG)
//                Logger.e("获取注册用指静脉模板失败(Code=" + errorCode + ")");
            return null;
        }
        return new Pair<>(bTemplateBuff, sReadLen);
    }

    public Boolean verifyTemplate(UserData regUserData, UserData matchUserData, short wSecurityLevel_1Vn) {
        long errorCode = 0;

        short sRegTemplateNum = regUserData.GetTemplateNum();
        short sMatchTemplateNum = matchUserData.GetTemplateNum();

        byte[] bTemlateReg = new byte[UserData.D_USER_TEMPLATE_SIZE * sRegTemplateNum];
        byte[] bTemlateMatch = new byte[UserData.D_USER_TEMPLATE_SIZE * sMatchTemplateNum];

        regUserData.GetTemplateData(bTemlateReg, (short) (UserData.D_USER_TEMPLATE_SIZE * sRegTemplateNum));
        matchUserData.GetTemplateData(bTemlateMatch, (short) (UserData.D_USER_TEMPLATE_SIZE * sMatchTemplateNum));
        VeinMatchLib.vmSetSecurityLevel(wSecurityLevel_1Vn, (short) 10);//设置静脉比对的安全级别，默认6(1:N)/8(1:1)，
        errorCode = VeinMatchLib.vmMatchTemplates(bTemlateMatch, bTemlateReg, sRegTemplateNum, (byte) 0x03);//验证多个模板

        if (0 == errorCode) {
//            if (BaseApplication.DEBUG)
//                Logger.e("JNI方式与当前注册用户模板进行验证，验证通过!");
            return true;
        } else {
//            if (BaseApplication.DEBUG)
//                Logger.e("JNI方式与当前注册用户模板进行验证，验证失败!uid=" + matchUserData.GetUid() + ",errorCode=" + errorCode);
            return false;
        }
    }

    @Deprecated
    public Pair<ApiException, UserData> registerTemplate(UserData regUserData, Member member) {
//        if (BaseApplication.DEBUG)
//            Logger.e("注册模板当前线程ID：" + Thread.currentThread().getId());

        ApiException apiException;
        if (null == m_SdkMain) {
//            if (BaseApplication.DEBUG)
//                Logger.e("SDK尚未初始化，请先初始化SDK后再操作(Code=" + SdkMain.SDK_ERRCODE_NOTINITED + ")");
            apiException = new ApiException("采集指静脉失败！", ApiException.REGISTER_TEMPLATE_ERROR);
            return new Pair<>(apiException, regUserData);
        }
        if (UserData.D_USER_TEMPLATE_NUM <= regUserData.GetTemplateNum()) {
//            if (BaseApplication.DEBUG)
//                Logger.e("单个用户最多只能注册" + UserData.D_USER_TEMPLATE_NUM + "个模板");
            apiException = new ApiException("采集指静脉失败！", ApiException.REGISTER_TEMPLATE_ERROR);
            return new Pair<>(apiException, regUserData);
        }
        //Wedone:检测手指，直到检测到手指已经放好
        boolean isWaitSuccess = true;
        isWaitSuccess = getFingerStatus((byte) 0x03, 50, 200);
        if (!isWaitSuccess) {
//            if (BaseApplication.DEBUG)
//                Logger.e("准备注册模板，未检测到手指");

            apiException = new ApiException("未检测到手指，采集指静脉失败！", ApiException.MISSING_FINGER_ERROR);
            return new Pair<>(apiException, regUserData);
        } else {
//            if (BaseApplication.DEBUG)
//                Logger.e("检测到手指，准备注册模板");
        }
        long errorCode = 0;
        byte[] bTemplateBuff = new byte[SdkMain.SDK_TEMPLATE_SIZE];//Wedone:准备用来保存模板数据的缓冲区

        //Wedone:调用读取模板的接口
        short sReadLen[] = new short[1]; // 用来保存返回数据长度的缓冲区
        errorCode = m_SdkMain.FV_ReadTemplate(bTemplateBuff, SdkMain.SDK_TEMPLATE_SIZE, sReadLen); //读取指静脉模板的接口

        if (SdkMain.SDK_ERRCODE_SUCCESS != errorCode) {
//            if (BaseApplication.DEBUG)
//                Logger.e("获取注册用指静脉模板失败(Code=" + errorCode + ")");
            apiException = new ApiException("采集指静脉失败！", ApiException.REGISTER_TEMPLATE_ERROR);
            return new Pair<>(apiException, regUserData);
        }
        //Wedone:检测到手指移开为止
        isWaitSuccess = getFingerStatus((byte) 0x00, 50, 200);

        if (!isWaitSuccess) {
//            if (BaseApplication.DEBUG)
//                Logger.e("注册模板完成，手指未移开");
            apiException = new ApiException("手指未移开，采集指静脉失败！", ApiException.MOVING_FINGER_ERROR);
            return new Pair<>(apiException, regUserData);
        } else {
//            if (BaseApplication.DEBUG)
//                Logger.e("注册模板完成，检测到手指移开，添加模板数据");
        }

        if (0 == regUserData.GetTemplateNum()) {
            int currentRegCnt = Utils.getRegUserCnt();
            //int memId = Integer.valueOf(member.getMemID());
            byte[] memId = String.format("%04d", currentRegCnt).getBytes();

            byte bUserId[] = new byte[UserData.D_USER_HDR_USERID_LEN];
            byte bUserName[] = new byte[UserData.D_USER_HDR_USERNAME_LEN];

            bUserId[0] = 'I';
            bUserId[1] = 'D';
            bUserId[2] = memId[0];
            bUserId[3] = memId[1];
            bUserId[4] = memId[2];
            bUserId[5] = memId[3];
            regUserData.SetUserId(bUserId, (short) 6);

            bUserName[0] = 'U';
            bUserName[1] = 'S';
            bUserName[2] = 'E';
            bUserName[3] = 'R';
            bUserName[4] = memId[0];
            bUserName[5] = memId[1];
            bUserName[6] = memId[2];
            bUserName[7] = memId[3];
            Utils.refreshRegUserCnt(currentRegCnt + 1);
            regUserData.SetUserName(bUserName, (short) 8);
        }
        //调用FV_ReadTemplate返回成功的话，第一个参数的缓冲区中就保存了所读取的模板数据
        regUserData.AddTemplateData(bTemplateBuff, sReadLen[0]);
        return new Pair<>(null, regUserData);
    }

    public long saveTemplate(UserData regUserData) {
//        if (BaseApplication.DEBUG)
//            Logger.e("当前线程ID：" + Thread.currentThread().getId());

        if (null == m_SdkMain) {
//            Logger.e("SDK尚未初始化，请先初始化SDK后再操作！\r\n");
            return SdkMain.SDK_ERRCODE_NOTINITED;
        }
        if (0 == regUserData.GetTemplateNum()) {
//            Logger.e("尚未注册模板，请先注册！\r\n");
            return SdkMain.SDK_ERRCODE_NOTEXIST;
        }
        byte bUserHdr[] = new byte[UserData.D_USER_HDR_LEN];
        short sTemplateDataLen = (short) (UserData.D_USER_TEMPLATE_SIZE * regUserData.GetTemplateNum());
        byte bTemplateData[] = new byte[sTemplateDataLen];
        regUserData.GetUserHdrData(bUserHdr, (short) UserData.D_USER_HDR_LEN);
        regUserData.GetTemplateData(bTemplateData, sTemplateDataLen);

        long lRetVal = 0;
        lRetVal = m_SdkMain.FV_DownloadUserData(bUserHdr, (short) UserData.D_USER_HDR_LEN, bTemplateData, sTemplateDataLen);
        if (SdkMain.SDK_ERRCODE_SUCCESS != lRetVal) {
//            Logger.e("保存数据失败，错误码=" + lRetVal + "!\r\n");
            return lRetVal;
        }
        return SdkMain.SDK_ERRCODE_SUCCESS;
    }
    @Deprecated
    public Pair<ApiException, UserData> verifyTemplate(UserData regUserData) {
//        if (BaseApplication.DEBUG)
//            Logger.e("验证模板当前线程ID：" + Thread.currentThread().getId());
        ApiException apiException;
        if (null == m_SdkMain) {
//            if (BaseApplication.DEBUG)
//                Logger.e("SDK尚未初始化，请先初始化SDK后再操作(Code=" + SdkMain.SDK_ERRCODE_NOTINITED + ")");
            apiException = new ApiException("验证指静脉失败！", ApiException.MATCH_TEMPLATE_ERROR);
            return new Pair<>(apiException, null);
        }
        //Wedone:检测手指，直到检测到手指已经放好
        boolean isWaitSuccess = true;
        isWaitSuccess = getFingerStatus((byte) 0x03, 50, 200);
        if (!isWaitSuccess) {
//            if (BaseApplication.DEBUG)
//             Logger.e("准备验证模板，未检测到手指");
            apiException = new ApiException("未检测到手指，验证指静脉失败！", ApiException.MISSING_FINGER_ERROR);
            return new Pair<>(apiException, regUserData);
        } else {
//            if (BaseApplication.DEBUG)
//                Logger.e("检测到手指，准备验证模板");
        }
        long errorCode = 0;
        byte bTemplateBuff[] = new byte[m_SdkMain.SDK_TEMPLATE_SIZE];//Wedone:准备用来保存模板数据的缓冲区
        //Wedone:调用读取模板的接口
        UserData matchUserData = new UserData();
        matchUserData.ClearData();
        short sReadLen[] = new short[1]; // 用来保存返回数据长度的缓冲区
        errorCode = m_SdkMain.FV_ReadTemplate(bTemplateBuff, SdkMain.SDK_TEMPLATE_SIZE, sReadLen); //读取指静脉模板的接口
        if (SdkMain.SDK_ERRCODE_SUCCESS != errorCode) {
//            if (BaseApplication.DEBUG)
//                Logger.e("获取验证用指静脉模板失败，错误码(Code=" + errorCode + ")");
            apiException = new ApiException("验证指静脉失败！", ApiException.MATCH_TEMPLATE_ERROR);
            return new Pair<>(apiException, null);
        }
        matchUserData.AddTemplateData(bTemplateBuff, sReadLen[0]);
        //Wedone:检测到手指移开为止
        isWaitSuccess = getFingerStatus((byte) 0x00, 50, 200);
        if (!isWaitSuccess) {
//            if (BaseApplication.DEBUG)
//                Logger.e("手指未移开");
            apiException = new ApiException("手指未移开，验证指静脉失败！", ApiException.MOVING_FINGER_ERROR);
            return new Pair<>(apiException, regUserData);
        } else {
//            if (BaseApplication.DEBUG)
//                Logger.e("获取验证用指静脉模板成功，检测到手指移开,对比模板！" + Base64.encodeToString(bTemplateBuff, Base64.NO_WRAP));
        }
        short sRegTemplateNum = regUserData.GetTemplateNum();
        short sMatchTemplateNum = matchUserData.GetTemplateNum();
        if (0 < sRegTemplateNum && 0 < sMatchTemplateNum) {
            byte[] bTemlateReg = new byte[UserData.D_USER_TEMPLATE_SIZE * sRegTemplateNum];
            byte[] bTemlateMatch = new byte[UserData.D_USER_TEMPLATE_SIZE * sMatchTemplateNum];
            regUserData.GetTemplateData(bTemlateReg, (short) (UserData.D_USER_TEMPLATE_SIZE * sRegTemplateNum));
            matchUserData.GetTemplateData(bTemlateMatch, (short) (UserData.D_USER_TEMPLATE_SIZE * sMatchTemplateNum));
            VeinMatchLib.vmSetSecurityLevel((short) 4, (short) 10);//设置静脉比对的安全级别，默认6(1:N)/8(1:1)，最高1，最低10
            //采用采集一个模板，验证一个模板这种方式，验证的时候只需要截取最后一个模板的数据
            //按照512字节长度为一个模板，截取最后一个模板的数据进行验证
            int start = UserData.D_USER_TEMPLATE_SIZE * (sRegTemplateNum - 1);
            int end = UserData.D_USER_TEMPLATE_SIZE * (sRegTemplateNum);
            byte[] temp = Arrays.copyOfRange(bTemlateReg, start, end);
            errorCode = VeinMatchLib.vmMatchTemplate(bTemlateMatch, temp, (byte) 0x03);//验证单个个模板
            //errorCode = VeinMatchLib.vmMatchTemplates(bTemlateMatch, bTemlateReg, sRegTemplateNum, (byte) 0x03);//验证多个模板
            if (0 == errorCode) {
//                if (BaseApplication.DEBUG)
//                 Logger.e("JNI方式与当前注册用户模板进行验证，验证通过!");
                return new Pair<>(null, matchUserData);
            } else {
//                if (BaseApplication.DEBUG)
//                    Logger.e("JNI方式与当前注册用户模板进行验证，验证失败!uid=" + matchUserData.GetUid() + ",errorCode=" + errorCode);
                apiException = new ApiException("验证指静脉失败！", ApiException.MATCH_TEMPLATE_ERROR);
                return new Pair<>(apiException, null);
            }
        }
        //这段代码应该是两种对比指静脉的方法
        //下面是方法二
        byte bUserHdr[] = new byte[UserData.D_USER_HDR_LEN];
        errorCode = m_SdkMain.FV_DeviceMatch(bTemplateBuff, SdkMain.SDK_TEMPLATE_SIZE, bUserHdr, (short) UserData.D_USER_HDR_LEN);

        if (SdkMain.SDK_ERRCODE_SUCCESS != errorCode) {
            apiException = new ApiException(new Throwable("模板比对时发生错误，错误码(Code=" + errorCode + ")"), (int) errorCode);
            apiException.setDisplayMessage("验证指静脉失败！");
            return new Pair<>(apiException, null);
        }
        matchUserData.SetUserHdrData(bUserHdr, (short) UserData.D_USER_HDR_LEN);
        if (0 != matchUserData.GetUid()) {
            return new Pair<>(null, matchUserData);
        } else {
//            if (BaseApplication.DEBUG)
//                Logger.e("与模块中已保存的用户进行验证，验证失败!uid=" + matchUserData.GetUid() + ",errorCode=" + errorCode);
            apiException = new ApiException("验证指静脉失败！", ApiException.MATCH_TEMPLATE_ERROR);
            return new Pair<>(apiException, null);
        }
    }
    public Pair<ApiException, String> getVerifyTemplate() {
        String veinFingerID = "";
        ApiException apiException;
        long errorCode = 0;
        byte bTemplateBuff[] = new byte[m_SdkMain.SDK_TEMPLATE_SIZE];//Wedone:准备用来保存模板数据的缓冲区
        //Wedone:调用读取模板的接口
        UserData matchUserData = new UserData();
        matchUserData.ClearData();
        short sReadLen[] = new short[1]; // 用来保存返回数据长度的缓冲区
        errorCode = m_SdkMain.FV_ReadTemplate(bTemplateBuff, SdkMain.SDK_TEMPLATE_SIZE, sReadLen); //读取指静脉模板的接口
//        Log.d("DeviceApi","=============="+errorCode);
        boolean isWaitSuccess = true;
        isWaitSuccess = getFingerStatus((byte) 0x03, 50, 200);
        if (!isWaitSuccess) {
            if (BaseApplication.DEBUG)
                Logger.e("SDK尚未初始化，请先初始化SDK后再操作(Code=" + SdkMain.SDK_ERRCODE_NOTINITED + ")");
            apiException = new ApiException("验证指静脉失败", ApiException.MATCH_TEMPLATE_ERROR);
            return new Pair<>(apiException, null);
        }

        if (SdkMain.SDK_ERRCODE_SUCCESS != errorCode) {
            apiException = new ApiException(new Throwable("模板比对时发生错误，错误码(Code=" + errorCode + ")"), (int) errorCode);
            apiException.setDisplayMessage("验证指静脉失败！");
            return new Pair<>(apiException, null);
        }
        if (SdkMain.SDK_ERRCODE_SUCCESS != errorCode) {
            if (BaseApplication.DEBUG)
                Logger.e("获取验证用指静脉模板失败，错误码(Code=" + errorCode + ")");
            apiException = new ApiException("获取指静脉失败！", ApiException.MATCH_TEMPLATE_ERROR);
            return new Pair<>(apiException, null);
        }
        matchUserData.AddTemplateData(bTemplateBuff, sReadLen[0]);
        veinFingerID = Base64.encodeToString(bTemplateBuff, Base64.NO_WRAP);
        if (veinFingerID.equals("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=") ) {
            Logger.e("SDK尚未初始化，请先初始化SDK后再操作(Code=" + SdkMain.SDK_ERRCODE_NOTINITED + ")");
            apiException = new ApiException("验证指静脉失败", ApiException.MATCH_TEMPLATE_ERROR);
            return new Pair<>(apiException, null);
        } else {
            if (BaseApplication.DEBUG)
                Logger.e("获取验证用指静脉模板成功！" + veinFingerID);
            return new Pair<>(null, veinFingerID);
        }
    }
}
