package com.soonvein.cloud.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.R;
import com.soonvein.cloud.activity.BindAcitvity;
import com.soonvein.cloud.activity.CallBackValue;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.bean.ReturnBean;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.contract.BindTaskContract;
import com.soonvein.cloud.core.BaseFragment;
import com.soonvein.cloud.utils.CleanMessageUtil;
import com.soonvein.cloud.utils.Utils;
import com.wedone.sdk.UserData;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/30.
 */

public class RegisterFragment_Two extends BaseFragment implements BindTaskContract.BindView {
    @Bind(R.id.bind_bt_back)
    Button back;
    @Bind(R.id.bind_bt_next)
    Button next;
    @Bind(R.id.bind_tv_name)
    TextView menber_name;
    @Bind(R.id.bind_tv_cartype)
    TextView cartype;
    @Bind(R.id.bind_timestart)
    TextView startTime;
    @Bind(R.id.bind_timeend)
    TextView endTime;
    @Bind(R.id.bind_tv_sex)
    TextView menber_sex;
    @Bind(R.id.bind_tv_phone)
    TextView menber_phone;
    private String deviceID;
    private Member mMemberInfo;
    private UserData regUserData;
    public BindTaskContract presenter;
    private static int MAXT_FINGER = 3;//表示注册几个指静脉模版
    Handler mHandler;
    private SharedPreferences userInfo;
    public static CallBackValue callBackValue;
    private BindAcitvity acitvity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.acitvity=(BindAcitvity) activity;
        callBackValue=(CallBackValue)activity;
    }

    public static RegisterFragment_Two newInstance(Member memberInfo) {
        RegisterFragment_Two fragment = new RegisterFragment_Two();
        Bundle args = new Bundle();
        args.putSerializable(Constant.EXTRAS_MEMBER, memberInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onError(ApiException error) {
        super.onError(error);
    }

    @Override
    public void onResultError(ApiException e) {

    }

    @Override
    public void onPermissionError(ApiException e) {

    }

    @Override
    public void registerTemplateSuccess(UserData regUserData) {

    }

    @Override
    public void saveTemplateSuccess(UserData regUserData) {

    }

    @Override
    protected void onVisible() {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    public void handleTips(String tips) {

    }

    @Override
    protected void onInvisible() {

    }

    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMemberInfo = (Member) bundle.getSerializable(Constant.EXTRAS_MEMBER);
            userInfo = acitvity.getSharedPreferences("user_info", 0);
            deviceID = userInfo.getString("DeviceID", "");
            if (mMemberInfo.cardTypeName!=null) {
                cartype.setText(mMemberInfo.cardTypeName);
                startTime.setText(mMemberInfo.beginTime+"-");
                endTime.setText(mMemberInfo.endTime);
            }
            menber_name.setText(mMemberInfo.getName());
            String phoneNum = mMemberInfo.getPhone();
            if (phoneNum.length() == 11) {
                phoneNum = phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, phoneNum.length());
            }
            menber_phone.setText(phoneNum);
            menber_sex.setText(mMemberInfo.getSex());
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.bind_menber;
    }

    @Override
    public void bindSuccess(ReturnBean returnBean) throws InterruptedException {

    }
    @OnClick({R.id.bind_bt_back, R.id.bind_bt_next})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bind_bt_back:
                if (mMemberInfo.needVerify()==true){
                    RegisterFragment_One memberInfoFragment = RegisterFragment_One.newInstance();
                    ((BindVeinMainFragment) getParentFragment()).addFragment(memberInfoFragment, 0);
                    callBackValue.setActivtyChange("1");
                }else {

                }
                break;
            case R.id.bind_bt_next:
                callBackValue.setActivtyChange("3");
                Logger.e("RegisterFragment_Two"+this.mMemberInfo.toString());
                RegisterFragment_Three memberInfoFragment2 = RegisterFragment_Three.newInstance(this.mMemberInfo);
                ((BindVeinMainFragment) getParentFragment()).addFragment(memberInfoFragment2, 2);
                break;
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
