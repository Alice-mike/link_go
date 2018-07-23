package com.soonvein.cloud.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.baidu.tts.client.SpeechSynthesizer;、
import com.soonvein.cloud.ActMainActivity;
import com.soonvein.cloud.R;
import com.soonvein.cloud.bean.SignedResponse;
import com.soonvein.cloud.bean.Voucher;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.core.BaseFragment;
import com.soonvein.cloud.utils.Utils;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CostInFragment extends BaseFragment {

    @Bind(R.id.tvMemberName)
    TextView tvMenberName;
    @Bind(R.id.tvMemberPhone)
    TextView tvMemberPhone;
    @Bind(R.id.tvCardCost)
    TextView tvCardCost;
    @Bind(R.id.tvCardBalance)
    TextView tvCardBalance;
//ActMainActivity mainActivity;
    private Voucher voucher;
//SpeechSynthesizer speechSynthesizer;
    public static CostInFragment newInstance(Voucher voucher) {
        CostInFragment fragment = new CostInFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constant.EXTRAS_SIGNED_INFO, voucher);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.costsuccess;
    }

    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            voucher = (Voucher) bundle.getSerializable(Constant.EXTRAS_SIGNED_INFO);
                tvMenberName.setText(voucher.getName());
                String phoneNum = voucher.getPhone();
            if (phoneNum.length() == 11) {
                phoneNum = phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, phoneNum.length());
            }
            tvMemberPhone.setText(phoneNum);
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String cost = "", balance = "";
            try {
                cost = df.format(this.voucher.getCost());
            } catch (Exception e) {
                cost = this.voucher.getCost() + "";
            }
            try {
                balance = df.format(this.voucher.getBalance());
            } catch (Exception e) {
                balance = this.voucher.getBalance() + "";
            }
            tvCardCost.setText(" "+cost+" 元");
            tvCardBalance.setText(" "+balance+" 元");
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
