package com.soonvein.cloud.contract;

import android.support.v4.util.Pair;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.base.AbsAPICallback;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.LessonResponse;
import com.soonvein.cloud.bean.SignedResponse;
import com.soonvein.cloud.bean.Voucher;
import com.soonvein.cloud.core.BasePresenter;
import com.soonvein.cloud.core.MvpView;
import com.soonvein.cloud.utils.RxUtils;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/11/29.
 */

public class GetVerifyTemplate extends BasePresenter <GetVerifyTemplate.VerifyTemplate>{
    public interface VerifyTemplate extends MvpView {
        void verifyTemplateSuccess(String veinFingerID);
        void verifyTemplateFailure(ApiException e);
//        void getMemberInfoSuccess(Member memberInfo);
        void eliminateSuccess(LessonResponse lessonResponse);
//        void signSuccess(Member memberInfo);
    }
    public void eliminateLesson(String deviceID,String type,String memberID, String coachID, String clerkID){
        this.mCompositeSubscription.add(this.mDataManager.eliminateLesson(deviceID,type,memberID,coachID,clerkID)
                .subscribe(new AbsAPICallback<LessonResponse>() {
                    @Override
                    public void onCompleted() {
                        if (GetVerifyTemplate.this.mCompositeSubscription != null)
                            GetVerifyTemplate.this.mCompositeSubscription.remove(this);
                    }
                    @Override
                    protected void onError(ApiException e) {
                        GetVerifyTemplate.this.getMvpView().onError(e);
                        Logger.e("eliminateLesson===onError"+e.getMessage());
                    }
                    @Override
                    protected void onPermissionError(ApiException e) {
                        GetVerifyTemplate.this.getMvpView().onPermissionError(e);
                        Logger.e("eliminateLesson===onPermissionError"+e.getMessage());
                    }
                    @Override
                    protected void onResultError(ApiException e) {
                        GetVerifyTemplate.this.getMvpView().onResultError(e);
                        Logger.e("eliminateLesson===onResultError"+e.getMessage());
                    }
                    @Override
                    public void onNext(LessonResponse lesson) {
                        GetVerifyTemplate.this.getMvpView().eliminateSuccess(lesson);
                        Logger.e("eliminateLesson"+lesson.toString());
                    }
                }));
    }
    public void getVerifyTemplate() {
        this.mCompositeSubscription.add(this.mDataManager.initSDK()
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Long, Observable<Pair<ApiException, String>>>() {
                    @Override
                    public Observable<Pair<ApiException, String>> call(Long result) {
                        return Observable.create(new Observable.OnSubscribe<Pair<ApiException, String>>() {
                            @Override
                            public void call(Subscriber<? super Pair<ApiException, String>> subscriber) {
                                subscriber.onNext(GetVerifyTemplate.this.mDataManager.getVerifyTemplate());
                                subscriber.onCompleted();
                            }
                        }).compose(RxUtils.applyIOToMainThreadSchedulers());
                    }
                })
                .subscribe(new Subscriber<Pair<ApiException, String>>() {
                    @Override
                    public void onCompleted() {
                        if (GetVerifyTemplate.this.mCompositeSubscription != null)
                            GetVerifyTemplate.this.mCompositeSubscription.remove(this);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("MatchVeinTaskContract----onError");
                        ApiException apiException = new ApiException(e, ApiException.MATCH_TEMPLATE_ERROR);
                        apiException.setDisplayMessage("获取验证模板失败！");
                        GetVerifyTemplate.this.getMvpView().onError(apiException);
                    }

                    @Override
                    public void onNext(Pair<ApiException, String> result) {
                        Logger.e("MatchVeinTaskContract----onNext");
                        if (result.first != null) {
                            GetVerifyTemplate.this.getMvpView().verifyTemplateFailure(result.first);
                        } else {
                            GetVerifyTemplate.this.getMvpView().verifyTemplateSuccess(result.second);
                        }
                    }
                }));
    }
}
