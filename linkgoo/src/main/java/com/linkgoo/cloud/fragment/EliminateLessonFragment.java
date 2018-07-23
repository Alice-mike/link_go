package com.soonvein.cloud.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.BaseApplication;
import com.soonvein.cloud.R;
import com.soonvein.cloud.TestActivityManager;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.CodeInfo;
import com.soonvein.cloud.bean.LessonResponse;
import com.soonvein.cloud.bean.UserResponse;
import com.soonvein.cloud.contract.UserLessonContract;
import com.soonvein.cloud.core.BaseFragment;
import com.soonvein.cloud.utils.Utils;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/31.
 */

public class EliminateLessonFragment extends BaseFragment implements UserLessonContract.UserLesson{

//    @Bind(R.id.failureImage)
//    ImageView failureImage;
//    @Bind(R.id.tipImageView)
//    ImageView tipImageView;
//    @Bind(R.id.tvTips)
//    TextView tvTips;
//    @Bind(R.id.eliminatetemp)
//    LinearLayout eliminatetemp;
//    @Bind(R.id.eliminateuser)
//    LinearLayout eliminateuser;
//    @Bind(R.id.emtmemberName)
//    TextView tvMemberName;
//    @Bind(R.id.emtmemberPhone)
//    TextView tvMemberPhone;
//    @Bind(R.id.emtuserType)
//    TextView tvuserTyper;
//    @Bind(R.id.emtmemberSex)
//    TextView tvmemberSex;
//    @Bind(R.id.emtnext)
//    Button btnext;
//    @Bind(R.id.successeliminate)
//    LinearLayout successeliminate;
//    @Bind(R.id.successName)
//    TextView successName;
//    @Bind(R.id.successPhone)
//    TextView successPhone;
//    @Bind(R.id.successCoach)
//    TextView successCoach;
//    @Bind(R.id.successLesson)
//    TextView successLesson;
//    @Bind(R.id.successDate)
//    TextView successDate;
    public Context mContext;
    int needclerk;
    public int usernum=1,userID;
    private String memberUID,coachUID,checkUID;
    private String TAG="EliminateLessonFragment" ;
    private MediaPlayer mediaPlayer;
    UserResponse userResponse;
    public Handler mHandler1;
    public UserLessonContract presenter;
    private String phoneNum, deviceID, veinFingerID;
    public static EliminateLessonFragment newInstance(){
        EliminateLessonFragment fragment= new EliminateLessonFragment();
        Bundle args=new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setUsernum(int usernum) {
        this.usernum = usernum;
    }

    public int getUsernum() {
        return usernum;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                deviceID = Utils.getCurrentDeviceID();
                phoneNum = "6991";
                veinFingerID = "IuqINyc2RVzlFoN50OAtont7aMLHK7dUDZFw2iALkiHUl8p2yna+MpLdF0KChyevTbJk8zXAnL3yBkdzhGZ39pW20x9AIcTncN1840KuCJU7aL4My1Yj25RuEZ1PTVMRZD+zDWzZ6ts0oWP+WvirmtpY5KVatmvUokJsmIkeoZl7cvQA/USyYrLFv43n3e5E5zOQN7nbd6nMqwkjSsVO1jNroaoRE9Bgj/uTECU1uhaHQMcrCHuJ1wcSPky4/yOaryPVnMZVxTVcy2y1dzZgo6O7C1cucIegLk+LQUYciGCZyNt1p43tJzpDKmKpmSYOdPvTcuxFVQBmyZvszbf9DtfJ6m9rasBkSqJ895HXZ6ZxrLa+UCrmBUIKUtLex2AbVhOGXPEO3sL1nb5lU9xz2wiRiB5G5JSbma3Wv5F+xhZOtWw857JZHCcE4QFF8N4DywtUt8rboUsac5fD0YKTqO/7RozgU0fvCRT/lKPfJcOG54EL7Hi26nbJwMI1lFs6bM0IBnD8+Gpzt2Zyema2m7ohCVioI+xqfrcWgy0K21i1lVsHV9kWO8GPeeLlObQTwrTgzCRnnbLfZe6PpK6TapkvygpZLa3/XLH+YV7BRri7F99yRZQ4iLfgk8rQh62uEaet0Y2FybDPnlXe2poQxMPkMHw+O8saMwVQcWwiA5o=";
                EliminateLessonFragment.this.showProgress(true, false, "请稍后");
                //签到需要查找会员信息
//                SignInFragment.this.presenter.getMemberInfoByVein(phoneNum, deviceID, veinFingerID);
            } else if (msg.what == 1) {

                String errorMsg = (String) msg.obj;
                if (Utils.isEmpty(errorMsg)) {
                    //失败页面展示
                    errorMsg = "签到失败，请重新绑定会员或注册新会员！";
                } else {
//                    failureImage.setVisibility(View.VISIBLE);
//                    tvTips.setText(errorMsg);
//                    failureImage.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            tipImageView.setVisibility(View.VISIBLE);
//                            failureImage.setVisibility(View.GONE);
//                            presenter.getVerifyTemplate();
//                        }
//                    });
                }
            }
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this.getContext();
    }

