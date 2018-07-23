package com.soonvein.cloud.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.BaseApplication;
import com.soonvein.cloud.R;
import com.soonvein.cloud.activity.CallBackValue;
import com.soonvein.cloud.activity.SigeActivity;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.SignedResponse;
import com.soonvein.cloud.bean.Voucher;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.contract.MatchVeinTaskContract;
import com.soonvein.cloud.core.BaseFragment;
import com.soonvein.cloud.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignFragment_Two extends BaseFragment implements MatchVeinTaskContract.MatchVeinView {
    @Bind(R.id.sige_put_img)
    ImageView sige_put_img;
    @Bind(R.id.sige_put_error)
    ImageView sige_put_error;
    @Bind(R.id.sige_put_succese)
    ImageView sige_put_succese;
    @Bind(R.id.sige_put_tv)
    TextView tvTips;
    private static final int START_COUNTING = 1;
    private static final int COUNT_NUMBER = 30;
    public MatchVeinTaskContract presenter;
    private String phoneNum, deviceID, veinFingerID, price, mark;
    MediaPlayer mediaPlayer1;
    private SharedPreferences userInfo;
    //标记是从哪里跳转到这个页面
    private int doWhat = 0;
    public Runnable runnable;
    public SigeActivity activity;
    public static CallBackValue callBackValue;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=(SigeActivity) activity;
        callBackValue=(CallBackValue) activity;
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                userInfo = activity.getSharedPreferences("user_info", 0);
                deviceID = userInfo.getString("DeviceID", "");
                phoneNum = "6991";
                veinFingerID = "IuqINyc2RVzlFoN50OAtont7aMLHK7dUDZFw2iALkiHUl8p2yna+MpLdF0KChyevTbJk8zXAnL3yBkdzhGZ39pW20x9AIcTncN1840KuCJU7aL4My1Yj25RuEZ1PTVMRZD" +
                        "+zDWzZ6ts0oWP+WvirmtpY5KVatmvUokJsmIkeoZl7cvQA/USyYrLFv43n3e5E5zOQN7nbd6nMqwkjSsVO1jNroaoRE9Bgj/uTECU1uhaHQMcrCHuJ1wcSPky4/yOaryPVnMZVxTVcy2y1dzZgo6O7C1cucIegLk+" +
                        "LQUYciGCZyNt1p43tJzpDKmKpmSYOdPvTcuxFVQBmyZvszbf9DtfJ6m9rasBkSqJ895HXZ6ZxrLa+UCrmBUIKUtLex2AbVhOGXPEO3sL1nb5lU9xz2wiRiB5G5JSbma3Wv5F+" +
                        "xhZOtWw857JZHCcE4QFF8N4DywtUt8rboUsac5fD0YKTqO/7RozgU0fvCRT/lKPfJcOG54EL7Hi26nbJwMI1lFs6bM0IBnD8+Gpzt2Zyema2m7ohCVioI+" +
                        "xqfrcWgy0K21i1lVsHV9kWO8GPeeLlObQTwrTgzCRnnbLfZe6PpK6TapkvygpZLa3/XLH+YV7BRri7F99yRZQ4iLfgk8rQh62uEaet0Y2FybDPnlXe2poQxMPkMHw+O8saMwVQcWwiA5o=";
                SignFragment_Two.this.showProgress(true, false, "请稍后");
                //签到需要查找会员信息
                if (doWhat == Constant.ACTION_SIGNIN) {
                    SignFragment_Two.this.presenter.signedMember(phoneNum, deviceID, veinFingerID);
                }
                //消费
                else if (doWhat == Constant.ACTION_CONSUME) {
                    SignFragment_Two.this.presenter.consumeRecord(phoneNum, deviceID, veinFingerID, price, "1", mark);
                }
            } else if (msg.what == 1) {
                runnable=new Runnable() {
                    @Override
                    public void run() {
                        tvTips.setText("请按图示放置手指");
                        presenter.getVerifyTemplate();
                        sige_put_error.setVisibility(View.GONE);
                    }
                };
                if (getContext() == activity) {
                    String errorMsg = (String) msg.obj;
                    tvTips.setText(errorMsg);
                    if (Utils.isEmpty(errorMsg)) {
                    }
                    mHandler.postDelayed(runnable,2000);
                }
            }
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer1=MediaPlayer.create(activity, R.raw.failure_sign);
    }
    public static SignFragment_Two newInstance(String phoneNum, int doWhat) {
        Logger.e("SignFragment_Two-------newInstance");
        SignFragment_Two fragment = new SignFragment_Two();
        Bundle args = new Bundle();
        args.putString(Constant.EXTRAS_PHONENUM, phoneNum);
        args.putInt(Constant.EXTRAS_DOWHAT, doWhat);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.sign_putfinger;
    }

    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        Logger.e("SignFragment_Two-------initViews");
        Bundle bundle = getArguments();
        if (bundle != null) {
            doWhat = bundle.getInt(Constant.EXTRAS_DOWHAT);
            userInfo = activity.getSharedPreferences("user_info", 0);
            deviceID = userInfo.getString("DeviceID", "");
            if (doWhat == Constant.ACTION_CONSUME) {
                phoneNum = bundle.getString(Constant.EXTRAS_PHONENUM);
                price = bundle.getString(Constant.EXTRAS_PRICE);
                mark = bundle.getString(Constant.EXTRAS_MARK);
            } else {
                phoneNum = bundle.getString(Constant.EXTRAS_PHONENUM);
            }
        }
    }
    @Override
    protected void initListeners() {
    }
    @Override
    protected void initData() {
        Logger.e("SignFragment_Two-------initData");
        presenter = new MatchVeinTaskContract();
        this.presenter.attachView(this);
    }
    @Override
    protected void onVisible() {
        Logger.e("SignFragment_Two-------onVisible");
        tvTips.setText("请按图示放置手指");
        this.presenter.getVerifyTemplate();
        //测试API接口用下面的代码
        //mHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onInvisible() {
    }

    @Override
    public void onDestroy() {
        this.showProgress(false);
        this.presenter.detachView();
        mHandler.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public void verifyTemplateSuccess(String veinFingerID) {
        Logger.e("SignFragment_Two-------verifyTemplateSuccess");
        this.showProgress(true, false, "请稍后");
        //签到需要查找会员信息
        if (doWhat == Constant.ACTION_SIGNIN) {
            this.presenter.signedMember(phoneNum, deviceID, veinFingerID);
            Logger.e("SignFragment_Two-----veinFingerID:"+veinFingerID);
        }
        //消费
        else if (doWhat == Constant.ACTION_CONSUME) {
            this.presenter.consumeRecord(phoneNum, deviceID, veinFingerID, price, "1", mark);
        }
    }

    @Override
    public void signSuccess(SignedResponse signedResponse) {
        Logger.e("SignFragment_Two-------signSuccess");
        this.showProgress(false);
        callBackValue.setActivtyChange("4");
        SignFragment_Three fragment = SignFragment_Three.newInstance(signedResponse);
        //签到
        ((SignInMainFragment) this.getParentFragment()).addFragment(fragment, 2);
    }

    @Override
    public void costSuccess(Voucher voucherInfo) {

    }
    @Override
    public void onError(ApiException e) {
        Logger.e("SignFragment_Two-------onError");
        super.onError(e);
        mediaPlayer1.start();
        sige_put_error.setVisibility(View.VISIBLE);
        if (BaseApplication.DEBUG) {
            Logger.e(e.getMessage());
            this.showToast(e.getMessage());
        }
        this.showProgress(false);
        Message msg = mHandler.obtainMessage();
        msg.what = 1;
        if (!Utils.isEmpty(e.getDisplayMessage())) {
            msg.obj = e.getDisplayMessage();
        }
        mHandler.sendMessage(msg);
    }
    @Override
    public void verifyTemplateFailure(ApiException e) {
        Logger.e("SignFragment_Two-------verifyTemplateFailure");
        onError(e);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
