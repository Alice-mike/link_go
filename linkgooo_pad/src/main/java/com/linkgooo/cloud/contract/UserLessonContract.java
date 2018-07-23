package com.soonvein.cloud.contract;

import android.support.v4.util.Pair;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.base.AbsAPICallback;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.CardInfo;
import com.soonvein.cloud.bean.CodeInfo;
import com.soonvein.cloud.bean.ResultResponse;
import com.soonvein.cloud.bean.UserResponse;
import com.soonvein.cloud.bean.LessonResponse;
import com.soonvein.cloud.core.BasePresenter;
import com.soonvein.cloud.core.MvpView;
import com.soonvein.cloud.utils.ReservoirUtils;
import com.soonvein.cloud.utils.RxUtils;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/8/2.
 */

public class UserLessonContract extends BasePresenter<UserLessonContract.UserLesson> {

    public interface UserLesson extends MvpView{
        void verifyTemplateSuccess(String veinFingerID);
        void verifyTemplateFailure(ApiException e);
        void verifyUserEliminateSuccess(UserResponse usersresponse);

//        void eliminateSuccess(LessonResponse lessonResponse);
        void selectLessonSuccess(LessonResponse lessonResponse);
        void signedCodeInfo(CodeInfo codeInfo);
    }
    public ReservoirUtils reservoirUtils;

    public UserLessonContract() {
        this.reservoirUtils = new ReservoirUtils();
    }

    public void verifyUserEliminateLesson(String deviceID,int userType,String veinfingerID){
        this.mCompositeSubscription.add(this.mDataManager.verifyUserEliminateLesson(deviceID,userType,veinfingerID)
                .subscribe(new AbsAPICallback<UserResponse>() {
                    @Override
                    public void onCompleted() {
                        if (UserLessonContract.this.mCompositeSubscription != null)
                            UserLessonContract.this.mCompositeSubscription.remove(this);
                    }
                    @Override
                    protected void onError(ApiException e) {
                        UserLessonContract.this.getMvpView().onError(e);
                    }
                    @Override
                    protected void onPermissionError(ApiException e) {
                        UserLessonContract.this.getMvpView().onPermissionError(e);
                    }
                    @Override
                    protected void onResultError(ApiException e) {
                        UserLessonContract.this.getMvpView().onResultError(e);
                    }
                    @Override
                    public void onNext(UserResponse usersMessage) {
                        UserLessonContract.this.getMvpView().verifyUserEliminateSuccess(usersMessage);
                        Logger.e("UserLessonContract"+usersMessage.toString());
                    }
                }));
    }
//    public void eliminateLesson(String deviceID,String type,String memberID, String coachID, String clerkID){
//        this.mCompositeSubscription.add(this.mDataManager.eliminateLesson(deviceID,type,memberID,coachID,clerkID)
//                .subscribe(new AbsAPICallback<LessonResponse>() {
//                    @Override
//                    public void onCompleted() {
//                        if (UserLessonContract.this.mCompositeSubscription != null)
//                            UserLessonContract.this.mCompositeSubscription.remove(this);
//                    }
//                    @Override
//                    protected void onError(ApiException e) {
//                        UserLessonContract.this.getMvpView().onError(e);
//                        Logger.e("eliminateLesson===onError"+e.getMessage());
//                    }
//                    @Override
//                    protected void onPermissionError(ApiException e) {
//                        UserLessonContract.this.getMvpView().onPermissionError(e);
//                        Logger.e("eliminateLesson===onPermissionError"+e.getMessage());
//                    }
//                    @Override
//                    protected void onResultError(ApiException e) {
//                        UserLessonContract.this.getMvpView().onResultError(e);
//                        Logger.e("eliminateLesson===onResultError"+e.getMessage());
//                    }
//                    @Override
//                    public void onNext(LessonResponse lesson) {
//                        UserLessonContract.this.getMvpView().eliminateSuccess(lesson);
//                        Logger.e("eliminateLesson"+lesson.toString());
//                    }
//                }));
//    }
    public void selectLesson (String deviceID,String type,String lessonID,String memberID,String coachID,String clerkID){
        this.mCompositeSubscription.add(this.mDataManager.selectLesson(deviceID,type,lessonID,memberID,coachID,clerkID)
                .subscribe(new AbsAPICallback<LessonResponse>() {
                    @Override
                    public void onCompleted() {
                        if (UserLessonContract.this.mCompositeSubscription != null)
                            UserLessonContract.this.mCompositeSubscription.remove(this);
                    }
                    @Override
                    protected void onError(ApiException e) {
                        UserLessonContract.this.getMvpView().onError(e);
                        Logger.e("eliminateLesson===onError"+e.getMessage());
                    }
                    @Override
                    protected void onPermissionError(ApiException e) {
                        UserLessonContract.this.getMvpView().onPermissionError(e);
                        Logger.e("eliminateLesson===onPermissionError"+e.getMessage());
                    }
                    @Override
                    protected void onResultError(ApiException e) {
                        UserLessonContract.this.getMvpView().onResultError(e);
                        Logger.e("eliminateLesson===onResultError"+e.getMessage());
                    }
                    @Override
                    public void onNext(LessonResponse lesson) {
                        UserLessonContract.this.getMvpView().selectLessonSuccess(lesson);
                        Logger.e("eliminateLesson"+lesson.toString());
                    }
                }));
    }