    @Override
    protected void initData() {
        presenter = new UserLessonContract();
        this.presenter.attachView(this);
        setUserID(1);
    }

    @Override
    protected void initListeners() {

    }

    @Override
    public void selectLessonSuccess(LessonResponse lessonResponse) {

    }

    @Override
    protected void initViews(View self, Bundle savedInstanceState) {

    }

    @Override
    protected void onInvisible() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_eliminatelesson_test;
    }
    @Override
    protected void onVisible() {
    }

    @Override
    public void verifyUserEliminateSuccess(UserResponse userResponse) {
//        eliminatetemp.setVisibility(View.GONE);
//        eliminateuser.setVisibility(View.VISIBLE);
//        this.showProgress(false);
//        Logger.e("EliminateLessonFragment---userResponse.getUserTyper"+userResponse.getUserTyper());
//        if (userResponse.getUserTyper()==1){
//            tvuserTyper.setText("教练");
//            coachUID=userResponse.getUid();
//        }else if (userResponse.getUserTyper()==2){
//            tvuserTyper.setText("会员");
//            memberUID=userResponse.getUid();
//        }else if (userResponse.getUserTyper()==3){
//            tvuserTyper.setText("员工");
//            checkUID=userResponse.getUid();
//        }
//        btnext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (tvuserTyper.getText()=="教练"){
//                    eliminateuser.setVisibility(View.GONE);
//                    eliminatetemp.setVisibility(View.VISIBLE);
//                    setUsernum(2);
//                    presenter.getVerifyTemplate();
//                    tvTips.setText("请会员放置手指");
//                    if (needclerk==0){
//                        btnext.setText("确定");
//                    }
//                }else if (tvuserTyper.getText()=="会员"){
//                    if (needclerk==0){
//                                presenter.eliminateLesson(Utils.getCurrentDeviceID(),memberUID,coachUID,checkUID);
//                        setUsernum(1);
//                        Logger.e("EliminateLessonFragment"+"DeviceID():"+Utils.getCurrentDeviceID()+"memberUID:"+memberUID+"coachUID:"+coachUID);
//                    }else if (needclerk==1){
//                        tvTips.setText("请员工放置手指");
//                        setUsernum(3);
//                        presenter.getVerifyTemplate();
//                    }
//                }else if (tvuserTyper.getText()=="员工"){
//                    setUsernum(1);
//                    presenter.eliminateLesson(Utils.getCurrentDeviceID(),memberUID,coachUID,checkUID);
//                    Logger.e("EliminateLessonFragment"+"memberUID"+memberUID+"coachUID"+coachUID+"checkUID"+checkUID);
//                }
//            }
//        });
//        tvMemberName.setText(userResponse.getName());
//        tvMemberPhone.setText(userResponse.getPhone());
//        tvmemberSex.setText(userResponse.getSex());
//        needclerk=userResponse.getNeedclerk();
//        ((EliminateLessonMainFragment) getParentFragment()).viewPager.setCurrentItem(0);
//        Logger.e("EliminateLessonFragment:verifyUserEliminateSuccess"+userResponse.toString());
    }
