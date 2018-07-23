package com.soonvein.cloud.contract;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.base.AbsAPICallback;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.CodeInfo;
import com.soonvein.cloud.bean.UpdateMessage;
import com.soonvein.cloud.core.BasePresenter;
import com.soonvein.cloud.core.MvpView;
import com.soonvein.cloud.utils.ReservoirUtils;

/**
 * Created by Administrator on 2017/9/11.
 */

public class VersoinUpdateContract extends BasePresenter<VersoinUpdateContract.VersoinUpdate> {

    public interface VersoinUpdate extends MvpView {
        void updateVersoin(UpdateMessage updateMessage);
    }
    public ReservoirUtils reservoirUtils;

    public VersoinUpdateContract() {
        this.reservoirUtils = new ReservoirUtils();
    }
    public void deviceUpgrade(String deviceID){
        this.mCompositeSubscription.add(this.mDataManager.deviceUpgrade(deviceID)
                .subscribe(new AbsAPICallback<UpdateMessage>() {
                    @Override
                    public void onCompleted() {
                        if (VersoinUpdateContract.this.mCompositeSubscription != null)
                            VersoinUpdateContract.this.mCompositeSubscription.remove(this);
                    }
                    @Override
                    protected void onError(ApiException e) {
                        Logger.e("VersoinUpdateContract onError"+e.getMessage());

                        VersoinUpdateContract.this.getMvpView().onError(e);
                    }
                    @Override
                    protected void onPermissionError(ApiException e) {
                        Logger.e("VersoinUpdateContract onPermissionError"+e.getMessage());

                        VersoinUpdateContract.this.getMvpView().onPermissionError(e);
                    }
                    @Override
                    protected void onResultError(ApiException e) {
                        Logger.e("VersoinUpdateContract onResultError"+e.getMessage());
                        VersoinUpdateContract.this.getMvpView().onResultError(e);
                    }
                    @Override
                    public void onNext(UpdateMessage updateMessage) {
                        Logger.e("VersoinUpdateContract"+updateMessage.toString());
                        VersoinUpdateContract.this.getMvpView().updateVersoin(updateMessage);
                    }
                }));
    }
}
