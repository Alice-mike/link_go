package com.link.cloud.contract;

import com.link.cloud.base.AbsAPICallback;
import com.link.cloud.base.ApiException;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.ResultResponse;
import com.link.cloud.core.BasePresenter;
import com.link.cloud.core.MvpView;
import com.link.cloud.utils.ReservoirUtils;
import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2017/9/11.
 */

public class DownloadFeature extends BasePresenter<DownloadFeature.download> {

    public interface download extends MvpView {
        void downloadSuccess(DownLoadData resultResponse);
    }
    public ReservoirUtils reservoirUtils;

    public DownloadFeature() {
        this.reservoirUtils = new ReservoirUtils();
    }

    public void download(String messageId,String appid,String shopId,String deviceId,String uid){
        this.mCompositeSubscription.add(this.mDataManager.downloadFeature(messageId,appid,shopId,deviceId,uid)
                .subscribe(new AbsAPICallback<DownLoadData>() {
                    @Override
                    public void onCompleted() {
                        if (DownloadFeature.this.mCompositeSubscription != null)
                            DownloadFeature.this.mCompositeSubscription.remove(this);
                    }
                    @Override
                    protected void onError(ApiException e) {
//                        Logger.e("VersoinUpdateContract onError"+e.getMessage());
                        DownloadFeature.this.getMvpView().onError(e);
                    }
                    @Override
                    protected void onPermissionError(ApiException e) {
                        Logger.e("VersoinUpdateContract onPermissionError"+e.getMessage());
                        DownloadFeature.this.getMvpView().onPermissionError(e);
                    }
                    @Override
                    protected void onResultError(ApiException e) {
                        Logger.e("VersoinUpdateContract onResultError"+e.getMessage());
                        DownloadFeature.this.getMvpView().onResultError(e);
                    }
                    @Override
                    public void onNext(DownLoadData resultResponse) {
//                        Logger.e("VersoinUpdateContract"+deviceData.toString());
                        DownloadFeature.this.getMvpView().downloadSuccess(resultResponse);
                    }
                }));
    }

}
