package com.soonvein.cloud.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.ActMainActivity;
import com.soonvein.cloud.BuildConfig;
import com.soonvein.cloud.R;
import com.soonvein.cloud.base.MyToast;
import com.soonvein.cloud.core.BaseFragment;
import com.soonvein.cloud.BaseApplication;

import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.contract.RegisterTaskContract;
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

public class RegisterFragment extends BaseFragment implements RegisterTaskContract.RegisterView {

    @Bind(R.id.phoneNum)
    EditText etPhoneNum;
    @Bind(R.id.verifyCode)
    EditText etVerifyCode;
//    @Bind(R.id.next)
//    Button next;
    //    @Bind(R.id.reset)
//    Button reset;
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
    Button next;
   @Bind(R.id.getVerifyCode)
    Button getVerifyCode;
    @Bind(R.id.verifyLayout)
    LinearLayout verifyLayout;
    RegisterTaskContract registerTaskContract;
//    String TAG="RegisterFragment";
    public RegisterTaskContract presenter;
    private Subscription countDown;
    Editable editable;
    int index;
    //用户保存手机号码与验证码，获取验证码成功添加一条记录，点击下一步删除一条记录
    private static final HashSet verifySet = new HashSet();
    //记录当前用户信息
    private Member memberInfo;
    private NextAction action = NextAction.GET_MEMBER_INFO;
    private Context context;
    ActMainActivity mainActivity;
    private MediaPlayer mPlayer,mPlayer1,mPlayer2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayer=MediaPlayer.create(getActivity(),R.raw.error_phone);
        mPlayer1=MediaPlayer.create(getActivity(),R.raw.error_verify);
//        mPlayer2=MediaPlayer.create(getActivity(),R.raw.different_phone);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        mainActivity= new ActMainActivity();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    enum NextAction {
        GET_MEMBER_INFO(0),
        GET_VERIFY_CODE(1),
        REGISTER(2);
        int state = 0;
        NextAction(int state) {
            this.state = state;
        }
        public int getState() {
            return state;
        }
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
//        Logger.e("initViews set size:" + verifySet.size());
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
        etPhoneNum.setShowSoftInputOnFocus(false);
        etVerifyCode.setShowSoftInputOnFocus(false);
//        needVerify = false;
        memberInfo = null;
        etVerifyCode.setText("");
        etPhoneNum.setText("");
        action = NextAction.GET_MEMBER_INFO;
        etPhoneNum.setClickable(true);
        verifyLayout.setVisibility(View.GONE);
        getVerifyCode.setClickable(true);
        getVerifyCode.setText(R.string.get_verify_code);
        registerTaskContract=new RegisterTaskContract();
    }

    @Override
    protected void initListeners() {
        this.presenter = new RegisterTaskContract();
        this.presenter.attachView(this);
    }

    @Override
    protected void onVisible() {
        initData();
    }

    @Override
    protected void onInvisible() {
        if (BuildConfig.DEBUG)
        if (countDown != null)
            countDown.unsubscribe();
    }

    @Override
    public void sendSMSSuccess(String phoneNum, String code) {
        Verfiy verfiy = new Verfiy(phoneNum, code);
        if (verifySet.contains(verfiy)) {
            verifySet.remove(verfiy);
        }
        verifySet.add(verfiy);
        if (BuildConfig.DEBUG) {
            Logger.e("sendSMSSuccess " + verfiy + ";verifyset size:" + verifySet.size());
            this.showProgress(false, true, "短信发送成功");
            etVerifyCode.setText("");
        } else {
            this.showProgress(false, true, "短信已发送");
            etVerifyCode.setText("");
        }
        //下面赋值为了解决Fragment第一次创建的时候，setUserVisibleHint不执行的问题，导致isVisble不正确
        //倒计时方法需要用这个属性做判断
        this.isVisble = true;
        reNewCountDown();
        verifyCodeExpriedCountDown(phoneNum, code);
    }
    @Override
    public void onSuccess(Member memberInfo) {
        if (memberInfo != null) {
            this.memberInfo = memberInfo;
            this.showProgress(false);
            if (memberInfo.needVerify()) {
                this.memberInfo = memberInfo;
                //获取用户信息成功
                action = NextAction.REGISTER;
                verifyLayout.setVisibility(View.VISIBLE);
                getVerifyCode.setClickable(true);
                getVerifyCode.setText(R.string.get_verify_code);
            } else {
                BindVeinFragment memberInfoFragment = BindVeinFragment.newInstance(this.memberInfo);
                ((BindVeinMainFragment) getParentFragment()).addFragment(memberInfoFragment, 1);
            }
        } else {
            //接口成功回调，但是没有返回用户信息
            if (countDown != null)
                countDown.unsubscribe();
            this.memberInfo = null;
            this.showProgress(false, false, "接口成功回调，但是没有返回用户信息");
        }
    }
    @Override
    public void onError(ApiException e) {
        super.onError(e);
        this.showProgress(false);
        this.showProgress(false, false, e.getDisplayMessage());
    }
    @Override
    public void onPermissionError(ApiException e) {
        onError(e);
    }
    @Override
    public void onResultError(ApiException e) {
        onError(e);
    }
    @OnClick({R.id.getVerifyCode,R.id.digitkeypad_ok,R.id.digitkeypad_0,R.id.digitkeypad_1,R.id.digitkeypad_2,R.id.digitkeypad_3,R.id.digitkeypad_4,
            R.id.digitkeypad_5,R.id.digitkeypad_6,R.id.digitkeypad_7,R.id.digitkeypad_8,R.id.digitkeypad_9,R.id.digitkeypad_c})
    public void onClick(View view) {
        if (etVerifyCode.hasFocus()){
            index = etVerifyCode.getSelectionStart();
            editable = etVerifyCode.getText();
        }else{
            index = etPhoneNum.getSelectionStart();
            editable = etPhoneNum.getText();
        }
         String phoneNum, verifyCode;
        View focusView = null;
        boolean cancel = false;
        switch (view.getId()) {
            case R.id.getVerifyCode:
                phoneNum = etPhoneNum.getText().toString().trim();
                etPhoneNum.setError(null);
                cancel = false;
                focusView = null;
                if (Utils.isEmpty(phoneNum)) {
                    etPhoneNum.setError(getResources().getString(R.string.error_invalid_phone_empty));
                    focusView = etPhoneNum;
                    cancel = true;
                } else if (!Utils.isMobileNumberValid(phoneNum)) {
                    etPhoneNum.setError(getResources().getString(R.string.error_invalid_mobile_phone));
                    focusView = etPhoneNum;
                    cancel = true;
                }
              if (etPhoneNum.getText().toString().equals(memberInfo.getPhone())) {
                  if (cancel) {
                      focusView.requestFocus();
                  } else {
                      //生成4位随机验证码
                      String code = Utils.generateVerificationCode(0, true, 9999, true);
                      etVerifyCode.setText("");
                      //this.presenter.sendSMS(accountSid, phoneNum, code);
                      this.presenter.sendSMSByCcpSDK(phoneNum, code);
                  }
              }else {
                  Log.d("RegisterFragment", "=========="+memberInfo.getPhone().toString()+"----"+etPhoneNum.toString());
                  MyToast.maketext(getActivity(),"两次输入的号码不一致，请重新输入",Toast.LENGTH_LONG).show();
                  mPlayer2.start();
              }
                break;
            case R.id.digitkeypad_0:
                editable.insert(index, "0");
                break;
            case R.id.digitkeypad_1:
                editable.insert(index, "1");
                break;
            case R.id.digitkeypad_2:
                editable.insert(index, "2");
                break;
            case R.id.digitkeypad_3:
                editable.insert(index, "3");
                break;
            case R.id.digitkeypad_4:
                editable.insert(index, "4");
                break;
            case R.id.digitkeypad_5:
                editable.insert(index, "5");
                break;
            case R.id.digitkeypad_6:
                editable.insert(index, "6");
                break;
            case R.id.digitkeypad_7:
                editable.insert(index, "7");
                break;
            case R.id.digitkeypad_8:
                editable.insert(index, "8");
                break;
            case R.id.digitkeypad_9:
                editable.insert(index, "9");
                break;
            case R.id.digitkeypad_c:
                if (index>0) {
                    editable.delete(index - 1, index);
                }
                break;
            case R.id.digitkeypad_ok:
                phoneNum = etPhoneNum.getText().toString().trim();
                verifyCode = etVerifyCode.getText().toString().trim();
                Verfiy verfiy = new Verfiy(phoneNum, verifyCode);
                etPhoneNum.setError(null);
                etVerifyCode.setError(null);
                cancel = false;
                focusView = null;
                switch (action) {
                    case GET_MEMBER_INFO:
                        if (Utils.isEmpty(phoneNum) || !Utils.isMobileNumberValid(phoneNum)) {
                            etPhoneNum.setError(getResources().getString(R.string.error_invalid_mobile_phone));
                            mPlayer.start();
                            focusView = etPhoneNum;
                            cancel = true;
                            break;
                        }
                        this.showProgress(true);
//                        etPhoneNum.setClickable(false);
                        String deviceID = Utils.getCurrentDeviceID();
                        this.presenter.getMemInfo(phoneNum, deviceID);
                        break;
                    case REGISTER:
                            if (Utils.isEmpty(verifyCode)) {
                                etVerifyCode.setError(getResources().getString(R.string.error_invalid_verify_code_empty));
                                focusView = etVerifyCode;
                                cancel = true;
                                break;
                            } else if (!validateVerifyCode(verfiy)) {
                                etVerifyCode.setError(getResources().getString(R.string.error_wrong_sms_code));
                                mPlayer1.start();
                                focusView = etVerifyCode;
                                cancel = true;
                                break;
                            }
                            this.showProgress(true, false, "请稍等...");
                            verifySet.remove(verfiy);
                            BindVeinFragment memberInfoFragment = BindVeinFragment.newInstance(memberInfo);
                            ((BindVeinMainFragment) getParentFragment()).addFragment(memberInfoFragment, 1);
                        Logger.e("RegisterFragment:"+memberInfo.toString());
                }
                if (cancel) {
                    focusView.requestFocus();
                }
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        etPhoneNum.getText().clear();
        etVerifyCode.getText().clear();
        verifyLayout.setVisibility(View.GONE);
    }
    @Override
    public void onDestroy() {
        this.showProgress(false);
        this.presenter.detachView();
        super.onDestroy();
        if (mPlayer!=null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }else if (mPlayer1!=null) {
            mPlayer1.stop();
            mPlayer1.release();
            mPlayer1 = null;
        }
    }
    public boolean validateVerifyCode(Verfiy verfiy) {
        if (verifySet.contains(verfiy))
            return true;
        else
            return false;
    }
    //重新获取验证码倒计时
    public void reNewCountDown() {
        countDown = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> arg0) {
                int i = 30;
                    while (i >= 0 && RegisterFragment.this.isVisble) {
                        try {
                            Thread.sleep(1000);
                            arg0.onNext(i);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        i--;
                    }
                arg0.onCompleted();
            }
        })
                .subscribeOn(Schedulers.newThread())// 此方法为上面发出事件设置线程为新线程
                .observeOn(AndroidSchedulers.mainThread())// 为消耗事件设置线程为UI线程
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        getVerifyCode.setClickable(false);
                    }

                    @Override
                    public void onCompleted() {
                        getVerifyCode.setClickable(true);
                        getVerifyCode.setText(R.string.get_verify_code);
                    }
                    @Override
                    public void onError(Throwable arg0) {
                    }
                    @Override
                    public void onNext(Integer arg0) {
                        getVerifyCode.setText(arg0 + "秒后可再次发送");
                    }
                });
    }

    //验证码失效倒计时
    private void verifyCodeExpriedCountDown(String currentPhoneNum, String currentVerifyCode) {
        //五分钟后验证码失效
        Observable.timer(60 * 5, TimeUnit.SECONDS)
                .subscribe(aLong -> {
                    Verfiy verfiy = new Verfiy(currentPhoneNum, currentVerifyCode);
                    if (RegisterFragment.verifySet != null)
                        RegisterFragment.verifySet.remove(verfiy);
                    if (BaseApplication.DEBUG) {
                        Logger.e("verifyCodeExpriedCountDown " + verfiy + ";verify size : " + RegisterFragment.verifySet.size());
                    }
                });
    }

    class Verfiy {
        private String phone;
        private String verifyCode;

        public Verfiy(String phone, String verifyCode) {
            this.phone = phone;
            this.verifyCode = verifyCode;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o.getClass() == Verfiy.class) {
                Verfiy n = (Verfiy) o;
                return n.phone.equals(phone) && n.verifyCode.equals(verifyCode);
            }
            return false;
        }

        public int hashCode() {
            return (phone + verifyCode).hashCode();
        }

        @Override
        public String toString() {
            return "Verfiy{" +
                    "phone='" + phone + '\'' +
                    ", verifyCode='" + verifyCode + '\'' +
                    '}';
        }
    }
}
