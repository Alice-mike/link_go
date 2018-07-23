package com.soonvein.cloud.fragment;

import android.app.Dialog;
import android.content.Context;
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
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.LessonResponse;
import com.soonvein.cloud.bean.Member;
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

public class MatchFingerVeinFragment extends BaseFragment implements MatchVeinTaskContract.MatchVeinView {

    @Bind(R.id.tipImageView)
    ImageView tipImageView;
    @Bind(R.id.failureImage)
    ImageView failureImage;
    @Bind(R.id.failureImageView)
    ImageView failureImageView;
    @Bind(R.id.tvTips)
    TextView tvTips;
    @Bind(R.id.count_down)
    TextView count_dowm;
    int count_time;
    private boolean flag=false;
    private static final int START_COUNTING = 1;
    private static final int COUNT_NUMBER = 30;
    public MatchVeinTaskContract presenter;
    private SignInMainFragment signInFragment;
    private PayMainFragment payFragment;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private String phoneNum, deviceID, veinFingerID, price, mark;
//    ActMainActivity mainActivity;
    MediaPlayer mediaPlayer,mediaPlayer1,mediaPlayer2;
    private MyHandler mHandler1 = new MyHandler();
    //标记是从哪里跳转到这个页面
    private int doWhat = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            flag=false;
            if (msg.what == 0) {
                deviceID = Utils.getCurrentDeviceID();
                phoneNum = "6991";
                veinFingerID = "IuqINyc2RVzlFoN50OAtont7aMLHK7dUDZFw2iALkiHUl8p2yna+MpLdF0KChyevTbJk8zXAnL3yBkdzhGZ39pW20x9AIcTncN1840KuCJU7aL4My1Yj25RuEZ1PTVMRZD" +
                        "+zDWzZ6ts0oWP+WvirmtpY5KVatmvUokJsmIkeoZl7cvQA/USyYrLFv43n3e5E5zOQN7nbd6nMqwkjSsVO1jNroaoRE9Bgj/uTECU1uhaHQMcrCHuJ1wcSPky4/yOaryPVnMZVxTVcy2y1dzZgo6O7C1cucIegLk+" +
                        "LQUYciGCZyNt1p43tJzpDKmKpmSYOdPvTcuxFVQBmyZvszbf9DtfJ6m9rasBkSqJ895HXZ6ZxrLa+UCrmBUIKUtLex2AbVhOGXPEO3sL1nb5lU9xz2wiRiB5G5JSbma3Wv5F+" +
                        "xhZOtWw857JZHCcE4QFF8N4DywtUt8rboUsac5fD0YKTqO/7RozgU0fvCRT/lKPfJcOG54EL7Hi26nbJwMI1lFs6bM0IBnD8+Gpzt2Zyema2m7ohCVioI+" +
                        "xqfrcWgy0K21i1lVsHV9kWO8GPeeLlObQTwrTgzCRnnbLfZe6PpK6TapkvygpZLa3/XLH+YV7BRri7F99yRZQ4iLfgk8rQh62uEaet0Y2FybDPnlXe2poQxMPkMHw+O8saMwVQcWwiA5o=";
                MatchFingerVeinFragment.this.showProgress(true, false, "请稍后");
                //签到需要查找会员信息
                if (doWhat == Constant.ACTION_SIGNIN) {
                    MatchFingerVeinFragment.this.presenter.signedMember(phoneNum, deviceID, veinFingerID);
                }
                //消费
                else if (doWhat == Constant.ACTION_CONSUME) {
                    MatchFingerVeinFragment.this.presenter.consumeRecord(phoneNum, deviceID, veinFingerID, price, "1", mark);
                }
            } else if (msg.what == 1) {

                String errorMsg = (String) msg.obj;
                if (Utils.isEmpty(errorMsg)) {
                    //失败页面展示
                    if (doWhat == Constant.ACTION_SIGNIN) {
                        //签到失败
                        errorMsg = "签到失败，请重新绑定会员或注册新会员！";

                    } else if (doWhat == Constant.ACTION_CONSUME) {
                        //消费
                        errorMsg = "消费失败，请重新绑定会员或注册新会员！";
                    }
                }
                //失败页面展示
                if (doWhat == Constant.ACTION_SIGNIN) {
                        //签到失败
                        tipImageView.setVisibility(View.VISIBLE);
                        failureImage.setVisibility(View.GONE);
                        failureImageView.setVisibility(View.VISIBLE);
                        tvTips.setText(errorMsg);
                        mediaPlayer1.start();
                    failureImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tipImageView.setVisibility(View.VISIBLE);
                                failureImage.setVisibility(View.GONE);
                                failureImageView.setVisibility(View.GONE);
                                MatchFingerVeinFragment.this.presenter.getVerifyTemplate();
                                tvTips.setText("获取指静脉");
                            }
                        });
