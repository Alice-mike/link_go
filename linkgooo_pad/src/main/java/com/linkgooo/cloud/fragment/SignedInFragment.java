package com.soonvein.cloud.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.baidu.tts.client.SpeechSynthesizer;
import com.soonvein.cloud.R;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.core.BaseFragment;

import com.soonvein.cloud.bean.SignedResponse;
import com.soonvein.cloud.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignedInFragment extends BaseFragment {
    @Bind(R.id.employeeLayout)
    LinearLayout employeeLayout;
    @Bind(R.id.ivEmployeeAvatar)
    ImageView ivEmployeeAvatar;
    @Bind(R.id.tvEmployeeName)
    TextView tvEmployeeName;
    @Bind(R.id.tvEmployeeSex)
    TextView tvEmployeeSex;
    @Bind(R.id.tvEmployeePosition)
    TextView tvEmployeePosition;
    @Bind(R.id.tvEmployeePhoneNum)
    TextView tvEmployeePhoneNum;
    @Bind(R.id.memberLayout1)
    LinearLayout memberLayout1;
    @Bind(R.id.memberLayout2)
    LinearLayout memberLayout2;
    @Bind(R.id.ivMemberAvatar)
    ImageView ivMemberAvatar;
    @Bind(R.id.tvCardBalanceQuantity)
    TextView tvCardBalanceQuantity;
    @Bind(R.id.tvExpiry)
    TextView tvExpiry;
    @Bind(R.id.tvMemberName)
    TextView tvMemberName;
    @Bind(R.id.tvMemberPhoneNum)
    TextView tvMemberPhoneNum;
    @Bind(R.id.tvCardType)
    TextView tvCardType;
    @Bind(R.id.tvCardBalance)
    TextView tvCardBalance;
    @Bind(R.id.tvDeviceName)
    TextView tvDeviceName;
    @Bind(R.id.tvCaseNo)
    TextView tvCaseNo;
    @Bind(R.id.cardTypeLayout)
    LinearLayout cardTypeLayout;
    @Bind(R.id.cardBalanceLayout)
    LinearLayout cardBalanceLayout;
    @Bind(R.id.cardBalanceQuantityLayout)
    LinearLayout cardBalanceQuantityLayout;
    private SignedResponse signedInfo;
    public Handler handler=new Handler();
    public static SignedInFragment newInstance(SignedResponse signedResponse) {
        SignedInFragment fragment = new SignedInFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constant.EXTRAS_SIGNED_INFO, signedResponse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_signedin_info;
    }

    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            signedInfo = (SignedResponse) bundle.getSerializable(Constant.EXTRAS_SIGNED_INFO);
            if (Utils.isEmpty(signedInfo.getPosition())) {
                //没有职位信息表示会员签到
                employeeLayout.setVisibility(View.GONE);
                memberLayout1.setVisibility(View.VISIBLE);
                memberLayout2.setVisibility(View.VISIBLE);

                tvMemberName.setText(signedInfo.getName());
                String phoneNum = signedInfo.getPhone();
                if (phoneNum.length() == 11) {
                    phoneNum = phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, phoneNum.length());
                }
                tvMemberPhoneNum.setText(phoneNum);
                //卡类型
                if (Utils.isEmpty(signedInfo.getCardType())) {
                    cardTypeLayout.setVisibility(View.GONE);
                } else {
                    tvCardType.setText(signedInfo.getCardType());
                    cardTypeLayout.setVisibility(View.VISIBLE);
                }
                //卡余额
                if (Utils.isEmpty(signedInfo.getCardBalance())) {
                    cardBalanceLayout.setVisibility(View.GONE);
                } else {
                    tvCardBalance.setText(signedInfo.getCardBalance());
                    cardBalanceLayout.setVisibility(View.VISIBLE);
                }

                tvDeviceName.setText(signedInfo.getDeviceName());

                tvCaseNo.setText(signedInfo.getCabinetNumber());

                //卡剩余次数
                if (Utils.isEmpty(signedInfo.getRemainder())) {
                    cardBalanceQuantityLayout.setVisibility(View.GONE);
                } else {
                    tvCardBalanceQuantity.setText(signedInfo.getRemainder());
                    cardBalanceQuantityLayout.setVisibility(View.VISIBLE);
                }
                //卡有效截止日期
                String endTime = signedInfo.getEndTime();
                if (Utils.isEmpty(endTime)) {
                    tvExpiry.setText("不限时间");
                } else {
                    tvExpiry.setText("至" + Utils.stringPattern(endTime, "yyyy-MM-dd", "yyyy年MM月dd日"));
                }
            } else {
                //有职位信息表示员工签到
                employeeLayout.setVisibility(View.VISIBLE);
                memberLayout1.setVisibility(View.GONE);
                memberLayout2.setVisibility(View.GONE);

                tvEmployeeName.setText(signedInfo.getName());
                tvEmployeeSex.setText(signedInfo.getSex());
                tvEmployeePosition.setText(signedInfo.getPosition());
                String phoneNum = signedInfo.getPhone();
                if (phoneNum.length() == 11) {
                    phoneNum = phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, phoneNum.length());
                }
                tvEmployeePhoneNum.setText(phoneNum);
            }
        }
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onVisible() {

    }

    @Override
    protected void onInvisible() {
    }


    @Override
    public void onDestroy() {
        this.showProgress(false);
        super.onDestroy();
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
