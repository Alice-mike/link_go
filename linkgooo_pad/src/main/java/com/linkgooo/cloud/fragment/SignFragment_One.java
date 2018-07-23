package com.soonvein.cloud.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.BaseApplication;
import com.soonvein.cloud.BuildConfig;
import com.soonvein.cloud.R;
import com.soonvein.cloud.VerificationCodeEditText;
import com.soonvein.cloud.activity.BindAcitvity;
import com.soonvein.cloud.activity.CallBackValue;
import com.soonvein.cloud.activity.SigeActivity;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.bean.SignedResponse;
import com.soonvein.cloud.bean.Voucher;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.contract.MatchVeinTaskContract;
import com.soonvein.cloud.contract.RegisterTaskContract;
import com.soonvein.cloud.core.BaseFragment;
import com.soonvein.cloud.utils.Utils;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SignFragment_One extends BaseFragment{
    @Bind(R.id.phone_sign_one)
    VerificationCodeEditText phoneNumView;
    @Bind(R.id.sign_keypad_0)
    Button number0;
    @Bind(R.id.sign_keypad_1)
    Button number1;
    @Bind(R.id.sign_keypad_2)
    Button number2;
    @Bind(R.id.sign_keypad_3)
    Button number3;
    @Bind(R.id.sign_keypad_4)
    Button number4;
    @Bind(R.id.sign_keypad_5)
    Button number5;
    @Bind(R.id.sign_keypad_6)
    Button number6;
    @Bind(R.id.sign_keypad_7)
    Button number7;
    @Bind(R.id.sign_keypad_8)
    Button number8;
    @Bind(R.id.sign_keypad_9)
    Button number9;
    @Bind(R.id.sign_keypad_delect)
    Button delect;
    @Bind(R.id.sign_keypad_ok)
    Button reset;
    private Context context;
    public MatchVeinTaskContract presenter;
    private String phoneNum, deviceID, veinFingerID, price, mark;
    private int doWhat = 0;
    private boolean flag=false;
    byte[] aByte={0};
    int action;
    public SigeActivity activity;
    private SharedPreferences userInfo;
    public static CallBackValue callBackValue;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=(SigeActivity) activity;
        callBackValue=(CallBackValue) activity;
    }
    public static SignFragment_One newInstance() {
        SignFragment_One fragment = new SignFragment_One();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=activity.getApplicationContext();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.sign_phone;
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
        phoneNum=phoneNumView.getText().toString();
        userInfo = activity.getSharedPreferences("user_info", 0);
        deviceID = userInfo.getString("DeviceID", "");

    }
    @Override
    protected void onVisible() {
    }
    @Override
    protected void onInvisible() {
    }
    @OnClick({R.id.sign_keypad_0,R.id.sign_keypad_1,R.id.sign_keypad_2,R.id.sign_keypad_3,R.id.sign_keypad_4,R.id.sign_keypad_5,R.id.sign_keypad_6,
            R.id.sign_keypad_7,R.id.sign_keypad_8,R.id.sign_keypad_9,R.id.sign_keypad_delect,R.id.sign_keypad_ok})
    public void onClick(View view){
        phoneNum=phoneNumView.getText().toString();
        int index = phoneNumView.getSelectionStart();
        Editable editable = phoneNumView.getText();
        switch (view.getId()) {
            case R.id.sign_keypad_0:
                editable.insert(index, "0");
                break;
            case R.id.sign_keypad_1:
                editable.insert(index, "1");
                break;
            case R.id.sign_keypad_2:
                editable.insert(index, "2");
                break;
            case R.id.sign_keypad_3:
                editable.insert(index, "3");
                break;
            case R.id.sign_keypad_4:
                editable.insert(index, "4");
                break;
            case R.id.sign_keypad_5:
                editable.insert(index, "5");
                break;
            case R.id.sign_keypad_6:
                editable.insert(index, "6");
                break;
            case R.id.sign_keypad_7:
                editable.insert(index, "7");
                break;
            case R.id.sign_keypad_8:
                editable.insert(index, "8");
                break;
            case R.id.sign_keypad_9:
                editable.insert(index, "9");
                break;
            case R.id.sign_keypad_delect:
                if (index > 0) {
                    editable.delete(index - 1, index);
                }
                break;
            case R.id.sign_keypad_ok:
                if (Utils.isEmpty(phoneNumView.getText().toString())){
                    phoneNumView.setError(getResources().getString(R.string.error_invalid_phone_4));
                }else {
                    if (phoneNumView.getText().length()==4){
                    Logger.e("SignFragment_One----phoneNum=="+phoneNum);
                    SignFragment_Two fragment = SignFragment_Two.newInstance(phoneNum, Constant.ACTION_SIGNIN);
                    ((SignInMainFragment)this.getParentFragment()).addFragment(fragment, 1);
                    callBackValue.setActivtyChange("2");}
                    else {
                        phoneNumView.setError(getResources().getString(R.string.error_invalid_phone_4));
                    }
                }
                break;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
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