//                    }
                } else if (doWhat == Constant.ACTION_CONSUME) {
                    //消费失败
                    tipImageView.setVisibility(View.VISIBLE);
                    failureImage.setVisibility(View.VISIBLE);
                    failureImageView.setVisibility(View.GONE);
                    tvTips.setText(errorMsg);
                    mediaPlayer2.start();
                    failureImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tipImageView.setVisibility(View.VISIBLE);
                            failureImage.setVisibility(View.GONE);
                            failureImageView.setVisibility(View.GONE);
                            MatchFingerVeinFragment.this.presenter.getVerifyTemplate();
                            tvTips.setText("获取指静脉");
                        }
                    });
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         mediaPlayer=MediaPlayer.create(getActivity(), R.raw.error_sign_finger);
        mediaPlayer1=MediaPlayer.create(getActivity(), R.raw.failure_sign);
        mediaPlayer2=MediaPlayer.create(getActivity(), R.raw.failure_cost);

    }

    public static MatchFingerVeinFragment newInstance(String phoneNum, int doWhat) {
        MatchFingerVeinFragment fragment = new MatchFingerVeinFragment();
        Bundle args = new Bundle();
        args.putString(Constant.EXTRAS_PHONENUM, phoneNum);
        args.putInt(Constant.EXTRAS_DOWHAT, doWhat);
        fragment.setArguments(args);
        return fragment;
    }

    public static MatchFingerVeinFragment newInstance(int doWhat, String phoneNum, String cost, String mark) {
        MatchFingerVeinFragment fragment = new MatchFingerVeinFragment();
        Bundle args = new Bundle();
        args.putInt(Constant.EXTRAS_DOWHAT, doWhat);
        args.putString(Constant.EXTRAS_PHONENUM, phoneNum);
        args.putString(Constant.EXTRAS_PRICE, cost);
        args.putString(Constant.EXTRAS_MARK, mark);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_vein_match;
    }

    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        init();
        Bundle bundle = getArguments();
        if (bundle != null) {
            doWhat = bundle.getInt(Constant.EXTRAS_DOWHAT);
            deviceID = Utils.getCurrentDeviceID();
            if (doWhat == Constant.ACTION_CONSUME) {
                phoneNum = bundle.getString(Constant.EXTRAS_PHONENUM);
                price = bundle.getString(Constant.EXTRAS_PRICE);
                mark = bundle.getString(Constant.EXTRAS_MARK);
            } else {
                phoneNum = bundle.getString(Constant.EXTRAS_PHONENUM);
            }
            tipImageView.setVisibility(View.VISIBLE);
            failureImageView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initListeners() {
    }

    @Override
    protected void initData() {
        presenter = new MatchVeinTaskContract();
        this.presenter.attachView(this);
    }

    @Override
    protected void onVisible() {
        tvTips.setText("请按图示放置手指");
        this.presenter.getVerifyTemplate();
        //测试API接口用下面的代码
        //mHandler.sendEmptyMessage(0);
    }
    private void init(){
        Message msg = mHandler1.obtainMessage();
        msg.what = START_COUNTING;
        msg.obj = COUNT_NUMBER;
        mHandler1.sendMessageDelayed(msg, 30);
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case START_COUNTING:
                    if (flag==false) {
                        int count = (int) msg.obj;
                        count_time=count;
                        if (count > 0) {
                            Message msg1 = obtainMessage();
                            msg1.what = START_COUNTING;
                            msg1.obj = count - 1;
                            sendMessageDelayed(msg1, 1000);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onInvisible() {
    }

    @Override
    public void onDestroy() {

        this.showProgress(false);
        this.presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void verifyTemplateSuccess(String veinFingerID) {
        this.showProgress(true, false, "请稍后");

        //签到需要查找会员信息
        if (doWhat == Constant.ACTION_SIGNIN) {
            this.presenter.signedMember(phoneNum, deviceID, veinFingerID);
        }
        //消费
        else if (doWhat == Constant.ACTION_CONSUME) {
            this.presenter.consumeRecord(phoneNum, deviceID, veinFingerID, price, "1", mark);
        }
    }

//    @Override
//    public void getMemberInfoSuccess(Member memberInfo) {
//        //签到需要根据指静脉和手机号码查找会员卡信息
//        if (memberInfo.getCardInfos().size() > 0) {
//            //只需要一张会员卡直接签到
//            memberInfo.setCardInfo(memberInfo.getCardInfos().get(0));
//            //签到
//            this.presenter.signedMember(memberInfo);
//        }
//    }

//    @Override
//    public void signSuccess(Member memberInfo) {
//        /*
//        this.showProgress(false);
//
//        ArrayList<CardInfo> cardInfos = memberInfo.getCardInfos();
//        for (CardInfo info : cardInfos) {
//            Logger.e(info.toString());
//        }
//        SignedInFragment fragment = SignedInFragment.newInstance(memberInfo);
//        //签到
//        ((SignInMainFragment) this.getParentFragment()).addFragment(fragment, 2);
//        */
//    }

    @Override
    public void signSuccess(SignedResponse signedResponse) {
        flag=true;
        this.showProgress(false);
        SignedInFragment fragment = SignedInFragment.newInstance(signedResponse);
        //签到
        ((SignInMainFragment) this.getParentFragment()).addFragment(fragment, 2);
    }
    @Override
    public void costSuccess(Voucher voucherInfo) {
        flag=true;
        this.showProgress(false);
        if (voucherInfo.getStatus() == 0) {
            new VoucherDialog(getContext(), voucherInfo).show();
        } else {
            if (!Utils.isEmpty(voucherInfo.getName())
                    && !Utils.isEmpty(voucherInfo.getPhone())) {
                new VoucherDialog(getContext(), voucherInfo).show();
            } else {
                ApiException e = new ApiException(new Throwable(voucherInfo.getMsg()), voucherInfo.getStatus());
                e.setDisplayMessage(voucherInfo.getMsg());
                onError(e);
            }
        }
    }
    @Override
    public void onError(ApiException e) {
        super.onError(e);
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
    class VoucherDialog extends Dialog implements View.OnClickListener {
        private Voucher voucherInfo;
        private TextView tvNickname, tvPhoneNum, tvCost, tvBalance;
//        private Button btConfilm;
        public VoucherDialog(Context context, Voucher voucherInfo) {
            super(context, R.style.customer_dialog);
            this.voucherInfo = voucherInfo;
            setContentView(R.layout.costsuccess);
            this.setCancelable(false);
            this.setCanceledOnTouchOutside(false);
            tvNickname = (TextView) findViewById(R.id.tvMemberName);
            tvPhoneNum = (TextView) findViewById(R.id.tvMemberPhone);
            tvCost = (TextView) findViewById(R.id.tvCardCost);
            tvBalance = (TextView) findViewById(R.id.tvCardBalance);
        }

        @Override
        protected void onStart() {
            super.onStart();
            tvNickname.setText(this.voucherInfo.getName());
            String phoneNum = this.voucherInfo.getPhone();
            if (phoneNum.length() == 11) {
                phoneNum = phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, phoneNum.length());
            }
            tvPhoneNum.setText(phoneNum);
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String cost = "", balance = "";
            try {
                cost = df.format(this.voucherInfo.getCost());
            } catch (Exception e) {
                cost = this.voucherInfo.getCost() + "";
            }
            try {
                balance = df.format(this.voucherInfo.getBalance());
            } catch (Exception e) {
                balance = this.voucherInfo.getBalance() + "";
            }
            tvCost.setText(cost+ " 元");
            tvBalance.setText(balance+ " 元");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((PayMainFragment) getParentFragment()).viewPager.setCurrentItem(0);
                }
            },5000);

//            speechSynthesizer.speak("本次消费"+cost+"元");
//            speechSynthesizer.speak("余额"+balance+"元");
        }

        @Override
        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.btConfilm:
//                    dismiss();
//                    ((PayMainFragment) MatchFingerVeinFragment.this.getParentFragment()).viewPager.setCurrentItem(0);
//                    break;
//            }
        }
    }
}