//    @Override
//    public void eliminateSuccess(LessonResponse lessonResponse) {
//        eliminatetemp.setVisibility(View.GONE);
//        eliminateuser.setVisibility(View.GONE);
//        successeliminate.setVisibility(View.VISIBLE);
//        this.showProgress(false);
//        successName.setText(lessonResponse.getMembername());
//        successPhone.setText(lessonResponse.getMemberphone());
//        successCoach.setText(lessonResponse.getCoach());
//        successLesson.setText(lessonResponse.getLessonName());
//        successDate.setText(lessonResponse.getLessonDate());
////        Logger.e("EliminateLessonFragment"+lessonResponse.toString());
////        EliminateLessonInfo memberInfoFragment = EliminateLessonInfo.newInstance(lessonResponse);
////        ((EliminateLessonMainFragment) getParentFragment()).addFragment(memberInfoFragment, 1);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                ((EliminateLessonMainFragment) getParentFragment()).viewPager.setCurrentItem(0);
//                eliminatetemp.setVisibility(View.VISIBLE);
//                failureImage.setVisibility(View.VISIBLE);
//                tvTips.setText("请按图示放置手指");
//                eliminateuser.setVisibility(View.GONE);
//                successeliminate.setVisibility(View.GONE);
//            }
//        },10000);
//    }

    @Override
    public void onPermissionError(ApiException e) {
        onError(e);
    }

    @Override
    public void onResultError(ApiException e) {
        onError(e);
    }

    //获取验证模板成功
    @Override
    public void verifyTemplateSuccess(String veinFingerID) {
        deviceID = Utils.getCurrentDeviceID();
        this.showProgress(true, false, "请稍后");
        this.presenter.verifyUserEliminateLesson(deviceID,getUsernum(), veinFingerID);
        Logger.e("EliminateLessonFragment:--verifyTemplateSuccess"+"deviceID"+deviceID+"getUsernum"+getUsernum()+"veinFingerID"+veinFingerID);
    }

    @Override
    public void verifyTemplateFailure(ApiException e) {
        onError(e);
    }
    //    @Override
//    public void verifyTemplateFailure(ApiException e) {
//        onError(e);
//    }

    @Override
    public void signedCodeInfo(CodeInfo codeInfo) {

    }

    @Override
    public void onError(ApiException error) {
        super.onError(error);
        if (BaseApplication.DEBUG) {
            Logger.e(error.getMessage());
            this.showToast(error.getMessage());
        }
        this.showProgress(false);
        Message msg=mHandler.obtainMessage();
        msg.what=1;
        if (!Utils.isEmpty(error.getDisplayMessage())) {
            msg.obj = error.getDisplayMessage();
        }
        mHandler.sendMessage(msg);
    }
//    @OnClick({R.id.failureImage,R.id.emtnext})
//    public void onClick(View view){
//        switch (view.getId()){
//            case R.id.failureImage:
//                this.presenter.getVerifyTemplate();
//                failureImage.setVisibility(View.GONE);
//            case R.id.tvnext:
////                Logger.e("EliminateLessonFragment---tvuserTyper"+tvuserTyper.getText().toString());
//                if (tvuserTyper.getText()=="教练"){
//                    eliminateuser.setVisibility(View.GONE);
//                    eliminatetemp.setVisibility(View.VISIBLE);
//                    setUsernum(2);
//                    this.presenter.getVerifyTemplate();
//                    tvTips.setText("请会员放置手指");
//                    presenter.getVerifyTemplate();
//                }else if (tvuserTyper.getText()=="会员"){
//                    if (needclerk==0){
//                        TestActivityManager.getInstance().getCurrentActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                //此时已在主线程中，可以更新UI了
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        presenter.eliminateLesson(Utils.getCurrentDeviceID(),memberUID,coachUID);
//                                    }
//                                }).start();
//                            }
//                        });
////                        eliminateuser.setVisibility(View.GONE);
//                        Logger.e("EliminateLessonFragment"+"DeviceID():"+Utils.getCurrentDeviceID()+"memberUID:"+memberUID+"coachUID:"+coachUID);
//                    }else {
//                        tvTips.setText("请员工放置手指");
//                        setUsernum(3);
//                        presenter.getVerifyTemplate();
//                    }
//                }else if (tvuserTyper.getText()=="员工"){
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            presenter.eliminateLesson(Utils.getCurrentDeviceID(),memberUID,coachUID,checkUID);
//                        }
//                    }).start();
//                    Logger.e("EliminateLessonFragment"+"memberUID"+memberUID+"coachUID"+coachUID+"checkUID"+checkUID);
//                }
//                break;
//        }
//    }
}
