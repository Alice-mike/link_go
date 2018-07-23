package com.link.cloud.contract;

import com.link.cloud.base.AbsAPICallback;
import com.link.cloud.base.ApiException;
import com.link.cloud.bean.ResultResponse;
import com.link.cloud.core.BasePresenter;
import com.link.cloud.core.MvpView;
import com.link.cloud.utils.ReservoirUtils;
import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2017/9/11.
 */

public class DeviceHeartBeat extends BasePresenter<DeviceHeartBeat.Devicehearbeat> {

    public interface Devicehearbeat extends MvpView {
        void deviceHearBeat(DeviceHeartBeat deviceHeartBeat);
    }
    public ReservoirUtils reservoirUtils;

    public DeviceHeartBeat() {
        this.reservoirUtils = new ReservoirUtils();
    }

    public void deviceUpgrade(String deviceID){
        this.mCompositeSubscription.add(this.mDataManager.deviceHeartBeat(deviceID)
                .subscribe(new AbsAPICallback<DeviceHeartBeat>() {
                    @Override
                    public void onCompleted() {
                        if (DeviceHeartBeat.this.mCompositeSubscription != null)
                            DeviceHeartBeat.this.mCompositeSubscription.remove(this);
                    }
                    @Override
                    protected void onError(ApiException e) {
                        Logger.e("VersoinUpdateContract onError"+e.getMessage());

                        DeviceHeartBeat.this.getMvpView().onError(e);
                    }
                    @Override
                    protected void onPermissionError(ApiException e) {
                        Logger.e("VersoinUpdateContract onPermissionError"+e.getMessage());

                        DeviceHeartBeat.this.getMvpView().onPermissionError(e);
                    }
                    @Override
                    protected void onResultError(ApiException e) {
                        Logger.e("VersoinUpdateContract onResultError"+e.getMessage());
                        DeviceHeartBeat.this.getMvpView().onResultError(e);
                    }
                    @Override
                    public void onNext(DeviceHeartBeat deviceHeartBeat) {
//                        Logger.e("VersoinUpdateContract"+updateMessage.toString());
                        DeviceHeartBeat.this.getMvpView().deviceHearBeat(deviceHeartBeat);
                    }
                }));
    }

}
