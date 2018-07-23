package com.link.cloud.contract;

import com.link.cloud.base.AbsAPICallback;
import com.link.cloud.base.ApiException;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.RestResponse;
import com.link.cloud.core.BasePresenter;
import com.link.cloud.core.MvpView;
import com.link.cloud.utils.ReservoirUtils;
import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2017/9/11.
 */

public class SendLogMessage extends BasePresenter<SendLogMessage.sendLog> {

    public interface sendLog extends MvpView {
        void sendLogSuccess(RestResponse resultResponse);
    }
    public ReservoirUtils reservoirUtils;
    public SendLogMessage() {
        this.reservoirUtils = new ReservoirUtils();
    }
    public void sendLog(String deviceId, String uid,String uids,String feature,String time,String scope, String result){
        this.mCompositeSubscription.add(this.mDataManager.sendLogMessage(deviceId,uid,uids,feature,time,scope,result)
                .subscribe(new AbsAPICallback<RestResponse>() {
                    @Override
                    public void onCompleted() {
                        if (SendLogMessage.this.mCompositeSubscription != null)
                            SendLogMessage.this.mCompositeSubscription.remove(this);
                    }
                    @Override
                    protected void onError(ApiException e) {
//                        Logger.e("VersoinUpdateContract onError"+e.getMessage());
                        SendLogMessage.this.getMvpView().onError(e);
                    }
                    @Override
                    protected void onPermissionError(ApiException e) {
                        Logger.e("VersoinUpdateContract onPermissionError"+e.getMessage());
                        SendLogMessage.this.getMvpView().onPermissionError(e);
                    }
                    @Override
                    protected void onResultError(ApiException e) {
                        Logger.e("VersoinUpdateContract onResultError"+e.getMessage());
                        SendLogMessage.this.getMvpView().onResultError(e);
                    }
                    @Override
                    public void onNext(RestResponse resultResponse) {
//                        Logger.e("VersoinUpdateContract"+deviceData.toString());
                        SendLogMessage.this.getMvpView().sendLogSuccess(resultResponse);
                    }
                }));
    }

}