    public void signedCodeInfo(String deviceID){
        this.mCompositeSubscription.add(this.mDataManager.signedCodeInfo(deviceID)
                .subscribe(new AbsAPICallback<CodeInfo>() {
                    @Override
                    public void onCompleted() {
                        if (UserLessonContract.this.mCompositeSubscription != null)
                            UserLessonContract.this.mCompositeSubscription.remove(this);
                    }

                    @Override
                    protected void onError(ApiException e) {
                        UserLessonContract.this.getMvpView().onError(e);
                    }

                    @Override
                    protected void onPermissionError(ApiException e) {
                        UserLessonContract.this.getMvpView().onPermissionError(e);
                    }

                    @Override
                    protected void onResultError(ApiException e) {
                        UserLessonContract.this.getMvpView().onResultError(e);
                    }

                    @Override
                    public void onNext(CodeInfo codeInfo) {
                        UserLessonContract.this.getMvpView().signedCodeInfo(codeInfo);
                    }
                }));
    }

//    public void getVerifyTemplate() {
//        this.mCompositeSubscription.add(this.mDataManager.initSDK()
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Func1<Long, Observable<Pair<ApiException, String>>>() {
//                    @Override
//                    public Observable<Pair<ApiException, String>> call(Long result) {
//                        return Observable.create(new Observable.OnSubscribe<Pair<ApiException, String>>() {
//                            @Override
//                            public void call(Subscriber<? super Pair<ApiException, String>> subscriber) {
//                                subscriber.onNext(UserLessonContract.this.mDataManager.getVerifyTemplate());
//                                subscriber.onCompleted();
//                            }
//                        }).compose(RxUtils.applyIOToMainThreadSchedulers());
//                    }
//                })
//                .subscribe(new Subscriber<Pair<ApiException, String>>() {
//                    @Override
//                    public void onCompleted() {
//                        if (UserLessonContract.this.mCompositeSubscription != null)
//                            UserLessonContract.this.mCompositeSubscription.remove(this);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ApiException apiException = new ApiException(e, ApiException.MATCH_TEMPLATE_ERROR);
//                        apiException.setDisplayMessage("获取验证模板失败！");
//                        UserLessonContract.this.getMvpView().onError(apiException);
//                    }
//
//                    @Override
//                    public void onNext(Pair<ApiException, String> result) {
//                        if (result.first != null) {
//                            UserLessonContract.this.getMvpView().verifyTemplateFailure(result.first);
//                        } else {
//                            UserLessonContract.this.getMvpView().verifyTemplateSuccess(result.second);
//                        }
//                    }
//                }));
//    }
}
