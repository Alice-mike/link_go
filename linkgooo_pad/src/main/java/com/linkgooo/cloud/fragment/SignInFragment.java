package com.soonvein.cloud.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.orhanobut.logger.Logger;
import com.soonvein.cloud.BaseApplication;
import com.soonvein.cloud.R;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.LessonResponse;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.bean.SignedResponse;
import com.soonvein.cloud.bean.Voucher;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.contract.MatchVeinTaskContract;
import com.soonvein.cloud.core.BaseFragment;
import com.soonvein.cloud.utils.Utils;
import com.wedone.sdk.SdkMain;

import butterknife.Bind;
import butterknife.OnClick;


public class SignInFragment extends BaseFragment implements MatchVeinTaskContract.MatchVeinView{

    @Bind(R.id.phoneNum)
    EditText phoneNumView;
    @Bind(R.id.tipImageView_sign)
    ImageView tipImageView;
    @Bind(R.id.failureImageView_sign)
    ImageView failureImageView;
    @Bind(R.id.failureImage_sign)
    ImageView failureImage;
    @Bind(R.id.tvTips)
    TextView tvTips;
    @Bind(R.id.digitkeypad_0)
    Button number0;
    @Bind(R.id.digitkeypad_1)
    Button number1;
    @Bind(R.id.digitkeypad_2)
    Button number2;
    @Bind(R.id.digitkeypad_3)
    Button number3;
    @Bind(R.id.digitkeypad_4)
    Button number4;
    @Bind(R.id.digitkeypad_5)
    Button number5;
    @Bind(R.id.digitkeypad_6)
    Button number6;
    @Bind(R.id.digitkeypad_7)
    Button number7;
    @Bind(R.id.digitkeypad_8)
    Button number8;
    @Bind(R.id.digitkeypad_9)
    Button number9;
    @Bind(R.id.digitkeypad_c)
    Button delect;
    @Bind(R.id.digitkeypad_ok)
    Button reset;
    private Context context;
    public MatchVeinTaskContract presenter;
    private MediaPlayer mediaPlayer,mediaPlayer1,mediaPlayer2;
    private String phoneNum, deviceID, veinFingerID, price, mark;
    private int doWhat = 0;
    private boolean flag=false;
//    private SdkMain sdkMain;
    byte[] aByte={0};
    int action;
    public static SignInFragment newInstance() {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                deviceID = Utils.getCurrentDeviceID();
                phoneNum = "6991";
                veinFingerID = "IuqINyc2RVzlFoN50OAtont7aMLHK7dUDZFw2iALkiHUl8p2yna+MpLdF0KChyevTbJk8zXAnL3yBkdzhGZ39pW20x9AIcTncN1840KuCJU7aL4My1Yj25RuEZ1PTVMRZD+zDWzZ6ts0oWP+WvirmtpY5KVatmvUokJsmIkeoZl7cvQA/USyYrLFv43n3e5E5zOQN7nbd6nMqwkjSsVO1jNroaoRE9Bgj/uTECU1uhaHQMcrCHuJ1wcSPky4/yOaryPVnMZVxTVcy2y1dzZgo6O7C1cucIegLk+LQUYciGCZyNt1p43tJzpDKmKpmSYOdPvTcuxFVQBmyZvszbf9DtfJ6m9rasBkSqJ895HXZ6ZxrLa+UCrmBUIKUtLex2AbVhOGXPEO3sL1nb5lU9xz2wiRiB5G5JSbma3Wv5F+xhZOtWw857JZHCcE4QFF8N4DywtUt8rboUsac5fD0YKTqO/7RozgU0fvCRT/lKPfJcOG54EL7Hi26nbJwMI1lFs6bM0IBnD8+Gpzt2Zyema2m7ohCVioI+xqfrcWgy0K21i1lVsHV9kWO8GPeeLlObQTwrTgzCRnnbLfZe6PpK6TapkvygpZLa3/XLH+YV7BRri7F99yRZQ4iLfgk8rQh62uEaet0Y2FybDPnlXe2poQxMPkMHw+O8saMwVQcWwiA5o=";
                SignInFragment.this.showProgress(true, false, "请稍后");
                //签到需要查找会员信息
            } else if (msg.what == 1) {
                String errorMsg = (String) msg.obj;
                if (Utils.isEmpty(errorMsg)) {
                    //失败页面展示
                    errorMsg = "签到失败，请重新绑定会员或注册新会员！";
                } else {
                    failureImage.setVisibility(View.GONE);
                    failureImageView.setVisibility(View.VISIBLE);
                    tvTips.setText(errorMsg);
                    mediaPlayer1.start();
                    failureImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            presenter.getVerifyTemplate();
                        }
                    });
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            failureImageView.setVisibility(View.GONE);
//                        }
//                    },3000);
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer=MediaPlayer.create(getActivity(),R.raw.phone_4);
                mediaPlayer1=MediaPlayer.create(getActivity(),R.raw.failure_sign);
                mediaPlayer2=MediaPlayer.create(getActivity(),R.raw.failure_cost);
                context=getActivity().getApplicationContext();
