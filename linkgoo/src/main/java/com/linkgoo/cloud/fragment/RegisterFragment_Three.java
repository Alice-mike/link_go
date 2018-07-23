package com.soonvein.cloud.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.BaseApplication;
import com.soonvein.cloud.R;
import com.soonvein.cloud.activity.BindAcitvity;
import com.soonvein.cloud.activity.CallBackValue;
import com.soonvein.cloud.activity.NewMainActivity;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.bean.ReturnBean;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.contract.BindTaskContract;
import com.soonvein.cloud.core.BaseFragment;
import com.soonvein.cloud.utils.Utils;
import com.wedone.sdk.UserData;

import java.util.Arrays;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/8/30.
 */

public class RegisterFragment_Three extends BaseFragment implements BindTaskContract.BindView {

    @Bind(R.id.bind_put_tv)
    TextView tvTips;
    @Bind(R.id.bind_put_img)
    ImageView bind_put_img;
    @Bind(R.id.bind_put_error)
    ImageView bind_put_error;
    @Bind(R.id.bind_put_succese)
    ImageView bingSuccessImage;
    private String deviceID;
    private Member mMemberInfo;
    private UserData regUserData;
    public BindTaskContract presenter;
    private static int MAXT_FINGER = 3;//表示注册几个指静脉模版
    Handler mHandler;
    CallBackValue callBackValue;
    public BindAcitvity activity;
    Runnable runnable,runnable1;
    private SharedPreferences userInfo;
    MediaPlayer mediaPlayer0,mediaPlayer1,mediaPlayer2,mediaPlayer3;
    public static RegisterFragment_Three newInstance(Member memberInfo) {
        RegisterFragment_Three fragment = new RegisterFragment_Three();
        Bundle args = new Bundle();
        args.putSerializable(Constant.EXTRAS_MEMBER, memberInfo);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=(BindAcitvity)activity;
        callBackValue=(CallBackValue)activity;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Logger.e("RegisterFragment_Three-----------onCreate ");
        super.onCreate(savedInstanceState);
        mediaPlayer0=MediaPlayer.create(activity,R.raw.error_acquire);//采集错误
        mediaPlayer1=MediaPlayer.create(activity,R.raw.error_finger);//请放置同一根手指
        mediaPlayer2=MediaPlayer.create(activity,R.raw.no_finger);//未检测到手指
        mediaPlayer3=MediaPlayer.create(activity,R.raw.no_move_finger);//未移开手指
        mHandler=new Handler();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.bind_putfinger;
    }
    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        Logger.e("RegisterFragment_Three-----------initViews ");
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMemberInfo = (Member) bundle.getSerializable(Constant.EXTRAS_MEMBER);
            userInfo = activity.getSharedPreferences("user_info", 0);
            deviceID = userInfo.getString("DeviceID", "");
            bind_put_img.setVisibility(View.VISIBLE);
            bind_put_img.setImageLevel(0);
            bind_put_error.setVisibility(View.GONE);
            bingSuccessImage.setVisibility(View.GONE);
        }
    }
    @Override
    protected void initListeners() {
    }
    @Override
    protected void initData() {
        Logger.e("RegisterFragment_Three-----------initData ");
        presenter = new BindTaskContract();
        this.presenter.attachView(this);
        regUserData = new UserData();
        regUserData.ClearData();
        nextOperate();
    }
    @Override
    protected void onVisible() {
        Logger.e("RegisterFragment_Three-----------onVisible ");
        //注册需要初始化指静脉模块
        regUserData = new UserData();
        regUserData.ClearData();

    }
    @Override
    protected void onInvisible() {
    }
    private void nextOperate() {
        int tempLateNum = regUserData.GetTemplateNum();
        Logger.e("RegisterFragment_Three-----------nextOperate "+tempLateNum);
        bind_put_img.setImageLevel(tempLateNum);
        if (tempLateNum < MAXT_FINGER) {
            this.presenter.registerTemplate(regUserData);
        } else {
            this.presenter.saveTemplate(regUserData);
        }
    }
    private void bindVeinMemeber() {
        Logger.e("RegisterFragment_Three-----------bindVeinMemeber ");
        int tempLateNum = regUserData.GetTemplateNum();
        if (BaseApplication.DEBUG)
            Logger.e("调用绑定指静脉接口,用户已绑定" + tempLateNum + "根手指静脉");
        if (tempLateNum < MAXT_FINGER) {
            this.showToast("程序异常，请联系管理员");
        } else {
            //保存验证模板到服务器
            byte[] bTemlateReg = new byte[UserData.D_USER_TEMPLATE_SIZE * tempLateNum];
            regUserData.GetTemplateData(bTemlateReg, (short) (UserData.D_USER_TEMPLATE_SIZE * tempLateNum));

            for (int i = 1; i <= tempLateNum; i++) {
                //按照512字节长度为一个模板，截取单个模板的数据
                int start = UserData.D_USER_TEMPLATE_SIZE * (i - 1);
                int end = UserData.D_USER_TEMPLATE_SIZE * i;
                byte[] temp = Arrays.copyOfRange(bTemlateReg, start, end);
                try {
                    if (BaseApplication.DEBUG) {
                        Logger.e("加密前验证模板" + i + ":" + Arrays.toString(temp));
                    }
                    String veinFingerID = Base64.encodeToString(temp, Base64.NO_WRAP);
                    if (i == 1) {
                        mMemberInfo.setVeinFingerID1(veinFingerID);
                    } else if (i == 2) {
                        mMemberInfo.setVeinFingerID2(veinFingerID);
                    } else if (i == 3) {
                        mMemberInfo.setVeinFingerID3(veinFingerID);
                    }
                    if (BaseApplication.DEBUG) {
                        Logger.e("加密后验证模板" + i + ":" + veinFingerID);
                    }
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                    this.showToast("程序异常，请联系管理员");
                    return;
                }
            }
            this.showProgress(true, false, "请稍后...");
            this.presenter.bindVeinMemeber(mMemberInfo.getPhone(), deviceID, mMemberInfo.getVeinFingerID1(), mMemberInfo.getVeinFingerID2(), mMemberInfo.getVeinFingerID3());
            Logger.e("BingVeinFragment:"+mMemberInfo.toString());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    @Override
    public void handleTips(String tips) {
        tvTips.setText(tips);
    }

    @Override
    public void registerTemplateSuccess(UserData regUserData) {
        this.regUserData = regUserData;
        nextOperate();
    }
    @Override
    public void saveTemplateSuccess(UserData regUserData) {
        this.regUserData = regUserData;
        //模板保存成功，调用API接口绑定用户指静脉
        bindVeinMemeber();
    }
    @Override
    public void bindSuccess(ReturnBean returnBean) {
        Logger.e("RegisterFragment_Three-----------bindSuccess ");
        if (BaseApplication.DEBUG)
            Logger.e("绑定手指静脉成功:" + returnBean);
        callBackValue.setActivtyChange("4");
        this.showProgress(false);
        bind_put_img.setImageLevel(3);
        bingSuccessImage.setVisibility(View.VISIBLE);
        tvTips.setText("绑定成功");
       runnable=new Runnable() {
            @Override
            public void run() {
               Intent intent = new Intent();
                intent.setClass(activity,NewMainActivity.class);
                startActivity(intent);
            }
        };
        mHandler.postDelayed(runnable
        ,3000);
    }
    @Override
    public void onError(ApiException e) {
        super.onError(e);
        this.showProgress(false);
        bind_put_error.setVisibility(View.VISIBLE);
        if (BaseApplication.DEBUG && !Utils.isEmpty(e.getDisplayMessage()))
            this.showToast(e.getDisplayMessage(), Toast.LENGTH_LONG);
        String errorMsg = "";
        if (e.getCode() == ApiException.REGISTER_TEMPLATE_ERROR) {
            errorMsg = getResources().getString(R.string.error_failure_register_template);
            mediaPlayer0.start();
        } else if (e.getCode() == ApiException.MATCH_TEMPLATE_ERROR) {
            errorMsg = getResources().getString(R.string.error_failure_match_template);
            mediaPlayer1.start();
        } else if (e.getCode() == ApiException.MISSING_FINGER_ERROR) {
            errorMsg = getResources().getString(R.string.error_failure_missing_finger);
            mediaPlayer2.start();
        } else if (e.getCode() == ApiException.MOVING_FINGER_ERROR) {
            errorMsg = getResources().getString(R.string.error_failure_moving_finger);
            mediaPlayer3.start();
        } else {
            errorMsg = e.getDisplayMessage();
        }
//        bindFailureImage.setVisibility(View.VISIBLE);
        tvTips.setText(errorMsg);
       runnable1= new Runnable() {
            @Override
            public void run() {
                nextOperate();
                bind_put_error.setVisibility(View.GONE);
                tvTips.setText("请按图示放置手指" );
            }
        };
        mHandler.postDelayed(runnable1,3000);
    }
    @Override
    public void onPermissionError(ApiException e) {
        onError(e);
    }

    @Override
    public void onResultError(ApiException e) {
        onError(e);
    }
    @Override
    public void onDestroy() {
        this.showProgress(false);
        this.presenter.detachView();
        this.presenter = null;

        super.onDestroy();
        if (runnable1!=null) {
            mHandler.removeCallbacks(runnable1);
        }
    if (runnable!=null) {
        mHandler.removeCallbacks(runnable);
    }
        if (mediaPlayer0!=null) {
            mediaPlayer0.stop();
            mediaPlayer0.release();
            mediaPlayer0 = null;
        }else if (mediaPlayer1!=null) {
            mediaPlayer1.stop();
            mediaPlayer1.release();
            mediaPlayer1 = null;
        }
        else if (mediaPlayer2!=null) {
            mediaPlayer2.stop();
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
        else if (mediaPlayer3!=null){
            mediaPlayer3.stop();
            mediaPlayer3.release();
            mediaPlayer3 = null;
        }
    }

}
