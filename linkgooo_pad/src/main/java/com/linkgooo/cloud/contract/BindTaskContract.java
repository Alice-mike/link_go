package com.soonvein.cloud.contract;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.util.Pair;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.BaseApplication;
import com.soonvein.cloud.R;
import com.soonvein.cloud.TestActivityManager;
import com.soonvein.cloud.base.AbsAPICallback;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.ReturnBean;
import com.soonvein.cloud.core.BasePresenter;
import com.soonvein.cloud.core.MvpView;
import com.soonvein.cloud.utils.ReservoirUtils;
import com.soonvein.cloud.utils.RxUtils;
import com.soonvein.cloud.utils.Utils;


import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Shaozy on 2016/8/11.
 */
public class BindTaskContract extends BasePresenter<BindTaskContract.BindView> {

    final int REGISTER_TEMPLATE = 0x001;
    final int SAVE_TEMPLATE = 0x002;
    final int VERIFY_TEMPLATE = 0x003;
//    final  static int MOVE_FINGER=0x004;//移开手指常量
    final static int PUT_FINGER=0x005;//放入手指常量
    long start=0,endtime=0;
    BaseApplication baseApplication;
    private MediaPlayer mediaPlayer0,mediaPlayer1;
    public interface BindView extends MvpView {
        void handleTips(String tips);


        void bindSuccess(ReturnBean returnBean) throws InterruptedException;
    }

    public ReservoirUtils reservoirUtils;
    public BindTaskContract() {
        this.reservoirUtils = new ReservoirUtils();
        mediaPlayer0=MediaPlayer.create(TestActivityManager.getInstance().getCurrentActivity(), R.raw.finger_move);
        mediaPlayer1=MediaPlayer.create(TestActivityManager.getInstance().getCurrentActivity(), R.raw.putfinger_again);

    }
    public void bindVeinMemeber(String phone, String deviceID, String veinFingerID1, String veinFingerID2, String veinFingerID3) {
        this.mCompositeSubscription.add(this.mDataManager.bindVeinMemeber(phone, deviceID, veinFingerID1, veinFingerID2, veinFingerID3)
                .subscribe(new AbsAPICallback<ReturnBean>() {
                    @Override
                    public void onCompleted() {
                        if (BindTaskContract.this.mCompositeSubscription != null)
                            BindTaskContract.this.mCompositeSubscription.remove(this);

                    }
                    @Override
                    public void onNext(ReturnBean returnBean) {
                        try {
                            BindTaskContract.this.getMvpView().bindSuccess(returnBean);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    protected void onError(ApiException e) {
                        BindTaskContract.this.getMvpView().onError(e);
                        Logger.e("BindTaskContract---onError"+e.getMessage());
                    }
                    @Override
                    protected void onPermissionError(ApiException e) {
                        BindTaskContract.this.getMvpView().onPermissionError(e);
                        Logger.e("BindTaskContract---onPermissionError"+e.getMessage());
                    }
                    @Override
                    protected void onResultError(ApiException e) {
                        BindTaskContract.this.getMvpView().onResultError(e);
                        Logger.e("BindTaskContract---onResultError"+e.getMessage());
                    }
                }));

    }

    @Override
    public void detachView() {
        super.detachView();
        if (mediaPlayer0!=null) {
            mediaPlayer0.stop();
            mediaPlayer0.release();
            mediaPlayer0 = null;
        }else if (mediaPlayer0!=null) {
            mediaPlayer0.stop();
            mediaPlayer0.release();
            mediaPlayer0 = null;
        }else if (mediaPlayer1!=null) {
            mediaPlayer1.stop();
            mediaPlayer1.release();
        }
    }
}