//                sdkMain=new SdkMain();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_signin;
    }

    @Override
    protected void initViews(View self, Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
        phoneNumView.setShowSoftInputOnFocus(false);
        presenter = new MatchVeinTaskContract();
        this.presenter.attachView(this);
        phoneNum=phoneNumView.getText().toString();
        tipImageView.setVisibility(View.VISIBLE);
        failureImage.setVisibility(View.GONE);
        failureImageView.setVisibility(View.GONE);
        deviceID = Utils.getCurrentDeviceID();
    }
    @Override
    protected void onVisible() {
    }
    @Override
    protected void onInvisible() {
    }

    @Override
    public void verifyTemplateFailure(ApiException e) {
        onError(e);
    }

    @Override
    public void signSuccess(SignedResponse signedResponse) {
        this.showProgress(false);
        SignedInFragment fragment = SignedInFragment.newInstance(signedResponse);
        //签到
        ((SignInMainFragment) this.getParentFragment()).addFragment(fragment, 1);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((SignInMainFragment) getParentFragment()).viewPager.setCurrentItem(0);
            }
        },5000);
    }
    @Override
    public void onResultError(ApiException e) {
        onError(e);
    }
    @Override
    public void onPermissionError(ApiException e) {
        onError(e);
    }
    @Override
    public void costSuccess(Voucher voucherInfo) {
    }
    @Override
    public void verifyTemplateSuccess(String veinFingerID) {
        phoneNum=phoneNumView.getText().toString();
        deviceID = Utils.getCurrentDeviceID();
        this.showProgress(true, false, "请稍后");
        this.presenter.signedMember(phoneNum, deviceID, veinFingerID);
        Logger.e("SignInfragment"+"phoneNum"+phoneNum+"deviceID"+deviceID+"veinFingerID"+veinFingerID);
    }
    @Override
    public void onError(ApiException e) {
        super.onError(e);
        if (BaseApplication.DEBUG) {
            Logger.e(e.getMessage());
            this.showToast(e.getMessage());
        }
        this.showProgress(false);
        Message msg=mHandler.obtainMessage();
        msg.what=1;
        if (!Utils.isEmpty(e.getDisplayMessage())) {
            msg.obj = e.getDisplayMessage();
        }
        mHandler.sendMessage(msg);
    }
    @OnClick({R.id.digitkeypad_0,R.id.digitkeypad_1,R.id.digitkeypad_2,R.id.digitkeypad_3,R.id.digitkeypad_4,R.id.digitkeypad_5,R.id.digitkeypad_6,
        R.id.digitkeypad_7,R.id.digitkeypad_8,R.id.digitkeypad_9,R.id.digitkeypad_c,R.id.failureImage_sign,R.id.digitkeypad_ok})
    public void onClick(View view){
        int index = phoneNumView.getSelectionStart();
        Editable editable = phoneNumView.getText();
    switch (view.getId()) {
        case R.id.digitkeypad_0:
            editable.insert(index, "0");
            init();
            break;
        case R.id.digitkeypad_1:
                editable.insert(index, "1");
            init();
            break;
        case R.id.digitkeypad_2:
            editable.insert(index, "2");
            init();
            break;
        case R.id.digitkeypad_3:
            editable.insert(index, "3");
            init();
            break;
        case R.id.digitkeypad_4:
            editable.insert(index, "4");
            init();
            break;
        case R.id.digitkeypad_5:
            editable.insert(index, "5");
            init();
            break;
        case R.id.digitkeypad_6:
            editable.insert(index, "6");
            init();
            break;
        case R.id.digitkeypad_7:
            editable.insert(index, "7");
            init();
            break;
        case R.id.digitkeypad_8:
            editable.insert(index, "8");
            init();
            break;
        case R.id.digitkeypad_9:
            editable.insert(index, "9");
            init();
            break;
        case R.id.digitkeypad_c:
            if (index > 0) {
                editable.delete(index - 1, index);
            }
            break;
        case R.id.digitkeypad_ok:
//            phoneNumView.getText().clear();
            if (Utils.isEmpty(phoneNumView.getText().toString())&&phoneNumView.getText().length()<4){
                phoneNumView.setError(getResources().getString(R.string.error_invalid_phone_4));
            }else {
            this.presenter.getVerifyTemplate();
        }
            break;
        case R.id.failureImage_sign:
            break;
    }
}
    private void init(){
//        if (phoneNumView.getText().length()==4)
//            isFingerPutOn();
    }
    @Override
    public void onStart() {
        super.onStart();
        tvTips.setText("请按图示放置手指");
        phoneNumView.getText().clear();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
