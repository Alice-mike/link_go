package com.soonvein.cloud.contract;

import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.core.BasePresenter;
import com.soonvein.cloud.core.MvpView;

/**
 * Created by Shaozy on 2016/8/11.
 */
public class LoginTaskContract extends BasePresenter<LoginTaskContract.LoginView> {

    public interface LoginView extends MvpView {
        void onLoginSuccess();
    }

    public LoginTaskContract() {
    }

    public void login(String deviceId, String pwd) {
        this.mDataManager.reservoirUtils.refresh(Constant.KEY_DEVICE_ID, deviceId);
        this.mDataManager.reservoirUtils.refresh(Constant.KEY_PASSWORD, pwd);
        this.getMvpView().onLoginSuccess();
    }
}
