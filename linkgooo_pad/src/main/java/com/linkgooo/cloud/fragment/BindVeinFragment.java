package com.soonvein.cloud.fragment;

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
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.bean.ReturnBean;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.contract.BindTaskContract;
import com.soonvein.cloud.core.BaseFragment;
import com.soonvein.cloud.utils.Utils;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.OnClick;
/**
 * 用户绑定指静脉进入该页面
 * 绑定指静脉进入该页面时需要注册两个指静脉模板绑定两根手指,流程：注册模板->验证模板数据->注册模板->验证模板数据->保存模板
 * 两次验证模板成功后提示绑定成功
 * <p>
 * 签到成功进入该页面只展示用户信息，不做任何处理
 */
public class BindVeinFragment extends BaseFragment implements BindTaskContract.BindView {
    @Bind(R.id.ivAvatar)
    ImageView ivAvatar;
    @Bind(R.id.tvNickname)
    TextView tvNickname;
    @Bind(R.id.tvPhoneNum)
    TextView tvPhoneNum;
    @Bind(R.id.tvSex)
    TextView tvSex;
    @Bind(R.id.tvTips)
    TextView tvTips;
    @Bind(R.id.bindHintIImage)
    ImageView bindHintIImage;
    @Bind(R.id.bindsuccessImage)
    ImageView bingSuccessImage;
    @Bind(R.id.bindFailureImage)
    ImageView bindFailureImage;
    private String deviceID;
    private Member mMemberInfo;
    public BindTaskContract presenter;
    private static int MAXT_FINGER = 3;//表示注册几个指静脉模版
        Handler mHandler;
        MediaPlayer mediaPlayer0,mediaPlayer1,mediaPlayer2,mediaPlayer3;
    public static BindVeinFragment newInstance(Member memberInfo) {
        BindVeinFragment fragment = new BindVeinFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constant.EXTRAS_MEMBER, memberInfo);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer0=MediaPlayer.create(getActivity(),R.raw.error_acquire);//采集错误
        mediaPlayer1=MediaPlayer.create(getActivity(),R.raw.error_finger);//请放置同一根手指
        mediaPlayer2=MediaPlayer.create(getActivity(),R.raw.no_finger_check);//未检测到手指
        mediaPlayer3=MediaPlayer.create(getActivity(),R.raw.no_move_finger);//未移开手指
        mHandler=new Handler();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bind_vein;
    }
    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMemberInfo = (Member) bundle.getSerializable(Constant.EXTRAS_MEMBER);
            deviceID = Utils.getCurrentDeviceID();
            tvNickname.setText(mMemberInfo.getName());
            String phoneNum = mMemberInfo.getPhone();
            if (phoneNum.length() == 11) {
                phoneNum = phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, phoneNum.length());
            }
            tvPhoneNum.setText(phoneNum);
            tvSex.setText(mMemberInfo.getSex());
            bindHintIImage.setVisibility(View.VISIBLE);
            bindHintIImage.setImageLevel(0);
            bindFailureImage.setVisibility(View.GONE);
            bingSuccessImage.setVisibility(View.GONE);
            tvTips.setText("");
        }
    }
    @Override
    protected void initListeners() {
    }
    @Override
    protected void initData() {
        presenter = new BindTaskContract();
        this.presenter.attachView(this);
    }
    @Override
    protected void onVisible() {
        //注册需要初始化指静脉模块

        nextOperate();
    }
    @Override
    protected void onInvisible() {
    }
    private void nextOperate() {

    }
    private void bindVeinMemeber() {
        }
    @OnClick({R.id.bindFailureImage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bindFailureImage:
                bindFailureImage.setVisibility(View.GONE);
                nextOperate();
                break;
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
    public void onDestroy() {
        this.showProgress(false);
        this.presenter.detachView();
        this.presenter = null;
        super.onDestroy();
        if (mediaPlayer3!=null) {
            mediaPlayer3.stop();
            mediaPlayer3.release();
            mediaPlayer3 = null;
        }else if (mediaPlayer0!=null) {
            mediaPlayer0.stop();
            mediaPlayer0.release();
            mediaPlayer0 = null;
        }else if (mediaPlayer1!=null) {
            mediaPlayer1.stop();
            mediaPlayer1.release();
        }
        else if (mediaPlayer2!=null) {
            mediaPlayer2.stop();
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
    }

    @Override
    public void handleTips(String tips) {
        tvTips.setText(tips);
    }

    @Override
    public void bindSuccess(ReturnBean returnBean) {
        if (BaseApplication.DEBUG)
            Logger.e("绑定手指静脉成功:" + returnBean);
        this.showProgress(false);
        bindHintIImage.setImageLevel(3);
        bingSuccessImage.setVisibility(View.VISIBLE);
        tvTips.setText("绑定成功");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((BindVeinMainFragment) getParentFragment()).bindViewPager.setCurrentItem(0);
            }
        },5000);
    }
    @Override
    public void onError(ApiException e) {
        super.onError(e);
        this.showProgress(false);
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
        bindFailureImage.setVisibility(View.VISIBLE);
        tvTips.setText(errorMsg);
    }
    @Override
    public void onPermissionError(ApiException e) {
        onError(e);
    }

    @Override
    public void onResultError(ApiException e) {
        onError(e);
    }

}
